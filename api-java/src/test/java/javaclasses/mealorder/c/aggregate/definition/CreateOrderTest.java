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

package javaclasses.mealorder.c.aggregate.definition;

import com.google.common.base.Throwables;
import com.google.protobuf.Message;
import io.spine.client.ActorRequestFactory;
import io.spine.client.TestActorRequestFactory;
import io.spine.core.Ack;
import io.spine.core.Command;
import io.spine.grpc.MemoizingObserver;
import io.spine.grpc.StreamObservers;
import io.spine.protobuf.AnyPacker;
import io.spine.server.BoundedContext;
import io.spine.server.commandbus.CommandBus;
import io.spine.server.rejection.RejectionBus;
import javaclasses.mealorder.MenuId;
import javaclasses.mealorder.Order;
import javaclasses.mealorder.OrderId;
import javaclasses.mealorder.c.command.CreateOrder;
import javaclasses.mealorder.c.context.BoundedContexts;
import javaclasses.mealorder.c.event.OrderCreated;
import javaclasses.mealorder.c.rejection.OrderAlreadyExists;
import javaclasses.mealorder.c.rejection.Rejections;
import javaclasses.mealorder.testdata.TestVendorCommandFactory;
import javaclasses.mealorder.testdata.VendorTestEnv.VendorRejectionsSubscriber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.spine.grpc.StreamObservers.memoizingObserver;
import static io.spine.protobuf.TypeConverter.toMessage;
import static io.spine.server.aggregate.AggregateMessageDispatcher.dispatchCommand;
import static javaclasses.mealorder.testdata.TestOrderCommandFactory.ORDER_DATE;
import static javaclasses.mealorder.testdata.TestOrderCommandFactory.ORDER_ID;
import static javaclasses.mealorder.testdata.TestOrderCommandFactory.createOrderInstance;
import static javaclasses.mealorder.testdata.TestVendorCommandFactory.MENU_ID_2;
import static javaclasses.mealorder.testdata.TestVendorCommandFactory.USER_ID;
import static javaclasses.mealorder.testdata.TestVendorCommandFactory.VENDOR_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Vlad Kozachenko
 * @author Yurii Haidamaka
 */
public class CreateOrderTest extends OrderCommandTest<CreateOrder> {

    private final ActorRequestFactory requestFactory =
            TestActorRequestFactory.newInstance(getClass());

    private final BoundedContext boundedContext = BoundedContexts.create();

    private final CommandBus commandBus = boundedContext.getCommandBus();
    private final RejectionBus rejectionBus = boundedContext.getRejectionBus();

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
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

    @Test
    @DisplayName("produce OrderCreated event")
    public void produceEvent() {
        final CreateOrder createOrderCmd = createOrderInstance(ORDER_ID,
                                                               MenuId.getDefaultInstance());
        final List<? extends Message> messageList = dispatchCommand(aggregate,
                                                                    envelopeOf(createOrderCmd));

        assertNotNull(aggregate.getState());
        assertNotNull(aggregate.getId());
        assertEquals(1, messageList.size());
        assertEquals(OrderCreated.class, messageList.get(0)
                                                    .getClass());

    }

    @Test
    @DisplayName("create the order")
    public void createOrder() {

        final MenuId menuId = MenuId.getDefaultInstance();

        final CreateOrder createOrder = createOrderInstance(ORDER_ID, menuId);

        final Command createOrderCommand = requestFactory.command()
                                                         .create(toMessage(createOrder));
        final MemoizingObserver<Ack> memoizingObserver = memoizingObserver();
        commandBus.post(createOrderCommand, memoizingObserver);

        assertTrue(memoizingObserver.isCompleted());
        System.out.println(memoizingObserver.firstResponse()
                                            .getMessageId());
        final Order oder = AnyPacker.unpack((memoizingObserver.firstResponse().));

//        final Order state = aggregate.getState();
//        assertEquals(state.getId(), createOrder.getOrderId());
//        assertEquals(OrderStatus.ORDER_ACTIVE, aggregate.getState()
//                                                        .getStatus());
    }

    @Test
    @DisplayName("throw OrderAlreadyExists rejection")
    public void notCreateOrder() {

        final CreateOrder createOrderCmd = createOrderInstance(ORDER_ID,
                                                               MenuId.getDefaultInstance());

        dispatchCommand(aggregate, envelopeOf(createOrderCmd));

        final Throwable t = assertThrows(Throwable.class,
                                         () -> dispatchCommand(aggregate,
                                                               envelopeOf(createOrderCmd)));
        final Throwable cause = Throwables.getRootCause(t);
        final OrderAlreadyExists rejection = (OrderAlreadyExists) cause;
        assertEquals(rejection.getMessageThrown()
                              .getOrderId(), ORDER_ID);
    }

    @Test
    @DisplayName("throw MenuNotAvailable rejection")
    public void throwMenuNotAvailable() {

        final Command createOrderCmd =
                requestFactory.command()
                              .create(toMessage(createOrderInstance(OrderId.newBuilder()
                                                                           .setUserId(
                                                                                   USER_ID)
                                                                           .setVendorId(
                                                                                   VENDOR_ID)
                                                                           .setOrderDate(ORDER_DATE)
                                                                           .build(), MENU_ID_2)));

        final VendorRejectionsSubscriber vendorRejectionsSubscriber = new VendorRejectionsSubscriber();

        rejectionBus.register(vendorRejectionsSubscriber);

        assertNull(VendorRejectionsSubscriber.getRejection());

        commandBus.post(createOrderCmd, StreamObservers.noOpObserver());

        Rejections.MenuNotAvailable menuNotAvailable = VendorRejectionsSubscriber.getRejection();

        assertEquals(USER_ID, menuNotAvailable.getUserId());
        assertEquals(ORDER_DATE, menuNotAvailable.getOrderDate());
        assertEquals(VENDOR_ID, menuNotAvailable.getVendorId());
    }

}
