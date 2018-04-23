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
import io.spine.money.Money;
import io.spine.net.EmailAddress;
import io.spine.time.LocalDate;
import io.spine.time.LocalTime;
import javaclasses.mealorder.Dish;
import javaclasses.mealorder.DishId;
import javaclasses.mealorder.MenuDateRange;
import javaclasses.mealorder.MenuId;
import javaclasses.mealorder.Order;
import javaclasses.mealorder.OrderId;
import javaclasses.mealorder.PhoneNumber;
import javaclasses.mealorder.PurchaseOrderId;
import javaclasses.mealorder.UserId;
import javaclasses.mealorder.VendorChange;
import javaclasses.mealorder.VendorId;
import javaclasses.mealorder.VendorName;

import java.util.ArrayList;
import java.util.List;

import static io.spine.time.MonthOfYear.FEBRUARY;
import static io.spine.time.Time.getCurrentTime;
import static javaclasses.mealorder.OrderStatus.ORDER_ACTIVE;

/**
 * The utility class that provides constant instances of
 * value objects and entities of the domain model. These
 * values are used for test needs upon creation of command
 * instances.
 *
 * @author Yegor Udovchenko
 */
public class TestValues {
    public static final VendorName VENDOR_NAME = VendorName.newBuilder()
                                                           .setValue("VendorName1")
                                                           .build();

    public static final VendorName VENDOR_NAME2 = VendorName.newBuilder()
                                                            .setValue("Позитив")
                                                            .build();

    public static final VendorId VENDOR_ID = VendorId.newBuilder()
                                                     .setValue("vendor:" + VENDOR_NAME)
                                                     .build();

    public static final VendorId VENDOR_ID2 = VendorId.newBuilder()
                                                      .setValue("vendor:" + VENDOR_NAME2)
                                                      .build();

    public static final VendorName NEW_VENDOR_NAME = VendorName.newBuilder()
                                                               .setValue("VendorName2")
                                                               .build();

    public static final EmailAddress EMAIL = EmailAddress.newBuilder()
                                                         .setValue("vendor1@example.com")
                                                         .build();

    public static final UserId USER_ID = UserId.newBuilder()
                                               .setEmail(EmailAddress.newBuilder()
                                                                     .setValue(
                                                                             "user@example.com")
                                                                     .build())
                                               .build();

    public static final UserId USER_ID2 = UserId.newBuilder()
                                                .setEmail(EmailAddress.newBuilder()
                                                                      .setValue(
                                                                              "petr@example.com")
                                                                      .build())
                                                .build();

    public static final LocalTime PO_DAILY_DEADLINE = LocalTime.newBuilder()
                                                               .setHours(10)
                                                               .setMinutes(0)
                                                               .build();

    public static final PhoneNumber PHONE_NUMBER1 = PhoneNumber.newBuilder()
                                                               .setValue("1234567890")
                                                               .build();

    public static final PhoneNumber PHONE_NUMBER2 = PhoneNumber.newBuilder()
                                                               .setValue("0987654321")
                                                               .build();

    public static final VendorChange VENDOR_CHANGE = VendorChange.newBuilder()
                                                                 .setPreviousVendorName(VENDOR_NAME)
                                                                 .setPreviousEmail(EMAIL)
                                                                 .addPreviousPhoneNumber(
                                                                         PHONE_NUMBER1)
                                                                 .addPreviousPhoneNumber(
                                                                         PHONE_NUMBER2)
                                                                 .setPreviousPoDailyDeadline(
                                                                         PO_DAILY_DEADLINE)
                                                                 .setNewVendorName(NEW_VENDOR_NAME)
                                                                 .setNewEmail(EMAIL)
                                                                 .addNewPhoneNumber(PHONE_NUMBER1)
                                                                 .addNewPhoneNumber(PHONE_NUMBER2)
                                                                 .setPreviousPoDailyDeadline(
                                                                         PO_DAILY_DEADLINE)
                                                                 .build();

    public static final MenuId MENU_ID = MenuId.newBuilder()
                                               .setVendorId(VENDOR_ID)
                                               .setWhenImported(getCurrentTime())
                                               .build();

    public static final MenuId MENU_ID2 = MenuId.newBuilder()
                                                .setVendorId(VENDOR_ID2)
                                                .setWhenImported(getCurrentTime())
                                                .build();

    public static final MenuId NONEXISTENT_MENU_ID = MenuId.newBuilder()
                                                           .setVendorId(VENDOR_ID)
                                                           .setWhenImported(
                                                                   Timestamp.getDefaultInstance())
                                                           .build();

    public static final LocalDate START_DATE = LocalDate.newBuilder()
                                                        .setYear(2019)
                                                        .setMonthValue(2)
                                                        .setDay(13)
                                                        .build();

    public static final LocalDate END_DATE = LocalDate.newBuilder()
                                                      .setYear(2019)
                                                      .setMonthValue(2)
                                                      .setDay(21)
                                                      .build();

