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

package javaclasses.mealorder.c.repository;

import com.google.protobuf.Message;
import io.spine.server.aggregate.AggregateRepository;
import io.spine.server.route.EventRoute;
import javaclasses.mealorder.Order;
import javaclasses.mealorder.OrderId;
import javaclasses.mealorder.c.aggregate.OrderAggregate;
import javaclasses.mealorder.c.event.PurchaseOrderCanceled;
import javaclasses.mealorder.c.event.PurchaseOrderCreated;

import java.util.HashSet;
import java.util.Set;

/**
 * Repository for the {@link OrderAggregate}.
 *
 * @author Vlad Kozachenko
 */
public class OrderRepository extends AggregateRepository<OrderId, OrderAggregate> {

    public OrderRepository() {
        super();

        EventRoute<OrderId, Message> defaultRoute = getEventRouting().getDefault();
        getEventRouting().replaceDefault((EventRoute<OrderId, Message>) (message, context) -> {
            if (message instanceof PurchaseOrderCreated) {
                final PurchaseOrderCreated purchaseOrderCreated = (PurchaseOrderCreated) message;
                Set<OrderId> orderIds = new HashSet<>();
                for (Order order : purchaseOrderCreated.getOrderList()) {
                    if (find(order.getId()).isPresent()) {
                        orderIds.add(order.getId());
                    }
                }
                return orderIds;
            } else {
                final PurchaseOrderCanceled purchaseOrderCanceled = (PurchaseOrderCanceled) message;
                Set<OrderId> orderIds = new HashSet<>();
                for (Order order : purchaseOrderCanceled.getOrderList()) {
                    if (find(order.getId()).isPresent()) {
                        orderIds.add(order.getId());
                    }
                }
                return orderIds;
            }
        });
    }
}
