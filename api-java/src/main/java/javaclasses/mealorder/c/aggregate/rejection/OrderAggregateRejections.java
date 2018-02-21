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

package javaclasses.mealorder.c.aggregate.rejection;

import com.google.protobuf.Timestamp;
import io.spine.time.LocalDate;
import javaclasses.mealorder.DishId;
import javaclasses.mealorder.OrderId;
import javaclasses.mealorder.OrderStatus;
import javaclasses.mealorder.UserId;
import javaclasses.mealorder.VendorId;
import javaclasses.mealorder.VendorMismatch;
import javaclasses.mealorder.c.command.AddDishToOrder;
import javaclasses.mealorder.c.command.CancelOrder;
import javaclasses.mealorder.c.command.CreateOrder;
import javaclasses.mealorder.c.command.RemoveDishFromOrder;
import javaclasses.mealorder.c.rejection.CannotAddDishToNotActiveOrder;
import javaclasses.mealorder.c.rejection.CannotCancelProcessedOrder;
import javaclasses.mealorder.c.rejection.CannotRemoveDishFromNotActiveOrder;
import javaclasses.mealorder.c.rejection.CannotRemoveMissingDish;
import javaclasses.mealorder.c.rejection.DishVendorMismatch;
import javaclasses.mealorder.c.rejection.MenuNotAvailable;
import javaclasses.mealorder.c.rejection.OrderAlreadyExists;

import static io.spine.time.Time.getCurrentTime;

/**
 * @author Vlad Kozachenko
 */
public class OrderAggregateRejections {

    private OrderAggregateRejections() {
    }

    public static class CreateOrderRejections {

        private CreateOrderRejections() {
        }

        public static void throwOrderAlreadyExists(CreateOrder cmd) throws OrderAlreadyExists {
            final OrderId orderId = cmd.getOrderId();
            final Timestamp timestamp = getCurrentTime();
            throw new OrderAlreadyExists(orderId, timestamp);
        }

        public static void throwMenuNotAvailable(CreateOrder cmd) throws MenuNotAvailable {
            final UserId userId = cmd.getOrderId()
                                     .getUserId();
            final VendorId vendorId = cmd.getOrderId()
                                         .getVendorId();
            final LocalDate orderDate = cmd.getOrderId()
                                           .getOrderDate();
            throw new MenuNotAvailable(userId, vendorId, orderDate, getCurrentTime());
        }
    }

    public static class AddDishToOrderRejections {

        private AddDishToOrderRejections() {
        }

        public static void throwDishVendorMismatch(AddDishToOrder cmd) throws DishVendorMismatch {
            final OrderId orderId = cmd.getOrderId();
            final UserId userId = cmd.getOrderId()
                                     .getUserId();
            final DishId dishId = cmd.getDish()
                                     .getId();
            VendorMismatch vendorMismatch = VendorMismatch.newBuilder()
                                                          .setTarget(orderId.getVendorId())
                                                          .setActual(dishId.getMenuId().getVendorId())
                                                          .build();
            final Timestamp timestamp = getCurrentTime();

            throw new DishVendorMismatch(orderId, dishId, userId, vendorMismatch, timestamp);
        }

        public static void throwCannotAddDishToNotActiveOrder(AddDishToOrder cmd,
                                                              OrderStatus orderStatus) throws
                                                                                       CannotAddDishToNotActiveOrder {
            final OrderId orderId = cmd.getOrderId();
            final UserId userId = cmd.getOrderId()
                                     .getUserId();
            final DishId dishId = cmd.getDish()
                                     .getId();
            final Timestamp timestamp = getCurrentTime();
            throw new CannotAddDishToNotActiveOrder(orderId, dishId, userId, orderStatus,
                                                    timestamp);
        }
    }

    public static class RemoveDishFromOrderRejections {

        private RemoveDishFromOrderRejections() {
        }

        public static void throwCannotRemoveMissingDish(RemoveDishFromOrder cmd) throws
                                                                                 CannotRemoveMissingDish {
            final OrderId orderId = cmd.getOrderId();
            final UserId userId = cmd.getOrderId()
                                     .getUserId();
            final DishId dishId = cmd.getDishId();
            final Timestamp timestamp = getCurrentTime();
            throw new CannotRemoveMissingDish(orderId, userId, dishId, timestamp);
        }

        public static void throwCannotRemoveDishFromNotActiveOrder(RemoveDishFromOrder cmd,
                                                                   OrderStatus orderStatus) throws
                                                                                            CannotRemoveDishFromNotActiveOrder {
            final OrderId orderId = cmd.getOrderId();
            final UserId userId = cmd.getOrderId()
                                     .getUserId();
            final DishId dishId = cmd.getDishId();
            final Timestamp timestamp = getCurrentTime();
            throw new CannotRemoveDishFromNotActiveOrder(orderId, dishId, userId, orderStatus,
                                                         timestamp);
        }
    }

    public static class CancelOrderRejections {

        private CancelOrderRejections(){}

        public static void throwCannotCancelProcessedOrder(CancelOrder cmd) throws
                                                                            CannotCancelProcessedOrder {

            final OrderId orderId = cmd.getOrderId();
            final UserId userId = cmd.getOrderId()
                                     .getUserId();
            final Timestamp timestamp = getCurrentTime();
            throw new CannotCancelProcessedOrder(orderId, userId, timestamp);
        }
    }
}
