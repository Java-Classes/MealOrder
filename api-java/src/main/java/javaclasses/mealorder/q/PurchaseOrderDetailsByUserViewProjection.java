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
import javaclasses.mealorder.UserId;
import javaclasses.mealorder.c.event.PurchaseOrderCreated;
import javaclasses.mealorder.c.event.PurchaseOrderDelivered;
import javaclasses.mealorder.c.event.PurchaseOrderSent;
import javaclasses.mealorder.c.event.PurchaseOrderValidationFailed;
import javaclasses.mealorder.q.projection.PurchaseOrderDetailsByUserView;
import javaclasses.mealorder.q.projection.PurchaseOrderDetailsByUserViewVBuilder;

import java.util.ArrayList;
import java.util.List;

public class PurchaseOrderDetailsByUserViewProjection extends Projection<PurchaseOrderId, PurchaseOrderDetailsByUserView, PurchaseOrderDetailsByUserViewVBuilder> {

    /**
     * Creates a new instance.
     *
     * @param id the ID for the new instance
     * @throws IllegalArgumentException if the ID is not of one of the supported types
     */
    public PurchaseOrderDetailsByUserViewProjection(PurchaseOrderId id) {
        super(id);
    }

    @Subscribe
    void on(PurchaseOrderCreated event) {
        final List<UserOrderDetails> usersOrders = getUserOrders(event);
        getBuilder().addAllOrder(usersOrders);
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
        //todo set which failed
    }

    private List<UserOrderDetails> getUserOrders(PurchaseOrderCreated event) {
        final List<UserOrderDetails> dishes = new ArrayList<>();
        for (int i = 0; i < event.getOrderList()
                                 .size(); i++) {
            if (!userOrderDetailsHasUser(dishes, event.getOrderList()
                                                      .get(i)
                                                      .getId()
                                                      .getUserId())) {
                final UserId user = event.getOrderList()
                                         .get(i)
                                         .getId()
                                         .getUserId();
                final List<DishItem> dishItems = getDishItemsForUser(user, event.getOrderList());
                final UserOrderDetails dish = UserOrderDetails.newBuilder()
                                                              .setId(event.getOrderList()
                                                                          .get(i)
                                                                          .getId()
                                                                          .getUserId())
                                                              .addAllDish(dishItems)
                                                              .setIsValid(true)
                                                              .build();
                dishes.add(dish);
            }
        }
        return dishes;
    }

    private boolean userOrderDetailsHasUser(List<UserOrderDetails> dishes,
                                            UserId userId) {
        return dishes.stream()
                     .anyMatch(dish ->
                                       dish.getId()
                                           .equals(userId));
    }

    private List<DishItem> getDishItemsForUser(UserId user, List<Order> orderList) {
        final List<DishItem> dishItems = new ArrayList<>();
        orderList.forEach(order -> {
            if (order.getId()
                     .getUserId()
                     .equals(user)) {
                order.getDishList()
                     .forEach(dish -> {
                         final DishItem dishItem = DishItem.newBuilder()
                                                           .setId(dish.getId())
                                                           .setName(dish.getName())
                                                           .setPrice(dish.getPrice())
                                                           .build();
                         dishItems.add(dishItem);
                     });
            }
        });
        return dishItems;
    }
}
