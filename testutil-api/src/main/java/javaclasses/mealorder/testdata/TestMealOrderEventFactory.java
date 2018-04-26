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

import com.google.protobuf.Timestamp;
import io.spine.net.EmailAddress;
import io.spine.time.LocalTime;
import javaclasses.mealorder.Dish;
import javaclasses.mealorder.MenuDateRange;
import javaclasses.mealorder.MenuId;
import javaclasses.mealorder.Order;
import javaclasses.mealorder.OrderId;
import javaclasses.mealorder.PhoneNumber;
import javaclasses.mealorder.PurchaseOrder;
import javaclasses.mealorder.PurchaseOrderId;
import javaclasses.mealorder.PurchaseOrderStatus;
import javaclasses.mealorder.UserId;
import javaclasses.mealorder.VendorChange;
import javaclasses.mealorder.VendorId;
import javaclasses.mealorder.VendorName;
import javaclasses.mealorder.c.event.DishAddedToOrder;
import javaclasses.mealorder.c.event.DishRemovedFromOrder;
import javaclasses.mealorder.c.event.MenuImported;
import javaclasses.mealorder.c.event.OrderCanceled;
import javaclasses.mealorder.c.event.OrderProcessed;
import javaclasses.mealorder.c.event.PurchaseOrderCreated;
import javaclasses.mealorder.c.event.PurchaseOrderDelivered;
import javaclasses.mealorder.c.event.PurchaseOrderSent;
import javaclasses.mealorder.c.event.PurchaseOrderValidationFailed;
import javaclasses.mealorder.c.event.VendorAdded;
import javaclasses.mealorder.c.event.VendorUpdated;

import java.util.List;

import static io.spine.time.Time.getCurrentTime;

public class TestMealOrderEventFactory {

    private static final Order ORDER = TestValues.ORDER;
    private static final PurchaseOrderId ID = TestValues.PURCHASE_ORDER_ID;
    private static final PurchaseOrder PURCHASE_ORDER = PurchaseOrder.newBuilder()
                                                                     .addOrder(ORDER)
                                                                     .setId(ID)
                                                                     .setStatus(
                                                                             PurchaseOrderStatus.CREATED)
                                                                     .build();
    private static final PurchaseOrder PURCHASE_ORDER2 = PurchaseOrder.newBuilder()
                                                                      .addAllOrder(
                                                                              TestValues.BIG_ORDER)
                                                                      .setId(TestValues.PURCHASE_ORDER_ID2)
                                                                      .setStatus(
                                                                              PurchaseOrderStatus.CREATED)
                                                                      .build();

    private TestMealOrderEventFactory() {
    }

    public static class PurchaseOrderEvents {
        private PurchaseOrderEvents() {
        }

        public static PurchaseOrderValidationFailed purchaseOrderValidationFailedInstance() {
            return purchaseOrderValidationFailedInstance(TestValues.PURCHASE_ORDER_ID,
                                                         TestValues.ORDER);
        }

        public static PurchaseOrderValidationFailed purchaseOrderValidationFailedInstance(
                PurchaseOrderId purchaseOrderId, Order order) {
            final Timestamp currentTime = getCurrentTime();
            final PurchaseOrderValidationFailed result =
                    PurchaseOrderValidationFailed.newBuilder()
                                                 .setId(purchaseOrderId)
                                                 .addFailureOrder(order)
                                                 .setWhenFailed(currentTime)
                                                 .build();
            return result;
        }

        public static PurchaseOrderSent purchaseOrderSentInstance() {
            return purchaseOrderSentInstance(PURCHASE_ORDER, TestValues.USER_ID.getEmail(),
                                             TestValues.EMAIL);
        }

        public static PurchaseOrderSent purchaseOrderSentInstance2() {
            return purchaseOrderSentInstance(PURCHASE_ORDER2, TestValues.USER_ID3.getEmail(),
                                             TestValues.EMAIL);
        }

        public static PurchaseOrderSent purchaseOrderSentInstance(PurchaseOrder po,
                                                                  EmailAddress senderEmail,
                                                                  EmailAddress vendorEmail) {
            final Timestamp currentTime = getCurrentTime();
            final PurchaseOrderSent result =
                    PurchaseOrderSent.newBuilder()
                                     .setPurchaseOrder(po)
                                     .setSenderEmail(senderEmail)
                                     .setVendorEmail(vendorEmail)
                                     .setWhenSent(currentTime)
                                     .build();
            return result;
        }

        public static PurchaseOrderDelivered purchaseOrderDeliveredInstance() {
            return purchaseOrderDeliveredInstance(TestValues.PURCHASE_ORDER_ID, TestValues.USER_ID);
        }

        public static PurchaseOrderDelivered purchaseOrderDeliveredInstance2() {
            return purchaseOrderDeliveredInstance(TestValues.PURCHASE_ORDER_ID2,
                                                  TestValues.USER_ID);
        }

        private static PurchaseOrderDelivered purchaseOrderDeliveredInstance(
                PurchaseOrderId purchaseOrderId,
                UserId userId) {
            final Timestamp currentTime = getCurrentTime();
            final PurchaseOrderDelivered result =
                    PurchaseOrderDelivered.newBuilder()
                                          .setId(purchaseOrderId)
                                          .setWhoMarkedAsDelivered(userId)
                                          .setWhenDelivered(currentTime)
                                          .build();
            return result;
        }

        public static PurchaseOrderCreated purchaseOrderCreatedInstance() {
            return purchaseOrderCreatedInstance(TestValues.PURCHASE_ORDER_ID, TestValues.USER_ID,
                                                TestValues.BIG_ORDER);
        }

