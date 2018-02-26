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

package javaclasses.mealorder.c.po;

import com.google.common.base.Throwables;
import com.google.protobuf.Message;
import javaclasses.mealorder.Order;
import javaclasses.mealorder.PurchaseOrder;
import javaclasses.mealorder.PurchaseOrderId;
import javaclasses.mealorder.PurchaseOrderStatus;
import javaclasses.mealorder.c.command.CreatePurchaseOrder;
import javaclasses.mealorder.c.event.PurchaseOrderCreated;
import javaclasses.mealorder.c.event.PurchaseOrderSent;
import javaclasses.mealorder.c.event.PurchaseOrderValidationFailed;
import javaclasses.mealorder.c.event.PurchaseOrderValidationPassed;
import javaclasses.mealorder.c.rejection.CannotCreatePurchaseOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.spine.server.aggregate.AggregateMessageDispatcher.dispatchCommand;
import static javaclasses.mealorder.PurchaseOrderStatus.SENT;
import static javaclasses.mealorder.testdata.TestPurchaseOrderCommandFactory.createPurchaseOrderInstance;
import static javaclasses.mealorder.testdata.TestPurchaseOrderCommandFactory.createPurchaseOrderWithDatesMismatchOrdersInstance;
import static javaclasses.mealorder.testdata.TestPurchaseOrderCommandFactory.createPurchaseOrderWithEmptyListInstance;
import static javaclasses.mealorder.testdata.TestPurchaseOrderCommandFactory.createPurchaseOrderWithEmptyOrdersInstance;
import static javaclasses.mealorder.testdata.TestPurchaseOrderCommandFactory.createPurchaseOrderWithInvalidOrdersInstance;
import static javaclasses.mealorder.testdata.TestPurchaseOrderCommandFactory.createPurchaseOrderWithNotActiveOrdersInstance;
import static javaclasses.mealorder.testdata.TestPurchaseOrderCommandFactory.createPurchaseOrderWithVendorMismatchInstance;
import static javaclasses.mealorder.testdata.TestValues.ORDER;
import static javaclasses.mealorder.testdata.TestValues.PURCHASE_ORDER_ID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Yegor Udovchenko
 */
