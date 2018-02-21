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
import io.spine.core.Command;
import io.spine.grpc.StreamObservers;
import io.spine.server.entity.Repository;
import javaclasses.mealorder.Order;
import javaclasses.mealorder.c.command.AddDishToOrder;
import javaclasses.mealorder.c.command.CancelOrder;
import javaclasses.mealorder.c.command.CreateOrder;
import javaclasses.mealorder.c.command.RemoveDishFromOrder;
import javaclasses.mealorder.c.event.DishRemovedFromOrder;
import javaclasses.mealorder.c.rejection.Rejections;
import javaclasses.mealorder.c.repository.OrderRepository;
import javaclasses.mealorder.testdata.OrderTestEnv;
import javaclasses.mealorder.testdata.OrderTestEnv.CannotRemoveDishFromNotActiveOrderSubscriber;
import javaclasses.mealorder.testdata.OrderTestEnv.DishRemovedFromOrderSubscriber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.spine.protobuf.TypeConverter.toMessage;
import static javaclasses.mealorder.OrderStatus.ORDER_ACTIVE;
import static javaclasses.mealorder.testdata.TestOrderCommandFactory.addDishToOrderInstance;
import static javaclasses.mealorder.testdata.TestOrderCommandFactory.cancelOrderInstance;
import static javaclasses.mealorder.testdata.TestOrderCommandFactory.createOrderInstance;
import static javaclasses.mealorder.testdata.TestOrderCommandFactory.removeDishFromOrderInstance;
import static javaclasses.mealorder.testdata.TestValues.DISH1;
import static javaclasses.mealorder.testdata.TestValues.MENU_ID;
import static javaclasses.mealorder.testdata.TestValues.ORDER_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Vlad Kozachenko
 */
