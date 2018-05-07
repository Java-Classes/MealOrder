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

import javaclasses.mealorder.LocalMonth;
import javaclasses.mealorder.MonthlySpendingsReportId;
import javaclasses.mealorder.c.event.PurchaseOrderDelivered;
import javaclasses.mealorder.c.event.PurchaseOrderSent;
import javaclasses.mealorder.q.MonthlySpendingsReportViewProjection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.spine.server.projection.ProjectionEventDispatcher.dispatch;
import static javaclasses.mealorder.testdata.TestMealOrderEventFactory.PurchaseOrderEvents.purchaseOrderDeliveredInstance;
import static javaclasses.mealorder.testdata.TestMealOrderEventFactory.PurchaseOrderEvents.purchaseOrderDeliveredInstance2;
import static javaclasses.mealorder.testdata.TestMealOrderEventFactory.PurchaseOrderEvents.purchaseOrderSentInstance;
import static javaclasses.mealorder.testdata.TestMealOrderEventFactory.PurchaseOrderEvents.purchaseOrderSentInstance2;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MonthlySpendingsReportViewProjectionTest extends ProjectionTest {

    private MonthlySpendingsReportViewProjection projection;

    @BeforeEach
    void setUp() {
        final LocalMonth localMonth = LocalMonth.newBuilder()
                                                .setMonthValue(2)
                                                .setYear(2019)
                                                .build();

        projection = new MonthlySpendingsReportViewProjection(MonthlySpendingsReportId.newBuilder()
                                                                                      .setMonth(
                                                                                              localMonth)
                                                                                      .build());
    }

    @Nested
    @DisplayName("PurchaseOrderSent event should be interpreted by MonthlySpendingsReportViewProjection")
    class PurchaseOrderSentEvent {
        @Test
        @DisplayName("Should add orders for a projection")
        void addView() {
            final PurchaseOrderSent purchaseOrderSent = purchaseOrderSentInstance();
            dispatch(projection, createEvent(purchaseOrderSent));
            assertEquals(1  , projection.getState().getOrderCount());
            assertEquals(1, projection.getState()
                                      .getOrderList()
                                      .get(0)
                                      .getDishList()
                                      .size());
            assertEquals(0, projection.getState()
                                      .getUserSpendingList()
                                      .size());
        }

        @Test
        @DisplayName("Should add compicated orders for a projection")
        void addView2() {
            final PurchaseOrderSent purchaseOrderSent = purchaseOrderSentInstance();
            dispatch(projection, createEvent(purchaseOrderSent));
            final PurchaseOrderSent purchaseOrderSent2 = purchaseOrderSentInstance2();
            dispatch(projection, createEvent(purchaseOrderSent2));
            assertEquals(5, projection.getState()
                                      .getOrderCount());
            assertEquals(1, projection.getState()
                                      .getOrderList()
                                      .get(0)
                                      .getDishList()
                                      .size());
            assertEquals(0, projection.getState()
                                      .getUserSpendingList()
                                      .size());
            assertEquals(2, projection.getState()
                                      .getOrderList()
                                      .get(3)
                                      .getDishList()
                                      .size());
        }
    }

    @Nested
    @DisplayName("PurchaseOrderDelivered event should be interpreted by MonthlySpendingsReportViewProjection")
    class PurchaseOrderDeliveredEvent {
        @Test
        @DisplayName("Should add user's spendings for a projection")
        void addView() {
            final PurchaseOrderSent purchaseOrderSent = purchaseOrderSentInstance();
            dispatch(projection, createEvent(purchaseOrderSent));
            final PurchaseOrderSent purchaseOrderSent2 = purchaseOrderSentInstance2();
            dispatch(projection, createEvent(purchaseOrderSent2));
            final PurchaseOrderDelivered purchaseOrderDelivered = purchaseOrderDeliveredInstance();
            final PurchaseOrderDelivered purchaseOrderDelivered2 = purchaseOrderDeliveredInstance2();

            dispatch(projection, createEvent(purchaseOrderDelivered));
            assertEquals("user@example.com", projection.getState()
                                                       .getUserSpendingList()
                                                       .get(0)
                                                       .getId()
                                                       .getEmail()
                                                       .getValue());
            assertEquals(56, projection.getState()
                                       .getUserSpendingList()
                                       .get(0)
                                       .getAmount()
                                       .getAmount());
            assertEquals(1, projection.getState()
                                      .getUserSpendingList()
                                      .size());

            dispatch(projection, createEvent(purchaseOrderDelivered2));
            assertEquals(336, projection.getState()
                                        .getUserSpendingList()
                                        .get(0)
                                        .getAmount()
                                        .getAmount());
            assertEquals(2, projection.getState()
                                      .getUserSpendingList()
                                      .size());
            assertEquals(448, projection.getState()
                                        .getUserSpendingList()
                                        .get(1)
                                        .getAmount()
                                        .getAmount());
        }
    }

}
