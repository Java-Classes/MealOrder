//
// Copyright 2018, TeamDev Ltd. All rights reserved.
//
// Redistribution and use in source and/or binary gorms, with or without
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

import com.google.common.base.Throwables;
import com.google.protobuf.Message;
import javaclasses.mealorder.Menu;
import javaclasses.mealorder.Vendor;
import javaclasses.mealorder.c.command.AddVendor;
import javaclasses.mealorder.c.command.ImportMenu;
import javaclasses.mealorder.c.command.SetDateRangeForMenu;
import javaclasses.mealorder.c.event.DateRangeForMenuSet;
import javaclasses.mealorder.c.rejection.CannotSetDateRange;
import javaclasses.mealorder.c.rejection.Rejections;
import javaclasses.mealorder.testdata.TestVendorCommandFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.spine.server.aggregate.AggregateMessageDispatcher.dispatchCommand;
import static javaclasses.mealorder.testdata.TestValues.INVALID_MENU_DATE_RANGE;
import static javaclasses.mealorder.testdata.TestValues.WEEK_FROM_TOMORROW;
import static javaclasses.mealorder.testdata.TestValues.MENU_DATE_RANGE_FROM_PAST;
import static javaclasses.mealorder.testdata.TestValues.MENU_ID;
import static javaclasses.mealorder.testdata.TestValues.USER_ID;
import static javaclasses.mealorder.testdata.TestValues.VENDOR_ID;
import static javaclasses.mealorder.testdata.TestVendorCommandFactory.setDateRangeForMenuInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Yurii Haidamaka
 */
@DisplayName("`SetDateRangeForMenu` command should be interpreted by `VendorAggregate` and")
public class SetDateRangeForMenuTest extends VendorCommandTest<AddVendor> {

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    @Test
    @DisplayName("produce `DateRangeForMenuSet` event")
    void produceEvent() {
        final ImportMenu importMenu = TestVendorCommandFactory.importMenuInstance();
        dispatchCommand(aggregate, envelopeOf(importMenu));

        final SetDateRangeForMenu setDateRangeForMenuCmd =
                TestVendorCommandFactory.setDateRangeForMenuInstance();

        final List<? extends Message> messageList =
                dispatchCommand(aggregate, envelopeOf(setDateRangeForMenuCmd));

        assertNotNull(aggregate.getId());
        assertEquals(1, messageList.size());
        assertEquals(DateRangeForMenuSet.class, messageList.get(0)
                                                           .getClass());

        final DateRangeForMenuSet dateRangeForMenuSet = (DateRangeForMenuSet) messageList.get(0);

        assertEquals(VENDOR_ID, dateRangeForMenuSet.getVendorId());
        assertEquals(MENU_ID, dateRangeForMenuSet.getMenuId());
        assertEquals(USER_ID, dateRangeForMenuSet.getWhoSet());
        assertEquals(WEEK_FROM_TOMORROW, dateRangeForMenuSet.getMenuDateRange());
    }

    @Test
    @DisplayName("set date range for menu")
    void setDateRange() {
        final AddVendor addVendor = TestVendorCommandFactory.addVendorInstance();
        dispatchCommand(aggregate, envelopeOf(addVendor));

        final ImportMenu importMenu = TestVendorCommandFactory.importMenuInstance();
        dispatchCommand(aggregate, envelopeOf(importMenu));

        final SetDateRangeForMenu setDateRangeForMenu = TestVendorCommandFactory.setDateRangeForMenuInstance();
        dispatchCommand(aggregate, envelopeOf(setDateRangeForMenu));

        final Vendor state = aggregate.getState();
        assertEquals(state.getId(), setDateRangeForMenu.getVendorId());

        final Menu menu = state.getMenuList()
                               .stream()
                               .filter(m -> m.getId()
                                             .equals(setDateRangeForMenu.getMenuId()))
                               .findFirst()
                               .get();

        assertEquals(VENDOR_ID, setDateRangeForMenu.getVendorId());
        assertEquals(MENU_ID, setDateRangeForMenu.getMenuId());
        assertEquals(USER_ID, setDateRangeForMenu.getUserId());
        assertEquals(menu.getMenuDateRange(), setDateRangeForMenu.getMenuDateRange());
    }

