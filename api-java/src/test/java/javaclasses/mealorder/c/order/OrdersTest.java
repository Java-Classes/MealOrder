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

import io.spine.test.Tests;
import io.spine.time.LocalDate;
import javaclasses.mealorder.MenuDateRange;
import javaclasses.mealorder.OrderId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.spine.test.Tests.assertHasPrivateParameterlessCtor;
import static javaclasses.mealorder.c.order.Orders.checkMenuAvailability;
import static javaclasses.mealorder.c.order.Orders.checkRangeIncludesDate;
import static javaclasses.mealorder.c.order.Orders.getVendorAggregateForOrder;
import static javaclasses.mealorder.testdata.TestValues.INVALID_END_DATE;
import static javaclasses.mealorder.testdata.TestValues.INVALID_START_DATE;
import static javaclasses.mealorder.testdata.TestValues.MENU_DATE_RANGE;
import static javaclasses.mealorder.testdata.TestValues.ORDER_ID;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Vlad Kozachenko
 */
@DisplayName("Orders should")
class OrdersTest {

    @Test
    @DisplayName("have the private constructor")
    void havePrivateConstructor() {
        assertHasPrivateParameterlessCtor(Orders.class);
    }

    @Test
    @DisplayName("return `true` if the menu is available on the ordering date")
    void returnTrueForExistingMenu() {
        assertTrue(checkRangeIncludesDate(MENU_DATE_RANGE, ORDER_ID.getOrderDate()));
    }

    @Test
    @DisplayName("return `false` order has no date")
    void returnFalseForOrderWithoutDate() {
        assertFalse(checkRangeIncludesDate(MENU_DATE_RANGE, OrderId.getDefaultInstance()
                                                                   .getOrderDate()));
    }

    @Test
    @DisplayName("return `false` if order date is after menu end date")
    void returnFalseForOrderDateAfterMenu() {
        assertFalse(checkRangeIncludesDate(MENU_DATE_RANGE, INVALID_START_DATE));
    }

    @Test
    @DisplayName("return `false` if order date is before menu end date")
    void returnFalseForOrderDateBeforeMenu() {
        assertFalse(checkRangeIncludesDate(MENU_DATE_RANGE, INVALID_END_DATE));
    }

    @Test
    @DisplayName("throw `NullPointerException` if `checkRangeIncludesDate` " +
            "was called with null as any of arguments")
    void throwNullPointerOnCheckRangeIncludesDate() {
        assertThrows(NullPointerException.class,
                     () -> checkRangeIncludesDate(Tests.nullRef(), Tests.nullRef()));
        assertThrows(NullPointerException.class,
                     () -> checkRangeIncludesDate(MenuDateRange.getDefaultInstance(),
                                                  Tests.nullRef()));
        assertThrows(NullPointerException.class,
                     () -> checkRangeIncludesDate(Tests.nullRef(), LocalDate.getDefaultInstance()));
    }

    @Test
    @DisplayName("throw `NullPointerException` if `getVendorAggregateForOrder` " +
            "was called with null as argument")
    void throwNullPointerOnGetVendorAggregateForOrder() {
        assertThrows(NullPointerException.class, () -> getVendorAggregateForOrder(Tests.nullRef()));
    }

    @Test
    @DisplayName("throw `NullPointerException` if `checkMenuAvailability` " +
            "was called with null as argument")
    void throwNullPointerOnCheckMenuAvailability() {
        assertThrows(NullPointerException.class, () -> checkMenuAvailability(Tests.nullRef()));
    }
}
