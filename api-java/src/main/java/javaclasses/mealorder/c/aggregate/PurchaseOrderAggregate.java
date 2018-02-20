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

package javaclasses.mealorder.c.aggregate;

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
import java.util.Optional;

import static io.spine.time.Time.getCurrentTime;
import static javaclasses.mealorder.PurchaseOrderStatus.CANCELED;
import static javaclasses.mealorder.PurchaseOrderStatus.CREATED;
import static javaclasses.mealorder.PurchaseOrderStatus.DELIVERED;
import static javaclasses.mealorder.PurchaseOrderStatus.INVALID;
import static javaclasses.mealorder.PurchaseOrderStatus.SENT;
import static javaclasses.mealorder.PurchaseOrderStatus.VALID;
import static javaclasses.mealorder.c.aggregate.PurchaseOrderValidator.findInvalidOrders;
import static javaclasses.mealorder.c.aggregate.PurchaseOrderValidator.isAllowedPurchaseOrderCreation;
import static javaclasses.mealorder.c.aggregate.rejection.PurchaseOrderAggregateRejections.throwCannotCancelDeliveredPurchaseOrder;
import static javaclasses.mealorder.c.aggregate.rejection.PurchaseOrderAggregateRejections.throwCannotCreatePurchaseOrder;
import static javaclasses.mealorder.c.aggregate.rejection.PurchaseOrderAggregateRejections.throwCannotMarkPurchaseOrderAsDelivered;
import static javaclasses.mealorder.c.aggregate.rejection.PurchaseOrderAggregateRejections.throwCannotOverruleValidationOfNotInvalidPO;

/**
 * The aggregate managing the state of a {@link PurchaseOrder}.
 *
 * @author Yegor Udovchenko
 */
@SuppressWarnings({"ClassWithTooManyMethods", /* Vendor po cannot be separated and should
                                                 process all commands and events related to it
                                                 according to the domain model.
                                                 The {@code Aggregate} does it with methods
                                                 annotated as {@code Assign} and {@code Apply}.
                                                 In that case class has too many methods.*/
        "OverlyCoupledClass",/* As each method needs dependencies  necessary to perform execution
                                                 that class also overly coupled.*/
        "unused"}) /* Methods that modifies the state of the aggregate with data from the passed event is used in the internal logic. */
