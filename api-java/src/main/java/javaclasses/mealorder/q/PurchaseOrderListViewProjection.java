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
import javaclasses.mealorder.PurchaseOrderId;
import javaclasses.mealorder.PurchaseOrderListId;
import javaclasses.mealorder.PurchaseOrderStatus;
import javaclasses.mealorder.c.event.PurchaseOrderDelivered;
import javaclasses.mealorder.c.event.PurchaseOrderSent;
import javaclasses.mealorder.c.event.PurchaseOrderValidationFailed;
import javaclasses.mealorder.q.projection.PurchaseOrderListView;
import javaclasses.mealorder.q.projection.PurchaseOrderListViewVBuilder;

import java.util.List;
import java.util.stream.IntStream;

public class PurchaseOrderListViewProjection extends Projection<PurchaseOrderListId, PurchaseOrderListView, PurchaseOrderListViewVBuilder> {

    /**
     * {@link MenuCalendarViewProjection} is a singleton.
     *
     * <p>The {@code ID} value should be the same for all JVMs
     * to support work with the same projection from execution to execution.
     */
    public static final PurchaseOrderListId ID = PurchaseOrderListId.newBuilder()
                                                                    .setValue(
                                                                            "PurchaseOrderListViewProjectionSingleton")
                                                                    .build();

    /**
     * Creates a new instance.
     *
     * @param id the ID for the new instance
     * @throws IllegalArgumentException if the ID is not of one of the supported types
     */
    public PurchaseOrderListViewProjection(PurchaseOrderListId id) {
        super(id);
    }

    @Subscribe
    void on(PurchaseOrderSent event) {
        final PurchaseOrderItem purchaseOrderItem = PurchaseOrderItem.newBuilder()
                                                                     .setId(event.getPurchaseOrder()
                                                                                 .getId())
                                                                     .setPurchaseOrderStatus(
                                                                             PurchaseOrderStatus.SENT)
                                                                     .build();
        getBuilder().addPurchaseOrder(purchaseOrderItem);
    }

    @Subscribe
    void on(PurchaseOrderDelivered event) {
        final PurchaseOrderItem purchaseOrderItem = PurchaseOrderItem.newBuilder()
                                                                     .setId(event.getId())
                                                                     .setPurchaseOrderStatus(
                                                                             PurchaseOrderStatus.DELIVERED)
                                                                     .build();
        getBuilder().setPurchaseOrder(getPurchaseOrderItemById(event.getId()), purchaseOrderItem);

    }

    @Subscribe
    void on(PurchaseOrderValidationFailed event) {
        final PurchaseOrderItem purchaseOrderItem = PurchaseOrderItem.newBuilder()
                                                                     .setId(event.getId())
                                                                     .setPurchaseOrderStatus(
                                                                             PurchaseOrderStatus.INVALID)
                                                                     .build();
        getBuilder().setPurchaseOrder(getPurchaseOrderItemById(event.getId()), purchaseOrderItem);
    }

    private int getPurchaseOrderItemById(PurchaseOrderId id) {
        final List<PurchaseOrderItem> orderList = getBuilder().getPurchaseOrder();
        final int index = IntStream.range(0, orderList.size())
                                   .filter(i -> orderList.get(i)
                                                         .getId()
                                                         .equals(id))
                                   .findFirst()
                                   .getAsInt();
        return index;
    }
}
