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

import com.google.protobuf.Message;
import io.spine.server.aggregate.AggregateRepository;
import io.spine.server.route.EventRoute;
import javaclasses.mealorder.Order;
import javaclasses.mealorder.OrderId;
import javaclasses.mealorder.c.event.PurchaseOrderCanceled;
import javaclasses.mealorder.c.event.PurchaseOrderCreated;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Repository for the {@link OrderAggregate}.
 *
 * @author Vlad Kozachenko
 */
public class OrderRepository extends AggregateRepository<OrderId, OrderAggregate> {

    public OrderRepository() {
        setUpEventRouting();
    }

    /**
     * Sets up event routing for aggregate.
     * Route events to aggregate instances with correct ids.
     *
     * Extract order ids from received events on which the order aggregate
     * have to react. Checks if the instances with this ids already created.
     * And routes events to correct instances.
     */
    private void setUpEventRouting() {
        getEventRouting().replaceDefault((EventRoute<OrderId, Message>) (message, context) -> {
            if (message instanceof PurchaseOrderCreated) {
                final PurchaseOrderCreated purchaseOrderCreated = (PurchaseOrderCreated) message;
                final Set<OrderId> orderIds =
                        filterOrderIds(purchaseOrderCreated.getOrderList());
                return orderIds;
            }
            final PurchaseOrderCanceled purchaseOrderCanceled = (PurchaseOrderCanceled) message;
            final Set<OrderId> orderIds =
                    filterOrderIds(purchaseOrderCanceled.getOrderList());
            return orderIds;
        });
    }

    /**
     * Extracts {@code OrderId} values from list of orders.
     * Checks each order whether it is in repository.
     *
     * @param orderList list of orders where ids may be extracted.
     * @return set of found ids.
     */
    private Set<OrderId> filterOrderIds(List<Order> orderList) {
        final Set<OrderId> orderIdSet =
                orderList.stream()
                         .filter(d -> find(d.getId()).isPresent())
                         .map(Order::getId)
                         .collect(Collectors.toSet());
        return orderIdSet;
    }
}