    @Test
    @DisplayName("produce `CannotSetDateRange` rejection if date range is not valid")
    void produceRejection() {
        final ImportMenu importMenu = TestVendorCommandFactory.importMenuInstance();
        dispatchCommand(aggregate, envelopeOf(importMenu));

        final SetDateRangeForMenu setInvalidDateRangeForMenu =
                setDateRangeForMenuInstance(VENDOR_ID, MENU_ID, USER_ID, INVALID_MENU_DATE_RANGE);

        final Throwable t = assertThrows(Throwable.class,
                                         () -> dispatchCommand(aggregate,
                                                               envelopeOf(
                                                                       setInvalidDateRangeForMenu)));
        final Throwable cause = Throwables.getRootCause(t);
        assertThat(cause, instanceOf(CannotSetDateRange.class));

        @SuppressWarnings("ConstantConditions") // Instance type checked before.
        final Rejections.CannotSetDateRange rejection =
                ((CannotSetDateRange) cause).getMessageThrown();

        assertEquals(setInvalidDateRangeForMenu.getVendorId(), rejection.getVendorId());
        assertEquals(setInvalidDateRangeForMenu.getMenuId(), rejection.getMenuId());
        assertEquals(setInvalidDateRangeForMenu.getMenuDateRange(), rejection.getMenuDateRange());
    }

    @Test
    @DisplayName("produce `CannotSetDateRange` rejection if the order date is from the past")
    void produceCannotSetDateRangeRejection() {
        final ImportMenu importMenu = TestVendorCommandFactory.importMenuInstance();
        dispatchCommand(aggregate, envelopeOf(importMenu));

        final SetDateRangeForMenu setRangeFromPast =
                setDateRangeForMenuInstance(VENDOR_ID, MENU_ID, USER_ID, MENU_DATE_RANGE_FROM_PAST);

        final Throwable t =
                assertThrows(Throwable.class, () -> dispatchCommand(aggregate,
                                                                    envelopeOf(setRangeFromPast)));
        final Throwable cause = Throwables.getRootCause(t);

        @SuppressWarnings("ConstantConditions") // Instance type checked before.
        final Rejections.CannotSetDateRange rejection =
                ((CannotSetDateRange) cause).getMessageThrown();

        assertEquals(setRangeFromPast.getVendorId(), rejection.getVendorId());
        assertEquals(setRangeFromPast.getMenuId(), rejection.getMenuId());
        assertEquals(setRangeFromPast.getMenuDateRange(), rejection.getMenuDateRange());
    }

    @Test
    @DisplayName("produce `CannotSetDateRange` rejection if vendor already has menu on this date range")
    void produceRejectionIfMenuExists() {
        final ImportMenu importMenu = TestVendorCommandFactory.importMenuInstance();
        dispatchCommand(aggregate, envelopeOf(importMenu));

        final SetDateRangeForMenu setRange = TestVendorCommandFactory.setDateRangeForMenuInstance();
        dispatchCommand(aggregate, envelopeOf(setRange));

        final Throwable t = assertThrows(Throwable.class,
                                         () -> dispatchCommand(aggregate, envelopeOf(setRange)));
        final Throwable cause = Throwables.getRootCause(t);
        assertThat(cause, instanceOf(CannotSetDateRange.class));

        @SuppressWarnings("ConstantConditions") // Instance type checked before.
        final Rejections.CannotSetDateRange rejection =
                ((CannotSetDateRange) cause).getMessageThrown();

        assertEquals(setRange.getVendorId(), rejection.getVendorId());
        assertEquals(setRange.getMenuId(), rejection.getMenuId());
        assertEquals(setRange.getMenuDateRange(), rejection.getMenuDateRange());
    }
}
