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
import javaclasses.mealorder.VendorId;
import javaclasses.mealorder.c.command.AddDishToOrder;
import javaclasses.mealorder.c.command.CancelOrder;
import javaclasses.mealorder.c.command.CreateOrder;
import javaclasses.mealorder.c.command.RemoveDishFromOrder;

import static io.spine.time.MonthOfYear.FEBRUARY;
import static javaclasses.mealorder.testdata.TestVendorCommandFactory.NONEXISTENT_MENU_ID;
import static javaclasses.mealorder.testdata.TestVendorCommandFactory.USER_ID;
import static javaclasses.mealorder.testdata.TestVendorCommandFactory.VENDOR_ID;

/**
 * @author Vlad Kozachenko
 */
public class TestOrderCommandFactory {

    public static final LocalDate ORDER_DATE = LocalDate.newBuilder()
                                                        .setYear(
                                                                2019)
                                                        .setMonth(
                                                                FEBRUARY)
                                                        .setDay(15)
                                                        .build();

    public static final OrderId ORDER_ID = OrderId.newBuilder()
                                                  .setUserId(USER_ID)
                                                  .setVendorId(VENDOR_ID)
                                                  .setOrderDate(ORDER_DATE)
                                                  .build();

    public static final VendorId INVALID_VENDOR_ID = VendorId.newBuilder()
                                                     .setValue("vendor:INVALID")
                                                     .build();


    public static final DishId INVALID_DISH_ID = DishId.newBuilder()
                                               .setMenuId(MenuId.newBuilder()
                                                                .setVendorId(INVALID_VENDOR_ID)
                                                                .build())
                                               .setSequentialNumber(1)
                                               .build();

    public static final Dish INVALID_DISH = Dish.newBuilder()
                                        .setId(INVALID_DISH_ID)
                                        .build();

    public static CreateOrder createOrderInstanceForNonExistenMenu() {
        return createOrderInstance(ORDER_ID, NONEXISTENT_MENU_ID);
    }

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