public class PurchaseOrderAggregate extends Aggregate<PurchaseOrderId,
        PurchaseOrder, PurchaseOrderVBuilder> {

    /**
     * {@inheritDoc}
     */
    public PurchaseOrderAggregate(PurchaseOrderId id) {
        super(id);
    }

    @Assign
    Triplet<PurchaseOrderCreated,
            EitherOfTwo<PurchaseOrderValidationPassed,
                    PurchaseOrderValidationFailed>,
            Optional<PurchaseOrderSent>> handle(CreatePurchaseOrder cmd)
            throws CannotCreatePurchaseOrder {

        if (!isAllowedPurchaseOrderCreation(cmd)) {
            throwCannotCreatePurchaseOrder(cmd);
        }

        Triplet result;
        final PurchaseOrderCreated poCreatedEvent = createPOCreatedEvent(cmd);

        final List<Order> invalidOrders = findInvalidOrders(cmd.getOrdersList());

        if (invalidOrders.isEmpty()) {
            final PurchaseOrderValidationPassed passedEvent = createPOValidationPassedEvent(cmd);
            final PurchaseOrder purchaseOrder = PurchaseOrder.newBuilder()
                                                             .setId(cmd.getId())
                                                             .setStatus(VALID)
                                                             .addAllOrders(cmd.getOrdersList())
                                                             .build();

            final EmailAddress senderEmail = cmd.getWhoCreates()
                                                .getEmail();
            final EmailAddress vendorEmail = cmd.getVendorEmail();
            ServiceFactory.getPurchaseOrderSender()
                          .formAndSendPurchaseOrder(
                                  purchaseOrder,
                                  senderEmail,
                                  vendorEmail);
            final PurchaseOrderSent poSentEvent = createPOSentEvent(purchaseOrder,
                                                                    senderEmail,
                                                                    vendorEmail);
            result = Triplet.of(poCreatedEvent, passedEvent, poSentEvent);

        } else {
            final PurchaseOrderValidationFailed validationFailedEvent = createPOValidationFailedEvent(
                    cmd, invalidOrders);
            result = Triplet.withNullable(poCreatedEvent, validationFailedEvent, null);
        }
        return result;
    }

    @Assign
    Pair<PurchaseOrderValidationOverruled, PurchaseOrderSent> handle(MarkPurchaseOrderAsValid cmd)
            throws CannotOverruleValidationOfNotInvalidPO {
        if (!isAllowedToMarkAsValid()) {
            throwCannotOverruleValidationOfNotInvalidPO(cmd);
        }

        final PurchaseOrderValidationOverruled overruledEvent =
                createPOValidationOverruledEvent(cmd);
        final EmailAddress senderEmail = cmd.getUserId()
                                            .getEmail();
        final EmailAddress vendorEmail = cmd.getVendorEmail();
        ServiceFactory.getPurchaseOrderSender()
                      .formAndSendPurchaseOrder(
                              getState(),
                              senderEmail,
                              vendorEmail);
        final PurchaseOrderSent poSentEvent = createPOSentEvent(getState(),
                                                                senderEmail,
                                                                vendorEmail);
        final Pair result = Pair.of(overruledEvent, poSentEvent);
        return result;
    }

    @Assign
    PurchaseOrderDelivered handle(MarkPurchaseOrderAsDelivered cmd)
            throws CannotMarkPurchaseOrderAsDelivered {
        if (!isAllowedToMarkAsDelivered()) {
            throwCannotMarkPurchaseOrderAsDelivered(cmd);
        }
        return createPOMarkedAsDeliveredEvent(cmd);
    }

    @Assign
    PurchaseOrderCanceled handle(CancelPurchaseOrder cmd)
            throws CannotCancelDeliveredPurchaseOrder {
        if (!isAllowedToCancel()) {
            throwCannotCancelDeliveredPurchaseOrder(cmd);
        }
        return createPOCanceledEvent(cmd, getState().getOrdersList());
    }

    /*
     * Event appliers
     *****************/

    @Apply
    void purchaseOrderCreated(PurchaseOrderCreated event) {
        getBuilder().setId(event.getId())
                    .addAllOrders(event.getOrdersList())
                    .setStatus(CREATED);

    }

    @Apply
    void purchaseOrderValidationPassed(PurchaseOrderValidationPassed event) {
        getBuilder().setStatus(VALID);
    }

    @Apply
    void empteEvent(Empty event) {
        // Empty applier for 'io.spine.server.tuple.Triplet' return value with 'null'
        // value in constructor.
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
        return PurchaseOrderCreated.newBuilder()
                                   .setId(cmd.getId())
                                   .setWhoCreated(cmd.getWhoCreates())
                                   .setWhenCreated(getCurrentTime())
                                   .addAllOrders(cmd.getOrdersList())
                                   .build();
    }

    private static PurchaseOrderValidationFailed createPOValidationFailedEvent(
            CreatePurchaseOrder cmd,
            List<Order> invalidOrders) {
        return PurchaseOrderValidationFailed.newBuilder()
                                            .setId(cmd.getId())
                                            .addAllFailureOrders(invalidOrders)
                                            .setWhenFailed(getCurrentTime())
                                            .build();
    }

    private static PurchaseOrderValidationPassed createPOValidationPassedEvent(
            CreatePurchaseOrder cmd) {
        return PurchaseOrderValidationPassed.newBuilder()
                                            .setId(cmd.getId())
                                            .setWhenPassed(getCurrentTime())
                                            .build();
    }

    private static PurchaseOrderValidationOverruled createPOValidationOverruledEvent(
            MarkPurchaseOrderAsValid cmd) {
        return PurchaseOrderValidationOverruled.newBuilder()
                                               .setId(cmd.getId())
                                               .setWhoOverruled(cmd.getUserId())
                                               .setWhenOverruled(getCurrentTime())
                                               .setReason(cmd.getReason())
                                               .build();
    }

    private static PurchaseOrderDelivered createPOMarkedAsDeliveredEvent(
            MarkPurchaseOrderAsDelivered cmd) {
        return PurchaseOrderDelivered.newBuilder()
                                     .setId(cmd.getId())
                                     .setWhoMarkedAsDelivered(cmd.getWhoMarksAsDelivered())
                                     .setWhenDelievered(getCurrentTime())
                                     .build();
    }

    private static PurchaseOrderCanceled createPOCanceledEvent(CancelPurchaseOrder cmd,
                                                               List<Order> orders) {
        final PurchaseOrderCanceled.Builder builder = PurchaseOrderCanceled
                .newBuilder()
                .setId(cmd.getId())
                .setUserId(cmd.getUserId())
                .addAllOrder(orders)
                .setWhenCanceled(getCurrentTime());

        switch (cmd.getReasonCase()) {
            case INVALID:
                builder.setInvalid(cmd.getInvalid());
                break;
            case CUSTOM_REASON:
                builder.setCustomReason(cmd.getCustomReason());
                break;
            case REASON_NOT_SET:
                builder.setCustomReason("Reason not set.");
                break;
        }
        return builder.build();
    }

    private PurchaseOrderSent createPOSentEvent(PurchaseOrder purchaseOrder,
                                                EmailAddress senderEmail, EmailAddress vendorEmail
    ) {
        return PurchaseOrderSent.newBuilder()
                                .setPurchaseOrder(purchaseOrder)
                                .setSenderEmail(senderEmail)
                                .setVendorEmail(vendorEmail)
                                .build();
    }
}
