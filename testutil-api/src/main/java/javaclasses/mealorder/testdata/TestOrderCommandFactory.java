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

import javaclasses.mealorder.Dish;
import javaclasses.mealorder.DishId;
import javaclasses.mealorder.MenuId;
import javaclasses.mealorder.OrderId;
import javaclasses.mealorder.c.command.AddDishToOrder;
import javaclasses.mealorder.c.command.CancelOrder;
import javaclasses.mealorder.c.command.CreateOrder;
import javaclasses.mealorder.c.command.RemoveDishFromOrder;

import static javaclasses.mealorder.testdata.TestValues.DISH1;
import static javaclasses.mealorder.testdata.TestValues.MENU_ID;
import static javaclasses.mealorder.testdata.TestValues.NONEXISTENT_MENU_ID;
import static javaclasses.mealorder.testdata.TestValues.ORDER_ID;
import static javaclasses.mealorder.testdata.TestValues.ORDER_ID_WITH_INVALID_VENDOR;

/**
 * @author Vlad Kozachenko
 */
public class TestOrderCommandFactory {

    public static CreateOrder createOrderInstanceForNonExistentMenu() {
        return createOrderInstance(ORDER_ID, NONEXISTENT_MENU_ID);
    }

    public static CreateOrder createOrderInstanceForNonExistentVendor() {
        return createOrderInstance(ORDER_ID_WITH_INVALID_VENDOR, MENU_ID);
    }

    public static CreateOrder createOrderInstance() {

        return createOrderInstance(ORDER_ID, MENU_ID);
    }

    public static CreateOrder createOrderInstance(OrderId orderId, MenuId menuId) {
        return CreateOrder.newBuilder()
                          .setOrderId(orderId)
                          .setMenuId(menuId)
                          .build();
    }

    public static CancelOrder cancelOrderInstance() {
        return cancelOrderInstance(ORDER_ID);
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

    public static AddDishToOrder addDishToOrderInstance() {
        return addDishToOrderInstance(ORDER_ID, DISH1);
    }

    public static RemoveDishFromOrder removeDishFromOrderInstance() {
        return removeDishFromOrderInstance(ORDER_ID, DISH1.getId());
    }

    public static RemoveDishFromOrder removeDishFromOrderInstance(OrderId orderId, DishId dishId) {
        return RemoveDishFromOrder.newBuilder()
                                  .setOrderId(orderId)
                                  .setDishId(dishId)
                                  .build();
    }
}
