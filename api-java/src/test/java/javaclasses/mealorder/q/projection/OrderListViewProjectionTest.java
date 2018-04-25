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

import javaclasses.mealorder.OrderListId;
import javaclasses.mealorder.c.event.DishAddedToOrder;
import javaclasses.mealorder.c.event.DishRemovedFromOrder;
import javaclasses.mealorder.q.OrderListViewProjection;
import javaclasses.mealorder.testdata.TestValues;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.spine.server.projection.ProjectionEventDispatcher.dispatch;
import static javaclasses.mealorder.testdata.TestMealOrderEventFactory.OrderEvents.dishAddedToOrderInstance;
import static javaclasses.mealorder.testdata.TestMealOrderEventFactory.OrderEvents.dishAddedToOrderInstance2;
import static javaclasses.mealorder.testdata.TestMealOrderEventFactory.OrderEvents.dishRemovedFromOrderInstance;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderListViewProjectionTest extends ProjectionTest {
    private OrderListViewProjection projection;

    @BeforeEach
    void setUp() {
        projection = new OrderListViewProjection(OrderListId.newBuilder()
                                                            .setUserId(TestValues.USER_ID)
                                                            .build());
    }

    @Nested
    @DisplayName("DishAddedToOrder event should be interpreted by OrderListViewProjection")
    class DishAddedToOrderEvent {

        @Test
        @DisplayName("Should add dish to the order")
        void addDish() {
            final DishAddedToOrder dishAddedToOrder = dishAddedToOrderInstance();
            dispatch(projection, createEvent(dishAddedToOrder));

            assertEquals(1, projection.getState()
                                      .getOrderList()
                                      .size());
            assertEquals("dishName1", projection.getState()
                                                .getOrderList()
                                                .get(0)
                                                .getDishList()
                                                .get(0)
                                                .getName());
        }

        @Test
        @DisplayName("Should add 3 dishes to the order")
        void addThreeDishes() {
            final DishAddedToOrder dishAddedToOrder = dishAddedToOrderInstance();
            dispatch(projection, createEvent(dishAddedToOrder));
            dispatch(projection, createEvent(dishAddedToOrder));
            dispatch(projection, createEvent(dishAddedToOrder));

            assertEquals(3, projection.getState()
                                      .getOrderList()
                                      .get(0)
                                      .getDishList()
                                      .size());
            assertEquals("dishName1", projection.getState()
                                                .getOrderList()
                                                .get(0)
                                                .getDishList()
                                                .get(2)
                                                .getName());
        }
    }

    @Test
    @DisplayName("Should add 2 dishes to different orders")
    void addThreeDishes() {
        final DishAddedToOrder dishAddedToOrder = dishAddedToOrderInstance();
        dispatch(projection, createEvent(dishAddedToOrder));
        final DishAddedToOrder dishAddedToOrder2 = dishAddedToOrderInstance2();
        dispatch(projection, createEvent(dishAddedToOrder2));

        assertEquals(2, projection.getState()
                                  .getOrderList()
                                  .size());
        assertEquals(1, projection.getState()
                                  .getOrderList()
                                  .get(1)
                                  .getDishList()
                                  .size());
        assertEquals("dishName1", projection.getState()
                                            .getOrderList()
                                            .get(0)
                                            .getDish(0)
                                            .getName());
        assertEquals("dishName2", projection.getState()
                                            .getOrderList()
                                            .get(1)
                                            .getDish(0)
                                            .getName());
    }

    @Test
    @DisplayName("Should remove 1 dish from the correct order")
    void remove() {
        final DishAddedToOrder dishAddedToOrder = dishAddedToOrderInstance();
        dispatch(projection, createEvent(dishAddedToOrder));
        dispatch(projection, createEvent(dishAddedToOrder));
        final DishAddedToOrder dishAddedToOrder2 = dishAddedToOrderInstance2();
        dispatch(projection, createEvent(dishAddedToOrder2));
        assertEquals(2, projection.getState()
                                  .getOrderList()
                                  .get(0)
                                  .getDishList()
                                  .size());
        final DishRemovedFromOrder dishRemovedFromOrder = dishRemovedFromOrderInstance();
        dispatch(projection, createEvent(dishRemovedFromOrder));
        assertEquals(1, projection.getState()
                                  .getOrderList()
                                  .get(0)
                                  .getDishList()
                                  .size());
        assertEquals(1, projection.getState()
                                  .getOrderList()
                                  .get(1)
                                  .getDishList()
                                  .size());
    }
}
