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
import javaclasses.mealorder.MenuCalendarId;
import javaclasses.mealorder.MenuDateRange;
import javaclasses.mealorder.c.event.DateRangeForMenuSet;
import javaclasses.mealorder.q.projection.MenuCalendarView;
import javaclasses.mealorder.q.projection.MenuCalendarViewVBuilder;

import java.time.LocalDate;
import java.util.List;

import static javaclasses.mealorder.q.Projections.getDatesBetween;
import static javaclasses.mealorder.q.Projections.toLocalDate;

public class MenuCalendarViewProjection extends Projection<MenuCalendarId, MenuCalendarView, MenuCalendarViewVBuilder> {

    /**
     * {@link MenuCalendarViewProjection} is a singleton.
     *
     * <p>The {@code ID} value should be the same for all JVMs
     * to support work with the same projection from execution to execution.
     */
    public static final MenuCalendarId ID = MenuCalendarId.newBuilder()
                                                          .setValue(
                                                                  "MenuCalendarViewProjectionSingleton")
                                                          .build();

    /**
     * Creates a new instance.
     *
     * @param id the ID for the new instance
     * @throws IllegalArgumentException if the ID is not of one of the supported types
     */
    public MenuCalendarViewProjection(MenuCalendarId id) {
        super(id);
    }

    @Subscribe
    void on(DateRangeForMenuSet event) {
        final List<LocalDate> datesBetween = getDatesBetween(event.getMenuDateRange()
                                                                  .getRangeStart(),
                                                             event.getMenuDateRange()
                                                                  .getRangeEnd());

        datesBetween.forEach(date -> {
            getBuilder().addCalendarItem(MenuCalendarItem.newBuilder()
                                                         .setDate(toLocalDate(date))
                                                         .setHasMenu(true)
                                                         .build());
        });
    }

}