    public static final MenuDateRange MENU_DATE_RANGE = MenuDateRange.newBuilder()
                                                                     .setRangeStart(START_DATE)
                                                                     .setRangeEnd(END_DATE)
                                                                     .build();

    public static final LocalDate START_DATE_FROM_PAST = LocalDate.newBuilder()
                                                                  .setYear(2017)
                                                                  .setMonthValue(2)
                                                                  .setDay(13)
                                                                  .build();

    public static final LocalDate END_DATE_FROM_PAST = LocalDate.newBuilder()
                                                                .setYear(2017)
                                                                .setMonthValue(2)
                                                                .setDay(19)
                                                                .build();
    public static final LocalDate END_DATE_FROM_PAST2 = LocalDate.newBuilder()
                                                                 .setYear(2017)
                                                                 .setMonthValue(3)
                                                                 .setDay(10)
                                                                 .build();

    public static final LocalDate INVALID_START_DATE = LocalDate.newBuilder()
                                                                .setYear(2020)
                                                                .setMonthValue(2)
                                                                .setDay(13)
                                                                .build();

    public static final LocalDate INVALID_END_DATE = LocalDate.newBuilder()
                                                              .setYear(2018)
                                                              .setMonthValue(2)
                                                              .setDay(19)
                                                              .build();

    public static final MenuDateRange INVALID_MENU_DATE_RANGE = MenuDateRange.newBuilder()
                                                                             .setRangeStart(
                                                                                     INVALID_START_DATE)
                                                                             .setRangeEnd(
                                                                                     INVALID_END_DATE)
                                                                             .build();
    public static final MenuDateRange MENU_DATE_RANGE2 = MenuDateRange.newBuilder()
                                                                      .setRangeStart(
                                                                              START_DATE_FROM_PAST)
                                                                      .setRangeEnd(
                                                                              END_DATE_FROM_PAST2)
                                                                      .build();

    public static final MenuDateRange MENU_DATE_RANGE_FROM_PAST = MenuDateRange.newBuilder()
                                                                               .setRangeStart(
                                                                                             START_DATE_FROM_PAST)
                                                                               .setRangeEnd(
                                                                                             END_DATE_FROM_PAST)
                                                                               .build();

    public static final Dish DISH1 = Dish.newBuilder()
                                         .setId(DishId.newBuilder()
                                                      .setMenuId(MENU_ID)
                                                      .setSequentialNumber(1)
                                                      .build())
                                         .setName("dishName1")
                                         .setCategory("category")
                                         .setPrice(Money.getDefaultInstance())
                                         .build();

    public static final Dish DISH2 = Dish.newBuilder()
                                         .setId(DishId.newBuilder()
                                                      .setMenuId(MENU_ID)
                                                      .setSequentialNumber(2)
                                                      .build())
                                         .setName("dishName2")
                                         .setCategory("category")
                                         .setPrice(Money.getDefaultInstance())
                                         .build();

    public static final Dish DISH3 = Dish.newBuilder()
                                         .setId(DishId.newBuilder()
                                                      .setMenuId(MENU_ID2)
                                                      .setSequentialNumber(1)
                                                      .build())
                                         .setName("Картофель")
                                         .setCategory("Второе блюдо")
                                         .setPrice(Money.newBuilder()
                                                        .setAmount(56)
                                                        .build())
                                         .build();
    public static final Dish DISH4 = Dish.newBuilder()
                                         .setId(DishId.newBuilder()
                                                      .setMenuId(MENU_ID2)
                                                      .setSequentialNumber(1)
                                                      .build())
                                         .setName("Борщ украинский")
                                         .setCategory("Первое блюдо")
                                         .setPrice(Money.newBuilder()
                                                        .setAmount(33)
                                                        .build())
                                         .build();
    public static final Dish DISH5 = Dish.newBuilder()
                                         .setId(DishId.newBuilder()
                                                      .setMenuId(MENU_ID2)
                                                      .setSequentialNumber(1)
                                                      .build())
                                         .setName("Стейк из лосося")
                                         .setCategory("Второе блюдо")
                                         .setPrice(Money.newBuilder()
                                                        .setAmount(80)
                                                        .build())
                                         .build();
    public static final Dish DISH6 = Dish.newBuilder()
                                         .setId(DishId.newBuilder()
                                                      .setMenuId(MENU_ID2)
                                                      .setSequentialNumber(1)
                                                      .build())
                                         .setName("Компот из сухофруктов")
                                         .setCategory("Напитки")
                                         .setPrice(Money.newBuilder()
                                                        .setAmount(10)
                                                        .build())
                                         .build();
    public static final Dish DISH7 = Dish.newBuilder()
                                         .setId(DishId.newBuilder()
                                                      .setMenuId(MENU_ID2)
                                                      .setSequentialNumber(1)
                                                      .build())
                                         .setName("Скумбрия на гриле")
                                         .setCategory("Второе блюдо")
                                         .setPrice(Money.newBuilder()
                                                        .setAmount(10)
                                                        .build())
                                         .build();
    public static final Dish DISH8 = Dish.newBuilder()
                                         .setId(DishId.newBuilder()
                                                      .setMenuId(MENU_ID2)
                                                      .setSequentialNumber(1)
                                                      .build())
                                         .setName("Суп гороховый")
                                         .setCategory("Первое блюдо")
                                         .setPrice(Money.newBuilder()
                                                        .setAmount(10)
                                                        .build())
                                         .build();

