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

import static com.google.common.base.Preconditions.checkNotNull;
import static io.spine.time.Time.getCurrentTime;

/**
 * Utility class for working with {@link OrderAggregate} rejections.
 *
 * @author Vlad Kozachenko
 */
class OrderAggregateRejections {

    /**
     * Prevent instantiation of this utility class.
     */
    private OrderAggregateRejections() {
    }

    /**
     * Utility class for working with {@link CreateOrder} command rejections.
     */
    static class CreateOrderRejections {

        /**
         * Prevent instantiation of this utility class.
         */
        private CreateOrderRejections() {
        }

        /**
         * Constructs and returns the {@link OrderAlreadyExists} rejection
         * according to the passed parameters.
         *
         * @param cmd the {@code CreateOrder} command which was rejected
         * @return  OrderAlreadyExists the rejection to return
         */
        static OrderAlreadyExists orderAlreadyExists(CreateOrder cmd) {
            checkNotNull(cmd);
            final OrderId orderId = cmd.getOrderId();
            final Timestamp timestamp = getCurrentTime();
            return new OrderAlreadyExists(orderId, timestamp);
        }

        /**
         * Constructs and returns the {@link MenuNotAvailable} rejection
         * according to the passed parameters.
         *
         * @param cmd the {@code CreateOrder} command which was rejected
         * @return  MenuNotAvailable the rejection to return
         */
        static MenuNotAvailable menuNotAvailable(CreateOrder cmd) {
            checkNotNull(cmd);
            final UserId userId = cmd.getOrderId()
                                     .getUserId();
            final VendorId vendorId = cmd.getOrderId()
                                         .getVendorId();
            final LocalDate orderDate = cmd.getOrderId()
                                           .getOrderDate();
            return new MenuNotAvailable(userId, vendorId, orderDate, getCurrentTime());
        }
    }

    /**
     * Utility class for working with {@link AddDishToOrder} command rejections.
     */
    static class AddDishToOrderRejections {

        /**
         * Prevent instantiation of this utility class.
         */
        private AddDishToOrderRejections() {
        }

        /**
         * Constructs and returns the {@link DishVendorMismatch} rejection
         * according to the passed parameters.
         *
         * @param cmd the {@code AddDishToOrder} command which was rejected
         * @return  DishVendorMismatch the rejection to return
         */
        static DishVendorMismatch dishVendorMismatch(AddDishToOrder cmd) {
            checkNotNull(cmd);
            final OrderId orderId = cmd.getOrderId();
            final UserId userId = cmd.getOrderId()
                                     .getUserId();
            final DishId dishId = cmd.getDish()
                                     .getId();
            final VendorId targetVendorId = orderId.getVendorId();
            final VendorId actualVendorId = dishId.getMenuId()
                                                  .getVendorId();
            final VendorMismatch vendorMismatch = VendorMismatch.newBuilder()
                                                                .setTarget(targetVendorId)
                                                                .setActual(actualVendorId)
                                                                .build();
            final Timestamp timestamp = getCurrentTime();

            return new DishVendorMismatch(orderId, dishId, userId, vendorMismatch, timestamp);
        }

        /**
         * Constructs and returns the {@link CannotAddDishToNotActiveOrder} rejection
         * according to the passed parameters.
         *
         * @param cmd the {@code AddDishToOrder} command which was rejected
         * @return  CannotAddDishToNotActiveOrder the rejection to return
         */
        static CannotAddDishToNotActiveOrder cannotAddDishToNotActiveOrder(
                AddDishToOrder cmd,
                OrderStatus orderStatus) {
            checkNotNull(cmd);
            checkNotNull(orderStatus);
            final OrderId orderId = cmd.getOrderId();
            final UserId userId = cmd.getOrderId()
                                     .getUserId();
            final DishId dishId = cmd.getDish()
                                     .getId();
            final Timestamp timestamp = getCurrentTime();
            return new CannotAddDishToNotActiveOrder(orderId,
                                                     dishId,
                                                     userId,
                                                     orderStatus,
                                                     timestamp);
        }
    }

    /**
     * Utility class for working with {@link RemoveDishFromOrder} command rejections.
     */
    static class RemoveDishFromOrderRejections {

        /**
         * Prevent instantiation of this utility class.
         */
        private RemoveDishFromOrderRejections() {
        }

        /**
         * Constructs and returns the {@link CannotRemoveMissingDish} rejection
         * according to the passed parameters.
         *
         * @param cmd the {@code RemoveDishFromOrder} command which was rejected
         * @return  CannotRemoveMissingDish the rejection to return
         */
        static CannotRemoveMissingDish cannotRemoveMissingDish(
                RemoveDishFromOrder cmd) {
            checkNotNull(cmd);
            final OrderId orderId = cmd.getOrderId();
            final UserId userId = cmd.getOrderId()
                                     .getUserId();
            final DishId dishId = cmd.getDishId();
            final Timestamp timestamp = getCurrentTime();
            return new CannotRemoveMissingDish(orderId, userId, dishId, timestamp);
        }

        /**
         * Constructs and returns the {@link CannotRemoveDishFromNotActiveOrder} rejection
         * according to the passed parameters.
         *
         * @param cmd the {@code RemoveDishFromOrder} command which was rejected
         * @return CannotRemoveDishFromNotActiveOrder the rejection to return
         */
        static CannotRemoveDishFromNotActiveOrder cannotRemoveDishFromNotActiveOrder(
                RemoveDishFromOrder cmd,
                OrderStatus orderStatus) {
            checkNotNull(cmd);
            final OrderId orderId = cmd.getOrderId();
            final UserId userId = cmd.getOrderId()
                                     .getUserId();
            final DishId dishId = cmd.getDishId();
            final Timestamp timestamp = getCurrentTime();
            return new CannotRemoveDishFromNotActiveOrder(orderId,
                                                          dishId,
                                                          userId,
                                                          orderStatus,
                                                          timestamp);
        }
    }

    /**
     * Utility class for working with {@link CancelOrder} command rejections.
     */
    static class CancelOrderRejections {

        /**
         * Prevent instantiation of this utility class.
         */
        private CancelOrderRejections() {
        }

        /**
         * Constructs and returns the {@link CannotCancelProcessedOrder} rejection
         * according to the passed parameters.
         *
         * @param cmd the {@code CancelOrder} command which was rejected
         * @return CannotCancelProcessedOrder the rejection to return
         */
        static CannotCancelProcessedOrder cannotCancelProcessedOrder(CancelOrder cmd) {
            checkNotNull(cmd);
            final OrderId orderId = cmd.getOrderId();
            final UserId userId = cmd.getOrderId()
                                     .getUserId();
            final Timestamp timestamp = getCurrentTime();
            return new CannotCancelProcessedOrder(orderId, userId, timestamp);
        }
    }
}
