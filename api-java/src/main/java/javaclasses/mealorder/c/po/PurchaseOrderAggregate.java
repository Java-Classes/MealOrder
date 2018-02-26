/*
 * Copyright 2018, TeamDev Ltd. All rights reserved.
 *
 * Redistribution and use in source and/or binary forms, with or without
 * modification, must retain the above copyright notice and the following
 * disclaimer.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package javaclasses.mealorder.c.po;

import com.google.common.base.Optional;
import com.google.protobuf.Empty;
import io.spine.net.EmailAddress;
import io.spine.server.aggregate.Aggregate;
import io.spine.server.aggregate.Apply;
import io.spine.server.command.Assign;
import io.spine.server.tuple.EitherOfTwo;
import io.spine.server.tuple.Pair;
import io.spine.server.tuple.Triplet;
import javaclasses.mealorder.Order;
import javaclasses.mealorder.PurchaseOrder;
import javaclasses.mealorder.PurchaseOrderId;
import javaclasses.mealorder.PurchaseOrderStatus;
import javaclasses.mealorder.PurchaseOrderVBuilder;
import javaclasses.mealorder.ServiceFactory;
import javaclasses.mealorder.c.command.CancelPurchaseOrder;
import javaclasses.mealorder.c.command.CreatePurchaseOrder;
import javaclasses.mealorder.c.command.MarkPurchaseOrderAsDelivered;
import javaclasses.mealorder.c.command.MarkPurchaseOrderAsValid;
import javaclasses.mealorder.c.event.PurchaseOrderCanceled;
import javaclasses.mealorder.c.event.PurchaseOrderCreated;
import javaclasses.mealorder.c.event.PurchaseOrderDelivered;
import javaclasses.mealorder.c.event.PurchaseOrderSent;
import javaclasses.mealorder.c.event.PurchaseOrderValidationFailed;
import javaclasses.mealorder.c.event.PurchaseOrderValidationOverruled;
import javaclasses.mealorder.c.event.PurchaseOrderValidationPassed;
import javaclasses.mealorder.c.rejection.CannotCancelDeliveredPurchaseOrder;
import javaclasses.mealorder.c.rejection.CannotCreatePurchaseOrder;
import javaclasses.mealorder.c.rejection.CannotMarkPurchaseOrderAsDelivered;
import javaclasses.mealorder.c.rejection.CannotOverruleValidationOfNotInvalidPO;

import java.util.List;

import static javaclasses.mealorder.PurchaseOrderStatus.CANCELED;
import static javaclasses.mealorder.PurchaseOrderStatus.CREATED;
import static javaclasses.mealorder.PurchaseOrderStatus.DELIVERED;
import static javaclasses.mealorder.PurchaseOrderStatus.INVALID;
import static javaclasses.mealorder.PurchaseOrderStatus.SENT;
import static javaclasses.mealorder.PurchaseOrderStatus.VALID;
import static javaclasses.mealorder.c.po.PurchaseOrderAggregateRejections.cannotCancelDeliveredPurchaseOrder;
import static javaclasses.mealorder.c.po.PurchaseOrderAggregateRejections.cannotCreatePurchaseOrder;
import static javaclasses.mealorder.c.po.PurchaseOrderAggregateRejections.cannotMarkPurchaseOrderAsDelivered;
import static javaclasses.mealorder.c.po.PurchaseOrderAggregateRejections.cannotOverruleValidationOfNotInvalidPO;
import static javaclasses.mealorder.c.po.PurchaseOrders.createPOCanceledEvent;
import static javaclasses.mealorder.c.po.PurchaseOrders.createPOCreatedEvent;
import static javaclasses.mealorder.c.po.PurchaseOrders.createPOMarkedAsDeliveredEvent;
import static javaclasses.mealorder.c.po.PurchaseOrders.createPOSentEvent;
import static javaclasses.mealorder.c.po.PurchaseOrders.createPOValidationFailedEvent;
import static javaclasses.mealorder.c.po.PurchaseOrders.createPOValidationOverruledEvent;
import static javaclasses.mealorder.c.po.PurchaseOrders.createPOValidationPassedEvent;
import static javaclasses.mealorder.c.po.PurchaseOrders.createPurchaseOrderInstance;
import static javaclasses.mealorder.c.po.PurchaseOrders.findInvalidOrders;
import static javaclasses.mealorder.c.po.PurchaseOrders.hasInvalidOrders;
import static javaclasses.mealorder.c.po.PurchaseOrders.isAllowedPurchaseOrderCreation;
import static javaclasses.mealorder.c.po.PurchaseOrders.isAllowedToCancel;
import static javaclasses.mealorder.c.po.PurchaseOrders.isAllowedToMarkAsDelivered;
import static javaclasses.mealorder.c.po.PurchaseOrders.isAllowedToMarkAsValid;

/**
 * The aggregate managing the state of a {@link PurchaseOrder}.
 *
 * @author Yegor Udovchenko
 */
