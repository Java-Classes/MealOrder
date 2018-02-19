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

import com.google.common.base.Throwables;
import com.google.protobuf.Message;
import javaclasses.mealorder.MenuId;
import javaclasses.mealorder.Order;
import javaclasses.mealorder.OrderStatus;
import javaclasses.mealorder.c.aggregate.order.OrderCommandTest;
import javaclasses.mealorder.c.command.CreateOrder;
import javaclasses.mealorder.c.event.OrderCreated;
import javaclasses.mealorder.c.rejection.OrderAlreadyExists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.spine.server.aggregate.AggregateMessageDispatcher.dispatchCommand;
import static javaclasses.mealorder.testdata.TestOrderCommandFactory.ORDER_ID;
import static javaclasses.mealorder.testdata.TestOrderCommandFactory.createOrderInstance;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Vlad Kozachenko
 */
public class CreateOrderTest extends OrderCommandTest<CreateOrder> {

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
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

        MenuId menuId = MenuId.getDefaultInstance();

        final CreateOrder createOrder = createOrderInstance(ORDER_ID, menuId);
        dispatchCommand(aggregate, envelopeOf(createOrder));

        final Order state = aggregate.getState();
        assertEquals(state.getId(), createOrder.getOrderId());
        assertEquals(OrderStatus.ORDER_ACTIVE, aggregate.getState()
                                                        .getStatus());
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

}
