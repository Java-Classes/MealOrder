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

import com.google.protobuf.Message;
import javaclasses.mealorder.Vendor;
import javaclasses.mealorder.c.command.AddVendor;
import javaclasses.mealorder.c.command.UpdateVendor;
import javaclasses.mealorder.c.event.VendorUpdated;
import javaclasses.mealorder.testdata.TestVendorCommandFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.spine.server.aggregate.AggregateMessageDispatcher.dispatchCommand;
import static javaclasses.mealorder.testdata.TestValues.NEW_VENDOR_NAME;
import static javaclasses.mealorder.testdata.TestValues.USER_ID;
import static javaclasses.mealorder.testdata.TestValues.VENDOR_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Yurii Haidamaka
 */
@DisplayName("`UpdateVendor` command should be interpreted by `VendorAggregate` and")
public class UpdateVendorTest extends VendorCommandTest<AddVendor> {

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    @Test
    @DisplayName("produce `UpdateVendor` event")
    void produceEvent() {
        final AddVendor addVendor = TestVendorCommandFactory.addVendorInstance();
        dispatchCommand(aggregate, envelopeOf(addVendor));

        final UpdateVendor updateVendor = TestVendorCommandFactory.updateVendorInstance();

        final List<? extends Message> messageList = dispatchCommand(aggregate,
                                                                    envelopeOf(updateVendor));
        assertNotNull(aggregate.getId());
        assertEquals(1, messageList.size());
        assertEquals(VendorUpdated.class, messageList.get(0).getClass());

        final VendorUpdated vendorUpdated = (VendorUpdated) messageList.get(0);

        assertEquals(VENDOR_ID, vendorUpdated.getVendorId());
        assertEquals(USER_ID, vendorUpdated.getWhoUploaded());
        assertEquals(NEW_VENDOR_NAME, vendorUpdated.getVendorChange().getNewVendorName());
    }

    @Test
    @DisplayName("update vendor")
    void updateVendor() {
        final AddVendor addVendorCmd = TestVendorCommandFactory.addVendorInstance();
        dispatchCommand(aggregate, envelopeOf(addVendorCmd));

        final UpdateVendor updateVendor = TestVendorCommandFactory.updateVendorInstance();
        dispatchCommand(aggregate, envelopeOf(updateVendor));

        final Vendor state = aggregate.getState();
        assertEquals(state.getId(), updateVendor.getVendorId());
    }
}
