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

package javaclasses.mealorder.c.order;

import io.spine.client.ActorRequestFactory;
import io.spine.client.TestActorRequestFactory;
import io.spine.core.Command;
import io.spine.grpc.StreamObservers;
import io.spine.server.BoundedContext;
import io.spine.server.commandbus.CommandBus;
import io.spine.server.event.EventBus;
import io.spine.server.rejection.RejectionBus;
import javaclasses.mealorder.c.BoundedContexts;
import javaclasses.mealorder.testdata.TestVendorCommandFactory;

import static io.spine.protobuf.TypeConverter.toMessage;

/**
 * @author Vlad Kozachenko
 */
public class OrderCommandTest {

    final ActorRequestFactory requestFactory =
            TestActorRequestFactory.newInstance(getClass());

    final BoundedContext boundedContext = BoundedContexts.create();

    final CommandBus commandBus = boundedContext.getCommandBus();
    final RejectionBus rejectionBus = boundedContext.getRejectionBus();
    final EventBus eventBus = boundedContext.getEventBus();

    public void setUp() {
        executeVendorCommands(requestFactory, commandBus);
    }

    private void executeVendorCommands(ActorRequestFactory requestFactory, CommandBus commandBus) {

        final Command addVendor = requestFactory.command()
                                                .create(toMessage(
                                                        TestVendorCommandFactory.addVendorInstance()));

        commandBus.post(addVendor, StreamObservers.noOpObserver());

        final Command importMenu = requestFactory.command()
                                                 .create(toMessage(
                                                         TestVendorCommandFactory.importMenuInstance()));
        commandBus.post(importMenu, StreamObservers.noOpObserver());

        final Command setDateRangeForMenu = requestFactory.command()
                                                          .create(toMessage(
                                                                  TestVendorCommandFactory.setDateRangeForMenuInstance()));
        commandBus.post(setDateRangeForMenu, StreamObservers.noOpObserver());
    }
}
