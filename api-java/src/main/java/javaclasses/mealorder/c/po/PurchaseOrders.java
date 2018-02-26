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

import io.spine.time.LocalDate;
import javaclasses.mealorder.Order;
import javaclasses.mealorder.PurchaseOrder;
import javaclasses.mealorder.PurchaseOrderId;
import javaclasses.mealorder.VendorId;
import javaclasses.mealorder.c.command.CreatePurchaseOrder;

import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static javaclasses.mealorder.OrderStatus.ORDER_ACTIVE;

/**
 * The utility class to manage the process of creation of a {@link PurchaseOrder} instance.
 *
 * @author Yegor Udovchenko
 */
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
     * Searches for orders which contain more than {@code MAX_SINGLE_DISH_COUNT}
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

    private static boolean doesOrderFitToPO(PurchaseOrderId purchaseOrderId, Order order) {
        final VendorId purchaseOrderVendorId = purchaseOrderId.getVendorId();
        final LocalDate purchaseOrderDate = purchaseOrderId.getPoDate();

        if (!(checkOrderIsActive(order) && checkOrderingDatesMatch(order, purchaseOrderDate))) {
            return false;
        }
        return checkOrderNotEmpty(order) && checkVendorsMatch(order, purchaseOrderVendorId);
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
