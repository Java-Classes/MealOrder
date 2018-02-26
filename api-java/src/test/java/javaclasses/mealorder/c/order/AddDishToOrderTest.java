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
import io.spine.core.Command;
import io.spine.server.entity.Repository;
import javaclasses.mealorder.Order;
import javaclasses.mealorder.VendorId;
import javaclasses.mealorder.c.command.AddDishToOrder;
import javaclasses.mealorder.c.command.CancelOrder;
import javaclasses.mealorder.c.command.CreateOrder;
import javaclasses.mealorder.c.event.DishAddedToOrder;
import javaclasses.mealorder.c.rejection.Rejections;
import javaclasses.mealorder.testdata.OrderTestEnv;
import javaclasses.mealorder.testdata.OrderTestEnv.CannotAddDishToNotActiveOrderSubscriber;
import javaclasses.mealorder.testdata.OrderTestEnv.DishAddedToOrderSubscriber;
import javaclasses.mealorder.testdata.OrderTestEnv.DishVendorMismatchSubscriber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.spine.grpc.StreamObservers.noOpObserver;
import static javaclasses.mealorder.OrderStatus.ORDER_ACTIVE;
import static javaclasses.mealorder.testdata.TestOrderCommandFactory.addDishToOrderInstance;
import static javaclasses.mealorder.testdata.TestOrderCommandFactory.cancelOrderInstance;
import static javaclasses.mealorder.testdata.TestOrderCommandFactory.createOrderInstance;
import static javaclasses.mealorder.testdata.TestValues.DISH1;
import static javaclasses.mealorder.testdata.TestValues.INVALID_DISH;
import static javaclasses.mealorder.testdata.TestValues.ORDER_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Vlad Kozachenko
 */
@DisplayName("`AddDishToOrder` command should be interpreted by `OrderAggregate` and ")
public class AddDishToOrderTest extends OrderCommandTest {

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        final CreateOrder createOrder = createOrderInstance();
        final Command createOrderCommand = requestFactory.command()
                                                         .create(createOrder);
        commandBus.post(createOrderCommand, noOpObserver());
    }

    @Test
    @DisplayName("produce `DishAddedToOrder` event")
    void produceEvent() {
        final AddDishToOrder addDishToOrder = addDishToOrderInstance();

        final Command addDishToOrderCommand = requestFactory.command()
                                                            .create(addDishToOrder);

        final DishAddedToOrderSubscriber dishAddedToOrderSubscriber =
                new DishAddedToOrderSubscriber();

        eventBus.register(dishAddedToOrderSubscriber);

        commandBus.post(addDishToOrderCommand, noOpObserver());

        final DishAddedToOrder event =
                (DishAddedToOrder) dishAddedToOrderSubscriber.getEventMessage();

        assertEquals(addDishToOrder.getOrderId(), event.getOrderId());
        assertEquals(addDishToOrder.getDish(), event.getDish());
    }

    @Test
    @DisplayName("add dish to order")
    void addDishToOrder() {
        final AddDishToOrder addDishToOrder = addDishToOrderInstance();

        final Command addDishToOrderCommand = requestFactory.command()
                                                            .create(addDishToOrder);

        commandBus.post(addDishToOrderCommand, noOpObserver());

        final Optional<Repository> repositoryOptional = boundedContext.findRepository(Order.class);

        assertTrue(repositoryOptional.isPresent());
        assertTrue(repositoryOptional.get() instanceof OrderRepository);
        final OrderRepository orderRepository = (OrderRepository) repositoryOptional.get();

        final Order order = orderRepository.find(ORDER_ID)
                                           .get()
                                           .getState();

        assertEquals(ORDER_ACTIVE, order.getStatus());
        assertEquals(addDishToOrder.getOrderId(), order.getId());
        assertEquals(addDishToOrder.getDish(), order.getDish(0));
    }

    @Test
    @DisplayName("throw `DishVendorMismatch` rejection")
    void notAddDish() {
        final AddDishToOrder addDishToOrder = addDishToOrderInstance(ORDER_ID, INVALID_DISH);
        final DishVendorMismatchSubscriber dishVendorMismatchSubscriber
                = new DishVendorMismatchSubscriber();
        final Command addDishToOrderCommand = requestFactory.command()
                                                            .create(addDishToOrder);

        rejectionBus.register(dishVendorMismatchSubscriber);
        assertNull(OrderTestEnv.DishVendorMismatchSubscriber.getRejection());

        commandBus.post(addDishToOrderCommand, noOpObserver());
        assertNotNull(OrderTestEnv.DishVendorMismatchSubscriber.getRejection());

        final Rejections.DishVendorMismatch dishVendorMismatch
                = OrderTestEnv.DishVendorMismatchSubscriber.getRejection();

        final VendorId expected = INVALID_DISH.getId()
                                              .getMenuId()
                                              .getVendorId();
        assertEquals(expected, dishVendorMismatch.getVendorMismatch()
                                                 .getActual());
        assertEquals(ORDER_ID.getVendorId(), dishVendorMismatch.getVendorMismatch()
                                                               .getTarget());
    }

    @Test
    @DisplayName("throw `CannotAddDishToNotActiveOrder` rejection")
    void notAddDishToNotActiveOrder() {
        final AddDishToOrder addDishToOrder = addDishToOrderInstance();
        final CancelOrder cancelOrder = cancelOrderInstance();

        final CannotAddDishToNotActiveOrderSubscriber rejectionSubscriber
                = new CannotAddDishToNotActiveOrderSubscriber();

        final Command cancelOrderCommand = requestFactory.command()
                                                         .create(cancelOrder);
        final Command addDishToOrderCommand = requestFactory.command()
                                                            .create(addDishToOrder);

        rejectionBus.register(rejectionSubscriber);
        assertNull(OrderTestEnv.CannotAddDishToNotActiveOrderSubscriber.getRejection());

        commandBus.post(cancelOrderCommand, noOpObserver());
        commandBus.post(addDishToOrderCommand, noOpObserver());
        assertNotNull(OrderTestEnv.CannotAddDishToNotActiveOrderSubscriber.getRejection());

        final Rejections.CannotAddDishToNotActiveOrder rejection
                = OrderTestEnv.CannotAddDishToNotActiveOrderSubscriber.getRejection();
        assertEquals(ORDER_ID, rejection.getOrderId());
        assertEquals(DISH1.getId(), rejection.getDishId());
    }
}
