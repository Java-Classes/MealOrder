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

package javaclasses.mealorder.c.aggregate.order;

import com.google.common.base.Optional;
import io.spine.core.Ack;
import io.spine.core.Command;
import io.spine.grpc.MemoizingObserver;
import io.spine.grpc.StreamObservers;
import io.spine.server.entity.Repository;
import javaclasses.mealorder.Order;
import javaclasses.mealorder.OrderStatus;
import javaclasses.mealorder.c.aggregate.OrderAggregate;
import javaclasses.mealorder.c.aggregate.VendorAggregate;
import javaclasses.mealorder.c.command.CreateOrder;
import javaclasses.mealorder.c.event.OrderCreated;
import javaclasses.mealorder.c.rejection.Rejections;
import javaclasses.mealorder.testdata.OrderTestEnv.MenuNotAvailableSubscriber;
import javaclasses.mealorder.testdata.OrderTestEnv.OrderAlreadyExistsSubscriber;
import javaclasses.mealorder.testdata.OrderTestEnv.OrderCreatedSubscriber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.spine.grpc.StreamObservers.memoizingObserver;
import static io.spine.protobuf.TypeConverter.toMessage;
import static javaclasses.mealorder.testdata.TestOrderCommandFactory.createOrderInstance;
import static javaclasses.mealorder.testdata.TestOrderCommandFactory.createOrderInstanceForNonExistenMenu;
import static javaclasses.mealorder.testdata.TestValues.DATE;
import static javaclasses.mealorder.testdata.TestValues.MENU_ID;
import static javaclasses.mealorder.testdata.TestValues.ORDER_ID;
import static javaclasses.mealorder.testdata.TestValues.USER_ID;
import static javaclasses.mealorder.testdata.TestValues.VENDOR_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * This class provides tests for the CreateOrder command.
 *
 * Before each test the necessary commands form {@link VendorAggregate} are executed.
 *
 * @author Vlad Kozachenko
 * @author Yurii Haidamaka
 */
public class CreateOrderTest extends OrderCommandTest{



    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    @Test
    @DisplayName("produce OrderCreated event")
    void produceEvent() {

        final CreateOrder createOrder = createOrderInstance(ORDER_ID, MENU_ID);

        final Command createOrderCommand = requestFactory.command()
                                                         .create(toMessage(createOrder));

        final OrderCreatedSubscriber orderCreatedSubscriber = new OrderCreatedSubscriber();

        eventBus.register(orderCreatedSubscriber);

        final MemoizingObserver<Ack> memoizingObserver = memoizingObserver();
        commandBus.post(createOrderCommand, memoizingObserver);

        assertTrue(memoizingObserver.isCompleted());

        OrderCreated event = (OrderCreated) orderCreatedSubscriber.getEventMessage();

        assertEquals(createOrder.getOrderId(), event.getOrderId());
        assertEquals(createOrder.getMenuId(), event.getMenuId());
    }

    @Test
    @DisplayName("create the order")
    void createOrder() {

        final CreateOrder createOrder = createOrderInstance(ORDER_ID, MENU_ID);

        final Command createOrderCommand = requestFactory.command()
                                                         .create(toMessage(createOrder));
        final MemoizingObserver<Ack> memoizingObserver = memoizingObserver();

        commandBus.post(createOrderCommand, memoizingObserver);

        assertTrue(memoizingObserver.isCompleted());

        final Optional<Repository> repositoryOptional = boundedContext.findRepository(
                Order.class);

        assertTrue(repositoryOptional.isPresent());

        final Optional<OrderAggregate> orderAggregateOptional
                = repositoryOptional.get()
                                    .find(createOrder.getOrderId());

        assertTrue(orderAggregateOptional.isPresent());

        final Order state = orderAggregateOptional.get()
                                                  .getState();

        assertEquals(createOrder.getOrderId(), state.getId());
        assertEquals(OrderStatus.ORDER_ACTIVE, state.getStatus());
    }

    @Test
    @DisplayName("throw OrderAlreadyExists rejection")
    void notCreateOrder() {

        final CreateOrder createOrder = createOrderInstance(ORDER_ID, MENU_ID);

        final Command createOrderCommand = requestFactory.command()
                                                         .create(toMessage(createOrder));

        final OrderAlreadyExistsSubscriber orderAlreadyExistsSubscriber
                = new OrderAlreadyExistsSubscriber();

        rejectionBus.register(orderAlreadyExistsSubscriber);

        assertNull(OrderAlreadyExistsSubscriber.getRejection());

        commandBus.post(createOrderCommand, StreamObservers.noOpObserver());
        commandBus.post(createOrderCommand, StreamObservers.noOpObserver());

        assertNotNull(OrderAlreadyExistsSubscriber.getRejection());

        Rejections.OrderAlreadyExists orderAlreadyExists
                = OrderAlreadyExistsSubscriber.getRejection();

        assertEquals(ORDER_ID, orderAlreadyExists.getOrderId());
    }

    @Test
    @DisplayName("throw MenuNotAvailable rejection")
    void throwMenuNotAvailable() {

        final Command createOrderCmd =
                requestFactory.command()
                              .create(toMessage(createOrderInstanceForNonExistenMenu()));

        final MenuNotAvailableSubscriber menuNotAvailableSubscriber = new MenuNotAvailableSubscriber();

        rejectionBus.register(menuNotAvailableSubscriber);

        assertNull(MenuNotAvailableSubscriber.getRejection());

        commandBus.post(createOrderCmd, StreamObservers.noOpObserver());

        assertNotNull(MenuNotAvailableSubscriber.getRejection());

        Rejections.MenuNotAvailable menuNotAvailable = MenuNotAvailableSubscriber.getRejection();

        assertEquals(USER_ID, menuNotAvailable.getUserId());
        assertEquals(DATE, menuNotAvailable.getOrderDate());
        assertEquals(VENDOR_ID, menuNotAvailable.getVendorId());
    }
}
