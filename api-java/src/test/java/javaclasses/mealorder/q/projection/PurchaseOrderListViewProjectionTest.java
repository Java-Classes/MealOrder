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

import javaclasses.mealorder.c.event.PurchaseOrderDelivered;
import javaclasses.mealorder.c.event.PurchaseOrderSent;
import javaclasses.mealorder.q.PurchaseOrderItem;
import javaclasses.mealorder.q.PurchaseOrderListViewProjection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.spine.server.projection.ProjectionEventDispatcher.dispatch;
import static javaclasses.mealorder.testdata.TestMealOrderEventFactory.PurchaseOrderEvents.purchaseOrderDeliveredInstance;
import static javaclasses.mealorder.testdata.TestMealOrderEventFactory.PurchaseOrderEvents.purchaseOrderSentInstance;
import static org.junit.jupiter.api.Assertions.assertEquals;
public class PurchaseOrderListViewProjectionTest extends ProjectionTest {
    private PurchaseOrderListViewProjection projection;

    @BeforeEach
    void setUp() {
        projection = new PurchaseOrderListViewProjection(PurchaseOrderListViewProjection.ID);
    }

    @Nested
    @DisplayName("PurchaseOrderSent event should be interpreted by PurchaseOrderListViewProjection")
    class PurchaseOrderSentEvent {

        @Test
        @DisplayName("Should set order for a projection")
        void addView() {
            final PurchaseOrderSent purchaseOrderSent = purchaseOrderSentInstance();
            dispatch(projection, createEvent(purchaseOrderSent));

            final List<PurchaseOrderItem> purchaseOrderList = projection.getState()
                                                                        .getPurchaseOrderList();
            assertEquals(1, purchaseOrderList.size());
            assertEquals(15, purchaseOrderList.get(0)
                                              .getId()
                                              .getPoDate()
                                              .getDay());
        }
    }

    @Nested
    @DisplayName("PurchaseOrderDelivered event should be interpreted by PurchaseOrderListViewProjection")
    class PurchaseOrderDeliveredEvent {

        @Test
        @DisplayName("Should change status of the PO")
        void addView() {
            final PurchaseOrderSent purchaseOrderSent = purchaseOrderSentInstance();
            dispatch(projection, createEvent(purchaseOrderSent));
            final PurchaseOrderDelivered purchaseOrderDelivered = purchaseOrderDeliveredInstance();
            dispatch(projection, createEvent(purchaseOrderDelivered));
            final List<PurchaseOrderItem> purchaseOrderList = projection.getState()
                                                                        .getPurchaseOrderList();
            assertEquals(1, purchaseOrderList.size());
            assertEquals("DELIVERED", purchaseOrderList.get(0)
                                                       .getPurchaseOrderStatus()
                                                       .name());
        }
    }
}
