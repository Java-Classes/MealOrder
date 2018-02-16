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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Yurii Haidamaka
 */
@DisplayName("The compare method from the LocalDateComparator class should compare two objects from the LocalDate class and")
class LocalDateComparatorTest {

    private final LocalDateComparator localDateComparator = new LocalDateComparator();

    @Test
    @DisplayName("return a negative integer if the first argument is less than the second")
    void returnNegativeInteger() {

        final LocalDate leftDate = LocalDate.newBuilder()
                                            .setYear(2017)
                                            .setMonth(MonthOfYear.APRIL)
                                            .setDay(2)
                                            .build();
        final LocalDate rightDate = LocalDate.newBuilder()
                                             .setYear(2018)
                                             .setMonth(MonthOfYear.MARCH)
                                             .setDay(16)
                                             .build();

        assertEquals(-1, localDateComparator.compare(leftDate, rightDate));
    }

    @Test
    @DisplayName("return a positive integer if the first argument is greater than the second")
    void returnPositiveInteger() {

        final LocalDate leftDate = LocalDate.newBuilder()
                                            .setYear(2018)
                                            .setMonth(MonthOfYear.MARCH)
                                            .setDay(16)
                                            .build();

        final LocalDate rightDate = LocalDate.newBuilder()
                                             .setYear(2017)
                                             .setMonth(MonthOfYear.APRIL)
                                             .setDay(2)
                                             .build();

        assertEquals(1, localDateComparator.compare(leftDate, rightDate));
    }

    @Test
    @DisplayName("return zero if the first argument is equal to the second")
    void returnZero() {

        final LocalDate leftDate = LocalDate.newBuilder()
                                            .setYear(2018)
                                            .setMonth(MonthOfYear.MARCH)
                                            .setDay(16)
                                            .build();

        final LocalDate rightDate = LocalDate.newBuilder()
                                             .setYear(2018)
                                             .setMonth(MonthOfYear.MARCH)
                                             .setDay(16)
                                             .build();

        assertEquals(0, localDateComparator.compare(leftDate, rightDate));
    }

}




