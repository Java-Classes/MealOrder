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
import javaclasses.mealorder.c.aggregate.OrderAggregate;
import javaclasses.mealorder.c.aggregate.PurchaseOrderSender;
import javaclasses.mealorder.c.aggregate.ServiceFactory;
import javaclasses.mealorder.c.command.AddDishToOrder;
import javaclasses.mealorder.c.command.CreateOrder;
import javaclasses.mealorder.c.command.CreatePurchaseOrder;
import javaclasses.mealorder.c.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.spine.protobuf.TypeConverter.toMessage;
import static javaclasses.mealorder.OrderStatus.ORDER_PROCESSED;
import static javaclasses.mealorder.testdata.TestOrderCommandFactory.createOrderInstance;
import static javaclasses.mealorder.testdata.TestPurchaseOrderCommandFactory.createPurchaseOrderInstance;
import static javaclasses.mealorder.testdata.TestValues.DISH1;
import static javaclasses.mealorder.testdata.TestValues.MENU_ID;
import static javaclasses.mealorder.testdata.TestValues.ORDER_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * @author Vlad Kozachenko
 */
@DisplayName("OrderAggregate should react on PurchaseOrderCreated command and")
public class OrderProcessedTest extends OrderCommandTest {

    final CreateOrder createOrder = createOrderInstance(ORDER_ID, MENU_ID);
    final Command createOrderCommand = requestFactory.command()
                                                     .create(toMessage(createOrder));

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        commandBus.post(createOrderCommand, StreamObservers.noOpObserver());
    }

    @Test
    @DisplayName("mark order as processed")
    void testMarkOrderProcessed() {

        final AddDishToOrder addDishToOrder = AddDishToOrder.newBuilder()
                                                            .setDish(DISH1)
                                                            .setOrderId(ORDER_ID)
                                                            .build();
        final Command addDishToOrderCommand = requestFactory.command()
                                                            .create(toMessage(addDishToOrder));
        boundedContext.getCommandBus()
                      .post(addDishToOrderCommand, StreamObservers.noOpObserver());

        final CreatePurchaseOrder createPurchaseOrder = createPurchaseOrderInstance();
        final Command createPOCommand = requestFactory.command()
                                                      .create(createPurchaseOrder);
        ServiceFactory.setPoSenderInstance(mock(PurchaseOrderSender.class));

        boundedContext.getCommandBus()
                      .post(createPOCommand, StreamObservers.noOpObserver());

        final Optional<Repository> repositoryOptional = boundedContext.findRepository(Order.class);

        assertTrue(repositoryOptional.isPresent());
        assertTrue(repositoryOptional.get() instanceof OrderRepository);
        final OrderRepository orderRepository = (OrderRepository) repositoryOptional.get();

        final OrderAggregate order = orderRepository.find(ORDER_ID)
                                                    .get();

        assertEquals(ORDER_PROCESSED, order.getState()
                                           .getStatus());
    }
}
