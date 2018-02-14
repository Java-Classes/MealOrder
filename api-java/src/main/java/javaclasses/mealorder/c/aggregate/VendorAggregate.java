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

package javaclasses.mealorder.c.aggregate;

import com.google.protobuf.Message;
import io.spine.server.aggregate.Aggregate;
import io.spine.server.aggregate.Apply;
import io.spine.server.command.Assign;
import javaclasses.mealorder.MenuDateRange;
import javaclasses.mealorder.Vendor;
import javaclasses.mealorder.VendorId;
import javaclasses.mealorder.VendorVBuilder;
import javaclasses.mealorder.c.command.AddVendor;
import javaclasses.mealorder.c.command.ImportMenu;
import javaclasses.mealorder.c.command.SetDateRangeForMenu;
import javaclasses.mealorder.c.command.UpdateVendor;
import javaclasses.mealorder.c.event.DateRangeForMenuSet;
import javaclasses.mealorder.c.event.MenuImported;
import javaclasses.mealorder.c.event.VendorAdded;
import javaclasses.mealorder.c.event.VendorUpdated;
import javaclasses.mealorder.c.rejection.CannotSetDateRange;

import java.util.List;

import static io.spine.time.Time.getCurrentTime;
import static java.util.Collections.singletonList;
import static javaclasses.mealorder.c.aggregate.VendorValidator.isValidDateRange;
import static javaclasses.mealorder.c.aggregate.rejection.VendorAggregateRejections.UpdateRejections.throwCannotSetDateRange;

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
                                                   .setVendorName(cmd.getVendorName())
                                                   .setEmail(cmd.getEmail())
                                                   .addAllPhoneNumbers(cmd.getPhoneNumbersList())
                                                   .setPoDailyDeadline(
                                                           cmd.getPoDailyDeadline())
                                                   .build();
        return singletonList(vendorAdded);
    }

    @Assign
    List<? extends Message> handle(UpdateVendor cmd) {

        final VendorUpdated vendorAdded = VendorUpdated.newBuilder()
                                                       .setVendorId(cmd.getVendorId())
                                                       .setWhoUploaded(cmd.getUserId())
                                                       .setWhenUpdated(getCurrentTime())
                                                       .setVendorChange(cmd.getVendorChange())
                                                       .build();
        return singletonList(vendorAdded);
    }

    @Assign
    List<? extends Message> handle(ImportMenu cmd) {
        final MenuImported menuImported = MenuImported.newBuilder()
                                                      .setVendorId(cmd.getVendorId())
                                                      .setMenuId(cmd.getMenuId())
                                                      .setWhoImported(cmd.getUserId())
                                                      .setWhenImported(getCurrentTime())
                                                      .addAllDishes(cmd.getDishesList())
                                                      .build();
        return singletonList(menuImported);
    }

    @Assign
    List<? extends Message> handle(SetDateRangeForMenu cmd) throws CannotSetDateRange {

        MenuDateRange menuDateRange = cmd.getMenuDateRange();

        if (!isValidDateRange(menuDateRange)) {
            throwCannotSetDateRange(cmd);
        }

        final DateRangeForMenuSet dateRangeForMenuSet = DateRangeForMenuSet.newBuilder()
                                                                           .setVendorId(
                                                                                   cmd.getVendorId())
                                                                           .setMenuId(
                                                                                   cmd.getMenuId())
                                                                           .setWhoSet(
                                                                                   cmd.getUserId())
                                                                           .setWhenSet(
                                                                                   getCurrentTime())
                                                                           .setMenuDateRange(
                                                                                   cmd.getMenuDateRange())
                                                                           .build();
        return singletonList(dateRangeForMenuSet);
    }

    @Apply
    private void vendorAdded(VendorAdded event) {

        getBuilder().setId(event.getVendorId())
                    .setVendorName(event.getVendorName())
                    .setEmail(event.getEmail())
                    .setPoDailyDeadline(event.getPoDailyDeadline())
                    .addAllPhoneNumbers(event.getPhoneNumbersList());
    }

    @Apply
    private void vendorUpdated(VendorUpdated event) {

        getBuilder().setId(event.getVendorId())
                    .setVendorName(event.getVendorChange()
                                        .getNewVendorName())
                    .setEmail(event.getVendorChange()
                                   .getNewEmail())
                    .setPoDailyDeadline(event.getVendorChange()
                                             .getNewPoDailyDeadline())
                    .addAllPhoneNumbers(event.getVendorChange()
                                             .getNewPhoneNumbersList());
    }

    @Apply
    private void menuImported(MenuImported event) {

        getBuilder().setId(event.getVendorId());
    }

    @Apply
    private void dateRangeForMenuSet(DateRangeForMenuSet event) {

        getBuilder().setId(event.getVendorId());
    }

}



