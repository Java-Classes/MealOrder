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

package javaclasses.mealorder.c.vendor;

import io.spine.server.aggregate.Aggregate;
import io.spine.server.aggregate.Apply;
import io.spine.server.command.Assign;
import javaclasses.mealorder.Menu;
import javaclasses.mealorder.MenuDateRange;
import javaclasses.mealorder.MenuId;
import javaclasses.mealorder.Vendor;
import javaclasses.mealorder.VendorChange;
import javaclasses.mealorder.VendorId;
import javaclasses.mealorder.VendorName;
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
import javaclasses.mealorder.c.rejection.VendorAlreadyExists;

import java.util.List;
import java.util.stream.IntStream;

import static io.spine.time.Time.getCurrentTime;
import static javaclasses.mealorder.c.vendor.VendorAggregateRejections.throwCannotSetDateRange;
import static javaclasses.mealorder.c.vendor.VendorAggregateRejections.throwVendorAlreadyExists;
import static javaclasses.mealorder.c.vendor.VendorValueValidator.isThereMenuForThisDateRange;
import static javaclasses.mealorder.c.vendor.VendorValueValidator.isValidDateRange;

/**
 * The aggregate managing the state of a {@link Vendor}.
 *
 * @author Yurii Haidamaka
 */
public class VendorAggregate extends Aggregate<VendorId, Vendor, VendorVBuilder> {

    /** Prevents instantiation of this utility class. */
    public VendorAggregate(VendorId id) {
        super(id);
    }

    @Assign
    VendorAdded handle(AddVendor cmd) throws VendorAlreadyExists {
        final VendorName vendorName = cmd.getVendorName();

        if (vendorName.equals(getState().getVendorName())) {
            throwVendorAlreadyExists(cmd);
        }

        final VendorAdded vendorAdded = VendorAdded.newBuilder()
                                                   .setVendorId(cmd.getVendorId())
                                                   .setWhoAdded(cmd.getUserId())
                                                   .setVendorName(vendorName)
                                                   .setEmail(cmd.getEmail())
                                                   .addAllPhoneNumber(cmd.getPhoneNumberList())
                                                   .setPoDailyDeadline(cmd.getPoDailyDeadline())
                                                   .build();
        return vendorAdded;
    }

    @Assign
    VendorUpdated handle(UpdateVendor cmd) {
        final VendorUpdated vendorUpdated = VendorUpdated.newBuilder()
                                                         .setVendorId(cmd.getVendorId())
                                                         .setWhoUploaded(cmd.getUserId())
                                                         .setWhenUpdated(getCurrentTime())
                                                         .setVendorChange(cmd.getVendorChange())
                                                         .build();
        return vendorUpdated;
    }

    @Assign
    MenuImported handle(ImportMenu cmd) {
        final MenuImported menuImported = MenuImported.newBuilder()
                                                      .setVendorId(cmd.getVendorId())
                                                      .setMenuId(cmd.getMenuId())
                                                      .setWhoImported(cmd.getUserId())
                                                      .setWhenImported(getCurrentTime())
                                                      .addAllDish(cmd.getDishList())
                                                      .build();
        return menuImported;
    }

    @Assign
    DateRangeForMenuSet handle(SetDateRangeForMenu cmd) throws CannotSetDateRange {
        final MenuDateRange range = cmd.getMenuDateRange();
        final Vendor vendor = getState();

        if (!isValidDateRange(range) || isThereMenuForThisDateRange(vendor, range)) {
            throwCannotSetDateRange(cmd);
        }
        final DateRangeForMenuSet dateRangeForMenuSet =
                DateRangeForMenuSet.newBuilder()
                                   .setVendorId(cmd.getVendorId())
                                   .setMenuId(cmd.getMenuId())
                                   .setWhoSet(cmd.getUserId())
                                   .setWhenSet(getCurrentTime())
                                   .setMenuDateRange(cmd.getMenuDateRange())
                                   .build();
        return dateRangeForMenuSet;
    }

    @Apply
    void vendorAdded(VendorAdded event) {
        getBuilder().setId(event.getVendorId())
                    .setVendorName(event.getVendorName())
                    .setEmail(event.getEmail())
                    .setPoDailyDeadline(event.getPoDailyDeadline())
                    .addAllPhoneNumber(event.getPhoneNumberList());
    }

    @Apply
    void vendorUpdated(VendorUpdated event) {
        final VendorChange vendorChange = event.getVendorChange();

        getBuilder().setVendorName(vendorChange.getNewVendorName())
                    .setEmail(vendorChange.getNewEmail())
                    .setPoDailyDeadline(vendorChange.getNewPoDailyDeadline())
                    .addAllPhoneNumber(vendorChange.getNewPhoneNumberList());
    }

    @Apply
    void menuImported(MenuImported event) {
        getBuilder().addMenu(Menu.newBuilder()
                                 .setId(event.getMenuId())
                                 .addAllDish(event.getDishList())
                                 .build());
    }

    @Apply
    void dateRangeForMenuSet(DateRangeForMenuSet event) {
        final List<Menu> menus = getBuilder().getMenu();
        final MenuId menuId = event.getMenuId();

        final int index = IntStream.range(0, menus.size())
                                   .filter(i -> menus.get(i)
                                                     .getId()
                                                     .equals(menuId))
                                   .findFirst()
                                   .getAsInt();

        final Menu menu = menus.get(index);
        getBuilder().setMenu(index, Menu.newBuilder(menu)
                                        .setMenuDateRange(event.getMenuDateRange())
                                        .build());
    }
}
