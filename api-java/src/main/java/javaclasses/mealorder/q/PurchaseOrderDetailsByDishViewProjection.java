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
import javaclasses.mealorder.Order;
import javaclasses.mealorder.PurchaseOrderId;
import javaclasses.mealorder.PurchaseOrderStatus;
import javaclasses.mealorder.c.event.PurchaseOrderCreated;
import javaclasses.mealorder.c.event.PurchaseOrderDelivered;
import javaclasses.mealorder.c.event.PurchaseOrderSent;
import javaclasses.mealorder.c.event.PurchaseOrderValidationFailed;
import javaclasses.mealorder.q.projection.PurchaseOrderDetailsByDishView;
import javaclasses.mealorder.q.projection.PurchaseOrderDetailsByDishViewVBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexander Karpets
 */
public class PurchaseOrderDetailsByDishViewProjection extends Projection<PurchaseOrderId, PurchaseOrderDetailsByDishView, PurchaseOrderDetailsByDishViewVBuilder> {

    /**
     * Creates a new instance.
     *
     * @param id the ID for the new instance
     * @throws IllegalArgumentException if the ID is not of one of the supported types
     */
    public PurchaseOrderDetailsByDishViewProjection(PurchaseOrderId id) {
        super(id);
    }

    @Subscribe
    void on(PurchaseOrderCreated event) {
        final List<DishItem> dishItems = getDishItems(event.getOrderList());
        getBuilder().addAllDish(dishItems);
        getBuilder().setId(event.getId());
    }

    @Subscribe
    void on(PurchaseOrderDelivered event) {
        getBuilder().setPurchaseOrderStatus(PurchaseOrderStatus.DELIVERED);
    }

    @Subscribe
    void on(PurchaseOrderSent event) {
        getBuilder().setPurchaseOrderStatus(PurchaseOrderStatus.SENT);
    }

    @Subscribe
    void on(PurchaseOrderValidationFailed event) {
        getBuilder().setPurchaseOrderStatus(PurchaseOrderStatus.INVALID);
    }

    private List<DishItem> getDishItems(List<Order> orderList) {
        List<DishItem> dishItems = new ArrayList<>();
        orderList.forEach(order ->
                                  order.getDishList()
                                       .forEach(dish -> {
                                           dishItems.add(DishItem.newBuilder()
                                                                 .setName(dish.getName())
                                                                 .setPrice(dish.getPrice())
                                                                 .setId(dish.getId())
                                                                 .build());
                                       })
        );
        return dishItems;
    }
}