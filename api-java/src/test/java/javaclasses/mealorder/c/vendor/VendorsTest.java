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

import io.spine.test.Tests;
import javaclasses.mealorder.Menu;
import javaclasses.mealorder.MenuDateRange;
import javaclasses.mealorder.Vendor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.spine.test.Tests.assertHasPrivateParameterlessCtor;
import static javaclasses.mealorder.c.vendor.Vendors.isThereMenuForThisDateRange;
import static javaclasses.mealorder.c.vendor.Vendors.isValidDateRange;
import static javaclasses.mealorder.testdata.TestValues.MENU_DATE_RANGE;
import static javaclasses.mealorder.testdata.TestValues.MENU_DATE_RANGE_FROM_PAST;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Yurii Haidamaka
 */
@DisplayName("`Vendors` should")
class VendorsTest {

    @Test
    @DisplayName("have the private constructor")
    void havePrivateConstructor() {
        assertHasPrivateParameterlessCtor(Vendors.class);
    }

    @Test
    @DisplayName("return false if menu date ranges are not overlapping")
    void returnFalseIfDateRangesAreNotOverlapping() {
        final Menu menu = Menu.newBuilder()
                              .setMenuDateRange(MENU_DATE_RANGE)
                              .build();
        final Vendor vendor = Vendor.newBuilder()
                                    .addMenu(menu)
                                    .build();

        assertFalse(isThereMenuForThisDateRange(vendor, MENU_DATE_RANGE_FROM_PAST));
    }

    @Test
    @DisplayName("don't check menu without `Vendor` and `MenuDateRange`")
    void doNotCheckMEnuWithoutVendorAndMenuDateRange() {
        final Vendor vendor = Vendor.getDefaultInstance();
        final MenuDateRange menuDateRange = MenuDateRange.getDefaultInstance();

        assertThrows(NullPointerException.class,
                     () -> isThereMenuForThisDateRange(Tests.nullRef(), menuDateRange));
        assertThrows(NullPointerException.class,
                     () -> isThereMenuForThisDateRange(vendor, Tests.nullRef()));
        assertThrows(NullPointerException.class,
                     () -> isThereMenuForThisDateRange(Tests.nullRef(), Tests.nullRef()));
    }

    @Test
    @DisplayName("doesn't validate dateRange without `DateRange`")
    void doNotValidateDateRangeWithoutDateRange() {
        assertThrows(NullPointerException.class,
                     () -> isValidDateRange(Tests.nullRef()));

    }
}
