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

package javaclasses.mealorder.testdata;

import io.spine.time.LocalDate;
import javaclasses.mealorder.Dish;
import javaclasses.mealorder.DishId;
import javaclasses.mealorder.MenuId;
import javaclasses.mealorder.OrderId;
import javaclasses.mealorder.UserId;
import javaclasses.mealorder.VendorId;
import javaclasses.mealorder.c.command.AddDishToOrder;
import javaclasses.mealorder.c.command.CancelOrder;
import javaclasses.mealorder.c.command.CreateOrder;
import javaclasses.mealorder.c.command.RemoveDishFromOrder;

import static io.spine.time.MonthOfYear.FEBRUARY;

/**
 * @author Vlad Kozachenko
 */
public class TestOrderCommandFactory {

    private static final String defaultVendorName = "Positiv";

    public static final VendorId VENDOR_ID = VendorId.newBuilder()
                                                     .setValue(defaultVendorName)
                                                     .build();

    public static final OrderId ORDER_ID = OrderId.newBuilder()
                                                  .setUserId(UserId.getDefaultInstance())
                                                  .setVendorId(VENDOR_ID)
                                                  .setOrderDate(LocalDate.newBuilder()
                                                                         .setYear(2018)
                                                                         .setMonth(FEBRUARY)
                                                                         .setDay(12)
                                                                         .build())
                                                  .build();

    public static final DishId DISH_ID = DishId.newBuilder()
                                               .setMenuId(MenuId.newBuilder()
                                                                .setVendorId(VENDOR_ID)
                                                                .build())
                                               .setSequentialNumber(1)
                                               .build();

    public static final Dish DISH = Dish.newBuilder()
                                        .setId(DISH_ID)
                                        .build();

    public static final LocalDate ORDER_DATE = LocalDate.newBuilder()
                                                        .setYear(
                                                                2018)
                                                        .setMonth(
                                                                FEBRUARY)
                                                        .setDay(12)
                                                        .build();

    public static CreateOrder createOrderInstance(OrderId orderId, MenuId menuId) {
        return CreateOrder.newBuilder()
                          .setOrderId(orderId)
                          .setMenuId(menuId)
                          .build();
    }

    public static CancelOrder cancelOrderInstance(OrderId orderId) {
        return CancelOrder.newBuilder()
                          .setOrderId(orderId)
                          .build();
    }

    public static AddDishToOrder addDishToOrderInstance(OrderId orderId, Dish dish) {
        return AddDishToOrder.newBuilder()
                             .setOrderId(orderId)
                             .setDish(dish)
                             .build();
    }

    public static RemoveDishFromOrder removeDishFromOrderInstance(OrderId orderId, DishId dishId) {
        return RemoveDishFromOrder.newBuilder()
                                  .setOrderId(orderId)
                                  .setDishId(dishId)
                                  .build();
    }
}
