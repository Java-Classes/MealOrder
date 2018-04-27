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

package javaclasses.mealorder.q.repository;

import io.spine.server.projection.ProjectionRepository;
import io.spine.server.route.EventRouting;
import io.spine.time.LocalDate;
import javaclasses.mealorder.OrderListId;
import javaclasses.mealorder.UserId;
import javaclasses.mealorder.c.event.DishAddedToOrder;
import javaclasses.mealorder.c.event.DishRemovedFromOrder;
import javaclasses.mealorder.c.event.OrderCanceled;
import javaclasses.mealorder.c.event.OrderProcessed;
import javaclasses.mealorder.q.OrderListViewProjection;
import javaclasses.mealorder.q.projection.OrderListView;

import static java.util.Collections.singleton;

public class OrderListViewRepository extends ProjectionRepository<OrderListId, OrderListViewProjection, OrderListView> {

    public OrderListViewRepository() {
        super();
        setUpEventRoute();
    }

    /**
     * Adds the {@link io.spine.server.route.EventRoute EventRoute}s to the repository.
     *
     * <p>Override this method in successor classes, otherwise all successors will use
     * {@code MyListViewProjection.ID}.
     */
    protected void setUpEventRoute() {

        final EventRouting<OrderListId> routing = getEventRouting();
        routing.route(DishAddedToOrder.class,
                      (message, context) -> {
                          final LocalDate date = message.getOrderId()
                                                        .getOrderDate();
                          final UserId userId = message.getOrderId()
                                                       .getUserId();
                          final OrderListId orderListId = OrderListId.newBuilder()
                                                                     .setOrderDate(date)
                                                                     .setUserId(userId)
                                                                     .build();
                          return singleton(orderListId);
                      });
        routing.route(DishRemovedFromOrder.class,
                      (message, context) -> {
                          final LocalDate date = message.getOrderId()
                                                        .getOrderDate();
                          final UserId userId = message.getOrderId()
                                                       .getUserId();
                          final OrderListId orderListId = OrderListId.newBuilder()
                                                                     .setOrderDate(date)
                                                                     .setUserId(userId)
                                                                     .build();
                          return singleton(orderListId);
                      });
        routing.route(OrderCanceled.class,
                      (message, context) -> {
                          final LocalDate date = message.getOrderId()
                                                        .getOrderDate();
                          final UserId userId = message.getOrderId()
                                                       .getUserId();
                          final OrderListId orderListId = OrderListId.newBuilder()
                                                                     .setOrderDate(date)
                                                                     .setUserId(userId)
                                                                     .build();
                          return singleton(orderListId);
                      });
        routing.route(OrderProcessed.class,
                      (message, context) -> {
                          final LocalDate date = message.getOrder()
                                                        .getId()
                                                        .getOrderDate();
                          final UserId userId = message.getOrder()
                                                       .getId()
                                                       .getUserId();
                          final OrderListId orderListId = OrderListId.newBuilder()
                                                                     .setOrderDate(date)
                                                                     .setUserId(userId)
                                                                     .build();
                          return singleton(orderListId);
                      });
    }
}
