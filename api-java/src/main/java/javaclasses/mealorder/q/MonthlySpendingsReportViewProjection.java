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
import javaclasses.mealorder.Dish;
import javaclasses.mealorder.MonthlySpendingsReportId;
import javaclasses.mealorder.PurchaseOrder;
import javaclasses.mealorder.UserId;
import javaclasses.mealorder.c.event.PurchaseOrderDelivered;
import javaclasses.mealorder.c.event.PurchaseOrderSent;
import javaclasses.mealorder.q.projection.MonthlySpendingsReportView;
import javaclasses.mealorder.q.projection.MonthlySpendingsReportViewVBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.IntStream;

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
        final List<UserOrderDetails> userOrderDetailsList = new ArrayList<>();

        final PurchaseOrder purchaseOrder = event.getPurchaseOrder();
        purchaseOrder.getOrderList()
                     .forEach(order -> {
                         final List<DishItem> dishItemList = new ArrayList<>();
            final List<Dish> dishList = order.getDishList();
                         dishList.forEach(dish -> {
                final DishItem dishItem = DishItem.newBuilder()
                                                  .setId(dish.getId())
                                                  .setPrice(dish.getPrice())
                                                  .setName(dish.getName())
                                                  .build();
                dishItemList.add(dishItem);
                         });
                         final UserId userId = order.getId()
                                                    .getUserId();
            final UserOrderDetails userOrderDetails = UserOrderDetails.newBuilder()
                                                                      .setId(userId)
                                                                      .addAllDish(dishItemList)
                                                                      .setPurchaseOrderId(
                                                                              event.getPurchaseOrder()
                                                                                   .getId())
                                                                      .build();
            userOrderDetailsList.add(userOrderDetails);
                     });
        getBuilder().addAllOrder(userOrderDetailsList);
    }

    @Subscribe
    void on(PurchaseOrderDelivered event) {
        final List<UserOrderDetails> orderDetailsList = getBuilder().getOrder();
        orderDetailsList.forEach(userOrderDetails -> {
            if (userOrderDetails.getPurchaseOrderId()
                                .equals(event.getId())) {
                long money = 0;
                for (DishItem dishItem : userOrderDetails.getDishList()) {
                    money += dishItem.getPrice()
                                     .getAmount();
                }
                final int index = getUserSpendingsIndex(userOrderDetails.getId());
                if (index == -1) {
                    final Money cost = Money.newBuilder()
                                            .setAmount(money)
                                            .build();
                    final UserSpendings spendings = UserSpendings.newBuilder()
                                                                 .setAmount(cost)
                                                                 .setId(userOrderDetails.getId())
                                                                 .build();
                    getBuilder().addUserSpending(spendings);
                    getBuilder().removeOrder(getBuilder().getOrder()
                                                         .indexOf(userOrderDetails));
                } else {
                    final Money cost = Money.newBuilder()
                                            .setAmount(getBuilder().getUserSpending()
                                                                   .get(index)
                                                                   .getAmount()
                                                                   .getAmount() + money)
                                            .build();
                    final UserSpendings spendings = UserSpendings.newBuilder(
                            getBuilder().getUserSpending()
                                        .get(index))
                                                                 .setAmount(cost)
                                                                 .build();
                    getBuilder().setUserSpending(index, spendings);
                    getBuilder().removeOrder(getBuilder().getOrder()
                                                         .indexOf(userOrderDetails));
                }
            }
        });
    }

    private int getUserSpendingsIndex(UserId id) {
        final List<UserSpendings> userSpendingsList = getBuilder().getUserSpending();
        final OptionalInt optionalInt = IntStream.range(0, userSpendingsList.size())
                                                 .filter(i -> userSpendingsList.get(i)
                                                                               .getId()
                                                                               .equals(id))
                                                 .findFirst();
        return optionalInt.isPresent() ? optionalInt.getAsInt() : -1;
    }
}
