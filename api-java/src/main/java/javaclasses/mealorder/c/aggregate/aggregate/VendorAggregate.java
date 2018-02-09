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

package javaclasses.mealorder.c.aggregate.aggregate;

import com.google.protobuf.Message;
import io.spine.server.aggregate.Aggregate;
import io.spine.server.aggregate.Apply;
import io.spine.server.command.Assign;
import javaclasses.mealorder.Vendor;
import javaclasses.mealorder.VendorId;
import javaclasses.mealorder.VendorVBuilder;
import javaclasses.mealorder.c.command.AddVendor;
import javaclasses.mealorder.c.event.VendorAdded;

import java.util.List;

import static java.util.Collections.singletonList;

/**
 * The aggregate managing the state of a {@link Vendor}.
 *
 * @author Yurii Haidamaka
 */
@SuppressWarnings({"ClassWithTooManyMethods", /* Vendor definition cannot be separated and should
                                                 process all commands and events related to it
                                                 according to the domain model.
                                                 The {@code Aggregate} does it with methods
                                                 annotated as {@code Assign} and {@code Apply}.
                                                 In that case class has too many methods.*/
        "OverlyCoupledClass"}) /* As each method needs dependencies  necessary to perform execution
                                                 that class also overly coupled.*/
public class VendorAggregate extends Aggregate<VendorId,
        Vendor,
        VendorVBuilder> {

    /**
     * {@inheritDoc}
     */
    public VendorAggregate(VendorId id) {
        super(id);
    }

    @Assign
    List<? extends Message> handle(AddVendor cmd) {

        final VendorAdded vendorAdded = VendorAdded.newBuilder()
                                                   .setVendorId(cmd.getVendorId())
                                                   .setWhoAdded(cmd.getUserId())
                                                   .setVendorName(
                                                           cmd.getVendorName())
                                                   .setEmail(cmd.getEmail())
                                                   .addAllPhoneNumbers(cmd.getPhoneNumbersList())
                                                   .setPoDailyDeadline(
                                                           cmd.getPoDailyDeadline())
                                                   .build();
        return singletonList(vendorAdded);
    }

    @Apply
    private void vendorAdded(VendorAdded event) {

        getBuilder().setId(event.getVendorId())
                    .setVendorName(event.getVendorName())
                    .setEmail(event.getEmail())
                    .setPoDailyDeadline(event.getPoDailyDeadline())
                    .addAllPhoneNumbers(event.getPhoneNumbersList());
    }
}



