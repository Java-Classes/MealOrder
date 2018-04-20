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
import javaclasses.mealorder.MenuDateRange;
import javaclasses.mealorder.MenuId;
import javaclasses.mealorder.Order;
import javaclasses.mealorder.PurchaseOrder;
import javaclasses.mealorder.PurchaseOrderId;
import javaclasses.mealorder.PurchaseOrderStatus;
import javaclasses.mealorder.UserId;
import javaclasses.mealorder.VendorId;
import javaclasses.mealorder.c.event.DateRangeForMenuSet;
import javaclasses.mealorder.c.event.PurchaseOrderCreated;
import javaclasses.mealorder.c.event.PurchaseOrderDelivered;
import javaclasses.mealorder.c.event.PurchaseOrderSent;

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

    private TestMealOrderEventFactory() {
    }

    public static class PurchaseOrderEvents {
        private PurchaseOrderEvents() {
        }

        public static PurchaseOrderSent purchaseOrderSentInstance() {
            return purchaseOrderSentInstance(PURCHASE_ORDER, TestValues.USER_ID.getEmail(),
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

        private static PurchaseOrderDelivered purchaseOrderDeliveredInstance(
                PurchaseOrderId purchaseOrderId,
                UserId userId) {
            final Timestamp currentTime = getCurrentTime();
            final PurchaseOrderDelivered result =
                    PurchaseOrderDelivered.newBuilder()
                                          .setId(purchaseOrderId)
                                          .setWhoMarkedAsDelivered(userId)
                                          .setWhenDelievered(currentTime)
                                          .build();
            return result;
        }

        public static PurchaseOrderCreated purchaseOrderCreatedInstance() {
            return purchaseOrderCreatedInstance(TestValues.PURCHASE_ORDER_ID, TestValues.USER_ID,
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

        public static DateRangeForMenuSet dateRangeForMenuSetInstance() {
            return dateRangeForMenuSetInstance(TestValues.VENDOR_ID, TestValues.MENU_ID,
                                               TestValues.USER_ID, TestValues.MENU_DATE_RANGE2);
        }

        private static DateRangeForMenuSet dateRangeForMenuSetInstance(VendorId vendorId,
                                                                       MenuId menuId,
                                                                       UserId userId,
                                                                       MenuDateRange menuDateRange) {
            final Timestamp currentTime = getCurrentTime();
            final DateRangeForMenuSet result =
                    DateRangeForMenuSet.newBuilder()
                                       .setVendorId(vendorId)
                                       .setMenuId(menuId)
                                       .setWhoSet(userId)
                                       .setWhenSet(currentTime)
                                       .setMenuDateRange(menuDateRange)
                                       .build();
            return result;
        }
    }
}
