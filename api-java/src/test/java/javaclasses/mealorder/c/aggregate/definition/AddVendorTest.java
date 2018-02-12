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

package javaclasses.mealorder.c.aggregate.definition;

import com.google.protobuf.Message;
import javaclasses.mealorder.Vendor;
import javaclasses.mealorder.c.command.AddVendor;
import javaclasses.mealorder.c.event.VendorAdded;
import javaclasses.mealorder.testdata.TestVendorCommandFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.spine.server.aggregate.AggregateMessageDispatcher.dispatchCommand;
import static javaclasses.mealorder.testdata.TestVendorCommandFactory.EMAIL;
import static javaclasses.mealorder.testdata.TestVendorCommandFactory.PHONE_NUMBERS;
import static javaclasses.mealorder.testdata.TestVendorCommandFactory.PO_DAILY_DEADLINE;
import static javaclasses.mealorder.testdata.TestVendorCommandFactory.USER_ID;
import static javaclasses.mealorder.testdata.TestVendorCommandFactory.VENDOR_ID;
import static javaclasses.mealorder.testdata.TestVendorCommandFactory.VENDOR_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Yurii Haidamaka
 */
@DisplayName("AddVendor command should be interpreted by VendorAggregate and")
public class AddVendorTest extends VendorCommandTest<AddVendor> {

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    @Test
    @DisplayName("produce VendorAdded event")
    void produceEvent() {
        final AddVendor addVendorCmd = TestVendorCommandFactory.addVendorInstance();

        final List<? extends Message> messageList = dispatchCommand(aggregate,
                                                                    envelopeOf(addVendorCmd));

        assertNotNull(aggregate.getId());
        assertEquals(1, messageList.size());
        assertEquals(VendorAdded.class, messageList.get(0)
                                                   .getClass());
        final VendorAdded vendorAdded = (VendorAdded) messageList.get(0);

        assertEquals(VENDOR_ID, vendorAdded.getVendorId());
        assertEquals(USER_ID, vendorAdded.getWhoAdded());
        assertEquals(VENDOR_NAME, vendorAdded.getVendorName());
        assertEquals(EMAIL, vendorAdded.getEmail());
        assertEquals(PO_DAILY_DEADLINE, vendorAdded.getPoDailyDeadline());
        assertEquals(PHONE_NUMBERS, vendorAdded.getPhoneNumbersList());

    }

    @Test
    @DisplayName("add the vendor")
    void addVendor() {
        final AddVendor addVendor = TestVendorCommandFactory.addVendorInstance();
        dispatchCommand(aggregate, envelopeOf(addVendor));

        final Vendor state = aggregate.getState();
        assertEquals(state.getId(), addVendor.getVendorId());
    }
}