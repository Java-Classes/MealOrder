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
import javaclasses.mealorder.Order;
import javaclasses.mealorder.PurchaseOrder;
import javaclasses.mealorder.PurchaseOrderId;
import javaclasses.mealorder.PurchaseOrderStatus;
import javaclasses.mealorder.UserId;
import javaclasses.mealorder.c.event.PurchaseOrderDelivered;
import javaclasses.mealorder.c.event.PurchaseOrderSent;

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
    }
}
