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

package javaclasses.mealorder.q.projection;

import javaclasses.mealorder.MenuListId;
import javaclasses.mealorder.c.event.MenuImported;
import javaclasses.mealorder.c.event.PurchaseOrderCreated;
import javaclasses.mealorder.q.MenuItem;
import javaclasses.mealorder.q.MenuListViewProjection;
import javaclasses.mealorder.testdata.TestValues;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.spine.server.projection.ProjectionEventDispatcher.dispatch;
import static javaclasses.mealorder.testdata.TestMealOrderEventFactory.PurchaseOrderEvents.purchaseOrderCreatedInstance2;
import static javaclasses.mealorder.testdata.TestMealOrderEventFactory.VendorEvents.menuImportedInstance;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MenuListViewProjectionTest extends ProjectionTest {
    private MenuListViewProjection projection;

    @BeforeEach
    void setUp() {
        projection = new MenuListViewProjection(MenuListId.newBuilder()
                                                          .setDate(TestValues.DATE)
                                                          .build());
    }

    @Nested
    @DisplayName("MenuImported event should be interpreted by MenuListViewProjection")
    class MenuImportedEvent {
        @Test
        @DisplayName("Should import menu")
        void addView() {
            final MenuImported menuImported = menuImportedInstance();
            dispatch(projection, createEvent(menuImported));
            final List<MenuItem> menuList = projection.getState()
                                                      .getMenuList();
            assertEquals(menuList.get(1)
                                 .getDishesByCategoryList(), menuList.get(0)
                                                                     .getDishesByCategoryList());
            assertEquals(25, menuList.size());
            assertEquals(18, menuList.get(5)
                                     .getMenuDate()
                                     .getDay());
            assertEquals(2017, menuList.get(5)
                                       .getMenuDate()
                                       .getYear());
            assertEquals(2, menuList.get(5)
                                    .getMenuDate()
                                    .getMonthValue());
            assertEquals(true, menuList.get(5)
                                       .getIsAvilable());
        }

        @Test
        @DisplayName("Should disable menu")
        void addView2() {
            final MenuImported menuImported = menuImportedInstance();
            dispatch(projection, createEvent(menuImported));
            final PurchaseOrderCreated purchaseOrderCreated = purchaseOrderCreatedInstance2();
            dispatch(projection, createEvent(purchaseOrderCreated));
            assertFalse(projection.getState()
                                  .getMenuList()
                                  .get(18)
                                  .getIsAvilable());
            assertTrue(projection.getState()
                                 .getMenuList()
                                 .get(19)
                                 .getIsAvilable());
        }
    }
}

