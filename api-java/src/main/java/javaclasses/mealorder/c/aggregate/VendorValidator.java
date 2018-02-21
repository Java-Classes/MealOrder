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

package javaclasses.mealorder.c.aggregate;

import io.spine.time.LocalDate;
import io.spine.time.MonthOfYear;
import javaclasses.mealorder.Menu;
import javaclasses.mealorder.MenuDateRange;
import javaclasses.mealorder.Vendor;

import java.util.Comparator;
import java.util.List;

/**
 * Validates values for vendor's commands.
 *
 * @author Yurii Haidamaka
 */
class VendorValidator {

    private VendorValidator() {
    }

    /**
     * Checks whether the date range doesn't contain dates from the past and the start date range is not greater than end date.
     *
     * @param menuDateRange date range to check
     */
    static boolean isValidDateRange(MenuDateRange menuDateRange) {

        final LocalDate startDateRange = menuDateRange.getRangeStart();
        final LocalDate endDateRange = menuDateRange.getRangeEnd();

        final java.time.LocalDate currentDateJava = java.time.LocalDate.now();
        final LocalDate currentDate = LocalDate.newBuilder()
                                               .setYear(currentDateJava.getYear())
                                               .setMonth(
                                                       MonthOfYear.valueOf(
                                                               currentDateJava.getMonthValue()))
                                               .setDay(currentDateJava.getDayOfMonth())
                                               .build();

        final Comparator<LocalDate> comparator = new LocalDateComparator();

        return comparator.compare(startDateRange, currentDate) >= 0
                && comparator.compare(startDateRange, endDateRange) <= 0;
    }

    /**
     * Checks whether the vendor doesn't have an available menu on this date range.
     *
     * @param vendor        aggregate vendor
     * @param menuDateRange date range to check
     */
    static boolean isThereMenuForThisDateRange(Vendor vendor, MenuDateRange menuDateRange) {
        final List<Menu> menus = vendor.getMenuList();
        return menus.stream()
                    .anyMatch(m -> areRangesOverlapping(menuDateRange, m.getMenuDateRange()));
    }

    /**
     * Checks whether two ranges overlap.
     *
     * @param menuDateRange1 the date range
     * @param menuDateRange2 the date range
     * @return boolean true if the date ranges are overlapped and false otherwise
     */
    private static boolean areRangesOverlapping(MenuDateRange menuDateRange1,
                                                MenuDateRange menuDateRange2) {

        final LocalDate startRange = menuDateRange1.getRangeStart();
        final LocalDate endRange = menuDateRange1.getRangeEnd();

        final Comparator<LocalDate> comparator = new LocalDateComparator();

        final boolean startResult =
                comparator.compare(startRange, menuDateRange2.getRangeStart()) >= 0
                        && comparator.compare(startRange, menuDateRange2.getRangeEnd()) <= 0;

        final boolean endResult =
                comparator.compare(endRange, menuDateRange2.getRangeStart()) >= 0
                        && comparator.compare(endRange, menuDateRange2.getRangeEnd()) <= 0;

        return startResult || endResult;
    }
}
