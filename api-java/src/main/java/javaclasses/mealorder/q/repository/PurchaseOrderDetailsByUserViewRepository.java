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

package javaclasses.mealorder.q.repository;

import io.spine.server.projection.ProjectionRepository;
import io.spine.server.route.EventRouting;
import javaclasses.mealorder.PurchaseOrderId;
import javaclasses.mealorder.c.event.PurchaseOrderCreated;
import javaclasses.mealorder.c.event.PurchaseOrderDelivered;
import javaclasses.mealorder.c.event.PurchaseOrderSent;
import javaclasses.mealorder.c.event.PurchaseOrderValidationFailed;
import javaclasses.mealorder.q.PurchaseOrderDetailsByUserViewProjection;
import javaclasses.mealorder.q.projection.PurchaseOrderDetailsByUserView;

import static java.util.Collections.singleton;

@SuppressWarnings("Duplicates") /* Has the same logic for routing as
                                   PurchaseOrderDetailsByDishViewRepository*/
public class PurchaseOrderDetailsByUserViewRepository extends ProjectionRepository<PurchaseOrderId, PurchaseOrderDetailsByUserViewProjection, PurchaseOrderDetailsByUserView> {

    public PurchaseOrderDetailsByUserViewRepository() {
        super();
        setUpEventRoute();
    }

    /**
     * Adds the {@link io.spine.server.route.EventRoute EventRoute}s to the repository.
     *
     * <p>Override this method in successor classes, otherwise all successors will use
     * {@code MyListViewProjection.ID}.
     */
    protected void setUpEventRoute() {

        final EventRouting<PurchaseOrderId> routing = getEventRouting();
        routing.route(PurchaseOrderCreated.class,
                      (message, context) -> {
                          final PurchaseOrderId purchaseOrderId = PurchaseOrderId.newBuilder()
                                                                                 .setVendorId(
                                                                                         message.getId()
                                                                                                .getVendorId())
                                                                                 .setPoDate(
                                                                                         message.getId()
                                                                                                .getPoDate())
                                                                                 .build();
                          return singleton(purchaseOrderId);
                      });
        routing.route(PurchaseOrderDelivered.class,
                      (message, context) -> {
                          final PurchaseOrderId purchaseOrderId = PurchaseOrderId.newBuilder()
                                                                                 .setVendorId(
                                                                                         message.getId()
                                                                                                .getVendorId())
                                                                                 .setPoDate(
                                                                                         message.getId()
                                                                                                .getPoDate())
                                                                                 .build();
                          return singleton(purchaseOrderId);
                      });
        routing.route(PurchaseOrderSent.class,
                      (message, context) -> {
                          final PurchaseOrderId purchaseOrderId = PurchaseOrderId.newBuilder()
                                                                                 .setVendorId(
                                                                                         message.getPurchaseOrder()
                                                                                                .getId()
                                                                                                .getVendorId())
                                                                                 .setPoDate(
                                                                                         message.getPurchaseOrder()
                                                                                                .getId()
                                                                                                .getPoDate())
                                                                                 .build();
                          return singleton(purchaseOrderId);
                      });
        routing.route(PurchaseOrderValidationFailed.class,
                      (message, context) -> {
                          final PurchaseOrderId purchaseOrderId = PurchaseOrderId.newBuilder()
                                                                                 .setVendorId(
                                                                                         message.getId()
                                                                                                .getVendorId())
                                                                                 .setPoDate(
                                                                                         message.getId()
                                                                                                .getPoDate())
                                                                                 .build();
                          return singleton(purchaseOrderId);
                      });
    }
}
