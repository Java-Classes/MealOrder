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
import io.grpc.stub.StreamObserver;
import io.spine.client.TestActorRequestFactory;
import io.spine.core.Ack;
import io.spine.core.Command;
import io.spine.grpc.StreamObservers;
import io.spine.server.BoundedContext;
import io.spine.server.entity.Repository;
import javaclasses.mealorder.Order;
import javaclasses.mealorder.c.aggregate.OrderAggregate;
import javaclasses.mealorder.c.command.AddDishToOrder;
import javaclasses.mealorder.c.command.CreateOrder;
import javaclasses.mealorder.c.command.CreatePurchaseOrder;
import javaclasses.mealorder.c.context.BoundedContexts;
import javaclasses.mealorder.c.repository.OrderRepository;
import javaclasses.mealorder.testdata.TestVendorCommandFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static javaclasses.mealorder.OrderStatus.ORDER_PROCESSED;
import static javaclasses.mealorder.testdata.TestPurchaseOrderCommandFactory.DISH;
import static javaclasses.mealorder.testdata.TestPurchaseOrderCommandFactory.ORDER;
import static javaclasses.mealorder.testdata.TestPurchaseOrderCommandFactory.ORDER_ID;
import static javaclasses.mealorder.testdata.TestPurchaseOrderCommandFactory.PURCHASE_ORDER_ID;
import static javaclasses.mealorder.testdata.TestPurchaseOrderCommandFactory.USER_ID;
import static javaclasses.mealorder.testdata.TestPurchaseOrderCommandFactory.createPurchaseOrderInstance;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Vlad Kozachenko
 */
public class OprderProcessedTest {

    private final StreamObserver<Ack> observer = StreamObservers.noOpObserver();
    private final TestActorRequestFactory requestFactory = TestActorRequestFactory.newInstance(
            getClass());
    private BoundedContext boundedContext;

    @BeforeEach
    public void setUpBoundedContext() {
        boundedContext = BoundedContexts.create();
    }

    @Test
    @DisplayName("create order and add it to purchase order")
    public void testMarkOrderProcessed() {

        final CreateOrder createOrder = CreateOrder.newBuilder()
                                                   .setOrderId(ORDER_ID)
                                                   .build();
        final Command createOrderCommand = requestFactory.createCommand(createOrder);
        boundedContext.getCommandBus()
                      .post(createOrderCommand, observer);

        final AddDishToOrder addDishToOrder = AddDishToOrder.newBuilder()
                                                            .setDish(DISH)
                                                            .setOrderId(ORDER_ID)
                                                            .build();
        final Command addDishToOrderCommand = requestFactory.createCommand(addDishToOrder);
        boundedContext.getCommandBus()
                      .post(addDishToOrderCommand, observer);

        final CreatePurchaseOrder createPurchaseOrder = createPurchaseOrderInstance();
        final Command createPOCommand = requestFactory.createCommand(createPurchaseOrder);
        boundedContext.getCommandBus()
                      .post(createPOCommand, observer);

        final Optional<Repository> repositoryOptional = boundedContext.findRepository(Order.class);

        assertTrue(repositoryOptional.isPresent());
        assertTrue(repositoryOptional.get() instanceof OrderRepository);
        final OrderRepository orderRepository = (OrderRepository) repositoryOptional.get();

        final OrderAggregate order = orderRepository.find(ORDER_ID).get();

        assertEquals(ORDER_PROCESSED, order.getState()
                                           .getStatus());

    }

}
