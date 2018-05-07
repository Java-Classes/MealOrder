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
import javaclasses.mealorder.MenuId;
import javaclasses.mealorder.VendorName;
import javaclasses.mealorder.c.event.MenuImported;
import javaclasses.mealorder.q.projection.FullMenuView;
import javaclasses.mealorder.q.projection.FullMenuViewVBuilder;

import java.util.List;

import static javaclasses.mealorder.q.Projections.getAllDishesByCategories;

public class FullMenuViewProjection extends Projection<MenuId, FullMenuView, FullMenuViewVBuilder> {

    /**
     * Creates a new instance.
     *
     * @param id the ID for the new instance
     * @throws IllegalArgumentException if the ID is not of one of the supported types
     */
    public FullMenuViewProjection(MenuId id) {
        super(id);
    }

    @Subscribe
    public void on(MenuImported event) {
        final VendorName vendorName = VendorName.newBuilder()
                                                .setValue(event.getVendorId()
                                                               .getValue())
                                                .build();

        final List<DishesByCategory> allDishesByCategories = getAllDishesByCategories(
                event.getDishList());

        getBuilder().addAllDishesByCategory(allDishesByCategories);
        getBuilder().setVendorName(vendorName);
        getBuilder().setMenuId(event.getMenuId());
        getBuilder().setMenuDateRange(event.getMenuDateRange());
    }
}
