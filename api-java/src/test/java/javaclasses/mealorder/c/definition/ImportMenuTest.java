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

package javaclasses.mealorder.c.definition;

import com.google.common.base.Throwables;
import com.google.protobuf.Message;
import javaclasses.mealorder.Vendor;
import javaclasses.mealorder.c.command.AddVendor;
import javaclasses.mealorder.c.command.ImportMenu;
import javaclasses.mealorder.c.command.SetDateRangeForMenu;
import javaclasses.mealorder.c.event.MenuImported;
import javaclasses.mealorder.c.rejection.CannotSetDateRange;
import javaclasses.mealorder.c.rejection.Rejections;
import javaclasses.mealorder.testdata.TestVendorCommandFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.spine.server.aggregate.AggregateMessageDispatcher.dispatchCommand;
import static javaclasses.mealorder.testdata.TestVendorCommandFactory.DISHES;
import static javaclasses.mealorder.testdata.TestVendorCommandFactory.INVALID_MENU_DATE_RANGE;
import static javaclasses.mealorder.testdata.TestVendorCommandFactory.MENU_ID;
import static javaclasses.mealorder.testdata.TestVendorCommandFactory.USER_ID;
import static javaclasses.mealorder.testdata.TestVendorCommandFactory.VENDOR_ID;
import static javaclasses.mealorder.testdata.TestVendorCommandFactory.setDateRangeForMenuInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Yurii Haidamaka
 */
@DisplayName("ImportMenu command should be interpreted by VendorAggregate and")
public class ImportMenuTest extends VendorCommandTest<AddVendor> {

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    @Test
    @DisplayName("produce MenuImported event")
    void produceEvent() {
        final ImportMenu importMenuCmd = TestVendorCommandFactory.importMenuInstance();

        final List<? extends Message> messageList = dispatchCommand(aggregate,
                                                                    envelopeOf(importMenuCmd));

        assertNotNull(aggregate.getId());
        assertEquals(1, messageList.size());
        assertEquals(MenuImported.class, messageList.get(0)
                                                    .getClass());
        final MenuImported menuImported = (MenuImported) messageList.get(0);

        assertEquals(VENDOR_ID, menuImported.getVendorId());
        assertEquals(MENU_ID, menuImported.getMenuId());
        assertEquals(USER_ID, menuImported.getWhoImported());
        assertEquals(DISHES, menuImported.getDishesList());
    }

    @Test
    @DisplayName("import menu")
    void importMenu() {
        final ImportMenu importMenu = TestVendorCommandFactory.importMenuInstance();
        dispatchCommand(aggregate, envelopeOf(importMenu));

        final Vendor state = aggregate.getState();
        assertEquals(state.getId(), importMenu.getVendorId());
    }


}
