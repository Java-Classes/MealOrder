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

package javaclasses.mealorder.c.order;

import io.spine.time.LocalDate;
import javaclasses.mealorder.MenuDateRange;
import javaclasses.mealorder.LocalDateComparator;

import java.util.Comparator;

/**
 * @author Vlad Kozachenko
 */
public class OrderValidator {

    private OrderValidator() {
    }

    /**
     * Checks whether the menu is available on the date of the order.
     *
     * @param range     menu date range to check
     * @param orderDate order date to check
     * @return boolean true if there is a menu on the order date
     */
    public static boolean isMenuAvailable(MenuDateRange range, LocalDate orderDate) {
        Comparator<LocalDate> comparator = new LocalDateComparator();

        return comparator.compare(range.getRangeStart(), orderDate) <= 0 &&
                comparator.compare(range.getRangeEnd(), orderDate) >= 0;
    }
}
