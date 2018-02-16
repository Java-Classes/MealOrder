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

import com.google.common.collect.ImmutableList;
import com.google.protobuf.Message;
import io.spine.net.EmailAddress;
import io.spine.server.aggregate.Aggregate;
import io.spine.server.aggregate.Apply;
import io.spine.server.command.Assign;
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

    private PurchaseOrderSender poSender;

    /**
     * {@inheritDoc}
     */
    public PurchaseOrderAggregate(PurchaseOrderId id, PurchaseOrderSender poSender) {
        super(id);
        this.poSender = poSender;
    }

    @Assign
    List<? extends Message> handle(CreatePurchaseOrder cmd) throws CannotCreatePurchaseOrder {
        if (!isAllowedPurchaseOrderCreation(cmd)) {
            throwCannotCreatePurchaseOrder(cmd);
        }

        ImmutableList.Builder<Message> result = ImmutableList.builder();
        result.add(createPOCreatedEvent(cmd));

        List<Order> invalidOrders = findInvalidOrders(cmd.getOrdersList());

        // TODO 2/16/2018[yegor.udovchenko]:add PO event signalizing sending error.
        if (invalidOrders.isEmpty()) {
            result.add(createPOValidationPassedEvent(cmd));
            final PurchaseOrder purchaseOrder = PurchaseOrder.newBuilder()
                                                             .setId(cmd.getId())
                                                             .setStatus(VALID)
                                                             .addAllOrders(cmd.getOrdersList())
                                                             .build();

            if (poSender.formAndSendPurchaseOrder(purchaseOrder, cmd.getWhoCreates()
                                                                    .getEmail(),
                                                  cmd.getVendorEmail())) {
                result.add(createPOSentEvent(purchaseOrder,
                                             cmd.getWhoCreates()
                                                .getEmail(),
                                             cmd.getVendorEmail()));
            }
        } else {
            result.add(createPOValidationFailedEvent(cmd, invalidOrders));
        }
        return result.build();
    }

    @Assign
    List<? extends Message> handle(MarkPurchaseOrderAsValid cmd)
            throws CannotOverruleValidationOfNotInvalidPO {
        if (!isAllowedToMarkAsValid()) {
            throwCannotOverruleValidationOfNotInvalidPO(cmd);
        }
        ImmutableList.Builder<Message> result = ImmutableList.builder();
        result.add(createPOValidationOverruledEvent(cmd));
        // TODO 2/16/2018[yegor.udovchenko]:add PO event signalizing sending error.
        if (poSender.formAndSendPurchaseOrder(getState(), cmd.getUserId()
                                                             .getEmail(), cmd.getVendorEmail())) {
            result.add(createPOSentEvent(getState(), cmd.getUserId()
                                                        .getEmail(),
                                         cmd.getVendorEmail()));
        }
        return result.build();
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
        return createPOCanceledEvent(cmd);
    }

    /*
     * Event appliers
     *****************/

    @Apply
    private void purchaseOrderCreated(PurchaseOrderCreated event) {
        getBuilder().setId(event.getId())
                    .addAllOrders(event.getOrdersList())
                    .setStatus(CREATED);

    }

    @Apply
    private void purchaseOrderValidationPassed(PurchaseOrderValidationPassed event) {
        getBuilder().setStatus(VALID);
    }

    @Apply
    private void purchaseOrderValidationFailed(PurchaseOrderValidationFailed event) {
        getBuilder().setStatus(INVALID);
    }

    @Apply
    private void purchaseOrderValidationOverruled(PurchaseOrderValidationOverruled event) {
        getBuilder().setStatus(VALID);
    }

    @Apply
    private void purchaseOrderCanceled(PurchaseOrderCanceled event) {
        getBuilder().setStatus(CANCELED);
    }

    @Apply
    private void purchaseOrderCanceled(PurchaseOrderDelivered event) {
        getBuilder().setStatus(DELIVERED);
    }

    @Apply
    private void purchaseOrderSent(PurchaseOrderSent event) {
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

    private static PurchaseOrderCanceled createPOCanceledEvent(CancelPurchaseOrder cmd) {
        final PurchaseOrderCanceled.Builder builder = PurchaseOrderCanceled
                .newBuilder()
                .setId(cmd.getId())
                .setUserId(cmd.getUserId())
                .setWhenCanceled(getCurrentTime());

        switch (cmd.getReasonCase()) {
            case INVALID:
                return builder.setInvalid(cmd.getInvalid())
                              .build();
            case CUSTOM_REASON:
                return builder.setCustomReason(cmd.getCustomReason())
                              .build();
            default:
                return builder.build();
        }
    }

    private PurchaseOrderSent createPOSentEvent(PurchaseOrder purchaseOrder,
                                                EmailAddress senderEmail,
                                                EmailAddress vendorEmail) {
        return PurchaseOrderSent.newBuilder()
                                .setPurchaseOrder(purchaseOrder)
                                .setSenderEmail(senderEmail)
                                .setVendorEmail(vendorEmail)
                                .setWhenSent(getCurrentTime())
                                .build();
    }
}
