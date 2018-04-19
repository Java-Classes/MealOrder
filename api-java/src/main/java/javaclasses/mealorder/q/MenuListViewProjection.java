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
import javaclasses.mealorder.Dish;
import javaclasses.mealorder.MenuId;
import javaclasses.mealorder.VendorName;
import javaclasses.mealorder.c.event.DateRangeForMenuSet;
import javaclasses.mealorder.c.event.MenuImported;
import javaclasses.mealorder.c.event.PurchaseOrderCreated;
import javaclasses.mealorder.q.projection.MenuListView;
import javaclasses.mealorder.q.projection.MenuListViewVBuilder;

import java.util.List;

public class MenuListViewProjection extends Projection<MenuId, MenuListView, MenuListViewVBuilder> {

    /**
     * Creates a new instance.
     *
     * @param id the ID for the new instance
     * @throws IllegalArgumentException if the ID is not of one of the supported types
     */
    public MenuListViewProjection(MenuId id) {
        super(id);
    }

    @Subscribe
    void on(MenuImported event) {
        final VendorName vendorName = VendorName.newBuilder()
                                                .setValue(event.getVendorId()
                                                               .getValue())
                                                .build();
        final Iterable<? extends DishesByCategory> allDishesByCategories = getAllDishesByCategories(
                event.getDishList());
        final MenuItem menuItem = MenuItem.newBuilder()
                                          .setVendorName(vendorName)
                                          .setIsAvailable(true)
                                          .addAllDishesByCategory(allDishesByCategories)
                                          .build();
        getBuilder().addMenu(menuItem);
        // Todo : Pasha from Timestamp to LocalDate or change proto files
//        final MenuListId menuListId = MenuListId.newBuilder()
//                                           .setDate(event.getWhenImported()
//                                                         .getSeconds()))
//                                           .build();
//        getBuilder().setListId(menuListId);
    }


    @Subscribe
    void on(DateRangeForMenuSet event) {
        final List<MenuItem> menuItems = getBuilder().getMenu();
        final MenuItem menuItem = menuItems.get(menuItems.size() - 1);
        final MenuItem newMenuItem = MenuItem.newBuilder(menuItem)
                                             .setMenuDateRange(event.getMenuDateRange())
                                             .build();
        getBuilder().setMenu(menuItems.size() - 1, newMenuItem);
    }

    @Subscribe
    void on(PurchaseOrderCreated event) {
        //Todo: somehow disables menus
    }

    private Iterable<? extends DishesByCategory> getAllDishesByCategories(
            List<Dish> dishList) {
        return null;
    }
}
