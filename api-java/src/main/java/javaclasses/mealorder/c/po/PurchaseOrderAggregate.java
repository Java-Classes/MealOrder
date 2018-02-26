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
import javaclasses.mealorder.PurchaseOrderVBuilder;
import javaclasses.mealorder.ServiceFactory;
import javaclasses.mealorder.UserId;
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

import static io.spine.time.Time.getCurrentTime;
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
import static javaclasses.mealorder.c.po.PurchaseOrders.findInvalidOrders;
import static javaclasses.mealorder.c.po.PurchaseOrders.hasInvalidOrders;
import static javaclasses.mealorder.c.po.PurchaseOrders.isAllowedPurchaseOrderCreation;

/**
 * The aggregate managing the state of a {@link PurchaseOrder}.
 *
 * @author Yegor Udovchenko
 */
@SuppressWarnings({"ClassWithTooManyMethods", /* Purchase order cannot be separated and should
                                                 process all commands and events related to it
                                                 according to the domain model.
                                                 The {@code Aggregate} does it with methods
                                                 annotated as {@code Assign} and {@code Apply}.
                                                 In that case class has too many methods.*/
        "OverlyCoupledClass",  /* As each method needs dependencies  necessary to perform execution
                                                 that class also overly coupled.*/
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
            cannotCreatePurchaseOrder(cmd);
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

        if (!isAllowedToMarkAsValid()) {
            cannotOverruleValidationOfNotInvalidPO(cmd);
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
        if (!isAllowedToMarkAsDelivered()) {
            cannotMarkPurchaseOrderAsDelivered(cmd);
        }
        final PurchaseOrderDelivered poMarkedAsDeliveredEvent = createPOMarkedAsDeliveredEvent(cmd);
        return poMarkedAsDeliveredEvent;
    }

    @Assign
    PurchaseOrderCanceled handle(CancelPurchaseOrder cmd) throws
                                                          CannotCancelDeliveredPurchaseOrder {
        if (!isAllowedToCancel()) {
            cannotCancelDeliveredPurchaseOrder(cmd);
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

    private boolean isAllowedToMarkAsValid() {
        return getState().getStatus() == INVALID;
    }

    private boolean isAllowedToMarkAsDelivered() {
        return getState().getStatus() == SENT;
    }

    private boolean isAllowedToCancel() {
        return getState().getStatus() != DELIVERED;
    }

    private static PurchaseOrderCreated createPOCreatedEvent(CreatePurchaseOrder cmd) {
        final PurchaseOrderId id = cmd.getId();
        final UserId whoCreates = cmd.getWhoCreates();
        final List<Order> orderList = cmd.getOrderList();
        final PurchaseOrderCreated result = PurchaseOrderCreated.newBuilder()
                                                                .setId(id)
                                                                .setWhoCreated(whoCreates)
                                                                .setWhenCreated(getCurrentTime())
                                                                .addAllOrder(orderList)
                                                                .build();
        return result;
    }

    private static PurchaseOrderValidationFailed createPOValidationFailedEvent(
            CreatePurchaseOrder cmd,
            List<Order> invalidOrders) {

        final PurchaseOrderId id = cmd.getId();
        final PurchaseOrderValidationFailed result = PurchaseOrderValidationFailed
                .newBuilder()
                .setId(id)
                .addAllFailureOrder(invalidOrders)
                .setWhenFailed(getCurrentTime())
                .build();

        return result;
    }

    private static PurchaseOrderValidationPassed createPOValidationPassedEvent(
            CreatePurchaseOrder cmd) {
        final PurchaseOrderId id = cmd.getId();
        final PurchaseOrderValidationPassed result = PurchaseOrderValidationPassed
                .newBuilder()
                .setId(id)
                .setWhenPassed(getCurrentTime())
                .build();

        return result;
    }

    private static PurchaseOrderValidationOverruled createPOValidationOverruledEvent(
            MarkPurchaseOrderAsValid cmd) {
        final PurchaseOrderId id = cmd.getId();
        final UserId userId = cmd.getUserId();
        final String reason = cmd.getReason();
        final PurchaseOrderValidationOverruled result = PurchaseOrderValidationOverruled
                .newBuilder()
                .setId(id)
                .setWhoOverruled(userId)
                .setWhenOverruled(getCurrentTime())
                .setReason(reason)
                .build();

        return result;
    }

    private static PurchaseOrderDelivered createPOMarkedAsDeliveredEvent(
            MarkPurchaseOrderAsDelivered cmd) {
        final PurchaseOrderId id = cmd.getId();
        final UserId whoMarksAsDelivered = cmd.getWhoMarksAsDelivered();
        final PurchaseOrderDelivered result = PurchaseOrderDelivered
                .newBuilder()
                .setId(id)
                .setWhoMarkedAsDelivered(whoMarksAsDelivered)
                .setWhenDelievered(getCurrentTime())
                .build();

        return result;
    }

    private static PurchaseOrderCanceled createPOCanceledEvent(CancelPurchaseOrder cmd,
                                                               List<Order> orders) {
        final PurchaseOrderId purchaseOrderId = cmd.getId();
        final UserId userId = cmd.getUserId();
        final PurchaseOrderCanceled.Builder builder = PurchaseOrderCanceled
                .newBuilder()
                .setId(purchaseOrderId)
                .setUserId(userId)
                .addAllOrder(orders)
                .setWhenCanceled(getCurrentTime());

        switch (cmd.getReasonCase()) {
            case INVALID:
                final boolean value = cmd.getInvalid();
                builder.setInvalid(value);
                break;
            case CUSTOM_REASON:
                final String customReason = cmd.getCustomReason();
                builder.setCustomReason(customReason);
                break;
            case REASON_NOT_SET:
                builder.setCustomReason("Reason not set.");
                break;
        }
        return builder.build();
    }

    private static PurchaseOrderSent createPOSentEvent(PurchaseOrder purchaseOrder,
                                                       EmailAddress senderEmail,
                                                       EmailAddress vendorEmail) {
        final PurchaseOrderSent result = PurchaseOrderSent.newBuilder()
                                                          .setPurchaseOrder(purchaseOrder)
                                                          .setSenderEmail(senderEmail)
                                                          .setVendorEmail(vendorEmail)
                                                          .build();
        return result;
    }

    private static PurchaseOrder createPurchaseOrderInstance(CreatePurchaseOrder cmd) {
        final PurchaseOrderId id = cmd.getId();
        final List<Order> orderList = cmd.getOrderList();
        final PurchaseOrder result = PurchaseOrder.newBuilder()
                                                  .setId(id)
                                                  .setStatus(VALID)
                                                  .addAllOrder(orderList)
                                                  .build();
        return result;
    }
}
