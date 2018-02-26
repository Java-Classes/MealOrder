//
// Copyright 2018, TeamDev Ltd. All rights reserved.
//
// Redistribution and use in source and/or binary forms, with or without
// modification, must retain the above copyright notice and the following
// disclaimer.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
// "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
// LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
// A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
// OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
// SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
// LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
// DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
// THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//

package javaclasses.mealorder.c.vendor;

import javaclasses.mealorder.c.command.AddVendor;
import javaclasses.mealorder.c.command.SetDateRangeForMenu;
import javaclasses.mealorder.c.rejection.CannotSetDateRange;
import javaclasses.mealorder.c.rejection.VendorAlreadyExists;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.spine.time.Time.getCurrentTime;

/**
 * Utility class for working with {@link VendorAggregate} rejection.
 *
 * @author Yurii Haidamaka
 */
class VendorAggregateRejections {

    /** Prevents instantiation of this utility class. */
    private VendorAggregateRejections() {
    }

    /**
     * Constructs and throws the {@link VendorAlreadyExists} rejection
     * according to the passed parameters.
     *
     * @param cmd the {@code AddVendor} command which thrown the rejection
     * @throws VendorAlreadyExists the rejection to throw
     */
    static VendorAlreadyExists vendorAlreadyExists(AddVendor cmd) throws
                                                                  VendorAlreadyExists {
        checkNotNull(cmd);
        final VendorAlreadyExists vendorAlreadyExists = new VendorAlreadyExists(cmd.getVendorId(),
                                                                                cmd.getVendorName(),
                                                                                getCurrentTime());
        throw vendorAlreadyExists;
    }

    /**
     * Constructs and throws the {@link CannotSetDateRange} rejection
     * according to the passed parameters.
     *
     * @param cmd the {@code SetDateRangeForMenu} command which thrown the rejection
     * @throws CannotSetDateRange the rejection to throw
     */
    static CannotSetDateRange cannotSetDateRange(SetDateRangeForMenu cmd) throws
                                                                          CannotSetDateRange {
        checkNotNull(cmd);
        final CannotSetDateRange cannotSetDateRange = new CannotSetDateRange(cmd.getVendorId(),
                                                                             cmd.getMenuId(),
                                                                             cmd.getMenuDateRange(),
                                                                             getCurrentTime());
        throw cannotSetDateRange;
    }
}
