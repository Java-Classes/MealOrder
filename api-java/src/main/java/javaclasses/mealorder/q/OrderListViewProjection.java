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

import com.google.common.collect.Iterables;
import io.spine.core.Subscribe;
import io.spine.server.projection.Projection;
import javaclasses.mealorder.DishId;
import javaclasses.mealorder.OrderId;
import javaclasses.mealorder.OrderListId;
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
public class OrderListViewProjection extends Projection<OrderListId, OrderListView, OrderListViewVBuilder> {

    /**
     * Creates a new instance.
     *
     * @param id the ID for the new instance
     * @throws IllegalArgumentException if the ID is not of one of the supported types
     */
    public OrderListViewProjection(OrderListId id) {
        super(id);
    }

    @Subscribe
    public void on(DishAddedToOrder event) {
        final OrderId orderId = event.getOrderId();
        final DishItem dish = DishItem.newBuilder()
                                      .setName(event.getDish()
                                                    .getName())
                                      .setPrice(event.getDish()
                                                     .getPrice())
                                      .setId(event.getDish()
                                                  .getId())
                                      .build();
        if (getOrderById(orderId).isPresent()) {
            final OrderListId listId = OrderListId.newBuilder()
                                                  .setUserId(event.getOrderId()
                                                                  .getUserId())
                                                  .setOrderDate(event.getOrderId()
                                                                     .getOrderDate())
                                                  .build();
            getBuilder().setOrder(getBuilder().getOrder()
                                              .indexOf(getOrderById(orderId).get()),
                                  OrderItem.newBuilder(getOrderById(orderId).get())
                                           .addDish(dish)
                                           .build())
                        .setListId(listId);
        } else {
            getBuilder().addOrder(OrderItem.newBuilder()
                                           .addDish(dish)
                                           .setId(event.getOrderId())
                                           .build());
        }
    }

    @Subscribe
    public void on(DishRemovedFromOrder event) {
        final List<OrderItem> orderItems = getBuilder().getOrder();
        final int removeIndex = getDishIndexFromOrder(event.getDish()
                                                           .getId(),
                                                      getOrderById(event.getOrderId()).get());
        final OrderItem newOrder = OrderItem.newBuilder(getOrderById(event.getOrderId()).get())
                                            .removeDish(removeIndex)
                                            .build();
        getBuilder().setOrder(orderItems.indexOf(getOrderById(event.getOrderId()).get()), newOrder);
    }

    @Subscribe
    public void on(OrderCanceled event) {
        final OrderItem order = getOrderById(event.getOrderId()).get();
        getBuilder().removeOrder(getBuilder().getOrder()
                                             .indexOf(order));
    }

    @Subscribe
    public void on(OrderProcessed event) {
        final OrderItem order = getOrderById(event.getOrder()
                                                  .getId()).get();
        getBuilder().setOrder(getBuilder().getOrder()
                                          .indexOf(order), OrderItem.newBuilder(order)
                                                                    .setIsProcessed(true)
                                                                    .build());
    }

    private Optional<OrderItem> getOrderById(OrderId orderId) {
        final List<OrderItem> orderItems = getBuilder().getOrder();
        final Optional<OrderItem> inventoryItem =
                orderItems.stream()
                          .filter(item -> item.getId()
                                              .equals(orderId))
                          .findFirst();
        return inventoryItem;
    }

    private int getDishIndexFromOrder(DishId dishId, OrderItem order) {
        final int index = Iterables.indexOf(order.getDishList(), dish -> dish.getId()
                                                                             .equals(dishId));
        return index;
    }
}
