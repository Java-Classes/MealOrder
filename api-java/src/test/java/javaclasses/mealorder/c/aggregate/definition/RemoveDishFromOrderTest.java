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
import javaclasses.mealorder.Dish;
import javaclasses.mealorder.DishId;
import javaclasses.mealorder.MenuId;
import javaclasses.mealorder.c.command.AddDishToOrder;
import javaclasses.mealorder.c.command.CancelOrder;
import javaclasses.mealorder.c.command.CreateOrder;
import javaclasses.mealorder.c.command.RemoveDishFromOrder;
import javaclasses.mealorder.c.event.DishAddedToOrder;
import javaclasses.mealorder.c.event.DishRemovedFromOrder;
import javaclasses.mealorder.c.rejection.CannotRemoveDishFromNotActiveOrder;
import javaclasses.mealorder.c.rejection.CannotRemoveMissingDish;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.spine.server.aggregate.AggregateMessageDispatcher.dispatchCommand;
import static javaclasses.mealorder.testdata.TestOrderCommandFactory.DISH;
import static javaclasses.mealorder.testdata.TestOrderCommandFactory.ORDER_ID;
import static javaclasses.mealorder.testdata.TestOrderCommandFactory.addDishToOrderInstance;
import static javaclasses.mealorder.testdata.TestOrderCommandFactory.cancelOrderInstance;
import static javaclasses.mealorder.testdata.TestOrderCommandFactory.createOrderInstance;
import static javaclasses.mealorder.testdata.TestOrderCommandFactory.removeDishFromOrderInstance;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Vlad Kozachenko
 */
public class RemoveDishFromOrderTest extends OrderCommandTest<CreateOrder> {

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    @Test
    @DisplayName("produce RemoveDishFromOrder event")
    public void produceEvent() {

        final CreateOrder createOrderCmd = createOrderInstance(ORDER_ID,
                                                               MenuId.getDefaultInstance());
        dispatchCommand(aggregate, envelopeOf(createOrderCmd));

        final AddDishToOrder addDishToOrder = addDishToOrderInstance(ORDER_ID, DISH);
        dispatchCommand(aggregate, envelopeOf(addDishToOrder));
        final RemoveDishFromOrder removeDishFromOrder = removeDishFromOrderInstance(ORDER_ID,
                                                                                    DISH.getId());
        final List<? extends Message> messageList = dispatchCommand(aggregate,
                                                                    envelopeOf(
                                                                            removeDishFromOrder));

        assertNotNull(aggregate.getState());
        assertNotNull(aggregate.getId());
        assertEquals(1, messageList.size());
        assertEquals(DishRemovedFromOrder.class, messageList.get(0)
                                                            .getClass());
    }

    @Test
    @DisplayName("removes the dish from the order")
    public void removeDish() {

        final CreateOrder createOrderCmd = createOrderInstance(ORDER_ID,
                                                               MenuId.getDefaultInstance());
        dispatchCommand(aggregate, envelopeOf(createOrderCmd));

        final AddDishToOrder addDishToOrder = addDishToOrderInstance(ORDER_ID, DISH);
        dispatchCommand(aggregate, envelopeOf(addDishToOrder));
        final RemoveDishFromOrder removeDishFromOrder = removeDishFromOrderInstance(ORDER_ID,
                                                                                    DISH.getId());
        dispatchCommand(aggregate, envelopeOf(removeDishFromOrder));

        assertEquals(0, aggregate.getState()
                                 .getDishesList()
                                 .size());
    }

    @Test
    @DisplayName("throw CannotRemoveMissingDish rejection")
    public void notRemoveDish() {

        final CreateOrder createOrderCmd = createOrderInstance(ORDER_ID,
                                                               MenuId.getDefaultInstance());
        dispatchCommand(aggregate, envelopeOf(createOrderCmd));

        final DishId dishId = DishId.newBuilder()
                                    .setSequentialNumber(123)
                                    .build();

        final RemoveDishFromOrder removeDishFromOrder = removeDishFromOrderInstance(ORDER_ID,
                                                                                    dishId);
        final Throwable t = assertThrows(Throwable.class,
                                         () -> dispatchCommand(aggregate,
                                                               envelopeOf(removeDishFromOrder)));
        final Throwable cause = Throwables.getRootCause(t);
        final CannotRemoveMissingDish rejection = (CannotRemoveMissingDish) cause;
        assertEquals(rejection.getMessageThrown()
                              .getDishId(), dishId);
    }

    @Test
    @DisplayName("throw CannotRemoveDishFromNotActiveOrder rejection")
    public void notRemoveDishFromNotActiveOrder() {

        final CreateOrder createOrderCmd = createOrderInstance(ORDER_ID,
                                                               MenuId.getDefaultInstance());
        dispatchCommand(aggregate, envelopeOf(createOrderCmd));

        final AddDishToOrder addDishToOrder = addDishToOrderInstance(ORDER_ID, DISH);
        dispatchCommand(aggregate, envelopeOf(addDishToOrder));

        final RemoveDishFromOrder removeDishFromOrder = removeDishFromOrderInstance(ORDER_ID,
                                                                                    DISH.getId());

        final CancelOrder cancelOrderCmd = cancelOrderInstance(ORDER_ID);
        dispatchCommand(aggregate, envelopeOf(cancelOrderCmd));

        final Throwable t = assertThrows(Throwable.class,
                                         () -> dispatchCommand(aggregate,
                                                               envelopeOf(removeDishFromOrder)));
        final Throwable cause = Throwables.getRootCause(t);
        final CannotRemoveDishFromNotActiveOrder rejection = (CannotRemoveDishFromNotActiveOrder) cause;
        assertEquals(rejection.getMessageThrown()
                              .getDishId(), DISH.getId());
    }
}
