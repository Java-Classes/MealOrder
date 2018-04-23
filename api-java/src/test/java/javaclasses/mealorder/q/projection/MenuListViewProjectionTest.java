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
import javaclasses.mealorder.c.event.DateRangeForMenuSet;
import javaclasses.mealorder.c.event.MenuImported;
import javaclasses.mealorder.q.MenuListViewProjection;
import javaclasses.mealorder.testdata.TestValues;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.spine.server.projection.ProjectionEventDispatcher.dispatch;
import static javaclasses.mealorder.testdata.TestMealOrderEventFactory.VendorEvents.dateRangeForMenuSetInstance;
import static javaclasses.mealorder.testdata.TestMealOrderEventFactory.VendorEvents.menuImportedInstance;

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
        }
    }

    @Nested
    @DisplayName("DateRangeForMenuSet event should be interpreted by MenuListViewProjection")
    class DateRangeForMenuSetEvent {
        @Test
        @DisplayName("Should set dates with menus")
        void addView() {
            final MenuImported menuImported = menuImportedInstance();
            dispatch(projection, createEvent(menuImported));
            final DateRangeForMenuSet dateRangeForMenuSet = dateRangeForMenuSetInstance();
            dispatch(projection, createEvent(dateRangeForMenuSet));
        }
    }
}

