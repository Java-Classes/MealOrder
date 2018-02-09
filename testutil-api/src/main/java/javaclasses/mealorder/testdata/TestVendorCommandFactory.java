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

package javaclasses.mealorder.testdata;

import io.spine.net.EmailAddress;
import io.spine.time.LocalTime;
import javaclasses.mealorder.PhoneNumber;
import javaclasses.mealorder.UserId;
import javaclasses.mealorder.VendorId;
import javaclasses.mealorder.VendorName;
import javaclasses.mealorder.c.command.AddVendor;

import java.util.ArrayList;
import java.util.List;

import static io.spine.Identifier.newUuid;

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
}