@DisplayName("RemoveDishFromOrder command should be interpreted by OrderAggregate and ")
public class RemoveDishFromOrderTest extends OrderCommandTest<CreateOrder> {

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        final CreateOrder createOrder = createOrderInstance(ORDER_ID, MENU_ID);
        final Command createOrderCommand = requestFactory.command()
                                                         .create(toMessage(createOrder));
        commandBus.post(createOrderCommand, StreamObservers.noOpObserver());
    }

    @Test
    @DisplayName("produce DishRemovedFromOrder event")
    public void produceEvent() {

        final DishRemovedFromOrderSubscriber eventSubscriber
                = new DishRemovedFromOrderSubscriber();
        eventBus.register(eventSubscriber);

        final AddDishToOrder addDishToOrder = addDishToOrderInstance(ORDER_ID, DISH1);
        final Command addDishToOrderCommand = requestFactory.command()
                                                            .create(toMessage(addDishToOrder));
        commandBus.post(addDishToOrderCommand, StreamObservers.noOpObserver());

        final RemoveDishFromOrder removeDishFromOrder = removeDishFromOrderInstance(ORDER_ID,
                                                                                    DISH1.getId());
        final Command removeDishFromOrderCommand = requestFactory.command()
                                                                 .create(toMessage(
                                                                         removeDishFromOrder));
        commandBus.post(removeDishFromOrderCommand, StreamObservers.noOpObserver());

        DishRemovedFromOrder event = (DishRemovedFromOrder) eventSubscriber.getEventMessage();

        assertEquals(removeDishFromOrder.getOrderId(), event.getOrderId());
        assertEquals(removeDishFromOrder.getDishId(), event.getDish()
                                                           .getId());
    }

    @Test
    @DisplayName("removes the dish from the order")
    public void removeDish() {

        final AddDishToOrder addDishToOrder = addDishToOrderInstance(ORDER_ID, DISH1);
        final Command addDishToOrderCommand = requestFactory.command()
                                                            .create(toMessage(addDishToOrder));
        commandBus.post(addDishToOrderCommand, StreamObservers.noOpObserver());

        final RemoveDishFromOrder removeDishFromOrder = removeDishFromOrderInstance(ORDER_ID,
                                                                                    DISH1.getId());
        final Command removeDishFromOrderCommand = requestFactory.command()
                                                                 .create(toMessage(
                                                                         removeDishFromOrder));
        commandBus.post(removeDishFromOrderCommand, StreamObservers.noOpObserver());

        final Optional<Repository> repositoryOptional = boundedContext.findRepository(Order.class);

        assertTrue(repositoryOptional.isPresent());
        assertTrue(repositoryOptional.get() instanceof OrderRepository);
        final OrderRepository orderRepository = (OrderRepository) repositoryOptional.get();

        final Order order = orderRepository.find(ORDER_ID)
                                           .get()
                                           .getState();

        assertEquals(ORDER_ACTIVE, order.getStatus());
        assertEquals(addDishToOrder.getOrderId(), order.getId());
        assertEquals(0, order.getDishCount());
    }

    @Test
    @DisplayName("throw CannotRemoveMissingDish rejection")
    public void notRemoveDish() {

        final OrderTestEnv.CannotRemoveMissingDishSubscriber rejectionSubscriber
                = new OrderTestEnv.CannotRemoveMissingDishSubscriber();

        rejectionBus.register(rejectionSubscriber);

        final RemoveDishFromOrder removeDishFromOrder = removeDishFromOrderInstance(ORDER_ID,
                                                                                    DISH1.getId());
        final Command removeDishFromOrderCommand = requestFactory.command()
                                                                 .create(toMessage(
                                                                         removeDishFromOrder));

        assertNull(OrderTestEnv.CannotRemoveMissingDishSubscriber.getRejection());

        commandBus.post(removeDishFromOrderCommand, StreamObservers.noOpObserver());

        assertNotNull(OrderTestEnv.CannotRemoveMissingDishSubscriber.getRejection());

        Rejections.CannotRemoveMissingDish rejection
                = OrderTestEnv.CannotRemoveMissingDishSubscriber.getRejection();

        assertEquals(ORDER_ID, rejection.getOrderId());
        assertEquals(DISH1.getId(), rejection.getDishId());
    }

    @Test
    @DisplayName("throw CannotRemoveDishFromNotActiveOrder rejection")
    public void notRemoveDishFromNotActiveOrder() {

        final CannotRemoveDishFromNotActiveOrderSubscriber rejectionSubscriber
                = new CannotRemoveDishFromNotActiveOrderSubscriber();

        rejectionBus.register(rejectionSubscriber);

        final AddDishToOrder addDishToOrder = addDishToOrderInstance(ORDER_ID, DISH1);
        final Command addDishToOrderCommand = requestFactory.command()
                                                            .create(toMessage(addDishToOrder));
        commandBus.post(addDishToOrderCommand, StreamObservers.noOpObserver());

        final CancelOrder cancelOrder = cancelOrderInstance(ORDER_ID);
        final Command cancelOrderCommand = requestFactory.command()
                                                         .create(toMessage(cancelOrder));
        commandBus.post(cancelOrderCommand, StreamObservers.noOpObserver());

        final RemoveDishFromOrder removeDishFromOrder = removeDishFromOrderInstance(ORDER_ID,
                                                                                    DISH1.getId());
        final Command removeDishFromOrderCommand = requestFactory.command()
                                                                 .create(toMessage(
                                                                         removeDishFromOrder));

        assertNull(OrderTestEnv.CannotRemoveDishFromNotActiveOrderSubscriber.getRejection());

        commandBus.post(removeDishFromOrderCommand, StreamObservers.noOpObserver());

        assertNotNull(OrderTestEnv.CannotRemoveDishFromNotActiveOrderSubscriber.getRejection());

        Rejections.CannotRemoveDishFromNotActiveOrder rejection
                = OrderTestEnv.CannotRemoveDishFromNotActiveOrderSubscriber.getRejection();

        assertEquals(ORDER_ID, rejection.getOrderId());
        assertEquals(DISH1.getId(), rejection.getDishId());
    }
}
