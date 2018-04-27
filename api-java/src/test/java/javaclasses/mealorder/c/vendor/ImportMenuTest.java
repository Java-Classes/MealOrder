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
import javaclasses.mealorder.Dish;
import javaclasses.mealorder.Menu;
import javaclasses.mealorder.Vendor;
import javaclasses.mealorder.c.command.AddVendor;
import javaclasses.mealorder.c.command.ImportMenu;
import javaclasses.mealorder.c.event.MenuImported;
import javaclasses.mealorder.c.rejection.CannotSetDateRange;
import javaclasses.mealorder.c.rejection.Rejections;
import javaclasses.mealorder.testdata.TestValues;
import javaclasses.mealorder.testdata.TestVendorCommandFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.spine.server.aggregate.AggregateMessageDispatcher.dispatchCommand;
import static javaclasses.mealorder.testdata.TestValues.DISH1;
import static javaclasses.mealorder.testdata.TestValues.DISH2;
import static javaclasses.mealorder.testdata.TestValues.INVALID_MENU_DATE_RANGE;
import static javaclasses.mealorder.testdata.TestValues.MENU;
import static javaclasses.mealorder.testdata.TestValues.MENU_DATE_RANGE;
import static javaclasses.mealorder.testdata.TestValues.MENU_DATE_RANGE_FROM_PAST;
import static javaclasses.mealorder.testdata.TestValues.MENU_ID;
import static javaclasses.mealorder.testdata.TestValues.USER_ID;
import static javaclasses.mealorder.testdata.TestValues.VENDOR_ID;
import static javaclasses.mealorder.testdata.TestVendorCommandFactory.importMenuInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Yurii Haidamaka
 */
@DisplayName("`ImportMenu` command should be interpreted by `VendorAggregate` and")
public class ImportMenuTest extends VendorCommandTest<AddVendor> {

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    @Test
    @DisplayName("produce `MenuImported` event")
    void produceEvent() {
        final AddVendor addVendorCmd = TestVendorCommandFactory.addVendorInstance();
        dispatchCommand(aggregate, envelopeOf(addVendorCmd));

        final ImportMenu importMenu = importMenuInstance();

        final List<? extends Message> messageList = dispatchCommand(aggregate,
                                                                    envelopeOf(importMenu));

        assertNotNull(aggregate.getId());
        assertEquals(1, messageList.size());
        assertEquals(MenuImported.class, messageList.get(0)
                                                    .getClass());

        final MenuImported menuImported = (MenuImported) messageList.get(0);

        assertEquals(VENDOR_ID, menuImported.getVendorId());
        assertEquals(MENU_ID, menuImported.getMenuId());
        assertEquals(USER_ID, menuImported.getWhoImported());

        final List<Dish> dishes = menuImported.getDishList();
        assertEquals(DISH1, dishes.get(0));
        assertEquals(DISH2, dishes.get(1));
    }

    @Test
    @DisplayName("import menu")
    void importMenu() {
        final AddVendor addVendorCmd = TestVendorCommandFactory.addVendorInstance();
        dispatchCommand(aggregate, envelopeOf(addVendorCmd));

        final ImportMenu importMenu = importMenuInstance();
        dispatchCommand(aggregate, envelopeOf(importMenu));

        final Vendor state = aggregate.getState();
        assertEquals(state.getId(), importMenu.getVendorId());
    }

    @Test
    @DisplayName("create correct date range")
    void createEvent() {
        final AddVendor addVendor = TestVendorCommandFactory.addVendorInstance();
        dispatchCommand(aggregate, envelopeOf(addVendor));
        final ImportMenu importMenu = importMenuInstance();
        final List<? extends Message> messageList =
                dispatchCommand(aggregate, envelopeOf(importMenu));

        assertNotNull(aggregate.getId());
        assertEquals(1, messageList.size());
        assertEquals(MenuImported.class, messageList.get(0)
                                                    .getClass());

        final MenuImported menuImported = (MenuImported) messageList.get(0);

        assertEquals(VENDOR_ID, menuImported.getVendorId());
        assertEquals(MENU_ID, menuImported.getMenuId());
        assertEquals(USER_ID, menuImported.getWhoImported());
        assertEquals(MENU_DATE_RANGE, menuImported.getMenuDateRange());
    }

