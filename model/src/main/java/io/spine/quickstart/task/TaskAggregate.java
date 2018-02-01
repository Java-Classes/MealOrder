/*
 *
 * Copyright 2016, TeamDev Ltd. All rights reserved.
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
 *
 */
package io.spine.quickstart.task;

import io.spine.quickstart.Task;
import io.spine.quickstart.TaskVBuilder;
import io.spine.quickstart.c.CreateTask;
import io.spine.quickstart.c.TaskCreated;
import io.spine.server.aggregate.Aggregate;
import io.spine.server.aggregate.Apply;
import io.spine.server.command.Assign;
import io.spine.serverapp.TaskId;

/**
 * Definition of the {@code Task} aggregate.
 *
 * <p>Within this small example it only handles a single command and emits one event.
 *
 * @author Alex Tymchenko
 */
final class TaskAggregate extends Aggregate<TaskId, Task, TaskVBuilder> {

    TaskAggregate(TaskId id) {
        super(id);
    }

    @SuppressWarnings("unused")     // The method is called by Spine via reflection.
    @Assign
    public TaskCreated handle(CreateTask cmd) {
        final TaskCreated result = TaskCreated.newBuilder()
                                              .setTitle(cmd.getTitle())
                                              .setId(cmd.getId())
                                              .build();
        return result;
    }

    @SuppressWarnings("unused")     // The method is called by Spine via reflection.
    @Apply
    public void on(TaskCreated event) {
        getBuilder().setTitle(event.getTitle());
    }
}
