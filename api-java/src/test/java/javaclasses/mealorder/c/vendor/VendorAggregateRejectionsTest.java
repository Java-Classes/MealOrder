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
import javaclasses.mealorder.c.command.AddVendor;
import javaclasses.mealorder.c.command.SetDateRangeForMenu;
import javaclasses.mealorder.c.rejection.CannotSetDateRange;
import javaclasses.mealorder.c.rejection.VendorAlreadyExists;
import javaclasses.mealorder.testdata.TestVendorCommandFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.spine.test.Tests.assertHasPrivateParameterlessCtor;
import static javaclasses.mealorder.c.vendor.VendorAggregateRejections.throwCannotSetDateRange;
import static javaclasses.mealorder.c.vendor.VendorAggregateRejections.throwVendorAlreadyExists;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Yurii Haidamaka
 */
@DisplayName("VendorAggregateRejections should")
class VendorAggregateRejectionsTest {

    @Test
    @DisplayName("have the private constructor")
    void havePrivateConstructor() {
        assertHasPrivateParameterlessCtor(VendorAggregateRejections.class);
    }

    @Test
    @DisplayName("throw VendorAlreadyExists rejection")
    void throwVendorAlreadyExistsRejection() {
        final AddVendor cmd = TestVendorCommandFactory.addVendorInstance();
        final VendorAlreadyExists rejection = assertThrows(VendorAlreadyExists.class,
                                                           () -> throwVendorAlreadyExists(cmd));

        assertEquals(cmd.getVendorId(), rejection.getMessageThrown().getVendorId());
        assertEquals(cmd.getVendorName(), rejection.getMessageThrown().getVendorName());
    }

    @Test
    @DisplayName("throw CannotSetDateRange rejection")
    void throwCannotSetDateRangeRejection() {
        SetDateRangeForMenu cmd = TestVendorCommandFactory.setDateRangeForMenuInstance();
        final CannotSetDateRange rejection = assertThrows(CannotSetDateRange.class,
                                                          () -> throwCannotSetDateRange(cmd));

        assertEquals(cmd.getVendorId(), rejection.getMessageThrown().getVendorId());
        assertEquals(cmd.getMenuId(), rejection.getMessageThrown().getMenuId());
        assertEquals(cmd.getMenuDateRange(), rejection.getMessageThrown().getMenuDateRange());
    }

    @Test
    @DisplayName("don't throw VendorAlreadyExists rejection for null command")
    void doNotThrowVendorAlreadyExistsRejection() {
        assertThrows(NullPointerException.class,
                     () -> throwVendorAlreadyExists(Tests.nullRef()));
    }
    @Test
    @DisplayName("don't throw CannotSetDateRange rejection for null command")
    void doNotThrowCannotSetDateRangeRejection() {
        assertThrows(NullPointerException.class,
                     () -> throwCannotSetDateRange(Tests.nullRef()));

    }
}
