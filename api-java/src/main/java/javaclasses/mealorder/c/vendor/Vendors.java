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

package javaclasses.mealorder.c.vendor;

import com.google.common.annotations.VisibleForTesting;
import io.spine.time.LocalDate;
import io.spine.time.MonthOfYear;
import javaclasses.mealorder.LocalDateComparator;
import javaclasses.mealorder.Menu;
import javaclasses.mealorder.MenuDateRange;
import javaclasses.mealorder.Vendor;

import java.util.Comparator;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Validates values for vendor's commands.
 *
 * @author Yurii Haidamaka
 */
public class Vendors {

    /** Prevents instantiation of this utility class. */
    private Vendors() {
    }

    /**
     * Checks whether the date range doesn't contain dates from the past
     * and the start date range is not greater than end date.
     *
     * @param menuDateRange date range to check
     */
    public static boolean isValidDateRange(MenuDateRange menuDateRange) {
        checkNotNull(menuDateRange);

        final LocalDate startDateRange = menuDateRange.getRangeStart();
        final LocalDate endDateRange = menuDateRange.getRangeEnd();

        final LocalDate currentDate = getCurrentDate();

        final Comparator<LocalDate> comparator = new LocalDateComparator();

        return comparator.compare(startDateRange, currentDate) >= 0
                && comparator.compare(startDateRange, endDateRange) <= 0;
    }

    private static LocalDate getCurrentDate() {
        final java.time.LocalDate currentDateJava = java.time.LocalDate.now();
        final LocalDate currentDate = LocalDate.newBuilder()
                                               .setYear(currentDateJava.getYear())
                                               .setMonth(MonthOfYear.valueOf(
                                                       currentDateJava.getMonthValue()))
                                               .setDay(currentDateJava.getDayOfMonth())
                                               .build();
        return currentDate;
    }

    /**
     * Checks whether the vendor doesn't have an available menu on this date range.
     *
     * @param vendor aggregate vendor
     * @param range  date range to check
     */
    public static boolean isThereMenuForThisDateRange(Vendor vendor, MenuDateRange range) {
        checkNotNull(vendor);
        checkNotNull(range);

        final List<Menu> menus = vendor.getMenuList();
        return menus.stream()
                    .anyMatch(m -> areRangesOverlapping(range, m.getMenuDateRange()));
    }

    /**
     * Checks whether two ranges overlap.
     *
     * @param range1 the date range
     * @param range2 the date range
     * @return boolean true if the date ranges are overlapped and false otherwise
     */
    @VisibleForTesting
    private static boolean areRangesOverlapping(MenuDateRange range1, MenuDateRange range2) {
        final LocalDate start = range1.getRangeStart();
        final LocalDate end = range1.getRangeEnd();

        final Comparator<LocalDate> comparator = new LocalDateComparator();

        final boolean startBelongsRange2 =
                comparator.compare(start, range2.getRangeStart()) >= 0 &&
                        comparator.compare(start, range2.getRangeEnd()) <= 0;

        final boolean endBelongsRange2 =
                comparator.compare(end, range2.getRangeStart()) >= 0
                        && comparator.compare(end, range2.getRangeEnd()) <= 0;

        return startBelongsRange2 || endBelongsRange2;
    }
}
