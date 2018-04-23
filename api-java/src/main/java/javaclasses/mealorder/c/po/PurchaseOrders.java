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

import io.spine.net.EmailAddress;
import io.spine.time.LocalDate;
import javaclasses.mealorder.Order;
import javaclasses.mealorder.PurchaseOrder;
import javaclasses.mealorder.PurchaseOrderId;
import javaclasses.mealorder.PurchaseOrderStatus;
import javaclasses.mealorder.UserId;
import javaclasses.mealorder.VendorId;
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

import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.spine.time.Time.getCurrentTime;
import static javaclasses.mealorder.OrderStatus.ORDER_ACTIVE;
import static javaclasses.mealorder.PurchaseOrderStatus.DELIVERED;
import static javaclasses.mealorder.PurchaseOrderStatus.INVALID;
import static javaclasses.mealorder.PurchaseOrderStatus.SENT;
import static javaclasses.mealorder.PurchaseOrderStatus.VALID;

/**
 * The utility class to manage the process of creation and modification
 * of a {@link PurchaseOrder} instance.
 *
 * <p> Contains methods for {@code CreatePurchaseOrder} command validation,
 * methods for creation event instances for {@code PurchaseOrderAggregate},
 * methods for validation of purchase order status transitions.
 *
 * @author Yegor Udovchenko
 */
@SuppressWarnings("TypeMayBeWeakened" /* Private methods of this class should
                                         not use parameters weakened to `ObjectOrBuilder` classes.
                                         Those methods are used for validation and not for
                                         construction.*/)
class PurchaseOrders {
    /**
     * {@code MAX_SINGLE_DISH_COUNT} is the max number of equal dishes in
     * the order.
     */
    private static final int MAX_SINGLE_DISH_COUNT = 20;

    /** Prevents instantiation of this utility class. */
    private PurchaseOrders() {
    }

    /**
     * Performs the validation of a purchase order creation process.
     *
     * <p> Takes order list and purchase order identifier from {@code cmd}.
     * Checks each order in the order list to match the purchase order date
     * and vendor. Also checks for orders with an empty dish list and orders with
     * not {@code 'ORDER_ACTIVE'} status.
     *
     * @param cmd command to create a purchase order
     * @return {@code true} if a purchase order creation is allowed
     */
    static boolean isAllowedPurchaseOrderCreation(CreatePurchaseOrder cmd) {
        checkNotNull(cmd);
        final PurchaseOrderId purchaseOrderId = cmd.getId();
        final List<Order> ordersList = cmd.getOrderList();

        final boolean orderListNotEmpty = !ordersList.isEmpty();
        final boolean ordersFitToPO = ordersList.stream()
                                                .allMatch(
                                                        o -> doesOrderFitToPO(purchaseOrderId, o));
        return orderListNotEmpty && ordersFitToPO;
    }

    /**
     * Finds orders which contain more than {@code MAX_SINGLE_DISH_COUNT}
     * equal dishes.
     *
     * <p> Those orders are considered invalid and returned as result.
     *
     * @param orders the list to check
     * @return list of invalid orders, empty if all orders are valid
     */
    static List<Order> findInvalidOrders(List<Order> orders) {
        checkNotNull(orders);
        final List<Order> invalidOrders = orders.stream()
                                                .filter(o -> !isOrderValid(o))
                                                .collect(Collectors.toList());
        return invalidOrders;
    }

    /**
     * Checks order list for those which contain more than {@code MAX_SINGLE_DISH_COUNT}
     * equal dishes.
     *
     * @param orders the list to check
     * @return {@code true} if any invalid order was found.
     */
    static boolean hasInvalidOrders(List<Order> orders) {
        checkNotNull(orders);
        final boolean result = orders.stream()
                                     .anyMatch(o -> !isOrderValid(o));
        return result;
    }

