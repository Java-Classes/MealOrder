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

package javaclasses.mealorder.testdata;

import io.spine.time.LocalDate;
import javaclasses.mealorder.Dish;
import javaclasses.mealorder.Order;
import javaclasses.mealorder.OrderId;
import javaclasses.mealorder.OrderStatus;
import javaclasses.mealorder.PurchaseOrderId;
import javaclasses.mealorder.VendorId;
import javaclasses.mealorder.c.command.CancelPurchaseOrder;
import javaclasses.mealorder.c.command.CreatePurchaseOrder;
import javaclasses.mealorder.c.command.MarkPurchaseOrderAsDelivered;
import javaclasses.mealorder.c.command.MarkPurchaseOrderAsValid;
import javaclasses.mealorder.c.event.PurchaseOrderValidationFailed;

import java.util.ArrayList;
import java.util.List;

import static javaclasses.mealorder.OrderStatus.ORDER_CANCELED;
import static javaclasses.mealorder.testdata.TestValues.EMAIL;
import static javaclasses.mealorder.testdata.TestValues.ORDER;
import static javaclasses.mealorder.testdata.TestValues.PURCHASE_ORDER_ID;
import static javaclasses.mealorder.testdata.TestValues.USER_ID;
import static javaclasses.mealorder.testdata.TestValues.USER_ID2;

/**
 * A factory of the purchase order commands for the test needs.
 *
 * @author Yegor Udovchenko
 */
public class TestPurchaseOrderCommandFactory {

    private TestPurchaseOrderCommandFactory() {
    }

    /**
     * Provides a default {@link CreatePurchaseOrder} instance.
     *
     * @return the {@code CreatePurchaseOrder} instance
     */
    public static CreatePurchaseOrder createPurchaseOrderInstance() {
        final CreatePurchaseOrder result = CreatePurchaseOrder.newBuilder()
                                                              .setId(PURCHASE_ORDER_ID)
                                                              .setWhoCreates(USER_ID)
                                                              .addOrder(ORDER)
                                                              .setVendorEmail(EMAIL)
                                                              .build();
        return result;
    }

    public static CreatePurchaseOrder createPurchaseOrderInstance(List<Order> order) {
        final CreatePurchaseOrder result = CreatePurchaseOrder.newBuilder()
                                                              .setId(PURCHASE_ORDER_ID)
                                                              .setWhoCreates(USER_ID)
                                                              .addAllOrder(order)
                                                              .setVendorEmail(EMAIL)
                                                              .build();
        return result;
    }
    /**
     * Provides a pre-configured {@link CreatePurchaseOrder} instance
     * with mismatch vendor identifier of purchase order and one of
     * orders in the list.
     *
     * @return the invalid {@code CreatePurchaseOrder} instance.
     */
    public static CreatePurchaseOrder createPurchaseOrderWithVendorMismatchInstance() {
        final CreatePurchaseOrder validCmd = createPurchaseOrderInstance();

        final VendorId invalidVendorId = VendorId.newBuilder()
                                                 .setValue("vendor:other")
                                                 .build();
        final PurchaseOrderId invalidId = PurchaseOrderId.newBuilder(PURCHASE_ORDER_ID)
                                                         .setVendorId(invalidVendorId)
                                                         .build();
        final CreatePurchaseOrder result = CreatePurchaseOrder.newBuilder(validCmd)
                                                              .setId(invalidId)
                                                              .build();
        return result;
    }

    /**
     * Provides a pre-configured {@link CreatePurchaseOrder} instance
     * with an order in the list which status is not {@code 'ORDER_ACTIVE'}
     *
     * @return the invalid {@code CreatePurchaseOrder} instance.
     */
    public static CreatePurchaseOrder createPurchaseOrderWithNotActiveOrdersInstance() {
        final CreatePurchaseOrder validCmd = createPurchaseOrderInstance();
        final Order order = validCmd.getOrder(0);
        final Order invalidOrder = Order.newBuilder(order)
                                        .setStatus(ORDER_CANCELED)
                                        .build();
        final CreatePurchaseOrder result = CreatePurchaseOrder.newBuilder(validCmd)
                                                              .addOrder(invalidOrder)
                                                              .build();
        return result;
    }

    /**
     * Provides a pre-configured {@link CreatePurchaseOrder} instance
     * with an order in the list which dish list is empty.
     *
     * @return the invalid {@code CreatePurchaseOrder} instance.
     */
    public static CreatePurchaseOrder createPurchaseOrderWithEmptyOrdersInstance() {
        final CreatePurchaseOrder validCmd = createPurchaseOrderInstance();
        final Order order = validCmd.getOrder(0);
        final OrderStatus status = order.getStatus();
        final OrderId id = order.getId();
        final Order invalidOrder = Order.newBuilder()
                                        .setStatus(status)
                                        .setId(id)
                                        .build();
        final CreatePurchaseOrder result = CreatePurchaseOrder.newBuilder(validCmd)
                                                              .addOrder(invalidOrder)
                                                              .build();
        return result;
    }

