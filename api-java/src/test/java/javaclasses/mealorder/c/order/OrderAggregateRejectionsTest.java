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

import javaclasses.mealorder.UserId;
import javaclasses.mealorder.VendorId;
import javaclasses.mealorder.c.command.AddDishToOrder;
import javaclasses.mealorder.c.command.CancelOrder;
import javaclasses.mealorder.c.command.CreateOrder;
import javaclasses.mealorder.c.command.RemoveDishFromOrder;
import javaclasses.mealorder.c.order.OrderAggregateRejections;
import javaclasses.mealorder.c.rejection.CannotAddDishToNotActiveOrder;
import javaclasses.mealorder.c.rejection.CannotCancelProcessedOrder;
import javaclasses.mealorder.c.rejection.CannotRemoveDishFromNotActiveOrder;
import javaclasses.mealorder.c.rejection.CannotRemoveMissingDish;
import javaclasses.mealorder.c.rejection.DishVendorMismatch;
import javaclasses.mealorder.c.rejection.MenuNotAvailable;
import javaclasses.mealorder.c.rejection.OrderAlreadyExists;
import javaclasses.mealorder.c.rejection.Rejections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.spine.test.Tests.assertHasPrivateParameterlessCtor;
import static javaclasses.mealorder.OrderStatus.ORDER_ACTIVE;
import static javaclasses.mealorder.OrderStatus.ORDER_CANCELED;
import static javaclasses.mealorder.OrderStatus.ORDER_PROCESSED;
import static javaclasses.mealorder.c.order.OrderAggregateRejections.AddDishToOrderRejections.throwCannotAddDishToNotActiveOrder;
import static javaclasses.mealorder.c.order.OrderAggregateRejections.AddDishToOrderRejections.throwDishVendorMismatch;
import static javaclasses.mealorder.c.order.OrderAggregateRejections.CancelOrderRejections.throwCannotCancelProcessedOrder;
import static javaclasses.mealorder.c.order.OrderAggregateRejections.CreateOrderRejections.throwMenuNotAvailable;
import static javaclasses.mealorder.c.order.OrderAggregateRejections.CreateOrderRejections.throwOrderAlreadyExists;
import static javaclasses.mealorder.c.order.OrderAggregateRejections.RemoveDishFromOrderRejections.throwCannotRemoveDishFromNotActiveOrder;
import static javaclasses.mealorder.c.order.OrderAggregateRejections.RemoveDishFromOrderRejections.throwCannotRemoveMissingDish;
import static javaclasses.mealorder.testdata.TestOrderCommandFactory.addDishToOrderInstance;
import static javaclasses.mealorder.testdata.TestOrderCommandFactory.cancelOrderInstance;
import static javaclasses.mealorder.testdata.TestOrderCommandFactory.createOrderInstance;
import static javaclasses.mealorder.testdata.TestOrderCommandFactory.removeDishFromOrderInstance;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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
        @DisplayName("throw OrderAlreadyExists rejection")
        void throwOrderAlreadyExistsRejection() {

            final CreateOrder cmd = createOrderInstance();

            final OrderAlreadyExists rejection =
                    assertThrows(OrderAlreadyExists.class,
                                 () -> throwOrderAlreadyExists(cmd));

            assertEquals(cmd.getOrderId(), rejection.getMessageThrown()
                                                    .getOrderId());
        }

        @Test
        @DisplayName("throw MenuNotAvailable rejection")
        void throwMenuNotAvailableRejection() {

            final CreateOrder cmd = createOrderInstance();

            final MenuNotAvailable rejection =
                    assertThrows(MenuNotAvailable.class,
                                 () -> throwMenuNotAvailable(cmd));

            assertEquals(cmd.getOrderId()
                            .getVendorId(), rejection.getMessageThrown()
                                                     .getVendorId());
            assertEquals(cmd.getOrderId()
                            .getUserId(), rejection.getMessageThrown()
                                                   .getUserId());
            assertEquals(cmd.getOrderId()
                            .getOrderDate(), rejection.getMessageThrown()
                                                      .getOrderDate());
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
        @DisplayName("throw DishVendorMismatch rejection")
        void throwDishVendorMismatchRejection() {

            final AddDishToOrder cmd = addDishToOrderInstance();

            final DishVendorMismatch rejection =
                    assertThrows(DishVendorMismatch.class,
                                 () -> throwDishVendorMismatch(cmd));

            final Rejections.DishVendorMismatch mismatch = rejection.getMessageThrown();
            assertEquals(cmd.getOrderId(), mismatch.getOrderId());
            final UserId expectedUserId = cmd.getOrderId()
                                             .getUserId();
            assertEquals(expectedUserId, mismatch.getUserId());
            final VendorId expectedVendorId = cmd.getOrderId()
                                                 .getVendorId();
            final VendorId target = mismatch.getVendorMismatch()
                                            .getTarget();
            assertEquals(expectedVendorId, target);
            final VendorId expectedDishVendorId = cmd.getDish()
                                                     .getId()
                                                     .getMenuId()
                                                     .getVendorId();
            final VendorId actualDishVendorId = mismatch.getVendorMismatch()
                                                        .getActual();
            assertEquals(expectedDishVendorId, actualDishVendorId);
        }

        @Test
        @DisplayName("throw CannotAddDishToNotActiveOrder rejection")
        void throwCannotAddDishToNotActiveOrderRejection() {

            final AddDishToOrder cmd = addDishToOrderInstance();

            final CannotAddDishToNotActiveOrder rejection =
                    assertThrows(CannotAddDishToNotActiveOrder.class,
                                 () -> throwCannotAddDishToNotActiveOrder(cmd, ORDER_PROCESSED));

            assertEquals(cmd.getOrderId(), rejection.getMessageThrown()
                                                    .getOrderId());
            assertEquals(cmd.getOrderId()
                            .getUserId(), rejection.getMessageThrown()
                                                   .getUserId());
            assertEquals(cmd.getDish()
                            .getId(), rejection.getMessageThrown()
                                               .getDishId());
            assertNotEquals(ORDER_ACTIVE, rejection.getMessageThrown()
                                                   .getOrderStatus());
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
        @DisplayName("throw CannotRemoveMissingDish rejection")
        void throwCannotRemoveMissingDishRejection() {

            final RemoveDishFromOrder cmd = removeDishFromOrderInstance();

            final CannotRemoveMissingDish rejection =
                    assertThrows(CannotRemoveMissingDish.class,
                                 () -> throwCannotRemoveMissingDish(cmd));

            assertEquals(cmd.getOrderId(), rejection.getMessageThrown()
                                                    .getOrderId());
            final UserId expectedUserId = cmd.getOrderId()
                                             .getUserId();
            final UserId actualUserId = rejection.getMessageThrown()
                                                 .getUserId();
            assertEquals(expectedUserId, actualUserId);
            assertEquals(cmd.getDishId(), rejection.getMessageThrown()
                                                   .getDishId());
        }

        @Test
        @DisplayName("throw CannotRemoveDishFromNotActiveOrder rejection")
        void throwCannotRemoveDishFromNotActiveOrderRejection() {

            final RemoveDishFromOrder cmd = removeDishFromOrderInstance();

            final CannotRemoveDishFromNotActiveOrder rejection =
                    assertThrows(CannotRemoveDishFromNotActiveOrder.class,
                                 () -> throwCannotRemoveDishFromNotActiveOrder(cmd,
                                                                               ORDER_CANCELED));

            assertEquals(cmd.getOrderId(), rejection.getMessageThrown()
                                                    .getOrderId());
            final UserId expectedUserId = cmd.getOrderId()
                                             .getUserId();
            final UserId actualUserId = rejection.getMessageThrown()
                                                 .getUserId();
            assertEquals(expectedUserId, actualUserId);
            assertEquals(cmd.getDishId(), rejection.getMessageThrown()
                                                   .getDishId());
            assertNotEquals(ORDER_ACTIVE, rejection.getMessageThrown()
                                                   .getOrderStatus());
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
        @DisplayName("throw CannotCancelProcessedOrder rejection")
        void throwCannotCancelProcessedOrderRejection() {

            final CancelOrder cmd = cancelOrderInstance();

            final CannotCancelProcessedOrder rejection =
                    assertThrows(CannotCancelProcessedOrder.class,
                                 () -> throwCannotCancelProcessedOrder(cmd));

            assertEquals(cmd.getOrderId(), rejection.getMessageThrown()
                                                    .getOrderId());
            final UserId expectedUserId = cmd.getOrderId()
                                             .getUserId();
            assertEquals(expectedUserId, rejection.getMessageThrown()
                                                  .getUserId());
        }
    }
}