    /**
     * Creates {@code PurchaseOrderCreated} instance.
     *
     * @param cmd the command which fired an event
     * @return {@code PurchaseOrderCreated} event instance
     */
    static PurchaseOrderCreated createPOCreatedEvent(CreatePurchaseOrder cmd) {
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

    /**
     * Creates {@code PurchaseOrderValidationFailed} instance.
     *
     * @param cmd           the command which fired an event
     * @param invalidOrders list of orders which validation has failed
     * @return {@code PurchaseOrderValidationFailed} event instance
     */
    static PurchaseOrderValidationFailed createPOValidationFailedEvent(CreatePurchaseOrder cmd,
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

    /**
     * Creates {@code PurchaseOrderValidationPassed} instance.
     *
     * @param cmd the command which fired an event
     * @return {@code PurchaseOrderValidationPassed} event instance
     */
    static PurchaseOrderValidationPassed createPOValidationPassedEvent(CreatePurchaseOrder cmd) {
        final PurchaseOrderId id = cmd.getId();
        final PurchaseOrderValidationPassed result = PurchaseOrderValidationPassed
                .newBuilder()
                .setId(id)
                .setWhenPassed(getCurrentTime())
                .build();

        return result;
    }

    /**
     * Creates {@code PurchaseOrderValidationOverruled} instance.
     *
     * @param cmd the command which fired an event
     * @return {@code PurchaseOrderValidationOverruled} event instance
     */
    static PurchaseOrderValidationOverruled createPOValidationOverruledEvent(
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

    /**
     * Creates {@code PurchaseOrderDelivered} instance.
     *
     * @param cmd the command which fired an event
     * @return {@code PurchaseOrderDelivered} event instance
     */
    static PurchaseOrderDelivered createPOMarkedAsDeliveredEvent(MarkPurchaseOrderAsDelivered cmd) {
        final PurchaseOrderId id = cmd.getId();
        final UserId whoMarksAsDelivered = cmd.getWhoMarksAsDelivered();
        final PurchaseOrderDelivered result = PurchaseOrderDelivered
                .newBuilder()
                .setId(id)
                .setWhoMarkedAsDelivered(whoMarksAsDelivered)
                .setWhenDelivered(getCurrentTime())
                .build();

        return result;
    }

    /**
     * Creates {@code PurchaseOrderCanceled} instance.
     *
     * @param cmd    the command which fired an event
     * @param orders list of orders which were canceled
     * @return {@code PurchaseOrderCanceled} event instance
     */
    @SuppressWarnings("all") /* To not use `default` switch branch*/
    static PurchaseOrderCanceled createPOCanceledEvent(CancelPurchaseOrder cmd,
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

    /**
     * Creates {@code PurchaseOrderSent} instance.
     *
     * @param purchaseOrder the purchase order to send
     * @param senderEmail   the sender email address
     * @param vendorEmail   the vendor email address
     * @return {@code PurchaseOrderSent} event instance
     */
    static PurchaseOrderSent createPOSentEvent(PurchaseOrder purchaseOrder,
                                               EmailAddress senderEmail,
                                               EmailAddress vendorEmail) {
        final PurchaseOrderSent result = PurchaseOrderSent.newBuilder()
                                                          .setPurchaseOrder(purchaseOrder)
                                                          .setSenderEmail(senderEmail)
                                                          .setVendorEmail(vendorEmail)
                                                          .build();
        return result;
    }

    /**
     * Creates {@code PurchaseOrder} instance.
     *
     * @param cmd the command to get {@code PurchaseOrder} state from
     * @return {@code PurchaseOrder} the created instance
     */
    static PurchaseOrder createPurchaseOrderInstance(CreatePurchaseOrder cmd) {
        final PurchaseOrderId id = cmd.getId();
        final List<Order> orderList = cmd.getOrderList();
        final PurchaseOrder result = PurchaseOrder.newBuilder()
                                                  .setId(id)
                                                  .setStatus(VALID)
                                                  .addAllOrder(orderList)
                                                  .build();
        return result;
    }

    /**
     * Check {@code status} value for availability to transit to
     * {@code VALID} state.
     *
     * @param status value to check
     * @return {@code true} if transition is allowed
     */
    static boolean isAllowedToMarkAsValid(PurchaseOrderStatus status) {
        return status == INVALID;
    }

    /**
     * Check {@code status} value for availability to transit to
     * {@code DELIVERED} state.
     *
     * @param status value to check
     * @return {@code true} if transition is allowed
     */
    static boolean isAllowedToMarkAsDelivered(PurchaseOrderStatus status) {
        return status == SENT;
    }

    /**
     * Check {@code status} value for availability to transit to
     * {@code CANCEL} state.
     *
     * @param status value to check
     * @return {@code true} if transition is allowed
     */
    static boolean isAllowedToCancel(PurchaseOrderStatus status) {
        return status != DELIVERED;
    }

    @SuppressWarnings("OverlyComplexBooleanExpression")
    private static boolean doesOrderFitToPO(PurchaseOrderId purchaseOrderId, Order order) {
        final VendorId purchaseOrderVendorId = purchaseOrderId.getVendorId();
        final LocalDate purchaseOrderDate = purchaseOrderId.getPoDate();

        return checkOrderIsActive(order) && checkOrderingDatesMatch(order, purchaseOrderDate) &&
                checkOrderNotEmpty(order) && checkVendorsMatch(order, purchaseOrderVendorId);
    }

    private static boolean isOrderValid(Order order) {
        final boolean result = order.getDishList()
                                    .stream()
                                    .collect(Collectors.groupingBy(d -> d,
                                                                   Collectors.counting()))
                                    .entrySet()
                                    .stream()
                                    .noneMatch(p -> p.getValue() > MAX_SINGLE_DISH_COUNT);
        return result;
    }

    private static boolean checkOrderingDatesMatch(Order order, LocalDate poDate) {
        final LocalDate orderDate = order.getId()
                                         .getOrderDate();
        return orderDate.equals(poDate);
    }

    private static boolean checkOrderNotEmpty(Order order) {
        final int dishCount = order.getDishCount();
        return dishCount != 0;
    }

    private static boolean checkVendorsMatch(Order order, VendorId vendorId) {
        final VendorId orderVendorId = order.getId()
                                            .getVendorId();
        return orderVendorId.equals(vendorId);
    }

    private static boolean checkOrderIsActive(Order order) {
        return order.getStatus() == ORDER_ACTIVE;
    }
}
