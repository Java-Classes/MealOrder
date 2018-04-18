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
import javaclasses.mealorder.MenuId;
import javaclasses.mealorder.c.event.DateRangeForMenuSet;
import javaclasses.mealorder.c.event.MenuImported;
import javaclasses.mealorder.c.event.PurchaseOrderCreated;
import javaclasses.mealorder.q.projection.MenuListView;
import javaclasses.mealorder.q.projection.MenuListViewVBuilder;

public class MenuListViewProjection extends Projection<MenuId, MenuListView, MenuListViewVBuilder> {

    /**
     * Creates a new instance.
     *
     * @param id the ID for the new instance
     * @throws IllegalArgumentException if the ID is not of one of the supported types
     */
    public MenuListViewProjection(MenuId id) {
        super(id);
    }

    @Subscribe
    void on(MenuImported event) {
        getBuilder().clearBookItem()
                    .clearBookItem();
        getBuilder().clearBookSynopsis()
                    .clearBookSynopsis();
    }

    @Subscribe
    void on(DateRangeForMenuSet event) {
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
    void on(PurchaseOrderCreated event) {
        BookItem bookItem = BookItem.newBuilder()
                                    .setBookId(event.getBookId())
                                    .setBookCoverUrl(event.getBookDetailsChange()
                                                          .getNewBookDetails()
                                                          .getBookCoverUrl())
                                    .setTitle(event.getBookDetailsChange()
                                                   .getNewBookDetails()
                                                   .getTitle())
                                    .addAllCategories(event.getBookDetailsChange()
                                                           .getNewBookDetails()
                                                           .getCategoriesList())
                                    .setAuthor(event.getBookDetailsChange()
                                                    .getNewBookDetails()
                                                    .getAuthor())
                                    .setAvailable(true)
                                    .build();

        getBuilder().setBookItem(bookItem);
        getBuilder().setBookSynopsis(event.getBookDetailsChange()
                                          .getNewBookDetails()
                                          .getSynopsis());
    }
}
