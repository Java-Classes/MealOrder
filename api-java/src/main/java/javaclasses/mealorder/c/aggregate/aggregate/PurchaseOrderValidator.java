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

import io.spine.time.LocalDate;
import javaclasses.mealorder.Order;
import javaclasses.mealorder.PurchaseOrder;
import javaclasses.mealorder.PurchaseOrderId;
import javaclasses.mealorder.VendorId;
import javaclasses.mealorder.c.command.CreatePurchaseOrder;

import java.util.List;

import static javaclasses.mealorder.OrderStatus.ORDER_ACTIVE;

/**
 * Validates command {@link CreatePurchaseOrder} to
 * create {@link PurchaseOrder} instance.
 *
 * @author Yegor Udovchenko
 */
public class PurchaseOrderValidator {

    private PurchaseOrderValidator() {
    }

    /**
     * Performs the validation of purchase order creation process.
     * Checks each order in the list to match purchase order date
     * and vendor. Also checks for empty dish list orders and orders with
     * not {@code 'ORDER_ACTIVE'} status.
     *
     * @param cmd command to create purchase order.
     * @return is purchase order creation possible.
     */
    static boolean isValidPurchaseOrderCreation(CreatePurchaseOrder cmd) {
        final PurchaseOrderId purchaseOrderId = cmd.getId();
        final VendorId purchaseOrderVendorId = purchaseOrderId.getVendorId();
        final LocalDate poDate = purchaseOrderId.getPoDate();
        final List<Order> ordersList = cmd.getOrdersList();

        for (final Order order : ordersList) {
            if (!(checkOrderIsActive(order) && checkOrderingDatesMatch(order, poDate))) {
                return false;
            }
            if (!(checkOrderNotEmpty(order) && checkVendorsMatch(order, purchaseOrderVendorId))) {
                return false;
            }
        }
        return true;
    }

    private static boolean checkOrderingDatesMatch(Order order, LocalDate poDate) {
        return order.hasId() && order.getId()
                                     .getOrderDate()
                                     .equals(poDate);
    }

    private static boolean checkOrderNotEmpty(Order order) {
        return order.getDishesCount() != 0;

    }

    private static boolean checkVendorsMatch(Order order, VendorId vendorId) {
        return order.hasId() && order.getId()
                                     .hasVendorId() && order.getId()
                                                            .getVendorId()
                                                            .equals(vendorId);
    }

    private static boolean checkOrderIsActive(Order order) {
        return order.getStatus() == ORDER_ACTIVE;
    }
}