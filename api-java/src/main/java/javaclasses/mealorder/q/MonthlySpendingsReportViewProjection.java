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
import io.spine.money.Money;
import io.spine.server.projection.Projection;
import javaclasses.mealorder.MonthlySpendingsReportId;
import javaclasses.mealorder.Dish;
import javaclasses.mealorder.Order;
import javaclasses.mealorder.PurchaseOrder;
import javaclasses.mealorder.UserId;
import javaclasses.mealorder.c.event.PurchaseOrderDelivered;
import javaclasses.mealorder.c.event.PurchaseOrderSent;
import javaclasses.mealorder.q.projection.MonthlySpendingsReportView;
import javaclasses.mealorder.q.projection.MonthlySpendingsReportViewVBuilder;

import java.util.ArrayList;
import java.util.List;

public class MonthlySpendingsReportViewProjection extends Projection<MonthlySpendingsReportId, MonthlySpendingsReportView, MonthlySpendingsReportViewVBuilder> {

    /**
     * Creates a new instance.
     *
     * @param id the ID for the new instance
     * @throws IllegalArgumentException if the ID is not of one of the supported types
     */
    public MonthlySpendingsReportViewProjection(MonthlySpendingsReportId id) {
        super(id);
    }

    @Subscribe
    void on(PurchaseOrderSent event) {
        final PurchaseOrder purchaseOrder = event.getPurchaseOrder();

        final List<UserOrderDetails> userOrderDetailsList = new ArrayList<>();
        final List<DishItem> dishItemList = new ArrayList<>();
        final List<Order> orders = purchaseOrder.getOrderList();

        for (int i = 0; i < orders.size(); i++) {
            final UserId userId = orders.get(i)
                                        .getId()
                                        .getUserId();
            final List<Dish> dishList = orders.get(i)
                                              .getDishList();
            for (int j = 0; j < dishList.size(); j++) {
                final DishItem dishItem = DishItem.newBuilder()
                                                  .setId(dishList.get(j)
                                                                 .getId())
                                                  .setName(dishList.get(j)
                                                                   .getName())
                                                  .setPrice(Money.newBuilder()
                                                                 .setAmount(dishList.get(j)
                                                                                    .getPrice()
                                                                                    .getAmount())
                                                                 .build())
                                                  .build();
                dishItemList.add(dishItem);
            }
            final UserOrderDetails userOrderDetails = UserOrderDetails.newBuilder()
                                                                      .setId(userId)
                                                                      .addAllDish(dishItemList)
                                                                      .build();
            userOrderDetailsList.add(userOrderDetails);
        }
        getBuilder().addAllOrder(userOrderDetailsList);

    }

    @Subscribe
    void on(PurchaseOrderDelivered event) {
        final List<UserSpendings> userSpendingsList = new ArrayList<>();

        for (int i = 0; i < getBuilder().getOrder()
                                        .size(); i++) {

            long money = 0;
            for (int j = 0; j < getBuilder().getOrder()
                                            .get(i)
                                            .getDishList()
                                            .size(); j++) {
                money += getBuilder().getOrder()
                                     .get(j)
                                     .getDish(j)
                                     .getPrice()
                                     .getAmount();
            }

            final Money usersMoney = Money.newBuilder()
                                          .setAmount(money)
                                          .build();

            final UserSpendings userSpendings = UserSpendings.newBuilder()
                                                             .setId(getBuilder().getOrder()
                                                                                .get(i)
                                                                                .getId())
                                                             .setAmount(usersMoney)
                                                             .build();
            userSpendingsList.add(userSpendings);
        }
        getBuilder().addAllUserSpending(userSpendingsList);
    }
}
