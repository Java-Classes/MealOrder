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

import io.spine.time.LocalDate;
import javaclasses.mealorder.PurchaseOrderId;
import javaclasses.mealorder.PurchaseOrderStatus;
import javaclasses.mealorder.VendorId;
import javaclasses.mealorder.c.event.PurchaseOrderCreated;
import javaclasses.mealorder.c.event.PurchaseOrderDelivered;
import javaclasses.mealorder.c.event.PurchaseOrderSent;
import javaclasses.mealorder.c.event.PurchaseOrderValidationFailed;
import javaclasses.mealorder.q.DishItem;
import javaclasses.mealorder.q.PurchaseOrderDetailsByDishViewProjection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.spine.server.projection.ProjectionEventDispatcher.dispatch;
import static javaclasses.mealorder.testdata.TestMealOrderEventFactory.PurchaseOrderEvents.purchaseOrderCreatedInstance;
import static javaclasses.mealorder.testdata.TestMealOrderEventFactory.PurchaseOrderEvents.purchaseOrderDeliveredInstance;
import static javaclasses.mealorder.testdata.TestMealOrderEventFactory.PurchaseOrderEvents.purchaseOrderSentInstance;
import static javaclasses.mealorder.testdata.TestMealOrderEventFactory.PurchaseOrderEvents.purchaseOrderValidationFailedInstance;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PurchaseOrderDetailsByDishViewProjectionTest extends ProjectionTest {
    private PurchaseOrderDetailsByDishViewProjection projection;

    @BeforeEach
    void setUp() {
        projection = new PurchaseOrderDetailsByDishViewProjection(PurchaseOrderId.newBuilder()
                                                                                 .setPoDate(
                                                                                         LocalDate.newBuilder()
                                                                                                  .setDay(14)
                                                                                                  .build())
                                                                                 .setVendorId(
                                                                                         VendorId.newBuilder()
                                                                                                 .setValue(
                                                                                                         "Пюре")
                                                                                                 .build())
                                                                                 .build());
    }

    @Nested
    @DisplayName("PurchaseOrderCreated event should be interpreted by PurchaseOrderDetailsByDishViewProjection")
    class PurchaseOrderCreatedEvent {

        @Test
        @DisplayName("Should set order for a projection")
        void addView() {
            final PurchaseOrderCreated purchaseOrderCreated = purchaseOrderCreatedInstance();
            dispatch(projection, createEvent(purchaseOrderCreated));
            final List<DishItem> dishes = projection.getState()
                                                    .getDishList();
            assertEquals(13, dishes.size());
            assertEquals("vendor:value: \"VendorName1\"\n", projection.getState()
                                                                      .getId()
                                                                      .getVendorId()
                                                                      .getValue());
        }
    }

    @Nested
    @DisplayName("PurchaseOrderSent event should be interpreted by PurchaseOrderDetailsByDishViewProjection")
    class PurchaseOrderValidationFailedEvent {

        @Test
        @DisplayName("Should change status for a projection")
        void addView() {
            final PurchaseOrderCreated purchaseOrderCreated = purchaseOrderCreatedInstance();
            dispatch(projection, createEvent(purchaseOrderCreated));
            final PurchaseOrderValidationFailed poValidationFailed = purchaseOrderValidationFailedInstance();
            dispatch(projection, createEvent(poValidationFailed));
            assertEquals(PurchaseOrderStatus.INVALID, projection.getState()
                                                                .getPurchaseOrderStatus());
        }
    }

    @Nested
    @DisplayName("PurchaseOrderSent event should be interpreted by PurchaseOrderDetailsByDishViewProjection")
    class PurchaseOrderSentEvent {

        @Test
        @DisplayName("Should set status for a projection")
        void addView() {
            final PurchaseOrderCreated purchaseOrderCreated = purchaseOrderCreatedInstance();
            dispatch(projection, createEvent(purchaseOrderCreated));
            final PurchaseOrderSent purchaseOrderSent = purchaseOrderSentInstance();
            dispatch(projection, createEvent(purchaseOrderSent));
            assertEquals(PurchaseOrderStatus.SENT, projection.getState()
                                                             .getPurchaseOrderStatus());
        }
    }
    @Nested
    @DisplayName("PurchaseOrderDelivered event should be interpreted by PurchaseOrderDetailsByDishViewProjection")
    class PurchaseOrderDeliveredEvent {

        @Test
        @DisplayName("Should change existing status for a projection")
        void addView() {
            final PurchaseOrderCreated purchaseOrderCreated = purchaseOrderCreatedInstance();
            dispatch(projection, createEvent(purchaseOrderCreated));
            final PurchaseOrderSent purchaseOrderSent = purchaseOrderSentInstance();
            dispatch(projection, createEvent(purchaseOrderSent));
            assertEquals(PurchaseOrderStatus.SENT, projection.getState()
                                                             .getPurchaseOrderStatus());

            final PurchaseOrderDelivered purchaseOrderDelivered = purchaseOrderDeliveredInstance();
            dispatch(projection, createEvent(purchaseOrderDelivered));
            assertEquals(PurchaseOrderStatus.DELIVERED, projection.getState()
                                                                  .getPurchaseOrderStatus());
        }
    }
}
