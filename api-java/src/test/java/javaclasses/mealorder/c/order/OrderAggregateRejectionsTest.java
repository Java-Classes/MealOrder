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

package javaclasses.mealorder.c.order;

import io.spine.test.Tests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.spine.test.Tests.assertHasPrivateParameterlessCtor;
import static javaclasses.mealorder.c.order.OrderAggregateRejections.AddDishToOrderRejections.cannotAddDishToNotActiveOrder;
import static javaclasses.mealorder.c.order.OrderAggregateRejections.AddDishToOrderRejections.dishVendorMismatch;
import static javaclasses.mealorder.c.order.OrderAggregateRejections.CancelOrderRejections.cannotCancelProcessedOrder;
import static javaclasses.mealorder.c.order.OrderAggregateRejections.CreateOrderRejections.menuNotAvailable;
import static javaclasses.mealorder.c.order.OrderAggregateRejections.CreateOrderRejections.orderAlreadyExists;
import static javaclasses.mealorder.c.order.OrderAggregateRejections.RemoveDishFromOrderRejections.cannotRemoveDishFromNotActiveOrder;
import static javaclasses.mealorder.c.order.OrderAggregateRejections.RemoveDishFromOrderRejections.cannotRemoveMissingDish;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Vlad Kozachenko
 */
@DisplayName("OrderAggregateRejections should")
class OrderAggregateRejectionsTest {

    @Test
    @DisplayName("have the private constructor")
    void havePrivateConstructor() {
        assertHasPrivateParameterlessCtor(OrderAggregateRejections.class);
    }

    @Nested
    @DisplayName("CreateOrderRejections should")
    class CreateOrderRejectionsTest {

        @Test
        @DisplayName("have the private constructor")
        void havePrivateConstructor() {
            assertHasPrivateParameterlessCtor(OrderAggregateRejections.CreateOrderRejections.class);
        }

        @Test
        @DisplayName("don't return orderAlreadyExists rejection for null command")
        void doNotThrowOrderAlreadyExistsRejection() {
            assertThrows(NullPointerException.class,
                         () -> orderAlreadyExists(Tests.nullRef()));
        }

        @Test
        @DisplayName("don't return MenuNotAvailable rejection for null command")
        void doNotThrowMenuNotAvailableRejection() {
            assertThrows(NullPointerException.class,
                         () -> menuNotAvailable(Tests.nullRef()));
        }
    }

    @Nested
    @DisplayName("AddDishToOrderRejections should")
    class AddDishToOrderRejectionsTest {

        @Test
        @DisplayName("have the private constructor")
        void havePrivateConstructor() {
            assertHasPrivateParameterlessCtor(
                    OrderAggregateRejections.AddDishToOrderRejections.class);
        }


        @Test
        @DisplayName("don't return DishVendorMismatch rejection for null command")
        void doNotThrowDishVendorMismatchRejection() {
            assertThrows(NullPointerException.class,
                         () -> dishVendorMismatch(Tests.nullRef()));
        }


        @Test
        @DisplayName("don't return CannotAddDishToNotActiveOrder rejection for null command")
        void doNotThrowCannotAddDishToNotActiveOrderRejection() {
            assertThrows(NullPointerException.class,
                         () -> cannotAddDishToNotActiveOrder(Tests.nullRef(), Tests.nullRef()));
        }
    }

    @Nested
    @DisplayName("RemoveDishFromOrderRejections should")
    class RemoveDishFromOrderRejectionsTest {

        @Test
        @DisplayName("have the private constructor")
        void havePrivateConstructor() {
            assertHasPrivateParameterlessCtor(
                    OrderAggregateRejections.RemoveDishFromOrderRejections.class);
        }


        @Test
        @DisplayName("don't return CannotRemoveMissingDish rejection for null command")
        void doNotThrowCannotRemoveMissingDishRejection() {
            assertThrows(NullPointerException.class,
                         () -> cannotRemoveMissingDish(Tests.nullRef()));
        }

        @Test
        @DisplayName("don't return CannotRemoveDishFromNotActiveOrder rejection for null command")
        void doNotThrowCannotRemoveDishFromNotActiveOrderRejection() {
            assertThrows(NullPointerException.class,
                         () -> cannotRemoveDishFromNotActiveOrder(Tests.nullRef(), Tests.nullRef()));
        }
    }

    @Nested
    @DisplayName("CancelOrderRejections should")
    class CancelOrderRejectionsTest {

        @Test
        @DisplayName("have the private constructor")
        void havePrivateConstructor() {
            assertHasPrivateParameterlessCtor(
                    OrderAggregateRejections.CancelOrderRejections.class);
        }


        @Test
        @DisplayName("don't return CannotCancelProcessedOrder rejection for null command")
        void doNotThrowCannotCancelProcessedOrderRejection() {
            assertThrows(NullPointerException.class,
                         () -> cannotCancelProcessedOrder(Tests.nullRef()));
        }
    }

}
