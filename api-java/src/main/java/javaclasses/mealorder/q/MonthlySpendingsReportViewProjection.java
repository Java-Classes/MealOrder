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
import javaclasses.mealorder.MenuListId;
import javaclasses.mealorder.PurchaseOrderId;
import javaclasses.mealorder.c.event.PurchaseOrderDelivered;
import javaclasses.mealorder.q.projection.MonthlySpendingsReportView;
import javaclasses.mealorder.q.projection.MonthlySpendingsReportViewVBuilder;

public class MonthlySpendingsReportViewProjection extends Projection<MenuListId, MonthlySpendingsReportView, MonthlySpendingsReportViewVBuilder> {

    /**
     * Creates a new instance.
     *
     * @param id the ID for the new instance
     * @throws IllegalArgumentException if the ID is not of one of the supported types
     */
    public MonthlySpendingsReportViewProjection(MenuListId id) {
        super(id);
    }

    @Subscribe
    void on(PurchaseOrderDelivered event) {
        final PurchaseOrderId taskId = event.getId();
//        final TaskEnrichment enrichment = getEnrichment(TaskEnrichment.class, context);
//        final Task task = enrichment.getTask();
//        final TaskItem view = TaskItem.newBuilder()
//                                      .setId(taskId)
//                                      .setDescription(task.getDescription())
//                                      .setDueDate(task.getDueDate())
//                                      .setPriority(task.getPriority())
//                                      .build();
//        addTaskItem(view);
    }
}
