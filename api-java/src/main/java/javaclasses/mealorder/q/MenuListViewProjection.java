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
import io.spine.time.LocalDate;
import javaclasses.mealorder.MenuDateRange;
import javaclasses.mealorder.MenuListId;
import javaclasses.mealorder.VendorName;
import javaclasses.mealorder.c.event.MenuImported;
import javaclasses.mealorder.c.event.PurchaseOrderCreated;
import javaclasses.mealorder.q.projection.MenuListView;
import javaclasses.mealorder.q.projection.MenuListViewVBuilder;

import java.util.List;

import static javaclasses.mealorder.q.Projections.getAllDishesByCategories;
import static javaclasses.mealorder.q.Projections.getDatesBetween;

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

        final MenuDateRange menuDateRange = event.getMenuDateRange();
        final List<java.time.LocalDate> datesBetween = getDatesBetween(
                menuDateRange.getRangeStart(), menuDateRange.getRangeEnd());
        datesBetween.forEach(date -> {
            final LocalDate localDate = LocalDate.newBuilder()
                                                 .setDay(date.getDayOfMonth())
                                                 .setMonthValue(date.getMonthValue())
                                                 .setYear(date.getYear())
                                                 .build();
            final MenuItem menuItem = MenuItem.newBuilder()
                                              .setVendorName(vendorName)
                                              .addAllDishesByCategory(allDishesByCategories)
                                              .setMenuDate(localDate)
                                              .setIsAvilable(true)
                                              .build();
            getBuilder().addMenu(menuItem);
        });

    }

    @Subscribe
    void on(PurchaseOrderCreated event) {
        final LocalDate poDate = event.getId()
                                      .getPoDate();
        getBuilder().getMenu()
                    .forEach(menuItem -> {
                        if (menuItem.getMenuDate()
                                    .equals(poDate)) {
                            final MenuItem newMenuItem = MenuItem.newBuilder(menuItem)
                                                                 .setIsAvilable(false)
                                                                 .build();
                            final int index = getBuilder().getMenu()
                                                          .indexOf(menuItem);
                            getBuilder().setMenu(index, newMenuItem);
                        }
                    });

    }

}
