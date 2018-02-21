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

import javaclasses.mealorder.Menu;
import javaclasses.mealorder.Vendor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.spine.test.Tests.assertHasPrivateParameterlessCtor;
import static javaclasses.mealorder.c.aggregate.VendorValidator.isThereMenuForThisDateRange;
import static javaclasses.mealorder.testdata.TestValues.MENU_DATE_RANGE;
import static javaclasses.mealorder.testdata.TestValues.MENU_DATE_RANGE_FROM_PAST;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * @author Yurii Haidamaka
 */
@DisplayName("VendorValidator should")
class VendorValidatorTest {

    @Test
    @DisplayName("have the private constructor")
    void havePrivateConstructor() {
        assertHasPrivateParameterlessCtor(VendorValidator.class);
    }

    @Test
    @DisplayName("return false if menu date ranges are not overlapping")
    void returnFalseIfDateRangesAreNotOverlapping() {

        final Vendor vendor = Vendor.newBuilder()
                                    .addMenu(Menu.newBuilder()
                                                 .setMenuDateRange(MENU_DATE_RANGE)
                                                 .build())
                                    .build();

        assertFalse(isThereMenuForThisDateRange(vendor, MENU_DATE_RANGE_FROM_PAST));

    }
}
