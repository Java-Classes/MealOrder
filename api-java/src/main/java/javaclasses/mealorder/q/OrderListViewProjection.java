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
import javaclasses.mealorder.OrderId;
import javaclasses.mealorder.c.event.DishAddedToOrder;
import javaclasses.mealorder.c.event.DishRemovedFromOrder;
import javaclasses.mealorder.c.event.OrderCanceled;
import javaclasses.mealorder.c.event.OrderProcessed;
import javaclasses.mealorder.q.projection.OrderListView;
import javaclasses.mealorder.q.projection.OrderListViewVBuilder;

public class OrderListViewProjection extends Projection<OrderId, OrderListView, OrderListViewVBuilder> {

    /**
     * Creates a new instance.
     *
     * @param id the ID for the new instance
     * @throws IllegalArgumentException if the ID is not of one of the supported types
     */
    public OrderListViewProjection(OrderId id) {
        super(id);
    }

    @Subscribe
    void on(DishAddedToOrder event) {
        getBuilder().clearBookItem()
                    .clearBookItem();
        getBuilder().clearBookSynopsis()
                    .clearBookSynopsis();
    }

    @Subscribe
    void on(DishRemovedFromOrder event) {
        BookItem bookItem = BookItem.newBuilder()
                                    .setBookId(event.getBookId())
                                    .setBookCoverUrl(event.getDetails()
                                                          .getBookCoverUrl())
                                    .setTitle(event.getDetails()
                                                   .getTitle())
                                    .addAllCategories(event.getDetails()
                                                           .getCategoriesList())
                                    .setAuthor(event.getDetails()
                                                    .getAuthor())
                                    .setAvailable(true)
                                    .build();

        getBuilder().setBookItem(bookItem);
        getBuilder().setBookSynopsis(event.getDetails()
                                          .getSynopsis());
    }

    @Subscribe
    void on(OrderCanceled event) {
        getBuilder().setBookItem(bookItem);
        getBuilder().setBookSynopsis(event.getBookDetailsChange()
                                          .getNewBookDetails()
                                          .getSynopsis());
    }

    @Subscribe
    void on(OrderProcessed event) {
        getBuilder().setBookItem(bookItem);
        getBuilder().setBookSynopsis(event.getBookDetailsChange()
                                          .getNewBookDetails()
                                          .getSynopsis());
    }
}
