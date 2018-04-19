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

package javaclasses.mealorder.q;

import io.spine.core.Subscribe;
import io.spine.server.projection.Projection;
import javaclasses.mealorder.Dish;
import javaclasses.mealorder.OrderId;
import javaclasses.mealorder.c.event.DishAddedToOrder;
import javaclasses.mealorder.c.event.DishRemovedFromOrder;
import javaclasses.mealorder.c.event.OrderCanceled;
import javaclasses.mealorder.c.event.OrderProcessed;
import javaclasses.mealorder.q.projection.OrderListView;
import javaclasses.mealorder.q.projection.OrderListViewVBuilder;

import java.util.List;
import java.util.Optional;

/**
 * @author Alexander Karpets
 */
public class OrderListViewProjection extends Projection<OrderId, OrderListView, OrderListViewVBuilder> {

    /**
     * Creates a new instance.
     *
     * @param id the ID for the new instance
     * @throws IllegalArgumentException if the ID is not of one of the supported types
     */
    public OrderListViewProjection(OrderId id) {
        super(id);
    }

    @Subscribe
    void on(DishAddedToOrder event) {
        final OrderId orderId = event.getOrderId();
        final DishItem dish = DishItem.newBuilder()
                                      .setName(event.getDish()
                                                    .getName())
                                      .setPrice(event.getDish()
                                                     .getPrice())
                                      .build();
        if (getBuilder().getOrder()
                        .contains(getOrderById(orderId))) {
            getBuilder().setOrder(getBuilder().getOrder()
                                              .indexOf(getOrderById(orderId)),
                                  OrderItem.newBuilder(getOrderById(orderId))
                                           .addDish(dish)
                                           .build());
        } else {
            getBuilder().addOrder(OrderItem.newBuilder()
                                           .addDish(dish)
                                           .setId(event.getOrderId())
                                           .build());
        }
    }

    @Subscribe
    void on(DishRemovedFromOrder event) {
        final List<OrderItem> orderItems = getBuilder().getOrder();
        final int removeIndex = getDishFromOrder(event.getDish(), getOrderById(event.getOrderId()));
        final OrderItem newOrder = OrderItem.newBuilder(getOrderById(event.getOrderId()))
                                            .removeDish(removeIndex)
                                            .build();
        getBuilder().setOrder(orderItems.indexOf(getOrderById(event.getOrderId())), newOrder);
    }

    @Subscribe
    void on(OrderCanceled event) {
        getBuilder().clearOrder();
    }

    @Subscribe
    void on(OrderProcessed event) {
        getBuilder().setProcessed(true);
    }

    private OrderItem getOrderById(OrderId orderId) {
        final List<OrderItem> orderItems = getBuilder().getOrder();
        final Optional<OrderItem> inventoryItem =
                orderItems.stream()
                          .filter(item -> item.getId()
                                              .equals(orderId))
                          .findFirst();
        return inventoryItem.get();
    }

    private int getDishFromOrder(Dish dish, OrderItem order) {
        return order.getDishList()
                    .indexOf(dish);
    }

}
