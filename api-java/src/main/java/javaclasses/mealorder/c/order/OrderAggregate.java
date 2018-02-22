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

import io.spine.core.React;
import io.spine.server.aggregate.Aggregate;
import io.spine.server.aggregate.Apply;
import io.spine.server.command.Assign;
import javaclasses.mealorder.Dish;
import javaclasses.mealorder.DishId;
import javaclasses.mealorder.MenuId;
import javaclasses.mealorder.Order;
import javaclasses.mealorder.OrderId;
import javaclasses.mealorder.OrderStatus;
import javaclasses.mealorder.OrderVBuilder;
import javaclasses.mealorder.UserId;
import javaclasses.mealorder.VendorId;
import javaclasses.mealorder.c.command.AddDishToOrder;
import javaclasses.mealorder.c.command.CancelOrder;
import javaclasses.mealorder.c.command.CreateOrder;
import javaclasses.mealorder.c.command.RemoveDishFromOrder;
import javaclasses.mealorder.c.event.DishAddedToOrder;
import javaclasses.mealorder.c.event.DishRemovedFromOrder;
import javaclasses.mealorder.c.event.OrderCanceled;
import javaclasses.mealorder.c.event.OrderCreated;
import javaclasses.mealorder.c.event.OrderProcessed;
import javaclasses.mealorder.c.event.PurchaseOrderCanceled;
import javaclasses.mealorder.c.event.PurchaseOrderCreated;
import javaclasses.mealorder.c.rejection.CannotAddDishToNotActiveOrder;
import javaclasses.mealorder.c.rejection.CannotCancelProcessedOrder;
import javaclasses.mealorder.c.rejection.CannotRemoveDishFromNotActiveOrder;
import javaclasses.mealorder.c.rejection.CannotRemoveMissingDish;
import javaclasses.mealorder.c.rejection.DishVendorMismatch;
import javaclasses.mealorder.c.rejection.MenuNotAvailable;
import javaclasses.mealorder.c.rejection.OrderAlreadyExists;

import java.util.List;
import java.util.stream.IntStream;

import static io.spine.time.Time.getCurrentTime;
import static javaclasses.mealorder.OrderStatus.ORDER_ACTIVE;
import static javaclasses.mealorder.OrderStatus.ORDER_CANCELED;
import static javaclasses.mealorder.OrderStatus.ORDER_PROCESSED;
import static javaclasses.mealorder.OrderStatus.ORDER_UNDEFINED;
import static javaclasses.mealorder.c.order.OrderAggregateRejections.AddDishToOrderRejections.throwCannotAddDishToNotActiveOrder;
import static javaclasses.mealorder.c.order.OrderAggregateRejections.AddDishToOrderRejections.throwDishVendorMismatch;
import static javaclasses.mealorder.c.order.OrderAggregateRejections.CancelOrderRejections.throwCannotCancelProcessedOrder;
import static javaclasses.mealorder.c.order.OrderAggregateRejections.CreateOrderRejections.throwOrderAlreadyExists;
import static javaclasses.mealorder.c.order.OrderAggregateRejections.RemoveDishFromOrderRejections.throwCannotRemoveDishFromNotActiveOrder;
import static javaclasses.mealorder.c.order.OrderAggregateRejections.RemoveDishFromOrderRejections.throwCannotRemoveMissingDish;
import static javaclasses.mealorder.c.order.Orders.checkMenuAvailability;

/**
 * The aggregate managing the state of a {@link Order}.
 *
 * @author Vlad Kozachenko
 */
@SuppressWarnings({"ClassWithTooManyMethods", /* Vendor definition cannot be separated and should
                                                 process all commands and events related to it
                                                 according to the domain model.
                                                 The {@code Aggregate} does it with methods
                                                 annotated as {@code Assign} and {@code Apply}.
                                                 In that case class has too many methods.*/
        "OverlyCoupledClass"}) /* As each method needs dependencies  necessary to perform execution
                                                 that class also overly coupled.*/
