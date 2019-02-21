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

import com.google.common.base.Optional;
import io.spine.core.Ack;
import io.spine.core.Command;
import io.spine.grpc.MemoizingObserver;
import io.spine.server.entity.Repository;
import javaclasses.mealorder.Order;
import javaclasses.mealorder.c.command.CancelOrder;
import javaclasses.mealorder.c.command.CreateOrder;
import javaclasses.mealorder.c.event.OrderCreated;
import javaclasses.mealorder.c.rejection.Rejections;
import javaclasses.mealorder.c.vendor.VendorAggregate;
import javaclasses.mealorder.testdata.OrderTestEnv.MenuNotAvailableSubscriber;
import javaclasses.mealorder.testdata.OrderTestEnv.OrderAlreadyExistsSubscriber;
import javaclasses.mealorder.testdata.OrderTestEnv.OrderCreatedSubscriber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.spine.grpc.StreamObservers.memoizingObserver;
import static io.spine.grpc.StreamObservers.noOpObserver;
import static javaclasses.mealorder.OrderStatus.ORDER_ACTIVE;
import static javaclasses.mealorder.testdata.TestOrderCommandFactory.cancelOrderInstance;
import static javaclasses.mealorder.testdata.TestOrderCommandFactory.createOrderInstance;
import static javaclasses.mealorder.testdata.TestOrderCommandFactory.createOrderInstanceForNonExistentMenu;
import static javaclasses.mealorder.testdata.TestOrderCommandFactory.createOrderInstanceForNonExistentVendor;
import static javaclasses.mealorder.testdata.TestOrderCommandFactory.createOrderInstanceWithInvalidDate;
import static javaclasses.mealorder.testdata.TestValues.FAR_FUTURE;
import static javaclasses.mealorder.testdata.TestValues.INVALID_VENDOR_ID;
import static javaclasses.mealorder.testdata.TestValues.ORDER_ID;
import static javaclasses.mealorder.testdata.TestValues.TOMORROW;
import static javaclasses.mealorder.testdata.TestValues.USER_ID;
import static javaclasses.mealorder.testdata.TestValues.VENDOR_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * This class provides tests for the CreateOrder command.
 *
 * <p>Before each test the necessary commands form {@link VendorAggregate} are executed.
 *
 * @author Vlad Kozachenko
 * @author Yurii Haidamaka
 */
@DisplayName("`CreateOrder` command should be interpreted by `OrderAggregate` and ")
public class CreateOrderTest extends OrderCommandTest {

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    @Test
    @DisplayName("produce `OrderCreated` event")
    void produceEvent() {
        final CreateOrder createOrder = createOrderInstance();

        final Command createOrderCommand = requestFactory.command()
                                                         .create(createOrder);

        final OrderCreatedSubscriber orderCreatedSubscriber = new OrderCreatedSubscriber();

        eventBus.register(orderCreatedSubscriber);

        final MemoizingObserver<Ack> memoizingObserver = memoizingObserver();
        commandBus.post(createOrderCommand, memoizingObserver);

        assertTrue(memoizingObserver.isCompleted());

        final OrderCreated event = (OrderCreated) orderCreatedSubscriber.getEventMessage();

        assertEquals(createOrder.getOrderId(), event.getOrderId());
        assertEquals(createOrder.getMenuId(), event.getMenuId());
    }

    @Test
    @DisplayName("create the order")
    void createOrder() {
        final CreateOrder createOrder = createOrderInstance();

        final Command createOrderCommand = requestFactory.command()
                                                         .create(createOrder);
        final MemoizingObserver<Ack> memoizingObserver = memoizingObserver();

        commandBus.post(createOrderCommand, memoizingObserver);
        assertTrue(memoizingObserver.isCompleted());

        final Optional<Repository> repositoryOptional =
                boundedContext.findRepository(Order.class);
        assertTrue(repositoryOptional.isPresent());

        final Optional<OrderAggregate> orderAggregateOptional =
                repositoryOptional.get()
                                  .find(createOrder.getOrderId());
        assertTrue(orderAggregateOptional.isPresent());

        final Order state = orderAggregateOptional.get()
                                                  .getState();
        assertEquals(createOrder.getOrderId(), state.getId());
        assertEquals(ORDER_ACTIVE, state.getStatus());
    }

