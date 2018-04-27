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

package javaclasses.mealorder.q.projection;

import javaclasses.mealorder.PurchaseOrderId;
import javaclasses.mealorder.PurchaseOrderStatus;
import javaclasses.mealorder.c.event.PurchaseOrderDelivered;
import javaclasses.mealorder.c.event.PurchaseOrderSent;
import javaclasses.mealorder.c.event.PurchaseOrderValidationFailed;
import javaclasses.mealorder.q.PurchaseOrderItemViewProjection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.spine.server.projection.ProjectionEventDispatcher.dispatch;
import static javaclasses.mealorder.testdata.TestMealOrderEventFactory.PurchaseOrderEvents.purchaseOrderDeliveredInstance;
import static javaclasses.mealorder.testdata.TestMealOrderEventFactory.PurchaseOrderEvents.purchaseOrderSentInstance;
import static javaclasses.mealorder.testdata.TestMealOrderEventFactory.PurchaseOrderEvents.purchaseOrderValidationFailedInstance;
import static javaclasses.mealorder.testdata.TestValues.DATE;
import static javaclasses.mealorder.testdata.TestValues.VENDOR_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PurchaseOrderItemViewProjectionTest extends ProjectionTest {
    private PurchaseOrderItemViewProjection projection;

    @BeforeEach
    void setUp() {
        projection = new PurchaseOrderItemViewProjection(PurchaseOrderId.newBuilder()
                                                                        .setVendorId(
                                                                                VENDOR_ID)
                                                                        .setPoDate(DATE)
                                                                        .build());
    }

    @Nested
    @DisplayName("PurchaseOrderSent event should be interpreted by PurchaseOrderItemViewProjection")
    class PurchaseOrderSentEvent {

        @Test
        @DisplayName("Should set order for a projection")
        void addView() {
            final PurchaseOrderSent purchaseOrderSent = purchaseOrderSentInstance();
            dispatch(projection, createEvent(purchaseOrderSent));

            assertEquals("vendor:value: \"VendorName1\"", projection.getState()
                                                                    .getId()
                                                                    .getVendorId()
                                                                    .getValue()
                                                                    .trim());
            assertEquals(15, projection.getState()
                                       .getId()
                                       .getPoDate()
                                       .getDay());
            assertEquals(2019, projection.getState()
                                         .getId()
                                         .getPoDate()
                                         .getYear());
            assertEquals(2, projection.getState()
                                      .getId()
                                      .getPoDate()
                                      .getMonthValue());
            assertEquals(PurchaseOrderStatus.SENT, projection.getState()
                                                             .getPurchaseOrderStatus());
        }
    }

    @Nested
    @DisplayName("PurchaseOrderDelivered event should be interpreted by PurchaseOrderItemViewProjection")
    class PurchaseOrderDeliveredEvent {

        @Test
        @DisplayName("Should change status of the PO")
        void addView() {
            final PurchaseOrderSent purchaseOrderSent = purchaseOrderSentInstance();
            dispatch(projection, createEvent(purchaseOrderSent));
            final PurchaseOrderDelivered purchaseOrderDelivered = purchaseOrderDeliveredInstance();
            dispatch(projection, createEvent(purchaseOrderDelivered));
            assertEquals(PurchaseOrderStatus.DELIVERED, projection.getState()
                                                                  .getPurchaseOrderStatus());
        }
    }

    @Nested
    @DisplayName("PurchaseOrderValidationFailed event should be interpreted by PurchaseOrderItemViewProjection")
    class PurchaseOrderValidationFailedEvent {

        @Test
        @DisplayName("Should change status of the PO")
        void addView() {
            final PurchaseOrderSent purchaseOrderSent = purchaseOrderSentInstance();
            dispatch(projection, createEvent(purchaseOrderSent));
            final PurchaseOrderValidationFailed poValidationFailed = purchaseOrderValidationFailedInstance();
            dispatch(projection, createEvent(poValidationFailed));
            assertEquals(PurchaseOrderStatus.INVALID, projection.getState()
                                                                .getPurchaseOrderStatus());
        }
    }
}
