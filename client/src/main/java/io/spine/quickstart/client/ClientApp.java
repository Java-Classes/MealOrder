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
package io.spine.quickstart.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.spine.Identifier;
import io.spine.client.ActorRequestFactory;
import io.spine.client.Query;
import io.spine.client.QueryResponse;
import io.spine.client.grpc.CommandServiceGrpc;
import io.spine.client.grpc.QueryServiceGrpc;
import io.spine.core.Ack;
import io.spine.core.Command;
import io.spine.core.UserId;
import io.spine.quickstart.Task;
import io.spine.quickstart.c.CreateTask;
import io.spine.serverapp.TaskId;
import io.spine.string.Stringifiers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A template of a client for Spine-powered server.
 *
 * <p>Illustrates a simple flow:
 *
 * <ul>
 *      <li>establishes a connection to the gRPC server;
 *      <li>sends a command to create a task through {@code CommandService};
 *      <li>verifies that the task is created by asking for all tasks via {@code QueryService}.
 * </ul>
 *
 * @author Alex Tymchenko
 */
public class ClientApp {

    /**
     * A host of the gRPC server to connect.
     */
    private static final String HOST = "127.0.0.1";

    /**
     * A port at which gRPC server listens for connections.
     */
    private static final int PORT = 8484;

    /**
     * Prevent this class from instantiation.
     */
    private ClientApp() {}

    /**
     * Connects to the gRPC server and interacts with it via exposed gRPC services.
     *
     * <p>Uses the hard-coded {@linkplain #HOST host} and {@linkplain #PORT port} for simplicity.
     */
    public static void main(String[] args) throws Exception {

        // Connect to the server and init the client-side stubs for gRPC services.
        log().info("Connecting to the server at {}:{}", HOST, PORT);

        final ManagedChannel channel = ManagedChannelBuilder.forAddress(HOST, PORT)
                                                            .usePlaintext(true)
                                                            .build();
        final CommandServiceGrpc.CommandServiceBlockingStub clientCommandService =
                CommandServiceGrpc.newBlockingStub(channel);

        final QueryServiceGrpc.QueryServiceBlockingStub queryClientService =
                QueryServiceGrpc.newBlockingStub(channel);

        // Create and post a command.
        final ActorRequestFactory requestFactory = ActorRequestFactory.newBuilder()
                                                                      .setActor(whoIsCalling())
                                                                      .build();
        final CreateTask createTask = newCreateTaskMsg("Wash my car");
        final Command cmd = requestFactory.command()
                                          .create(createTask);

        final Ack acked = clientCommandService.post(cmd);
        log().info("A command has been posted: " + Stringifiers.toString(createTask));
        log().info("(command acknowledgement: {})", Stringifiers.toString(acked));

        /*
         * Events, reflecting the changes caused by a command, travel from the write-side
         * to the read-side asynchronously.
         * Therefore some time should pass for the read-side to reflect the changes made.
         */
        Thread.sleep(100);

        // Create and post a query.
        final Query readAllTasks = requestFactory.query()
                                                 .all(Task.class);

        log().info("Reading all tasks...");
        final QueryResponse response = queryClientService.read(readAllTasks);
        log().info("A response received: {}", Stringifiers.toString(response));
    }

    /**
     * Creates a message for the {@link CreateTask} command.
     *
     * @param title value to use for a title in the new tasl
     * @return the message for {@code CreateTask} command
     */
    private static CreateTask newCreateTaskMsg(String title) {
        final TaskId newTaskId = TaskId.newBuilder()
                                       .setValue(Identifier.newUuid())
                                       .build();
        final CreateTask message = CreateTask.newBuilder()
                                             .setId(newTaskId)
                                             .setTitle(title)
                                             .build();
        return message;
    }

    /**
     * Obtains an ID of the user, who posts commands.
     *
     * <p>Generated randomly each time for simplicity.
     *
     * <p>Must be substituted with a real {@code UserId} in a production application.
     */
    private static UserId whoIsCalling() {
        final UserId actorId = UserId.newBuilder()
                                     .setValue(Identifier.newUuid())
                                     .build();
        return actorId;
    }

    /**
     * A singleton logger to use in scope of this application.
     */
    private enum LogSingleton {
        INSTANCE;

        @SuppressWarnings("NonSerializableFieldInSerializableClass")
        private final Logger value = LoggerFactory.getLogger(ClientApp.class);
    }

    private static Logger log() {
        return LogSingleton.INSTANCE.value;
    }
}
