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
import javaclasses.mealorder.MenuDateRange;

/**
 * Validates vendor commands and state transitions.
 *
 * @author Yurii Haidamaka
 */
class VendorValidator {

    private VendorValidator() {
    }

    /**
     * Checks whether the date range doesn't contains dates from the past and the start range is no more than the end range.
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

        LocalDateComparator localDateComparator = new LocalDateComparator();
        int startAndCurrentDatesCompareResult = localDateComparator.compare(startDateRange,
                                                                            currentDate);
        int startAndEndDatesCompareResult = localDateComparator.compare(startDateRange,
                                                                        endDateRange);

        return startAndCurrentDatesCompareResult < 0 && startAndEndDatesCompareResult < 0;
    }

}