    public static final LocalDate DATE = LocalDate.newBuilder()
                                                  .setYear(
                                                          2019)
                                                  .setMonth(
                                                          FEBRUARY)
                                                  .setDay(15)
                                                  .build();

    public static final OrderId ORDER_ID = OrderId.newBuilder()
                                                  .setUserId(USER_ID)
                                                  .setVendorId(VENDOR_ID)
                                                  .setOrderDate(DATE)
                                                  .build();

    public static final VendorId INVALID_VENDOR_ID = VendorId.newBuilder()
                                                             .setValue("vendor:INVALID")
                                                             .build();

    public static final OrderId ORDER_ID_WITH_INVALID_VENDOR = OrderId.newBuilder()
                                                                      .setUserId(USER_ID)
                                                                      .setVendorId(
                                                                              INVALID_VENDOR_ID)
                                                                      .setOrderDate(DATE)
                                                                      .build();

    public static final OrderId ORDER_ID_WITH_INVALID_DATE = OrderId.newBuilder()
                                                                    .setUserId(USER_ID)
                                                                    .setVendorId(VENDOR_ID)
                                                                    .setOrderDate(
                                                                            INVALID_START_DATE)
                                                                    .build();

    public static final DishId INVALID_DISH_ID = DishId.newBuilder()
                                                       .setMenuId(MenuId.newBuilder()
                                                                        .setVendorId(
                                                                                INVALID_VENDOR_ID)
                                                                        .build())
                                                       .setSequentialNumber(1)
                                                       .build();

    public static final Dish INVALID_DISH = Dish.newBuilder()
                                                .setId(INVALID_DISH_ID)
                                                .build();

    public static final PurchaseOrderId PURCHASE_ORDER_ID = PurchaseOrderId
            .newBuilder()
            .setVendorId(VENDOR_ID)
            .setPoDate(DATE)
            .build();

    public static final Order ORDER = Order.newBuilder()
                                           .setId(ORDER_ID)
                                           .addDish(DISH1)
                                           .setStatus(ORDER_ACTIVE)
                                           .build();
    public static final List<Order> BIG_ORDER = createOrder();
    public static final List<Dish> BIG_MENU = createMenu();

    private static List<Dish> createMenu() {
        final List<Dish> menu = new ArrayList<>();
        menu.add(DISH3);
        menu.add(DISH4);
        menu.add(DISH5);
        menu.add(DISH6);
        menu.add(DISH7);
        menu.add(DISH8);

        return menu;
    }

    private static List<Order> createOrder() {
        List<Order> orders = new ArrayList<>();
        final Order order1 = Order.newBuilder()
                                  .addDish(DISH1)
                                  .addDish(DISH1)
                                  .addDish(DISH1)
                                  .setId(
                                          OrderId.newBuilder()
                                                 .setUserId(USER_ID)
                                                 .setVendorId(VENDOR_ID)
                                                 .setOrderDate(END_DATE)
                                                 .build())
                                  .build();
        final Order order2 = Order.newBuilder()
                                  .addDish(DISH1)
                                  .addDish(DISH1)
                                  .addDish(DISH2)
                                  .addDish(DISH2)
                                  .addDish(DISH2)
                                  .setId(
                                          OrderId.newBuilder()
                                                 .setUserId(USER_ID2)
                                                 .setVendorId(VENDOR_ID)
                                                 .setOrderDate(END_DATE)
                                                 .build())
                                  .build();
        final Order order3 = Order.newBuilder()
                                  .addDish(DISH2)
                                  .addDish(DISH2)
                                  .setId(
                                          OrderId.newBuilder()
                                                 .setUserId(USER_ID)
                                                 .setVendorId(VENDOR_ID2)
                                                 .setOrderDate(END_DATE)
                                                 .build())
                                  .build();
        final Order order4 = Order.newBuilder()
                                  .addDish(DISH1)
                                  .addDish(DISH1)
                                  .addDish(DISH2)
                                  .setId(
                                          OrderId.newBuilder()
                                                 .setUserId(USER_ID2)
                                                 .setVendorId(VENDOR_ID2)
                                                 .setOrderDate(END_DATE)
                                                 .build())
                                  .build();
        orders.add(order1);
        orders.add(order2);
        orders.add(order3);
        orders.add(order4);
        return orders;
    }
}
