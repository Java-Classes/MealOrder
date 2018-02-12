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

import com.google.protobuf.Timestamp;
import io.spine.money.Currency;
import io.spine.money.Money;
import io.spine.net.EmailAddress;
import io.spine.time.LocalDate;
import io.spine.time.MonthOfYear;
import javaclasses.mealorder.Dish;
import javaclasses.mealorder.DishId;
import javaclasses.mealorder.MenuId;
import javaclasses.mealorder.Order;
import javaclasses.mealorder.OrderId;
import javaclasses.mealorder.PurchaseOrderId;
import javaclasses.mealorder.UserId;
import javaclasses.mealorder.VendorId;
import javaclasses.mealorder.c.command.CreatePurchaseOrder;

import java.util.ArrayList;
import java.util.List;

import static io.spine.Identifier.newUuid;
import static io.spine.time.Time.getCurrentTime;

/**
 * A factory of the purchase order commands for the test needs.
 *
 * @author Yegor Udovchenko
 */
public class TestPurchaseOrderCommandFactory {

    public static final VendorId VENDOR_ID = VendorId.newBuilder()
                                                     .setValue(newUuid())
                                                     .build();
    public static final LocalDate DATE = LocalDate
            .newBuilder()
            .setYear(2018)
            .setMonth(MonthOfYear.FEBRUARY)
            .setDay(12)
            .build();
    public static final PurchaseOrderId PURCHASE_ORDER_ID = PurchaseOrderId
            .newBuilder()
            .setVendorId(VENDOR_ID)
            .setPoDate(DATE)
            .build();
    public static final UserId USER_ID = UserId.newBuilder()
                                               .setEmail(EmailAddress
                                                                 .newBuilder()
                                                                 .setValue("example@example.net")
                                                                 .build())
                                               .build();
    public static final OrderId ORDER_ID = OrderId.newBuilder()
                                                  .setVendorId(VENDOR_ID)
                                                  .setUserId(USER_ID)
                                                  .setOrderDate(DATE)
                                                  .build();
    public static final MenuId MENU_ID = MenuId.newBuilder()
                                               .setVendorId(VENDOR_ID)
                                               .setWhenImported(getCurrentTime())
                                               .build();
    public static final DishId DISH_ID = DishId.newBuilder()
                                               .setMenuId(MENU_ID)
                                               .setSequentialNumber(1)
                                               .build();
    public static final Dish DISH = Dish.newBuilder()
                                        .setId(DISH_ID)
                                        .setName("Dish")
                                        .setCategory("Category")
                                        .setPrice(Money.newBuilder()
                                                       .setCurrency(Currency.USD)
                                                       .setAmount(100)
                                                       .build())
                                        .build();
    public static final Order ORDER = Order.newBuilder()
                                           .setId(ORDER_ID)
                                           .addDishes(DISH)
                                           .build();

    public static final List<Order> ORDERS = new ArrayList<Order>() {{
        add(ORDER);
    }};

    private TestPurchaseOrderCommandFactory() {
    }

    /**
     * Provides a pre-configured {@link CreatePurchaseOrder} instance.
     *
     * @return the {@code CreatePurchaseOrder} instance
     */
    public static CreatePurchaseOrder createPurchaseOrderInstance() {
        final CreatePurchaseOrder result = CreatePurchaseOrder.newBuilder()
                                                              .setId(PURCHASE_ORDER_ID)
                                                              .setWhoCreates(USER_ID)
                                                              .addAllOrders(ORDERS)
                                                              .build();
        return result;
    }

    /**
     * Provides a pre-configured {@link CreatePurchaseOrder} instance.
     *
     * @param id an identifier of the created purchase order
     * @return the {@code CreatePurchaseOrder} instance
     */
    public static CreatePurchaseOrder createPurchaseOrderInstance(PurchaseOrderId id) {
        final MenuId menuId = MenuId.newBuilder()
                                    .setVendorId(id.getVendorId())
                                    .setWhenImported(Timestamp.getDefaultInstance())
                                    .build();
        final DishId dishId = DishId.newBuilder()
                                    .setMenuId(menuId)
                                    .setSequentialNumber(1)
                                    .build();
        final Dish dish = Dish.newBuilder()
                              .setId(dishId)
                              .setName("Dish")
                              .setCategory("Category")
                              .setPrice(Money.newBuilder()
                                             .setCurrency(Currency.USD)
                                             .setAmount(100)
                                             .build())
                              .build();
        final OrderId orderId = OrderId.newBuilder()
                                       .setVendorId(id.getVendorId())
                                       .setOrderDate(id.getPoDate())
                                       .setUserId(USER_ID)
                                       .build();
        final Order order = Order.newBuilder()
                                 .setId(orderId)
                                 .addDishes(dish)
                                 .build();
        final CreatePurchaseOrder result = CreatePurchaseOrder.newBuilder()
                                                              .setId(id)
                                                              .setWhoCreates(USER_ID)
                                                              .addOrders(order)
                                                              .build();
        return result;
    }
}