    @Test
    @DisplayName("set date range for menu")
    void setDateRange() {
        final AddVendor addVendor = TestVendorCommandFactory.addVendorInstance();
        dispatchCommand(aggregate, envelopeOf(addVendor));

        final ImportMenu importMenu = importMenuInstance();
        dispatchCommand(aggregate, envelopeOf(importMenu));

        final Vendor state = aggregate.getState();
        assertEquals(state.getId(), importMenu.getVendorId());

        final Menu menu = state.getMenuList()
                               .stream()
                               .filter(m -> m.getId()
                                             .equals(importMenu.getMenuId()))
                               .findFirst()
                               .get();

        assertEquals(VENDOR_ID, importMenu.getVendorId());
        assertEquals(MENU_ID, importMenu.getMenuId());
        assertEquals(USER_ID, importMenu.getUserId());
        assertEquals(menu.getMenuDateRange(), importMenu.getMenuDateRange());
        assertEquals(importMenu.getMenuDateRange(), state.getMenuList()
                                                         .get(0)
                                                         .getMenuDateRange());
    }

    @Test
    @DisplayName("produce `CannotSetDateRange` rejection if date range is not valid")
    void produceRejection() {
        final AddVendor addVendor = TestVendorCommandFactory.addVendorInstance();
        dispatchCommand(aggregate, envelopeOf(addVendor));

        final ImportMenu invalidImportMenu =
                importMenuInstance(VENDOR_ID, USER_ID, MENU_ID, INVALID_MENU_DATE_RANGE,
                                   TestValues.MENU);

        final Throwable t = assertThrows(Throwable.class,
                                         () -> dispatchCommand(aggregate,
                                                               envelopeOf(
                                                                       invalidImportMenu)));
        final Throwable cause = Throwables.getRootCause(t);
        assertThat(cause, instanceOf(CannotSetDateRange.class));

        @SuppressWarnings("ConstantConditions") // Instance type checked before.
        final Rejections.CannotSetDateRange rejection =
                ((CannotSetDateRange) cause).getMessageThrown();

        assertEquals(invalidImportMenu.getVendorId(), rejection.getVendorId());
        assertEquals(invalidImportMenu.getMenuId(), rejection.getMenuId());
        assertEquals(invalidImportMenu.getMenuDateRange(), rejection.getMenuDateRange());
    }

    @Test
    @DisplayName("produce `CannotSetDateRange` rejection if the order date is from the past")
    void produceCannotSetDateRangeRejection() {
        final AddVendor addVendor = TestVendorCommandFactory.addVendorInstance();
        dispatchCommand(aggregate, envelopeOf(addVendor));
        final ImportMenu importMenu = importMenuInstance();
        dispatchCommand(aggregate, envelopeOf(importMenu));
        final ImportMenu importMenuFromPast =
                importMenuInstance(VENDOR_ID, USER_ID, MENU_ID, MENU_DATE_RANGE_FROM_PAST, MENU);

        final Throwable t =
                assertThrows(Throwable.class, () -> dispatchCommand(aggregate,
                                                                    envelopeOf(
                                                                            importMenuFromPast)));
        final Throwable cause = Throwables.getRootCause(t);

        @SuppressWarnings("ConstantConditions") // Instance type checked before.
        final Rejections.CannotSetDateRange rejection =
                ((CannotSetDateRange) cause).getMessageThrown();

        assertEquals(importMenuFromPast.getVendorId(), rejection.getVendorId());
        assertEquals(importMenuFromPast.getMenuId(), rejection.getMenuId());
        assertEquals(importMenuFromPast.getMenuDateRange(), rejection.getMenuDateRange());
    }

    @Test
    @DisplayName("produce `CannotSetDateRange` rejection if vendor already has menu on this date range")
    void produceRejectionIfMenuExists() {
        final AddVendor addVendor = TestVendorCommandFactory.addVendorInstance();
        dispatchCommand(aggregate, envelopeOf(addVendor));
        final ImportMenu importMenu = importMenuInstance();
        dispatchCommand(aggregate, envelopeOf(importMenu));

        final Throwable t = assertThrows(Throwable.class,
                                         () -> dispatchCommand(aggregate, envelopeOf(importMenu)));
        final Throwable cause = Throwables.getRootCause(t);
        assertThat(cause, instanceOf(CannotSetDateRange.class));

        @SuppressWarnings("ConstantConditions") // Instance type checked before.
        final Rejections.CannotSetDateRange rejection =
                ((CannotSetDateRange) cause).getMessageThrown();

        assertEquals(importMenu.getVendorId(), rejection.getVendorId());
        assertEquals(importMenu.getMenuId(), rejection.getMenuId());
        assertEquals(importMenu.getMenuDateRange(), rejection.getMenuDateRange());
    }
}
