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
 * Validates command {@link CreatePurchaseOrder} to
 * creation {@link PurchaseOrder} instance.
 *
 * @author Yegor Udovchenko
 */
public class PurchaseOrders {
    private static final int MAX_SINGLE_DISH_COUNT = 20;

    /** Prevents instantiation of this utility class. */
    private PurchaseOrders() {
    }

    /**
     * Performs the validation of a purchase order creation process.
     *
     * <p>Checks each order in the list to match purchase order date
     * and vendor. Also checks for empty dish list orders and orders with
     * not {@code 'ORDER_ACTIVE'} status.
     *
     * @param cmd command to create a purchase order.
     * @return is allowed a purchase order creation.
     */
    public static boolean isAllowedPurchaseOrderCreation(CreatePurchaseOrder cmd) {
        checkNotNull(cmd);
        final PurchaseOrderId purchaseOrderId = cmd.getId();
        List<Order> ordersList = cmd.getOrderList();

        final boolean result = ordersList.stream()
                                         .allMatch(o -> doesOrderBelongToPO(purchaseOrderId, o));
        return !ordersList.isEmpty() && result;
    }

    private static boolean doesOrderBelongToPO(PurchaseOrderId purchaseOrderId, Order order) {
        final VendorId purchaseOrderVendorId = purchaseOrderId.getVendorId();
        final LocalDate purchaseOrderDate = purchaseOrderId.getPoDate();

        if (!(checkOrderIsActive(order) && checkOrderingDatesMatch(order, purchaseOrderDate))) {
            return false;
        }
        return checkOrderNotEmpty(order) && checkVendorsMatch(order, purchaseOrderVendorId);
    }

    /**
     * Finds orders which contain more than {@code MAX_SINGLE_DISH_COUNT}
     * equal dishes.
     *
     * <p>Those orders are considered invalid.
     *
     * @param orders list to check.
     * @return list of invalid orders.(Empty if all orders are valid)
     */
    public static List<Order> findInvalidOrders(List<Order> orders) {
        checkNotNull(orders);
        final List<Order> invalidOrders = orders.stream()
                                                .filter(o -> !isOrderValid(o))
                                                .collect(Collectors.toList());
        return invalidOrders;
    }

    public static boolean hasInvalidOrders(List<Order> orders) {
        checkNotNull(orders);
        final boolean result = orders.stream()
                                     .anyMatch(o -> !isOrderValid(o));
        return result;
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
        return order.getId()
                    .getOrderDate()
                    .equals(poDate);
    }

    private static boolean checkOrderNotEmpty(Order order) {
        return order.getDishCount() != 0;
    }

    private static boolean checkVendorsMatch(Order order, VendorId vendorId) {
        return order.getId()
                    .getVendorId()
                    .equals(vendorId);
    }

    private static boolean checkOrderIsActive(Order order) {
        return order.getStatus() == ORDER_ACTIVE;
    }
}
