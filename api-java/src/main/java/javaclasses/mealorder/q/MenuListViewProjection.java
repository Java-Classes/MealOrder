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

import com.google.protobuf.Timestamp;
import io.spine.core.Subscribe;
import io.spine.server.projection.Projection;
import io.spine.time.LocalDate;
import javaclasses.mealorder.CategoryName;
import javaclasses.mealorder.Dish;
import javaclasses.mealorder.MenuDateRange;
import javaclasses.mealorder.MenuForDay;
import javaclasses.mealorder.MenuListId;
import javaclasses.mealorder.VendorName;
import javaclasses.mealorder.c.event.DateRangeForMenuSet;
import javaclasses.mealorder.c.event.MenuImported;
import javaclasses.mealorder.c.event.PurchaseOrderCreated;
import javaclasses.mealorder.q.projection.MenuListView;
import javaclasses.mealorder.q.projection.MenuListViewVBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.IntStream;

import static javaclasses.mealorder.q.Projections.getDatesBetween;
import static javaclasses.mealorder.q.Projections.timeStampToLocalDate;

public class MenuListViewProjection extends Projection<MenuListId, MenuListView, MenuListViewVBuilder> {

    /**
     * Creates a new instance.
     *
     * @param id the ID for the new instance
     * @throws IllegalArgumentException if the ID is not of one of the supported types
     */
    public MenuListViewProjection(MenuListId id) {
        super(id);
    }

    @Subscribe
    void on(MenuImported event) {
        final VendorName vendorName = VendorName.newBuilder()
                                                .setValue(event.getVendorId()
                                                               .getValue())
                                                .build();
        final List<DishesByCategory> allDishesByCategories = getAllDishesByCategories(
                event.getDishList());
        final MenuItem menuItem = MenuItem.newBuilder()
                                          .setVendorName(vendorName)
                                          .addAllDishesByCategory(allDishesByCategories)
                                          .build();
        getBuilder().addMenu(menuItem);

    }

    @Subscribe
    void on(DateRangeForMenuSet event) {
        final List<MenuItem> menuItems = getBuilder().getMenu();
        final MenuItem menuItem = menuItems.get(menuItems.size() - 1);
        final List<MenuForDay> menuDays = new ArrayList<>();

        final MenuDateRange menuDateRange = event.getMenuDateRange();
        final List<java.time.LocalDate> datesBetween = getDatesBetween(
                menuDateRange.getRangeStart(), menuDateRange.getRangeEnd());
        datesBetween.forEach(date -> {
            final LocalDate localDate = LocalDate.newBuilder()
                                                 .setDay(date.getDayOfMonth())
                                                 .setMonthValue(date.getMonthValue())
                                                 .setYear(date.getYear())
                                                 .build();
            final MenuForDay menuForDay = MenuForDay.newBuilder()
                                                    .setDate(localDate)
                                                    .setIsAvailable(true)
                                                    .build();
            menuDays.add(menuForDay);
        });

        final MenuItem newMenuItem = MenuItem.newBuilder(menuItem)
                                             .addAllMenuDays(menuDays)
                                             .build();
        getBuilder().setMenu(menuItems.size() - 1, newMenuItem);
    }

    @Subscribe
    void on(PurchaseOrderCreated event) {
        final List<MenuItem> newMenu = getBuilder().getMenu();
        for (int i = 0; i < newMenu.size(); i++) {
            final MenuItem menuItem = newMenu.get(i);
            final List<MenuForDay> menuDaysList = menuItem.getMenuDaysList();
            for (int j = 0; j < menuDaysList.size(); j++) {
                final MenuForDay menuForDay = menuDaysList.get(j);
                final Timestamp whenCreated = event.getWhenCreated();
                final LocalDate date = timeStampToLocalDate(whenCreated);
                if (menuForDay.getDate()
                              .equals(date)) {
                    final MenuForDay newMenuForDay = MenuForDay.newBuilder()
                                                               .setDate(date)
                                                               .setIsAvailable(false)
                                                               .build();
                    menuDaysList.set(j, newMenuForDay);
                }
            }
            final MenuItem newMenuItem = MenuItem.newBuilder(menuItem)
                                                 .clearMenuDays()
                                                 .addAllMenuDays(menuDaysList)
                                                 .build();
            newMenu.set(i, newMenuItem);
        }
        getBuilder().clearMenu()
                    .addAllMenu(newMenu);
    }

    private List<DishesByCategory> getAllDishesByCategories(
            List<Dish> dishList) {
        final List<DishesByCategory> dishesByCategoryList = new ArrayList<>();
        dishList.forEach(dish -> {
            final int index = categoryIndex(dishesByCategoryList, dish.getCategory());
            final DishItem newDish = DishItem.newBuilder()
                                             .setId(dish.getId())
                                             .setName(dish.getName())
                                             .setPrice(dish.getPrice())
                                             .build();
            if (index == -1) {
                final CategoryName category = CategoryName.newBuilder()
                                                          .setValue(dish.getCategory())
                                                          .build();

                final DishesByCategory dishesByCategory = DishesByCategory.newBuilder()
                                                                          .setCategory(category)
                                                                          .addDishes(newDish)
                                                                          .build();
                dishesByCategoryList.add(dishesByCategory);
            } else {
                final DishesByCategory newDishesByCategory = DishesByCategory.newBuilder(
                        dishesByCategoryList.get(index))
                                                                             .addDishes(newDish)
                                                                             .build();
                dishesByCategoryList.set(index, newDishesByCategory);
            }
        });
        return dishesByCategoryList;
    }

    private int categoryIndex(List<DishesByCategory> dishByCategory, String category) {
        final OptionalInt optionalInt = IntStream.range(0, dishByCategory.size())
                                                 .filter(i -> dishByCategory.get(i)
                                                                            .getCategory()
                                                                            .getValue()
                                                                            .equals(category))
                                                 .findFirst();
        return optionalInt.isPresent() ? optionalInt.getAsInt() : -1;
    }

}
