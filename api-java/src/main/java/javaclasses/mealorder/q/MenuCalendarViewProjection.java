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
import javaclasses.mealorder.MenuId;
import javaclasses.mealorder.VendorId;
import javaclasses.mealorder.c.event.DateRangeForMenuSet;
import javaclasses.mealorder.q.projection.MenuCalendarView;
import javaclasses.mealorder.q.projection.MenuCalendarViewVBuilder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class MenuCalendarViewProjection extends Projection<MenuId, MenuCalendarView, MenuCalendarViewVBuilder> {

    /**
     * {@link MenuCalendarViewProjection} is a singleton.
     *
     * <p>The {@code ID} value should be the same for all JVMs
     * to support work with the same projection from execution to execution.
     */
    public static final VendorId ID = VendorId.newBuilder()
                                              .setValue("MenuCalendarViewProjectionSingleton")
                                              .build();

    /**
     * Creates a new instance.
     *
     * @param id the ID for the new instance
     * @throws IllegalArgumentException if the ID is not of one of the supported types
     */
    public MenuCalendarViewProjection(MenuId id) {
        super(id);
    }

    @Subscribe
    void on(DateRangeForMenuSet event) {
        final MenuDateRange menuDateRange = event.getMenuDateRange();
        Date start = new Date(menuDateRange.getRangeStart()
                                           .getYear(), menuDateRange.getRangeStart()
                                                                    .getMonth()
                                                                    .getNumber(),
                              menuDateRange.getRangeStart()
                                           .getDay());
        Date end = new Date(menuDateRange.getRangeEnd()
                                         .getYear(), menuDateRange.getRangeEnd()
                                                                  .getMonth()
                                                                  .getNumber(),
                            menuDateRange.getRangeEnd()
                                         .getDay());
        final List<Date> datesBetween = getDatesBetween(start, end);
        datesBetween.forEach(date -> {
            getBuilder().addCalendarItem(MenuCalendarItem.newBuilder()
                                                         .setDate(toLocalDate(date))
                                                         .setHasMenu(true)
                                                         .build());
        });
    }

    private LocalDate toLocalDate(Date date) {
        return LocalDate.newBuilder()
                        .setDay(date.getDay())
                        .setMonthValue(date.getMonth())
                        .setYear(date.getYear())
                        .build();
    }

    private List<Date> getDatesBetween(
            Date startDate, Date endDate) {
        List<Date> datesInRange = new ArrayList<>();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(startDate);

        Calendar endCalendar = new GregorianCalendar();
        endCalendar.setTime(endDate);

        while (calendar.before(endCalendar)) {
            Date result = calendar.getTime();
            datesInRange.add(result);
            calendar.add(Calendar.DATE, 1);
        }
        return datesInRange;
    }
}