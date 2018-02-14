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

package javaclasses.mealorder.c.aggregate.aggregate;

import com.google.common.collect.ImmutableList;
import com.google.protobuf.Message;
import io.spine.server.aggregate.Aggregate;
import io.spine.server.aggregate.Apply;
import io.spine.server.command.Assign;
import javaclasses.mealorder.Order;
import javaclasses.mealorder.PurchaseOrder;
import javaclasses.mealorder.PurchaseOrderId;
import javaclasses.mealorder.PurchaseOrderVBuilder;
import javaclasses.mealorder.UserId;
import javaclasses.mealorder.c.command.CancelPurchaseOrder;
import javaclasses.mealorder.c.command.CreatePurchaseOrder;
import javaclasses.mealorder.c.command.MarkPurchaseOrderAsDelivered;
import javaclasses.mealorder.c.command.MarkPurchaseOrderAsValid;
import javaclasses.mealorder.c.event.PurchaseOrderCanceled;
import javaclasses.mealorder.c.event.PurchaseOrderCreated;
import javaclasses.mealorder.c.event.PurchaseOrderDelivered;
import javaclasses.mealorder.c.event.PurchaseOrderValidationFailed;
import javaclasses.mealorder.c.event.PurchaseOrderValidationOverruled;
import javaclasses.mealorder.c.event.PurchaseOrderValidationPassed;
import javaclasses.mealorder.c.rejection.CannotCancelDeliveredPurchaseOrder;
import javaclasses.mealorder.c.rejection.CannotCreatePurchaseOrder;
import javaclasses.mealorder.c.rejection.CannotMarkCanceledPurchaseOrderAsDelivered;

import java.util.List;

import static io.spine.time.Time.getCurrentTime;
import static java.util.Collections.singletonList;
import static javaclasses.mealorder.c.aggregate.PurchaseOrderAggregateRejections.throwCannotCreatePurchaseOrder;
import static javaclasses.mealorder.c.aggregate.aggregate.PurchaseOrderValidator.isValidPurchaseOrderCreation;

/**
 * The aggregate managing the state of a {@link PurchaseOrder}.
 *
 * @author Yegor Udovchenko
 */
@SuppressWarnings({"ClassWithTooManyMethods", /* Vendor definition cannot be separated and should
                                                 process all commands and events related to it
                                                 according to the domain model.
                                                 The {@code Aggregate} does it with methods
                                                 annotated as {@code Assign} and {@code Apply}.
                                                 In that case class has too many methods.*/
        "OverlyCoupledClass"}) /* As each method needs dependencies  necessary to perform execution
                                                 that class also overly coupled.*/
public class PurchaseOrderAggregate extends Aggregate<PurchaseOrderId,
        PurchaseOrder, PurchaseOrderVBuilder> {

    /**
     * {@inheritDoc}
     */
    public PurchaseOrderAggregate(PurchaseOrderId id) {
        super(id);
    }

    @Assign
    List<? extends Message> handle(CreatePurchaseOrder cmd) throws CannotCreatePurchaseOrder {
        if (!isValidPurchaseOrderCreation(cmd)) {
            throwCannotCreatePurchaseOrder(cmd);
        }

        ImmutableList.Builder<Message> result = ImmutableList.builder();
        final PurchaseOrderCreated createdEvent = createPurchaseOrderCreatedEvent(cmd);
        result.add(createdEvent);

        List<Order> invalidOrders = PurchaseOrderValidator.findInvalidOrders(cmd.getOrdersList());

        if (invalidOrders.isEmpty()) {
            final PurchaseOrderValidationPassed validationPassedEvent =
                    createPOValidationPassedEvent(cmd);
            result.add(validationPassedEvent);
        } else {
            final PurchaseOrderValidationFailed validationFailedEvent =
                    createPOValidationFailedEvent(cmd, invalidOrders);
            return result.add(validationFailedEvent)
                         .build();
        }

        return result.build();
    }

    @Assign
    List<? extends Message> handle(MarkPurchaseOrderAsValid cmd) {
        final PurchaseOrderId purchaseOrderId = cmd.getId();
        final UserId userId = cmd.getUserId();
        final String reason = cmd.getReason();

        final PurchaseOrderValidationOverruled result = PurchaseOrderValidationOverruled
                .newBuilder()
                .setId(purchaseOrderId)
                .setWhoOverruled(userId)
                .setWhenOverruled(getCurrentTime())
                .setReason(reason)
                .build();

        return singletonList(result);
    }

    @Assign
    List<? extends Message> handle(MarkPurchaseOrderAsDelivered cmd)
            throws CannotMarkCanceledPurchaseOrderAsDelivered {
        final PurchaseOrderId purchaseOrderId = cmd.getId();
        final UserId userId = cmd.getWhoMarksAsDelivered();

        final PurchaseOrderDelivered result = PurchaseOrderDelivered
                .newBuilder()
                .setId(purchaseOrderId)
                .setWhoMarkedAsDelivered(userId)
                .setWhenDelievered(getCurrentTime())
                .build();

        return singletonList(result);
    }

    @Assign
    List<? extends Message> handle(CancelPurchaseOrder cmd)
            throws CannotCancelDeliveredPurchaseOrder {
        final PurchaseOrderId purchaseOrderId = cmd.getId();
        final UserId userId = cmd.getUserId();

        final PurchaseOrderCanceled.Builder builder = PurchaseOrderCanceled
                .newBuilder()
                .setId(purchaseOrderId)
                .setUserId(userId)
                .setWhenCanceled(getCurrentTime());

        switch (cmd.getReasonCase()) {
            case INVALID:
                return singletonList(builder.setInvalid(cmd.getInvalid())
                                            .build());
            case CUSTOM_REASON:
                return singletonList(builder.setCustomReason(cmd.getCustomReason())
                                            .build());
            default:
                return singletonList(builder.build());
        }
    }

    /*
     * Event appliers
     *****************/

    @Apply
    private void purchaseOrderCreated(PurchaseOrderCreated event) {
        getBuilder().setId(event.getId())
                    .addAllOrders(event.getOrdersList());

    }

    private PurchaseOrderCreated createPurchaseOrderCreatedEvent(CreatePurchaseOrder cmd) {
        return PurchaseOrderCreated.newBuilder()
                                   .setId(cmd.getId())
                                   .setWhoCreated(cmd.getWhoCreates())
                                   .setWhenCreated(getCurrentTime())
                                   .addAllOrders(cmd.getOrdersList())
                                   .build();
    }

    private PurchaseOrderValidationFailed createPOValidationFailedEvent(CreatePurchaseOrder cmd,
                                                                        List<Order> invalidOrders) {
        return PurchaseOrderValidationFailed.newBuilder()
                                            .setId(cmd.getId())
                                            .addAllFailureOrders(invalidOrders)
                                            .setWhenFailed(getCurrentTime())
                                            .build();
    }

    private PurchaseOrderValidationPassed createPOValidationPassedEvent(CreatePurchaseOrder cmd) {
        return PurchaseOrderValidationPassed.newBuilder()
                                            .setId(cmd.getId())
                                            .setWhenPassed(getCurrentTime())
                                            .build();
    }
}
