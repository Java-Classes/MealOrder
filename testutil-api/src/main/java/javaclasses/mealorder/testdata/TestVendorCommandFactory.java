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

import io.spine.money.Money;
import io.spine.net.EmailAddress;
import io.spine.time.LocalDate;
import io.spine.time.LocalTime;
import javaclasses.mealorder.Dish;
import javaclasses.mealorder.DishId;
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

import java.util.ArrayList;
import java.util.List;

import static io.spine.Identifier.newUuid;
import static io.spine.time.Time.getCurrentTime;

/**
 * A factory of the vendor commands for the test needs.
 *
 * @author Yurii Haidamaka
 */
public class TestVendorCommandFactory {

    public static final VendorId VENDOR_ID = VendorId.newBuilder()
                                                     .setValue(newUuid())
                                                     .build();
    public static final VendorName VENDOR_NAME = VendorName.newBuilder()
                                                           .setValue("Mashed Potato")
                                                           .build();
    public static final VendorName NEW_VENDOR_NAME = VendorName.newBuilder()
                                                               .setValue("New Mashed Potato")
                                                               .build();
    public static final EmailAddress EMAIL = EmailAddress.newBuilder()
                                                         .setValue("mashed.potato@gmail.com")
                                                         .build();
    public static final UserId USER_ID = UserId.newBuilder()
                                               .setEmail(EmailAddress.newBuilder()
                                                                     .setValue(
                                                                             "yurii.haidamaka@teamdev.com")
                                                                     .build())
                                               .build();

    public static final LocalTime PO_DAILY_DEADLINE = LocalTime.newBuilder()
                                                               .setHours(10)
                                                               .setMinutes(0)
                                                               .build();
    public static final List<PhoneNumber> PHONE_NUMBERS = new ArrayList<PhoneNumber>() {{
        add(PhoneNumber.newBuilder()
                       .setValue("0634596796")
                       .build());
        add(PhoneNumber.newBuilder()
                       .setValue("0983162589")
                       .build());
    }};

    public static final VendorChange VENDOR_CHANGE = VendorChange.newBuilder()
                                                                 .setPreviousVendorName(VENDOR_NAME)
                                                                 .setPreviousEmail(EMAIL)
                                                                 .addAllPreviousPhoneNumbers(
                                                                         PHONE_NUMBERS)
                                                                 .setPreviousPoDailyDeadline(
                                                                         PO_DAILY_DEADLINE)
                                                                 .setNewVendorName(NEW_VENDOR_NAME)
                                                                 .setNewEmail(EMAIL)
                                                                 .addAllNewPhoneNumbers(
                                                                         PHONE_NUMBERS)
                                                                 .setPreviousPoDailyDeadline(
                                                                         PO_DAILY_DEADLINE)
                                                                 .build();

    public static final MenuId MENU_ID = MenuId.newBuilder()
                                               .setVendorId(VENDOR_ID)
                                               .setWhenImported(getCurrentTime())
                                               .build();

    public static final MenuDateRange MENU_DATE_RANGE = MenuDateRange.newBuilder()
                                                                     .setRangeStart(
                                                                             LocalDate.newBuilder()
                                                                                      .setYear(2018)
                                                                                      .setMonthValue(
                                                                                              2)
                                                                                      .setDay(13)
                                                                                      .build())
                                                                     .setRangeEnd(
                                                                             LocalDate.newBuilder()
                                                                                      .setYear(2018)
                                                                                      .setMonthValue(
                                                                                              2)
                                                                                      .setDay(19)
                                                                                      .build())
                                                                     .build();

    public static final MenuDateRange INVALID_MENU_DATE_RANGE = MenuDateRange.newBuilder()
                                                                             .setRangeStart(
                                                                                     LocalDate.newBuilder()
                                                                                              .setYear(
                                                                                                      2017)
                                                                                              .setMonthValue(
                                                                                                      2)
                                                                                              .setDay(13)
                                                                                              .build())
                                                                             .setRangeEnd(
                                                                                     LocalDate.newBuilder()
                                                                                              .setYear(
                                                                                                      2017)
                                                                                              .setMonthValue(
                                                                                                      2)
                                                                                              .setDay(19)
                                                                                              .build())
                                                                             .build();

    public static final List<Dish> DISHES = new ArrayList<Dish>() {{
        add(Dish.newBuilder()
                .setId(DishId.newBuilder()
                             .setMenuId(MENU_ID)
                             .setSequentialNumber(1)
                             .build())
                .setName("chicken Kiev")
                .setCategory("main course")
                .setPrice(Money.getDefaultInstance())
                .build());

        add(Dish.newBuilder()
                .setId(DishId.newBuilder()
                             .setMenuId(MENU_ID)
                             .setSequentialNumber(2)
                             .build())
                .setName("noodles soup")
                .setCategory("main course")
                .setPrice(Money.getDefaultInstance())
                .build());
    }};

    private TestVendorCommandFactory() {
    }

    /**
     * Provides a pre-configured {@link javaclasses.mealorder.c.command.AddVendor} instance.
     *
     * @return the {@code AddVendor} instance
     */
    public static AddVendor addVendorInstance() {
        final AddVendor result = addVendorInstance(VENDOR_ID, USER_ID, VENDOR_NAME, EMAIL,
                                                   PO_DAILY_DEADLINE,
                                                   PHONE_NUMBERS);
        return result;
    }

    /**
     * Provides a pre-configured {@link AddVendor} instance.
     *
     * @param vendorId        the identifier of a created vendor
     * @param vendorName      the name of a created vendor
     * @param email           the email address to send a purchase order
     * @param poDailyDeadline daily deadline time
     * @param phoneNumbers    the phone numbers of a created vendor
     * @return the {@code CreateBasicTask} instance
     */
    public static AddVendor addVendorInstance(VendorId vendorId, UserId userId,
                                              VendorName vendorName,
                                              EmailAddress email, LocalTime poDailyDeadline,
                                              List<PhoneNumber> phoneNumbers) {

        final AddVendor result = AddVendor.newBuilder()
                                          .setVendorId(vendorId)
                                          .setUserId(userId)
                                          .setVendorName(vendorName)
                                          .setEmail(email)
                                          .setPoDailyDeadline(poDailyDeadline)
                                          .addAllPhoneNumbers(phoneNumbers)
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
        final ImportMenu result = importMenuInstance(VENDOR_ID, USER_ID, MENU_ID, DISHES);
        return result;
    }

    /**
     * Provides a pre-configured {@link ImportMenu} instance.
     *
     * @param vendorId the identifier of a created vendor
     * @param userId   the identifier of the user who updates vendor profile
     * @param menuId   the identifier of the imported menu
     * @param dishes   the collection of dishes
     * @return the {@code CreateBasicTask} instance
     */
    public static ImportMenu importMenuInstance(VendorId vendorId, UserId userId, MenuId menuId,
                                                List<Dish> dishes) {

        final ImportMenu result = ImportMenu.newBuilder()
                                            .setVendorId(vendorId)
                                            .setUserId(userId)
                                            .setMenuId(menuId)
                                            .addAllDishes(dishes)
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
