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

package javaclasses.mealorder.q.projection;

import javaclasses.mealorder.c.event.PurchaseOrderSent;
import javaclasses.mealorder.q.PurchaseOrderListViewProjection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class PurchaseOrderListViewProjectionTest extends ProjectionTest {

    private PurchaseOrderListViewProjection projection;

    @BeforeEach
    void setUp() {
        projection = new PurchaseOrderListViewProjection(PurchaseOrderListViewProjection.ID);
    }

    @Nested
    @DisplayName("TaskCreated event should be interpreted by MyListViewProjection and")
    class TaskCreatedEvent {

        @Test
        @DisplayName("add TaskItem to MyListView")
        void addView() {
            final PurchaseOrderSent taskCreatedEvent = taskCreatedInstance();
            dispatch(projection, createEvent(taskCreatedEvent));

            final List<TaskItem> views = projection.getState()
                                                   .getMyList()
                                                   .getItemsList();
            assertEquals(1, views.size());

            final TaskItem view = views.get(0);
            assertEquals(TASK_PRIORITY, view.getPriority());
            assertEquals(DESCRIPTION, view.getDescription()
                                          .getValue());
        }
    }
}