    /**
     * Provides a pre-configured {@link CreatePurchaseOrder} instance
     * with an order in the list which date is not consistent with PO date.
     *
     * @return the invalid {@code CreatePurchaseOrder} instance.
     */
    public static CreatePurchaseOrder createPurchaseOrderWithDatesMismatchOrdersInstance() {
        final CreatePurchaseOrder validCmd = createPurchaseOrderInstance();
        final Order order = validCmd.getOrder(0);
        final OrderId id = order.getId();
        final OrderId.Builder invalidOrderId = OrderId.newBuilder(id)
                                                      .setOrderDate(LocalDate.getDefaultInstance());
        final Order invalidOrder = Order.newBuilder(order)
                                        .setId(invalidOrderId)
                                        .build();
        final CreatePurchaseOrder result = CreatePurchaseOrder.newBuilder(validCmd)
                                                              .addOrder(invalidOrder)
                                                              .build();
        return result;
    }

    /**
     * Provides a pre-configured {@link CreatePurchaseOrder} instance
     * with an order in the list which date is not consistent with PO date.
     *
     * @return the invalid {@code CreatePurchaseOrder} instance.
     */
    public static CreatePurchaseOrder createPurchaseOrderWithEmptyListInstance() {
        final CreatePurchaseOrder validCmd = createPurchaseOrderInstance();
        final PurchaseOrderId id = validCmd.getId();
        final CreatePurchaseOrder result = CreatePurchaseOrder.newBuilder()
                                                              .setId(id)
                                                              .setVendorEmail(EMAIL)
                                                              .setWhoCreates(USER_ID2)
                                                              .build();
        return result;
    }

    /**
     * Provides a pre-configured {@link CreatePurchaseOrder} instance
     * with orders which should fail order validation and cause
     * {@link PurchaseOrderValidationFailed} event.
     *
     * @return the {@code CreatePurchaseOrder} instance with invalid orders.
     */
    public static CreatePurchaseOrder createPurchaseOrderWithInvalidOrdersInstance() {
        final CreatePurchaseOrder validCmd = createPurchaseOrderInstance();
        final Order order = validCmd.getOrder(0);
        final Dish dish = order.getDish(0);

        List<Dish> dishes = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            dishes.add(dish);
        }
        final Order invalidOrder = Order.newBuilder(order)
                                        .addAllDish(dishes)
                                        .build();
        final CreatePurchaseOrder result = CreatePurchaseOrder.newBuilder(validCmd)
                                                              .addOrder(invalidOrder)
                                                              .build();
        return result;
    }

    /**
     * Provides a default {@link MarkPurchaseOrderAsValid} instance
     *
     * @return the {@code MarkPurchaseOrderAsValid} instance.
     */
    public static MarkPurchaseOrderAsValid markPurchaseOrderAsValidInstance() {
        final MarkPurchaseOrderAsValid result = MarkPurchaseOrderAsValid.newBuilder()
                                                                        .setId(PURCHASE_ORDER_ID)
                                                                        .setReason("Because I can.")
                                                                        .setUserId(USER_ID)
                                                                        .setVendorEmail(EMAIL)
                                                                        .build();
        return result;
    }

    /**
     * Provides a pre-configured {@link CancelPurchaseOrder} instance
     * with 'CUSTOM_REASON' set.
     *
     * @return the {@code CancelPurchaseOrder} instance.
     */
    public static CancelPurchaseOrder cancelPOWithCustomReasonInstance() {
        final CancelPurchaseOrder result = CancelPurchaseOrder.newBuilder()
                                                              .setId(PURCHASE_ORDER_ID)
                                                              .setCustomReason("Because why not.")
                                                              .setUserId(USER_ID)
                                                              .build();
        return result;
    }

    /**
     * Provides a pre-configured {@link CancelPurchaseOrder} instance
     * with non set reason.
     *
     * @return the {@code CancelPurchaseOrder} instance.
     */
    public static CancelPurchaseOrder cancelPOWithEmptyReasonInstance() {
        final CancelPurchaseOrder result = CancelPurchaseOrder.newBuilder()
                                                              .setId(PURCHASE_ORDER_ID)
                                                              .setUserId(USER_ID)
                                                              .build();
        return result;
    }

    /**
     * Provides a pre-configured {@link CancelPurchaseOrder} instance
     * with 'INVALID' reason set.
     *
     * @return the {@code CancelPurchaseOrder} instance.
     */
    public static CancelPurchaseOrder cancelPOWithInvalidReasonInstance() {
        final CancelPurchaseOrder result = CancelPurchaseOrder.newBuilder()
                                                              .setId(PURCHASE_ORDER_ID)
                                                              .setInvalid(true)
                                                              .setUserId(USER_ID)
                                                              .build();
        return result;
    }

    /**
     * Provides a default {@link MarkPurchaseOrderAsDelivered} instance
     *
     * @return the {@code MarkPurchaseOrderAsDelivered} instance.
     */
    public static MarkPurchaseOrderAsDelivered markPurchaseOrderAsDeliveredInstance() {
        final MarkPurchaseOrderAsDelivered result = MarkPurchaseOrderAsDelivered
                .newBuilder()
                .setId(PURCHASE_ORDER_ID)
                .setWhoMarksAsDelivered(USER_ID)
                .build();
        return result;
    }
}
