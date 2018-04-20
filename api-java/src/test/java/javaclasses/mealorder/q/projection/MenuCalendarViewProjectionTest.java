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

import javaclasses.mealorder.c.event.DateRangeForMenuSet;
import javaclasses.mealorder.q.MenuCalendarItem;
import javaclasses.mealorder.q.MenuCalendarViewProjection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static io.spine.server.projection.ProjectionEventDispatcher.dispatch;
import static javaclasses.mealorder.testdata.TestMealOrderEventFactory.VendorEvents.dateRangeForMenuSetInstance;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MenuCalendarViewProjectionTest extends ProjectionTest {
    private MenuCalendarViewProjection projection;

    @BeforeEach
    void setUp() {
        projection = new MenuCalendarViewProjection(MenuCalendarViewProjection.ID);
    }

    @Nested
    @DisplayName("DateRangeForMenuSet event should be interpreted by MenuCalendarViewProjection")
    class PurchaseOrderSentEvent {
        private final Logger logger = LoggerFactory.getLogger(PurchaseOrderSentEvent.class);

        @Test
        @DisplayName("Should set dates with menus")
        void addView() {
            final DateRangeForMenuSet dateRangeForMenuSet = dateRangeForMenuSetInstance();
            dispatch(projection, createEvent(dateRangeForMenuSet));
            final List<MenuCalendarItem> calendarItems = projection.getState()
                                                                   .getCalendarItemList();
            assertEquals(25, calendarItems.size());
        }
    }
}
