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

package javaclasses.mealorder.c.aggregate;

import io.spine.change.ValueMismatch;
import io.spine.server.aggregate.Aggregate;
import io.spine.server.aggregate.Apply;
import io.spine.server.command.Assign;
import javaclasses.mealorder.Dish;
import javaclasses.mealorder.DishId;
import javaclasses.mealorder.MenuId;
import javaclasses.mealorder.Order;
import javaclasses.mealorder.OrderId;
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
import javaclasses.mealorder.c.rejection.CannotAddDishToNotActiveOrder;
import javaclasses.mealorder.c.rejection.CannotRemoveDishFromNotActiveOrder;
import javaclasses.mealorder.c.rejection.CannotRemoveMissingDish;
import javaclasses.mealorder.c.rejection.DishVendorMismatch;
import javaclasses.mealorder.c.rejection.OrderAlreadyExists;

import static javaclasses.mealorder.OrderStatus.ORDER_ACTIVE;
import static javaclasses.mealorder.OrderStatus.ORDER_CANCELED;
import static javaclasses.mealorder.c.aggregate.rejection.OrderAggregateRejections.AddDishToOrderRejections.throwCannotAddDishToNotActiveOrder;
import static javaclasses.mealorder.c.aggregate.rejection.OrderAggregateRejections.AddDishToOrderRejections.throwDishVendorMismatch;
import static javaclasses.mealorder.c.aggregate.rejection.OrderAggregateRejections.CreateOrderRejections.throwOrderAldeadyExists;
import static javaclasses.mealorder.c.aggregate.rejection.OrderAggregateRejections.RemoveDishFromOrderRejections.throwCannotRemoveDishFromNotActiveOrder;
import static javaclasses.mealorder.c.aggregate.rejection.OrderAggregateRejections.RemoveDishFromOrderRejections.throwCannotRemoveMissingDish;

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
    OrderCreated handle(CreateOrder cmd) throws OrderAlreadyExists {
        final OrderId orderId = cmd.getOrderId();
        final MenuId menuId = cmd.getMenuId();

        if (getVersion().getNumber() != 0 && ORDER_CANCELED != getState().getStatus()) {
            throwOrderAldeadyExists(cmd);
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

        final VendorId dishVendorId = dish.getId()
                                          .getMenuId()
                                          .getVendorId();

        if (getState().getStatus() != ORDER_ACTIVE) {
            throwCannotAddDishToNotActiveOrder(cmd, getState().getStatus());
        }

        if (!orderId.getVendorId()
                    .equals(dishVendorId)) {
            // TODO:2018-02-15:vladislav.kozachenko: Learn more how to use ValueMismatch. What is 3rd argument (newValue)?
            ValueMismatch vendorMismatch = unexpectedValue(orderId.getVendorId(), dishVendorId,
                                                           dishVendorId);
            throwDishVendorMismatch(cmd, vendorMismatch);
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

        DishRemovedFromOrder result = null;
        for (Dish dish : getState().getDishesList()) {
            if (dish.getId()
                    .equals(dishId)) {
                result = DishRemovedFromOrder.newBuilder()
                                             .setOrderId(orderId)
                                             .setDish(dish)
                                             .build();
                return result;
            }
        }
        throwCannotRemoveMissingDish(cmd);
        return result;
    }

    @Assign
    OrderCanceled handle(CancelOrder cmd) {
        final OrderId orderId = cmd.getOrderId();
        final UserId userId = cmd.getWhoCancels();

        final OrderCanceled result = OrderCanceled.newBuilder()
                                                  .setOrderId(orderId)
                                                  .setWhoCanceled(userId)
                                                  .build();
        return result;
    }

    // Event appliers

    @Apply
    private void orderCreated(OrderCreated event) {
        getBuilder().setId(event.getOrderId())
                    .setStatus(ORDER_ACTIVE)
                    .build();
    }

    @Apply
    private void dishAddedToOrder(DishAddedToOrder event) {
        getBuilder().addDishes(event.getDish())
                    .build();
    }

    @Apply
    private void dishRemovedFromOrder(DishRemovedFromOrder event) {
        for (int i = 0; i < getState().getDishesCount(); i++) {
            if (event.getDish()
                     .equals(getState().getDishes(i))) {
                getBuilder().removeDishes(i)
                            .build();
                return;
            }
        }
    }

    @Apply
    private void orderCanceled(OrderCanceled event) {
        getBuilder().setStatus(ORDER_CANCELED)
                    .build();
    }
}
