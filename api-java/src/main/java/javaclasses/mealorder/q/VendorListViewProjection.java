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

package javaclasses.mealorder.q;

import io.spine.core.Subscribe;
import io.spine.server.projection.Projection;
import javaclasses.mealorder.VendorId;
import javaclasses.mealorder.VendorListId;
import javaclasses.mealorder.c.event.VendorAdded;
import javaclasses.mealorder.c.event.VendorUpdated;
import javaclasses.mealorder.q.projection.VendorListView;
import javaclasses.mealorder.q.projection.VendorListViewVBuilder;

import java.util.List;
import java.util.stream.IntStream;

/**
 * @author Alexander Karpets
 */
public class VendorListViewProjection extends Projection<VendorListId, VendorListView, VendorListViewVBuilder> {

    /**
     * {@link VendorListViewProjection} is a singleton.
     *
     * <p>The {@code ID} value should be the same for all JVMs
     * to support work with the same projection from execution to execution.
     */
    public static final VendorListId ID = VendorListId.newBuilder()
                                                      .setValue("VendorListViewProjectionSingleton")
                                                      .build();

    /**
     * Creates a new instance.
     *
     * @param id the ID for the new instance
     * @throws IllegalArgumentException if the ID is not of one of the supported types
     */
    public VendorListViewProjection(VendorListId id) {
        super(id);
    }

    @Subscribe
    public void on(VendorAdded event) {
        final VendorItem vendorItem = VendorItem.newBuilder()
                                                .setId(event.getVendorId())
                                                .setEmail(event.getEmail())
                                                .setVendorName(event.getVendorName())
                                                .addAllPhoneNumber(event.getPhoneNumberList())
                                                .setPoDailyDeadline(event.getPoDailyDeadline())
                                                .build();
        getBuilder().addVendor(vendorItem);
    }

    @Subscribe
    public void on(VendorUpdated event) {
        final VendorItem vendorItem = VendorItem.newBuilder()
                                                .setId(event.getVendorId())
                                                .setEmail(event.getVendorChange()
                                                               .getNewEmail())
                                                .setVendorName(event.getVendorChange()
                                                                    .getNewVendorName())
                                                .addAllPhoneNumber(event.getVendorChange()
                                                                        .getNewPhoneNumberList())
                                                .setPoDailyDeadline(event.getVendorChange()
                                                                         .getNewPoDailyDeadline())
                                                .build();
        getBuilder().setVendor(getVendorById(event.getVendorId()), vendorItem);
    }

    private int getVendorById(VendorId vendorId) {
        final List<VendorItem> vendorList = getBuilder().getVendor();
        int index = IntStream.range(0, vendorList.size())
                             .filter(i -> vendorList.get(i)
                                                    .getId()
                                                    .equals(vendorId))
                             .findFirst()
                             .getAsInt();
        return index;
    }
}

