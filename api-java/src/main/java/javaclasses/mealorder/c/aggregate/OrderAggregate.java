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

import com.google.common.base.Optional;
import io.spine.core.React;
import io.spine.server.aggregate.Aggregate;
import io.spine.server.aggregate.AggregateRepository;
import io.spine.server.aggregate.Apply;
import io.spine.server.command.Assign;
import io.spine.time.LocalDate;
import javaclasses.mealorder.Dish;
import javaclasses.mealorder.DishId;
import javaclasses.mealorder.Menu;
import javaclasses.mealorder.MenuDateRange;
import javaclasses.mealorder.MenuId;
import javaclasses.mealorder.Order;
import javaclasses.mealorder.OrderId;
import javaclasses.mealorder.OrderVBuilder;
import javaclasses.mealorder.UserId;
import javaclasses.mealorder.VendorId;
import javaclasses.mealorder.VendorMismatch;
import javaclasses.mealorder.c.command.AddDishToOrder;
import javaclasses.mealorder.c.command.CancelOrder;
import javaclasses.mealorder.c.command.CreateOrder;
import javaclasses.mealorder.c.command.RemoveDishFromOrder;
import javaclasses.mealorder.c.event.DishAddedToOrder;
import javaclasses.mealorder.c.event.DishRemovedFromOrder;
import javaclasses.mealorder.c.event.OrderCanceled;
import javaclasses.mealorder.c.event.OrderCreated;
import javaclasses.mealorder.c.event.OrderProcessed;
import javaclasses.mealorder.c.event.PurchaseOrderCreated;
import javaclasses.mealorder.c.rejection.CannotAddDishToNotActiveOrder;
import javaclasses.mealorder.c.rejection.CannotRemoveDishFromNotActiveOrder;
import javaclasses.mealorder.c.rejection.CannotRemoveMissingDish;
import javaclasses.mealorder.c.rejection.DishVendorMismatch;
import javaclasses.mealorder.c.rejection.MenuNotAvailable;
import javaclasses.mealorder.c.rejection.OrderAlreadyExists;
import javaclasses.mealorder.c.repository.VendorRepository;

import java.util.Comparator;
import java.util.List;

import static io.spine.time.Time.getCurrentTime;
import static javaclasses.mealorder.OrderStatus.ORDER_ACTIVE;
import static javaclasses.mealorder.OrderStatus.ORDER_CANCELED;
import static javaclasses.mealorder.OrderStatus.ORDER_PROCESSED;
import static javaclasses.mealorder.c.aggregate.OrderValidator.isMenuAvailable;
import static javaclasses.mealorder.c.aggregate.rejection.OrderAggregateRejections.AddDishToOrderRejections.throwCannotAddDishToNotActiveOrder;
import static javaclasses.mealorder.c.aggregate.rejection.OrderAggregateRejections.AddDishToOrderRejections.throwDishVendorMismatch;
import static javaclasses.mealorder.c.aggregate.rejection.OrderAggregateRejections.CreateOrderRejections.throwMenuNotAvailable;
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
    OrderCreated handle(CreateOrder cmd) throws OrderAlreadyExists, MenuNotAvailable {
        final OrderId orderId = cmd.getOrderId();
        final MenuId menuId = cmd.getMenuId();

        final AggregateRepository vendorRepository = VendorRepository.getInstance()
                                                                     .getRepository();

        final Optional<VendorAggregate> vendor = vendorRepository.find(
                orderId.getVendorId());
        if (!vendor.isPresent()) {
            throwMenuNotAvailable(cmd);
        }

        final VendorAggregate vendorAggregate = vendor.get();

        final List<Menu> menus = vendorAggregate
                .getState()
                .getMenusList();

        final java.util.Optional<Menu> menu = menus.stream()
                                                   .filter(m -> cmd.getMenuId()
                                                                   .equals(m.getId()))
                                                   .findFirst();

        if (!menu.isPresent() || !isMenuAvailable(menu.get()
                                                      .getMenuDateRange(), cmd.getOrderId()
                                                                              .getOrderDate())) {
            throwMenuNotAvailable(cmd);
        }

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
            VendorMismatch vendorMismatch = VendorMismatch.newBuilder()
                                                          .setTarget(orderId.getVendorId())
                                                          .setActual(dishVendorId)
                                                          .build();
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

        List<Dish> dishesList = getState().getDishesList();

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
    void orderCreated(OrderCreated event) {

        if (getBuilder().getStatus() == ORDER_CANCELED) {
            getBuilder().clearDishes();
        }

        getBuilder().setId(event.getOrderId())
                    .setStatus(ORDER_ACTIVE)
                    .build();
    }

    @Apply
    void dishAddedToOrder(DishAddedToOrder event) {
        getBuilder().addDishes(event.getDish())
                    .build();
    }

    @Apply
    void dishRemovedFromOrder(DishRemovedFromOrder event) {
        for (int i = 0; i < getBuilder().getDishes()
                                        .size(); i++) {
            if (event.getDish()
                     .equals(getBuilder().getDishes()
                                         .get(i))) {
                getBuilder().removeDishes(i)
                            .build();
                return;
            }
        }
    }

    @Apply
    void orderCanceled(OrderCanceled event) {
        getBuilder().setStatus(ORDER_CANCELED)
                    .build();
    }

    @React
    OrderProcessed on(PurchaseOrderCreated event) {
        return OrderProcessed.newBuilder()
                             .setOrder(getState())
                             .setWhenProcessed(getCurrentTime())
                             .build();
    }

    @Apply
    private void orderProcessed(OrderProcessed event) {
        getBuilder().setStatus(ORDER_PROCESSED)
                    .build();
    }
}
