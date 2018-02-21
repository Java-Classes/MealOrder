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

package javaclasses.mealorder.testdata;

import io.spine.net.EmailAddress;
import io.spine.time.LocalTime;
import javaclasses.mealorder.Dish;
import javaclasses.mealorder.MenuDateRange;
import javaclasses.mealorder.MenuId;
import javaclasses.mealorder.PhoneNumber;
import javaclasses.mealorder.UserId;
import javaclasses.mealorder.VendorChange;
import javaclasses.mealorder.VendorId;
import javaclasses.mealorder.VendorName;
import javaclasses.mealorder.c.command.AddVendor;
import javaclasses.mealorder.c.command.ImportMenu;
import javaclasses.mealorder.c.command.SetDateRangeForMenu;
import javaclasses.mealorder.c.command.UpdateVendor;

import static javaclasses.mealorder.testdata.TestValues.*;

/**
 * A factory of the vendor commands for the test needs.
 *
 * @author Yurii Haidamaka
 */
public class TestVendorCommandFactory {

    private TestVendorCommandFactory() {
    }

    /**
     * Provides a pre-configured {@link javaclasses.mealorder.c.command.AddVendor} instance.
     *
     * @return the {@code AddVendor} instance
     */
    public static AddVendor addVendorInstance() {
        final AddVendor result = addVendorInstance(VENDOR_ID, USER_ID, VENDOR_NAME, EMAIL,
                                                   PO_DAILY_DEADLINE, PHONE_NUMBER1, PHONE_NUMBER2);
        return result;
    }

    /**
     * Provides a pre-configured {@link AddVendor} instance.
     *
     * @param vendorId        the identifier of a created vendor
     * @param vendorName      the name of a created vendor
     * @param email           the email address to send a purchase order
     * @param poDailyDeadline daily deadline time
     * @param phones          the array of phone numbers of a created vendor
     * @return the {@code CreateBasicTask} instance
     */
    public static AddVendor addVendorInstance(VendorId vendorId, UserId userId,
                                              VendorName vendorName,
                                              EmailAddress email, LocalTime poDailyDeadline,
                                              PhoneNumber... phones) {
        final AddVendor result = AddVendor.newBuilder()
                                          .setVendorId(vendorId)
                                          .setUserId(userId)
                                          .setVendorName(vendorName)
                                          .setEmail(email)
                                          .setPoDailyDeadline(poDailyDeadline)
                                          .addPhoneNumbers(phones[0])
                                          .addPhoneNumbers(phones[1])
                                          .build();
        return result;
    }

    /**
     * Provides a pre-configured {@link UpdateVendor} instance.
     *
     * @return the {@code UpdateVendor} instance
     */
    public static UpdateVendor updateVendorInstance() {
        final UpdateVendor result = updateVendorInstance(VENDOR_ID, USER_ID, VENDOR_CHANGE);
        return result;
    }

    /**
     * Provides a pre-configured {@link UpdateVendor} instance.
     *
     * @param vendorId     the identifier of a created vendor
     * @param userId       the identifier of the user who updates vendor profile
     * @param vendorChange changes in a vendor profile.
     * @return the {@code CreateBasicTask} instance
     */
    public static UpdateVendor updateVendorInstance(VendorId vendorId, UserId userId,
                                                    VendorChange vendorChange) {

        final UpdateVendor result = UpdateVendor.newBuilder()
                                                .setVendorId(vendorId)
                                                .setUserId(userId)
                                                .setVendorChange(vendorChange)
                                                .build();
        return result;
    }

    /**
     * Provides a pre-configured {@link ImportMenu} instance.
     *
     * @return the {@code ImportMenu} instance
     */
    public static ImportMenu importMenuInstance() {
        final ImportMenu result = importMenuInstance(VENDOR_ID, USER_ID, MENU_ID, DISH1, DISH2);
        return result;
    }

    /**
     * Provides a pre-configured {@link ImportMenu} instance.
     *
     * @param vendorId the identifier of a created vendor
     * @param userId   the identifier of the user who updates vendor profile
     * @param menuId   the identifier of the imported menu
     * @param dishes   the array of dishes
     * @return the {@code CreateBasicTask} instance
     */
    public static ImportMenu importMenuInstance(VendorId vendorId, UserId userId, MenuId menuId,
                                                Dish... dishes) {

        final ImportMenu result = ImportMenu.newBuilder()
                                            .setVendorId(vendorId)
                                            .setUserId(userId)
                                            .setMenuId(menuId)
                                            .addDishes(dishes[0])
                                            .addDishes(dishes[1])
                                            .build();
        return result;
    }

    /**
     * Provides a pre-configured {@link SetDateRangeForMenu} instance.
     *
     * @return the {@code ImportMenu} instance
     */
    public static SetDateRangeForMenu setDateRangeForMenuInstance() {
        final SetDateRangeForMenu result = setDateRangeForMenuInstance(VENDOR_ID, MENU_ID, USER_ID,
                                                                       MENU_DATE_RANGE);
        return result;
    }

    /**
     * Provides a pre-configured {@link SetDateRangeForMenu} instance.
     *
     * @param vendorId      the identifier of a created vendor
     * @param userId        the identifier of the user who updates vendor profile
     * @param menuId        the identifier of the imported menu
     * @param menuDateRange a menu date range that should be set.
     * @return the {@code CreateBasicTask} instance
     */
    public static SetDateRangeForMenu setDateRangeForMenuInstance(VendorId vendorId, MenuId menuId,
                                                                  UserId userId,
                                                                  MenuDateRange menuDateRange) {

        final SetDateRangeForMenu result = SetDateRangeForMenu.newBuilder()
                                                              .setVendorId(vendorId)
                                                              .setUserId(userId)
                                                              .setMenuId(menuId)
                                                              .setMenuDateRange(menuDateRange)
                                                              .build();
        return result;
    }
}