@SuppressWarnings({"OverlyCoupledClass",  /* As each method needs dependencies  necessary to
                                            perform execution that class also overly coupled.*/
        "unused" /* Methods annotated with {@code Apply} are called in runtime using reflection.*/
})
public class PurchaseOrderAggregate extends Aggregate<PurchaseOrderId,
        PurchaseOrder, PurchaseOrderVBuilder> {

    PurchaseOrderAggregate(PurchaseOrderId id) {
        super(id);
    }

    // TODO 2/26/2018[yegor.udovchenko]: find out how to create returning type triplet
    @Assign
    Triplet<PurchaseOrderCreated,
            EitherOfTwo<PurchaseOrderValidationPassed,
                    PurchaseOrderValidationFailed>,
            Optional<PurchaseOrderSent>> handle(CreatePurchaseOrder cmd) throws
                                                                         CannotCreatePurchaseOrder {

        if (!isAllowedPurchaseOrderCreation(cmd)) {
            throw cannotCreatePurchaseOrder(cmd);
        }

        Triplet result;
        final PurchaseOrderCreated poCreatedEvent = createPOCreatedEvent(cmd);
        final List<Order> orderList = cmd.getOrderList();

        if (!hasInvalidOrders(orderList)) {
            final PurchaseOrderValidationPassed passedEvent = createPOValidationPassedEvent(cmd);
            final PurchaseOrder purchaseOrder = createPurchaseOrderInstance(cmd);
            final EmailAddress senderEmail = cmd.getWhoCreates()
                                                .getEmail();
            final EmailAddress vendorEmail = cmd.getVendorEmail();

            ServiceFactory.getPurchaseOrderSender()
                          .send(purchaseOrder, senderEmail, vendorEmail);

            final PurchaseOrderSent poSentEvent = createPOSentEvent(purchaseOrder,
                                                                    senderEmail,
                                                                    vendorEmail);
            result = Triplet.of(poCreatedEvent, passedEvent, poSentEvent);
            return result;
        }

        final List<Order> invalidOrders = findInvalidOrders(orderList);
        final PurchaseOrderValidationFailed validationFailedEvent =
                createPOValidationFailedEvent(cmd, invalidOrders);
        result = Triplet.withNullable(poCreatedEvent, validationFailedEvent, null);
        return result;
    }

    @Assign
    Pair<PurchaseOrderValidationOverruled, PurchaseOrderSent> handle(MarkPurchaseOrderAsValid cmd)
            throws CannotOverruleValidationOfNotInvalidPO {

        final PurchaseOrderStatus status = getState().getStatus();
        if (!isAllowedToMarkAsValid(status)) {
            throw cannotOverruleValidationOfNotInvalidPO(cmd);
        }

        final PurchaseOrderValidationOverruled overruledEvent =
                createPOValidationOverruledEvent(cmd);
        final EmailAddress senderEmail = cmd.getUserId()
                                            .getEmail();
        final EmailAddress vendorEmail = cmd.getVendorEmail();
        ServiceFactory.getPurchaseOrderSender()
                      .send(getState(), senderEmail, vendorEmail);

        final PurchaseOrderSent poSentEvent = createPOSentEvent(getState(),
                                                                senderEmail,
                                                                vendorEmail);
        final Pair result = Pair.of(overruledEvent, poSentEvent);
        return result;
    }

    @Assign
    PurchaseOrderDelivered handle(MarkPurchaseOrderAsDelivered cmd) throws
                                                                    CannotMarkPurchaseOrderAsDelivered {
        final PurchaseOrderStatus status = getState().getStatus();
        if (!isAllowedToMarkAsDelivered(status)) {
            throw cannotMarkPurchaseOrderAsDelivered(cmd);
        }
        final PurchaseOrderDelivered poMarkedAsDeliveredEvent = createPOMarkedAsDeliveredEvent(cmd);
        return poMarkedAsDeliveredEvent;
    }

    @Assign
    PurchaseOrderCanceled handle(CancelPurchaseOrder cmd) throws
                                                          CannotCancelDeliveredPurchaseOrder {
        final PurchaseOrderStatus status = getState().getStatus();
        if (!isAllowedToCancel(status)) {
            throw cannotCancelDeliveredPurchaseOrder(cmd);
        }
        final List<Order> orderList = getState().getOrderList();
        final PurchaseOrderCanceled poCanceledEvent = createPOCanceledEvent(cmd, orderList);
        return poCanceledEvent;
    }

    /*
     * Event appliers
     *****************/

    @Apply
    void purchaseOrderCreated(PurchaseOrderCreated event) {
        getBuilder().setId(event.getId())
                    .addAllOrder(event.getOrderList())
                    .setStatus(CREATED);
    }

    @Apply
    void purchaseOrderValidationPassed(PurchaseOrderValidationPassed event) {
        getBuilder().setStatus(VALID);
    }

    @Apply
    void emptyEvent(Empty event) {
        // Applier for empty event.
        // Used when CreatePurchaseOrder handler returns three events:
        // PurchaseOrderCreated, PurchaseOrderValidationFailed, Empty
        // to handle Empty event without changing aggregate state.
    }

    @Apply
    void purchaseOrderValidationFailed(PurchaseOrderValidationFailed event) {
        getBuilder().setStatus(INVALID);
    }

    @Apply
    void purchaseOrderValidationOverruled(PurchaseOrderValidationOverruled event) {
        getBuilder().setStatus(VALID);
    }

    @Apply
    void purchaseOrderCanceled(PurchaseOrderCanceled event) {
        getBuilder().setStatus(CANCELED);
    }

    @Apply
    void purchaseOrderCanceled(PurchaseOrderDelivered event) {
        getBuilder().setStatus(DELIVERED);
    }

    @Apply
    void purchaseOrderSent(PurchaseOrderSent event) {
        getBuilder().setStatus(SENT);
    }
}