public class OrderAggregate extends Aggregate<OrderId,
        Order,
        OrderVBuilder> {

    /**
     * {@inheritDoc}
     */
    public OrderAggregate(OrderId id) {
        super(id);
    }

    @Assign
    OrderCreated handle(CreateOrder cmd) throws OrderAlreadyExists, MenuNotAvailable {
        final OrderId orderId = cmd.getOrderId();
        final MenuId menuId = cmd.getMenuId();

        checkMenuAvailability(cmd);
        final OrderStatus orderStatus = getState().getStatus();
        if (orderStatus != ORDER_UNDEFINED && orderStatus != ORDER_CANCELED) {
            throwOrderAlreadyExists(cmd);
        }
        final OrderCreated result = OrderCreated.newBuilder()
                                                .setOrderId(orderId)
                                                .setMenuId(menuId)
                                                .build();
        return result;
    }

    @Assign
    DishAddedToOrder handle(AddDishToOrder cmd) throws DishVendorMismatch,
                                                       CannotAddDishToNotActiveOrder {
        final OrderId orderId = cmd.getOrderId();
        final Dish dish = cmd.getDish();

        final OrderStatus orderStatus = getState().getStatus();
        if (orderStatus != ORDER_ACTIVE) {
            throwCannotAddDishToNotActiveOrder(cmd, orderStatus);
        }

        final VendorId dishVendorId = dish.getId()
                                          .getMenuId()
                                          .getVendorId();
        final VendorId orderVendorId = orderId.getVendorId();
        if (!orderVendorId.equals(dishVendorId)) {
            throwDishVendorMismatch(cmd);
        }

        final DishAddedToOrder result = DishAddedToOrder.newBuilder()
                                                        .setOrderId(orderId)
                                                        .setDish(dish)
                                                        .build();
        return result;
    }

    @Assign
    DishRemovedFromOrder handle(RemoveDishFromOrder cmd) throws CannotRemoveMissingDish,
                                                                CannotRemoveDishFromNotActiveOrder {
        final OrderId orderId = cmd.getOrderId();
        final DishId dishId = cmd.getDishId();

        if (getState().getStatus() != ORDER_ACTIVE) {
            throwCannotRemoveDishFromNotActiveOrder(cmd, getState().getStatus());
        }

        final List<Dish> dishesList = getState().getDishList();

        java.util.Optional<Dish> dish = dishesList.stream()
                                                  .filter(d -> d.getId()
                                                                .equals(dishId))
                                                  .findFirst();

        if (!dish.isPresent()) {
            throwCannotRemoveMissingDish(cmd);
        }
        DishRemovedFromOrder result = DishRemovedFromOrder.newBuilder()
                                                          .setOrderId(orderId)
                                                          .setDish(dish.get())
                                                          .build();
        return result;
    }

    @Assign
    OrderCanceled handle(CancelOrder cmd) throws CannotCancelProcessedOrder {
        final OrderId orderId = cmd.getOrderId();
        final UserId userId = cmd.getWhoCancels();

        if (getState().getStatus() == ORDER_PROCESSED) {
            throwCannotCancelProcessedOrder(cmd);
        }

        final OrderCanceled result = OrderCanceled.newBuilder()
                                                  .setOrderId(orderId)
                                                  .setWhoCanceled(userId)
                                                  .setWhenCanceled(getCurrentTime())
                                                  .build();
        return result;
    }

    /*
     * Event appliers
     *****************/

    @Apply
    void orderCreated(OrderCreated event) {

        if (getBuilder().getStatus() == ORDER_CANCELED) {
            getBuilder().clearDish()
                        .build();
        }

        getBuilder().setId(event.getOrderId())
                    .setStatus(ORDER_ACTIVE)
                    .build();
    }

    @Apply
    void dishAddedToOrder(DishAddedToOrder event) {
        getBuilder().addDish(event.getDish())
                    .build();
    }

    @Apply
    void dishRemovedFromOrder(DishRemovedFromOrder event) {

        final List<Dish> dishes = getBuilder().getDish();
        final Dish eventDish = event.getDish();
        final int index = IntStream.range(0, dishes.size())
                                   .filter(i -> dishes.get(i)
                                                      .equals(eventDish))
                                   .findFirst()
                                   .getAsInt();

        getBuilder().removeDish(index)
                    .build();
    }

    @Apply
    void orderCanceled(OrderCanceled event) {
        getBuilder().setStatus(ORDER_CANCELED)
                    .build();
    }

    @Apply
    void orderProcessed(OrderProcessed event) {
        getBuilder().setStatus(ORDER_PROCESSED)
                    .build();
    }

    /*
     * Reacts
     *****************/

    @React
    OrderProcessed on(PurchaseOrderCreated event) {
        return OrderProcessed.newBuilder()
                             .setOrder(getState())
                             .setWhenProcessed(getCurrentTime())
                             .build();
    }

    @React
    OrderCanceled on(PurchaseOrderCanceled event) {
        return OrderCanceled.newBuilder()
                            .setOrderId(getState().getId())
                            .setWhoCanceled(event.getUserId())
                            .setWhenCanceled(getCurrentTime())
                            .build();
    }
}
