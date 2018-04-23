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
import javaclasses.mealorder.VendorId;
import javaclasses.mealorder.c.event.PurchaseOrderCreated;
import javaclasses.mealorder.q.PurchaseOrderDetailsByUserViewProjection;
import javaclasses.mealorder.q.UserOrderDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.spine.server.projection.ProjectionEventDispatcher.dispatch;
import static javaclasses.mealorder.testdata.TestMealOrderEventFactory.PurchaseOrderEvents.purchaseOrderCreatedInstance;

public class PurchaseOrderDetailsByUserViewProjectionTest extends ProjectionTest {
    private PurchaseOrderDetailsByUserViewProjection projection;

    @BeforeEach
    void setUp() {
        projection = new PurchaseOrderDetailsByUserViewProjection(PurchaseOrderId.newBuilder()
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
    @DisplayName("PurchaseOrderSent event should be interpreted by PurchaseOrderListViewProjection")
    class PurchaseOrderSentEvent {

        @Test
        @DisplayName("Should set order for a projection")
        void addView() {
            final PurchaseOrderCreated purchaseOrderCreated = purchaseOrderCreatedInstance();
            dispatch(projection, createEvent(purchaseOrderCreated));
            final List<UserOrderDetails> orderList = projection.getState()
                                                               .getOrderList();
        }
    }
}