        public static PurchaseOrderCreated purchaseOrderCreatedInstance2() {
            return purchaseOrderCreatedInstance(TestValues.PURCHASE_ORDER_ID2, TestValues.USER_ID,
                                                TestValues.BIG_ORDER);
        }

        public static PurchaseOrderCreated purchaseOrderCreatedInstance(
                PurchaseOrderId purchaseOrderId, UserId userId,
                List<Order> orders) {
            final Timestamp currentTime = getCurrentTime();
            final PurchaseOrderCreated result =
                    PurchaseOrderCreated.newBuilder()
                                        .setId(purchaseOrderId)
                                        .setWhenCreated(currentTime)
                                        .setWhoCreated(userId)
                                        .addAllOrder(orders)
                                        .build();
            return result;
        }
    }

    public static class VendorEvents {
        private VendorEvents() {
        }

        public static MenuImported menuImportedInstance() {
            return menuImportedInstance(TestValues.VENDOR_ID, TestValues.MENU_ID,
                                        TestValues.USER_ID, TestValues.BIG_MENU,
                                        TestValues.MENU_DATE_RANGE2);
        }

        private static MenuImported menuImportedInstance(VendorId vendorId, MenuId menuId,
                                                         UserId whoImported, List<Dish> dishes,
                                                         MenuDateRange menuDateRange) {
            final Timestamp currentTime = getCurrentTime();
            final MenuImported result =
                    MenuImported.newBuilder()
                                .setVendorId(vendorId)
                                .setMenuId(menuId)
                                .setWhoImported(whoImported)
                                .setWhenImported(currentTime)
                                .addAllDish(dishes)
                                .setMenuDateRange(menuDateRange)
                                .build();
            return result;
        }

        public static VendorAdded vendorAddedInstance() {
            return vendorAddedInstance(TestValues.VENDOR_ID, TestValues.USER_ID,
                                       TestValues.VENDOR_NAME, TestValues.EMAIL,
                                       TestValues.PHONE_NUMBERS, TestValues.PO_DAILY_DEADLINE);
        }

        private static VendorAdded vendorAddedInstance(VendorId vendorId, UserId userId,
                                                       VendorName vendorName, EmailAddress email,
                                                       List<PhoneNumber> numbers,
                                                       LocalTime deadline) {
            final Timestamp currentTime = getCurrentTime();
            final VendorAdded result =
                    VendorAdded.newBuilder()
                               .setVendorId(vendorId)
                               .setWhoAdded(userId)
                               .setWhenAdded(currentTime)
                               .setVendorName(vendorName)
                               .addAllPhoneNumber(numbers)
                               .setEmail(email)
                               .setPoDailyDeadline(deadline)
                               .build();
            return result;
        }

        public static VendorUpdated vendorUpdatedInstance() {
            return vendorUpdatedInstance(TestValues.VENDOR_ID, TestValues.USER_ID,
                                         TestValues.VENDOR_CHANGE);
        }

        private static VendorUpdated vendorUpdatedInstance(VendorId vendorId, UserId userId,
                                                           VendorChange vendorChange) {
            final Timestamp currentTime = getCurrentTime();
            final VendorUpdated result =
                    VendorUpdated.newBuilder()
                                 .setVendorId(vendorId)
                                 .setWhoUploaded(userId)
                                 .setVendorChange(vendorChange)
                                 .setWhenUpdated(currentTime)
                                 .build();
            return result;
        }

    }

    public static class OrderEvents {
        private OrderEvents() {
        }

        public static DishAddedToOrder dishAddedToOrderInstance() {
            return dishAddedToOrderInstance(TestValues.ORDER_ID, TestValues.DISH1);
        }

        public static DishAddedToOrder dishAddedToOrderInstance2() {
            return dishAddedToOrderInstance(TestValues.ORDER_ID2, TestValues.DISH2);
        }

        private static DishAddedToOrder dishAddedToOrderInstance(OrderId orderId, Dish dish) {
            final Timestamp currentTime = getCurrentTime();
            final DishAddedToOrder result =
                    DishAddedToOrder.newBuilder()
                                    .setOrderId(orderId)
                                    .setDish(dish)
                                    .setWhenAdded(currentTime)
                                    .build();
            return result;
        }

        public static OrderCanceled orderCanceledInstance() {
            return orderCanceledInstance(TestValues.ORDER_ID, TestValues.USER_ID);
        }

        private static OrderCanceled orderCanceledInstance(OrderId orderId, UserId userId) {
            final Timestamp currentTime = getCurrentTime();
            final OrderCanceled result =
                    OrderCanceled.newBuilder()
                                 .setOrderId(orderId)
                                 .setWhoCanceled(userId)
                                 .setWhenCanceled(currentTime)
                                 .build();
            return result;
        }

        public static OrderProcessed orderProcessedInstance() {
            return orderProcessedInstance(TestValues.ORDER);
        }

        private static OrderProcessed orderProcessedInstance(Order order) {
            final Timestamp currentTime = getCurrentTime();
            final OrderProcessed result =
                    OrderProcessed.newBuilder()
                                  .setOrder(order)
                                  .setWhenProcessed(currentTime)
                                  .build();
            return result;
        }

        public static DishRemovedFromOrder dishRemovedFromOrderInstance() {
            return dishRemovedFromOrderInstance(TestValues.ORDER_ID, TestValues.DISH1);
        }

        private static DishRemovedFromOrder dishRemovedFromOrderInstance(OrderId orderId,
                                                                         Dish dish) {
            final Timestamp currentTime = getCurrentTime();
            final DishRemovedFromOrder result =
                    DishRemovedFromOrder.newBuilder()
                                        .setOrderId(orderId)
                                        .setDish(dish)
                                        .setWhenRemoved(currentTime)
                                        .build();
            return result;
        }
    }
}
