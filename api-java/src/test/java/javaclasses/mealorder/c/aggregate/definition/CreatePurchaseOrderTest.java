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
import javaclasses.mealorder.PurchaseOrder;
import javaclasses.mealorder.c.command.CreatePurchaseOrder;
import javaclasses.mealorder.c.event.PurchaseOrderCreated;
import javaclasses.mealorder.c.event.PurchaseOrderValidationPassed;
import javaclasses.mealorder.c.rejection.CannotCreatePurchaseOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.spine.server.aggregate.AggregateMessageDispatcher.dispatchCommand;
import static javaclasses.mealorder.testdata.TestPurchaseOrderCommandFactory.createPurchaseOrderInstance;
import static javaclasses.mealorder.testdata.TestPurchaseOrderCommandFactory.createPurchaseOrderWithEmptyOrdersInstance;
import static javaclasses.mealorder.testdata.TestPurchaseOrderCommandFactory.createPurchaseOrderWithNotActiveOrdersInstance;
import static javaclasses.mealorder.testdata.TestPurchaseOrderCommandFactory.createPurchaseOrderWithVendorMismatchInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    @DisplayName("create the purchase order")
    void createPurchaseOrder() {
        final CreatePurchaseOrder createPurchaseOrder = createPurchaseOrderInstance();
        dispatchCommand(aggregate, envelopeOf(createPurchaseOrder));

        final PurchaseOrder state = aggregate.getState();
        assertEquals(state.getId(), createPurchaseOrder.getId());
        assertEquals(state.getOrdersCount(), 1);
    }

    @Test
    @DisplayName("produce PurchaseOrderCreated and PurchaseOrderValidationPassed events")
    void produceEvent() {
        final CreatePurchaseOrder createPOcmd = createPurchaseOrderInstance(purchaseOrderId);
        final List<? extends Message> messageList = dispatchCommand(aggregate,
                                                                    envelopeOf(createPOcmd));

        assertNotNull(aggregate.getId());
        assertEquals(2, messageList.size());
        assertEquals(PurchaseOrderCreated.class, messageList.get(0)
                                                            .getClass());
        assertEquals(PurchaseOrderValidationPassed.class, messageList.get(1)
                                                                     .getClass());
        final PurchaseOrderCreated purchaseOrderCreated = (PurchaseOrderCreated) messageList.get(0);
        final PurchaseOrderValidationPassed purchaseOrderValidationPassed =
                (PurchaseOrderValidationPassed) messageList.get(1);

        assertEquals(purchaseOrderId, purchaseOrderCreated.getId());
        assertEquals(purchaseOrderId, purchaseOrderValidationPassed.getId());
    }

    @Test
    @DisplayName("throw CannotCreatePurchaseOrder rejection " +
            "upon an attempt to add orders from another vendor")
    void cannotCreatePurchaseOrderForNotMatchingOrders() {
        final CreatePurchaseOrder invalidCmd = createPurchaseOrderWithVendorMismatchInstance(
                purchaseOrderId);

        Throwable t = assertThrows(Throwable.class,
                                   () -> dispatchCommand(aggregate,
                                                         envelopeOf(invalidCmd)));
        assertThat(Throwables.getRootCause(t), instanceOf(CannotCreatePurchaseOrder.class));
    }

    @Test
    @DisplayName("throw CannotCreatePurchaseOrder rejection " +
            "upon an attempt to add not active orders")
    void cannotCreatePurchaseOrderForNotActiveOrders() {
        final CreatePurchaseOrder invalidCmd = createPurchaseOrderWithNotActiveOrdersInstance(
                purchaseOrderId);

        Throwable t = assertThrows(Throwable.class,
                                   () -> dispatchCommand(aggregate,
                                                         envelopeOf(invalidCmd)));
        assertThat(Throwables.getRootCause(t), instanceOf(CannotCreatePurchaseOrder.class));
    }

    @Test
    @DisplayName("throw CannotCreatePurchaseOrder rejection " +
            "upon an attempt to add an empty order")
    void cannotCreatePurchaseOrderForEmptyOrders() {
        final CreatePurchaseOrder invalidCmd = createPurchaseOrderWithEmptyOrdersInstance(
                purchaseOrderId);

        Throwable t = assertThrows(Throwable.class,
                                   () -> dispatchCommand(aggregate,
                                                         envelopeOf(invalidCmd)));
        assertThat(Throwables.getRootCause(t), instanceOf(CannotCreatePurchaseOrder.class));
    }
}
