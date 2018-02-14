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

import com.google.protobuf.Message;
import javaclasses.mealorder.Dish;
import javaclasses.mealorder.DishId;
import javaclasses.mealorder.c.command.AddDishToOrder;
import javaclasses.mealorder.c.command.CreateOrder;
import javaclasses.mealorder.c.command.RemoveDishFromOrder;
import javaclasses.mealorder.c.event.DishAddedToOrder;
import javaclasses.mealorder.c.event.DishRemovedFromOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.spine.server.aggregate.AggregateMessageDispatcher.dispatchCommand;
import static javaclasses.mealorder.testdata.TestOrderCommandFactory.ORDER_ID;
import static javaclasses.mealorder.testdata.TestOrderCommandFactory.addDishToOrderInstance;
import static javaclasses.mealorder.testdata.TestOrderCommandFactory.removeDishFromOrderInstance;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

        final Dish dish = Dish.getDefaultInstance();

        final AddDishToOrder addDishToOrder = addDishToOrderInstance(ORDER_ID, dish);
        dispatchCommand(aggregate, envelopeOf(addDishToOrder));
        final RemoveDishFromOrder removeDishFromOrder = removeDishFromOrderInstance(ORDER_ID,
                                                                                    dish.getId());
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

        final Dish dish = Dish.newBuilder()
                              .setId(DishId.newBuilder()
                                           .setSequentialNumber(123)
                                           .build())
                              .setName("Картошка")
                              .build();

        final AddDishToOrder addDishToOrder = addDishToOrderInstance(ORDER_ID, dish);
        dispatchCommand(aggregate, envelopeOf(addDishToOrder));
        final RemoveDishFromOrder removeDishFromOrder = removeDishFromOrderInstance(ORDER_ID,
                                                                                    dish.getId());
        dispatchCommand(aggregate, envelopeOf(removeDishFromOrder));

        assertEquals(0, aggregate.getState()
                                 .getDishesList()
                                 .size());
    }
}
