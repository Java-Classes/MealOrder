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
import javaclasses.mealorder.PurchaseOrder;
import javaclasses.mealorder.c.command.CreatePurchaseOrder;
import javaclasses.mealorder.c.event.PurchaseOrderCreated;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.spine.server.aggregate.AggregateMessageDispatcher.dispatchCommand;
import static javaclasses.mealorder.testdata.TestPurchaseOrderCommandFactory.ORDERS;
import static javaclasses.mealorder.testdata.TestPurchaseOrderCommandFactory.USER_ID;
import static javaclasses.mealorder.testdata.TestPurchaseOrderCommandFactory.createPurchaseOrderInstance;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Yegor Udovchenko
 */
@DisplayName("CreatePurchaseOrder command should be interpreted by PurchaseOrderAggregate and")
public class CreatePurchaseOrderTest extends PurchaseOrderCommandTest<CreatePurchaseOrder> {
    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    @Test
    @DisplayName("produce PurchaseOrderCreated event")
    void produceEvent() {
        final CreatePurchaseOrder createPOcmd = createPurchaseOrderInstance(purchaseOrderId, ORDERS,
                                                                            USER_ID);
        final List<? extends Message> messageList = dispatchCommand(aggregate,
                                                                    envelopeOf(createPOcmd));

        assertNotNull(aggregate.getId());
        assertEquals(1, messageList.size());
        assertEquals(PurchaseOrderCreated.class, messageList.get(0)
                                                            .getClass());
        final PurchaseOrderCreated purchaseOrderCreated = (PurchaseOrderCreated) messageList.get(0);

        assertEquals(purchaseOrderId, purchaseOrderCreated.getId());
        assertEquals(ORDERS, purchaseOrderCreated.getOrdersList());
    }

    @Test
    @DisplayName("create the purchase order")
    void createTask() {
        final CreatePurchaseOrder createPurchaseOrder = createPurchaseOrderInstance();
        dispatchCommand(aggregate, envelopeOf(createPurchaseOrder));

        final PurchaseOrder state = aggregate.getState();
        assertEquals(state.getId(), createPurchaseOrder.getId());
        assertEquals(state.getOrdersCount(), 1);
    }
}