@DisplayName("`CreatePurchaseOrder` command should be interpreted by `PurchaseOrderAggregate` and")
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
        final PurchaseOrderId actualId = state.getId();
        final int actualOrderCount = state.getOrderCount();
        final Order actualOrder = state.getOrderList()
                                       .get(0);
        final PurchaseOrderStatus actualStatus = state.getStatus();

        assertEquals(PURCHASE_ORDER_ID, actualId);
        assertEquals(1, actualOrderCount);
        assertEquals(ORDER, actualOrder);
        assertEquals(SENT, actualStatus);
    }

    @Test
    @DisplayName("produce `PurchaseOrderCreated`, `PurchaseOrderValidationPassed`, " +
            "`PurchaseOrderSent` events")
    void produceCreatedValidationPassedAndSentEvent() {
        final CreatePurchaseOrder createPOcmd = createPurchaseOrderInstance();
        final List<? extends Message> messageList = dispatchCommand(aggregate,
                                                                    envelopeOf(createPOcmd));
        final PurchaseOrderCreated purchaseOrderCreated = (PurchaseOrderCreated) messageList.get(0);
        final PurchaseOrderValidationPassed poValidationPassed =
                (PurchaseOrderValidationPassed) messageList.get(1);
        final PurchaseOrderSent purchaseOrderSent = (PurchaseOrderSent) messageList.get(2);
        final PurchaseOrderId aggregateId = aggregate.getId();
        final int messageListSize = messageList.size();
        final Class<? extends Message> messageClassAtZero = messageList.get(0)
                                                                       .getClass();
        final Class<? extends Message> messageClassAtOne = messageList.get(1)
                                                                      .getClass();
        final Class<? extends Message> messageClassAtTwo = messageList.get(2)
                                                                      .getClass();
        final PurchaseOrderId purchaseOrderCreatedId = purchaseOrderCreated.getId();
        final PurchaseOrderId poValidationPassedId = poValidationPassed.getId();
        final PurchaseOrderId sentId = purchaseOrderSent.getPurchaseOrder()
                                                        .getId();
        final List<Order> sentOrders = purchaseOrderSent.getPurchaseOrder()
                                                        .getOrderList();
        final List<Order> storedOrders = aggregate.getState()
                                                  .getOrderList();

        assertNotNull(aggregateId);
        assertEquals(3, messageListSize);
        assertEquals(PurchaseOrderCreated.class, messageClassAtZero);
        assertEquals(PurchaseOrderValidationPassed.class, messageClassAtOne);
        assertEquals(PurchaseOrderSent.class, messageClassAtTwo);
        assertEquals(purchaseOrderId, purchaseOrderCreatedId);
        assertEquals(purchaseOrderId, poValidationPassedId);
        assertEquals(purchaseOrderId, sentId);
        assertEquals(sentOrders, storedOrders);
    }

    @Test
    @DisplayName("produce `PurchaseOrderCreated` and `PurchaseOrderValidationFailed` events")
    void produceCreatedAndValidationFailedEvent() {
        final CreatePurchaseOrder createPOcmd = createPurchaseOrderWithInvalidOrdersInstance();
        final List<? extends Message> messageList = dispatchCommand(aggregate,
                                                                    envelopeOf(createPOcmd));

        final PurchaseOrderId aggregateId = aggregate.getId();
        final int messageListSize = messageList.size();
        final Class<? extends Message> messageClassAtZero = messageList.get(0)
                                                                       .getClass();
        final Class<? extends Message> messageClassAtOne = messageList.get(1)
                                                                      .getClass();
        final PurchaseOrderCreated purchaseOrderCreated = (PurchaseOrderCreated) messageList.get(0);
        final PurchaseOrderValidationFailed poValidationFailed =
                (PurchaseOrderValidationFailed) messageList.get(1);
        final PurchaseOrderId actualCreatedId = purchaseOrderCreated.getId();
        final PurchaseOrderId actualFailedId = poValidationFailed.getId();
        final int actualFailureOrderCount = poValidationFailed.getFailureOrderCount();

        assertNotNull(aggregateId);
        assertEquals(3, messageListSize);
        assertEquals(PurchaseOrderCreated.class, messageClassAtZero);
        assertEquals(PurchaseOrderValidationFailed.class, messageClassAtOne);
        assertEquals(purchaseOrderId, actualCreatedId);
        assertEquals(purchaseOrderId, actualFailedId);
        assertEquals(1, actualFailureOrderCount);
    }

    @Nested
    @DisplayName("throw `CannotCreatePurchaseOrder` rejection ")
    class CannotCreatePurchaseOrderTests {

        @Test
        @DisplayName("upon an attempt to add orders from another vendor")
        void cannotCreatePurchaseOrderForNotMatchingOrders() {
            final CreatePurchaseOrder invalidCmd = createPurchaseOrderWithVendorMismatchInstance();

            final Throwable t = assertThrows(Throwable.class,
                                             () -> dispatchCommand(aggregate,
                                                                   envelopeOf(invalidCmd)));
            assertThat(Throwables.getRootCause(t), instanceOf(CannotCreatePurchaseOrder.class));
        }

        @Test
        @DisplayName("upon an attempt to add not active orders")
        void cannotCreatePurchaseOrderForNotActiveOrders() {
            final CreatePurchaseOrder invalidCmd = createPurchaseOrderWithNotActiveOrdersInstance();

            final Throwable t = assertThrows(Throwable.class,
                                             () -> dispatchCommand(aggregate,
                                                                   envelopeOf(invalidCmd)));
            assertThat(Throwables.getRootCause(t), instanceOf(CannotCreatePurchaseOrder.class));
        }

        @Test
        @DisplayName("upon an attempt to add an empty order")
        void cannotCreatePurchaseOrderForEmptyOrders() {
            final CreatePurchaseOrder invalidCmd = createPurchaseOrderWithEmptyOrdersInstance();

            final Throwable t = assertThrows(Throwable.class,
                                             () -> dispatchCommand(aggregate,
                                                                   envelopeOf(invalidCmd)));
            assertThat(Throwables.getRootCause(t), instanceOf(CannotCreatePurchaseOrder.class));
        }

        @Test
        @DisplayName("upon an attempt to add an order from another date")
        void cannotCreatePurchaseOrderForOrdersFromAnotherDate() {
            final CreatePurchaseOrder invalidCmd = createPurchaseOrderWithDatesMismatchOrdersInstance();

            final Throwable t = assertThrows(Throwable.class,
                                             () -> dispatchCommand(aggregate,
                                                                   envelopeOf(invalidCmd)));
            assertThat(Throwables.getRootCause(t), instanceOf(CannotCreatePurchaseOrder.class));
        }

        @Test
        @DisplayName("upon an attempt to add an empty order list")
        void cannotCreatePurchaseOrderForEmptyOrderList() {
            final CreatePurchaseOrder invalidCmd = createPurchaseOrderWithEmptyListInstance();

            final Throwable t = assertThrows(Throwable.class,
                                             () -> dispatchCommand(aggregate,
                                                                   envelopeOf(invalidCmd)));
            assertThat(Throwables.getRootCause(t), instanceOf(CannotCreatePurchaseOrder.class));
        }
    }
}
