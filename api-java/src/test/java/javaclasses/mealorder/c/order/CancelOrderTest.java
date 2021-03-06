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
import javaclasses.mealorder.OrderStatus;
import javaclasses.mealorder.PurchaseOrderSender;
import javaclasses.mealorder.ServiceFactory;
import javaclasses.mealorder.c.command.AddDishToOrder;
import javaclasses.mealorder.c.command.CancelOrder;
import javaclasses.mealorder.c.command.CancelPurchaseOrder;
import javaclasses.mealorder.c.command.CreateOrder;
import javaclasses.mealorder.c.command.CreatePurchaseOrder;
import javaclasses.mealorder.c.event.OrderCanceled;
import javaclasses.mealorder.c.rejection.Rejections;
import javaclasses.mealorder.testdata.OrderTestEnv.CannotCancelProcessedOrderSubscriber;
import javaclasses.mealorder.testdata.OrderTestEnv.OrderCanceledSubscriber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.spine.grpc.StreamObservers.memoizingObserver;
import static io.spine.grpc.StreamObservers.noOpObserver;
import static javaclasses.mealorder.OrderStatus.ORDER_CANCELED;
import static javaclasses.mealorder.testdata.TestOrderCommandFactory.cancelOrderInstance;
import static javaclasses.mealorder.testdata.TestOrderCommandFactory.createOrderInstance;
import static javaclasses.mealorder.testdata.TestPurchaseOrderCommandFactory.cancelPOWithEmptyReasonInstance;
import static javaclasses.mealorder.testdata.TestPurchaseOrderCommandFactory.createPurchaseOrderInstance;
import static javaclasses.mealorder.testdata.TestValues.DISH1;
import static javaclasses.mealorder.testdata.TestValues.ORDER_ID;
import static javaclasses.mealorder.testdata.TestValues.USER_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * @author Vlad Kozachenko
 */
@DisplayName("`CancelOrder` command should be interpreted by `OrderAggregate` and ")
public class CancelOrderTest extends OrderCommandTest {

    final CreateOrder createOrder = createOrderInstance();
    final Command createOrderCommand = requestFactory.command()
                                                     .create(createOrder);

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        commandBus.post(createOrderCommand, noOpObserver());
    }

    @Test
    @DisplayName("produce `OrderCanceled` event")
    void produceEvent() {
        final CancelOrder cancelOrder = cancelOrderInstance();

        final Command cancelOrderCommand = requestFactory.command()
                                                         .create(cancelOrder);

        final OrderCanceledSubscriber orderCanceledSubscriber = new OrderCanceledSubscriber();

        eventBus.register(orderCanceledSubscriber);

        commandBus.post(cancelOrderCommand, noOpObserver());

        final OrderCanceled event = (OrderCanceled) orderCanceledSubscriber.getEventMessage();

        assertEquals(cancelOrder.getOrderId(), event.getOrderId());
        assertEquals(cancelOrder.getWhoCancels(), event.getWhoCanceled());
    }

    @Test
    @DisplayName("cancel order")
    void cancelOrder() {
        final CancelOrder cancelOrder = cancelOrderInstance();

        final Command cancelOrderCommand = requestFactory.command()
                                                         .create(cancelOrder);

        final MemoizingObserver<Ack> memoizingObserver = memoizingObserver();

        commandBus.post(cancelOrderCommand, memoizingObserver);

        assertTrue(memoizingObserver.isCompleted());

        final Optional<Repository> repositoryOptional = boundedContext.findRepository(
                Order.class);

        assertTrue(repositoryOptional.isPresent());

        final Optional<OrderAggregate> orderAggregateOptional
                = repositoryOptional.get()
                                    .find(cancelOrder.getOrderId());

        assertTrue(orderAggregateOptional.isPresent());

        final Order state = orderAggregateOptional.get()
                                                  .getState();

        assertEquals(createOrder.getOrderId(), state.getId());
        assertEquals(OrderStatus.ORDER_CANCELED, state.getStatus());
    }

    @Test
    @DisplayName("cancel order by react on `PurchaseOrderCanceled`")
    void testOrderCanceledByReact() {
        final AddDishToOrder addDishToOrder = AddDishToOrder.newBuilder()
                                                            .setDish(DISH1)
                                                            .setOrderId(ORDER_ID)
                                                            .build();
        final Command addDishToOrderCommand = requestFactory.command()
                                                            .create(addDishToOrder);
        commandBus.post(addDishToOrderCommand, noOpObserver());

        final CreatePurchaseOrder createPurchaseOrder = createPurchaseOrderInstance();
        final Command createPOCommand = requestFactory.command()
                                                      .create(createPurchaseOrder);
        ServiceFactory.setPoSenderInstance(mock(PurchaseOrderSender.class));

        commandBus.post(createPOCommand, noOpObserver());

        final CancelPurchaseOrder cancelPurchaseOrder = cancelPOWithEmptyReasonInstance();
        final Command cancelPurchaseOrderCommand = requestFactory.command()
                                                                 .create(cancelPurchaseOrder);
        commandBus.post(cancelPurchaseOrderCommand, noOpObserver());

        final Optional<Repository> repositoryOptional = boundedContext.findRepository(Order.class);

        assertTrue(repositoryOptional.isPresent());
        assertTrue(repositoryOptional.get() instanceof OrderRepository);
        final OrderRepository orderRepository = (OrderRepository) repositoryOptional.get();

        final OrderAggregate order = orderRepository.find(ORDER_ID)
                                                    .get();

        assertEquals(ORDER_CANCELED, order.getState()
                                          .getStatus());
    }

    @Test
    @DisplayName("throw `CannotCancelProcessedOrder` rejection")
    void throwsCannotCancelProcessedOrder() {
        final AddDishToOrder addDishToOrder = AddDishToOrder.newBuilder()
                                                            .setDish(DISH1)
                                                            .setOrderId(ORDER_ID)
                                                            .build();
        final Command addDishToOrderCommand = requestFactory.command()
                                                            .create(addDishToOrder);
        commandBus.post(addDishToOrderCommand, noOpObserver());

        final CreatePurchaseOrder createPurchaseOrder = createPurchaseOrderInstance();
        final Command createPOCommand = requestFactory.command()
                                                      .create(createPurchaseOrder);
        ServiceFactory.setPoSenderInstance(mock(PurchaseOrderSender.class));

        commandBus.post(createPOCommand, noOpObserver());

        final CancelOrder cancelOrder = cancelOrderInstance();

        final Command cancelOrderCommand = requestFactory.command()
                                                         .create(cancelOrder);

        final CannotCancelProcessedOrderSubscriber cannotCancelProcessedOrderSubscriber =
                new CannotCancelProcessedOrderSubscriber();

        rejectionBus.register(cannotCancelProcessedOrderSubscriber);

        assertNull(CannotCancelProcessedOrderSubscriber.getRejection());

        commandBus.post(cancelOrderCommand, noOpObserver());

        assertNotNull(CannotCancelProcessedOrderSubscriber.getRejection());

        final Rejections.CannotCancelProcessedOrder cannotCancelProcessedOrder =
                CannotCancelProcessedOrderSubscriber.getRejection();

        assertEquals(ORDER_ID, cannotCancelProcessedOrder.getOrderId());
        assertEquals(USER_ID, cannotCancelProcessedOrder.getUserId());
    }
}