    @Test
    @DisplayName("create the order after its cancellation")
    void createOrderAfterCancellation() {
        final CreateOrder createOrder = createOrderInstance();
        final Command createOrderCommand = requestFactory.command()
                                                         .create(createOrder);
        final CancelOrder cancelOrder = cancelOrderInstance();
        final Command cancelOrderCommand = requestFactory.command()
                                                         .create(cancelOrder);
        final MemoizingObserver<Ack> memoizingObserver = memoizingObserver();

        commandBus.post(createOrderCommand, noOpObserver());
        commandBus.post(cancelOrderCommand, noOpObserver());
        commandBus.post(createOrderCommand, memoizingObserver);
        assertTrue(memoizingObserver.isCompleted());

        final Optional<Repository> repositoryOptional =
                boundedContext.findRepository(Order.class);
        assertTrue(repositoryOptional.isPresent());

        final Optional<OrderAggregate> orderAggregateOptional =
                repositoryOptional.get()
                                  .find(createOrder.getOrderId());
        assertTrue(orderAggregateOptional.isPresent());

        final Order state = orderAggregateOptional.get()
                                                  .getState();
        assertEquals(createOrder.getOrderId(), state.getId());
        assertEquals(ORDER_ACTIVE, state.getStatus());
    }

    @Test
    @DisplayName("throw `OrderAlreadyExists` rejection")
    void notCreateOrder() {
        final CreateOrder createOrder = createOrderInstance();
        final Command createOrderCommand = requestFactory.command()
                                                         .create(createOrder);
        final OrderAlreadyExistsSubscriber orderAlreadyExistsSubscriber
                = new OrderAlreadyExistsSubscriber();

        rejectionBus.register(orderAlreadyExistsSubscriber);
        assertNull(OrderAlreadyExistsSubscriber.getRejection());

        commandBus.post(createOrderCommand, noOpObserver());
        commandBus.post(createOrderCommand, noOpObserver());
        assertNotNull(OrderAlreadyExistsSubscriber.getRejection());

        final Rejections.OrderAlreadyExists orderAlreadyExists
                = OrderAlreadyExistsSubscriber.getRejection();
        assertEquals(ORDER_ID, orderAlreadyExists.getOrderId());
    }

    @Test
    @DisplayName("throw `MenuNotAvailable` rejection if menu is absent")
    void throwMenuNotAvailableIfMenuAbsent() {
        final Command createOrderCmd =
                requestFactory.command()
                              .create(createOrderInstanceForNonExistentMenu());
        final MenuNotAvailableSubscriber menuNotAvailableSubscriber = new MenuNotAvailableSubscriber();
        MenuNotAvailableSubscriber.clear();

        rejectionBus.register(menuNotAvailableSubscriber);
        assertNull(MenuNotAvailableSubscriber.getRejection());

        commandBus.post(createOrderCmd, noOpObserver());
        assertNotNull(MenuNotAvailableSubscriber.getRejection());

        final Rejections.MenuNotAvailable menuNotAvailable = MenuNotAvailableSubscriber.getRejection();
        assertEquals(USER_ID, menuNotAvailable.getUserId());
        assertEquals(TOMORROW, menuNotAvailable.getOrderDate());
        assertEquals(VENDOR_ID, menuNotAvailable.getVendorId());
    }

    @Test
    @DisplayName("throw `MenuNotAvailable` rejection if the order date isn't in menu date range")
    void throwMenuNotAvailableIfOrderDateWrong() {
        final Command createOrderCmd =
                requestFactory.command()
                              .create(createOrderInstanceWithInvalidDate());
        final MenuNotAvailableSubscriber menuNotAvailableSubscriber =
                new MenuNotAvailableSubscriber();
        MenuNotAvailableSubscriber.clear();

        rejectionBus.register(menuNotAvailableSubscriber);
        assertNull(MenuNotAvailableSubscriber.getRejection());

        commandBus.post(createOrderCmd, noOpObserver());
        assertNotNull(MenuNotAvailableSubscriber.getRejection());

        final Rejections.MenuNotAvailable menuNotAvailable =
                MenuNotAvailableSubscriber.getRejection();
        assertEquals(USER_ID, menuNotAvailable.getUserId());
        assertEquals(FAR_FUTURE, menuNotAvailable.getOrderDate());
        assertEquals(VENDOR_ID, menuNotAvailable.getVendorId());
    }

    @Test
    @DisplayName("throw `MenuNotAvailable` rejection if the vendor doesn't exist ")
    void throwMenuNotAvailableIfVendorAbsent() {
        final Command createOrderCmd =
                requestFactory.command()
                              .create(createOrderInstanceForNonExistentVendor());
        final MenuNotAvailableSubscriber menuNotAvailableSubscriber
                = new MenuNotAvailableSubscriber();
        MenuNotAvailableSubscriber.clear();

        rejectionBus.register(menuNotAvailableSubscriber);
        assertNull(MenuNotAvailableSubscriber.getRejection());

        commandBus.post(createOrderCmd, noOpObserver());
        assertNotNull(MenuNotAvailableSubscriber.getRejection());

        final Rejections.MenuNotAvailable menuNotAvailable
                = MenuNotAvailableSubscriber.getRejection();
        assertEquals(USER_ID, menuNotAvailable.getUserId());
        assertEquals(TOMORROW, menuNotAvailable.getOrderDate());
        assertEquals(INVALID_VENDOR_ID, menuNotAvailable.getVendorId());
    }
}
