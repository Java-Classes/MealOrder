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
import javaclasses.mealorder.PurchaseOrder;
import javaclasses.mealorder.PurchaseOrderId;
import javaclasses.mealorder.PurchaseOrderStatus;
import javaclasses.mealorder.c.command.CancelPurchaseOrder;
import javaclasses.mealorder.c.command.CreatePurchaseOrder;
import javaclasses.mealorder.c.command.MarkPurchaseOrderAsDelivered;
import javaclasses.mealorder.c.event.PurchaseOrderCanceled;
import javaclasses.mealorder.c.rejection.CannotCancelDeliveredPurchaseOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.spine.server.aggregate.AggregateMessageDispatcher.dispatchCommand;
import static javaclasses.mealorder.PurchaseOrderStatus.CANCELED;
import static javaclasses.mealorder.c.event.PurchaseOrderCanceled.ReasonCase.CUSTOM_REASON;
import static javaclasses.mealorder.c.event.PurchaseOrderCanceled.ReasonCase.INVALID;
import static javaclasses.mealorder.testdata.TestPurchaseOrderCommandFactory.cancelPOWithCustomReasonInstance;
import static javaclasses.mealorder.testdata.TestPurchaseOrderCommandFactory.cancelPOWithEmptyReasonInstance;
import static javaclasses.mealorder.testdata.TestPurchaseOrderCommandFactory.cancelPOWithInvalidReasonInstance;
import static javaclasses.mealorder.testdata.TestPurchaseOrderCommandFactory.createPurchaseOrderInstance;
import static javaclasses.mealorder.testdata.TestPurchaseOrderCommandFactory.markPurchaseOrderAsDeliveredInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Yegor Udovchenko
 */
@DisplayName("`CancelPurchaseOrder` command should be interpreted by `PurchaseOrderAggregate` and")
public class CancelPurchaseOrderTest extends PurchaseOrderCommandTest<CancelPurchaseOrder> {
    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    @Test
    @DisplayName("set the purchase order status to `CANCELED`")
    void cancelPurchaseOrder() {
        dispatchCreatedCmd();
        final CancelPurchaseOrder cancelCmd = cancelPOWithCustomReasonInstance();
        dispatchCommand(aggregate, envelopeOf(cancelCmd));
        final PurchaseOrder state = aggregate.getState();
        final PurchaseOrderId actualId = state.getId();
        final PurchaseOrderStatus actualStatus = state.getStatus();

        assertEquals(purchaseOrderId, actualId);
        assertEquals(CANCELED, actualStatus);
    }

    @Nested
    @DisplayName("produce `PurchaseOrderCanceled` event with")
    class ProduceEventWithReasonsTests {
        @Test
        @DisplayName("`CUSTOM` reason")
        void producePOCanceledWithCustomReasonEvent() {
            dispatchCreatedCmd();
            final CancelPurchaseOrder cancelCmd = cancelPOWithCustomReasonInstance();
            final List<? extends Message> messageList = dispatchCommand(aggregate,
                                                                        envelopeOf(cancelCmd));
            final PurchaseOrderCanceled poCanceled = (PurchaseOrderCanceled) messageList.get(0);
            final int messageListSize = messageList.size();
            final PurchaseOrderId cmdId = poCanceled.getId();
            final PurchaseOrderCanceled.ReasonCase reasonCase = poCanceled.getReasonCase();
            final String expectedReason = cancelCmd.getCustomReason();
            final String actualReason = poCanceled.getCustomReason();
            final int orderCount = poCanceled.getOrderCount();
            final PurchaseOrderId aggregateId = aggregate.getId();
            final Class<? extends Message> eventClass = messageList.get(0)
                                                                   .getClass();

            assertNotNull(aggregateId);
            assertEquals(1, messageListSize);
            assertEquals(PurchaseOrderCanceled.class, eventClass);
            assertEquals(purchaseOrderId, cmdId);
            assertEquals(CUSTOM_REASON, reasonCase);
            assertEquals(expectedReason, actualReason);
            assertEquals(1, orderCount);
        }

        @Test
        @DisplayName("`CUSTOM` reason for undefined reason")
        void producePOCanceledWithCustomReasonFromUndefinedEvent() {
            dispatchCreatedCmd();
            final CancelPurchaseOrder cancelCmd = cancelPOWithEmptyReasonInstance();
            final List<? extends Message> messageList = dispatchCommand(aggregate,
                                                                        envelopeOf(cancelCmd));
            final PurchaseOrderCanceled poCanceled = (PurchaseOrderCanceled) messageList.get(0);

            final PurchaseOrderId aggregateId = aggregate.getId();
            final PurchaseOrderCanceled.ReasonCase reasonCase = poCanceled.getReasonCase();
            final String customReason = poCanceled.getCustomReason();
            final int orderCount = poCanceled.getOrderCount();
            final int messageListSize = messageList.size();
            final PurchaseOrderId eventId = poCanceled.getId();
            final Class<? extends Message> eventClass = messageList.get(0)
                                                                   .getClass();

            assertNotNull(aggregateId);
            assertEquals(1, messageListSize);
            assertEquals(PurchaseOrderCanceled.class, eventClass);
            assertEquals(purchaseOrderId, eventId);
            assertEquals(CUSTOM_REASON, reasonCase);
            assertEquals("Reason not set.", customReason);
            assertEquals(1, orderCount);
        }

        @Test
        @DisplayName("`INVALID` reason")
        void producePOCanceledWithInvalidReasonEvent() {
            dispatchCreatedCmd();
            final CancelPurchaseOrder cancelCmd = cancelPOWithInvalidReasonInstance();
            final List<? extends Message> messageList = dispatchCommand(aggregate,
                                                                        envelopeOf(cancelCmd));
            final PurchaseOrderCanceled poCanceled = (PurchaseOrderCanceled) messageList.get(0);

            final PurchaseOrderId aggregateId = aggregate.getId();
            final int messageListSize = messageList.size();
            final Class<? extends Message> eventClass = messageList.get(0)
                                                                   .getClass();
            final PurchaseOrderId eventId = poCanceled.getId();
            final PurchaseOrderCanceled.ReasonCase reasonCase = poCanceled.getReasonCase();
            final boolean expectedInvalidValue = cancelCmd.getInvalid();
            final boolean actualInvalidReason = poCanceled.getInvalid();
            final int orderCount = poCanceled.getOrderCount();

            assertNotNull(aggregateId);
            assertEquals(1, messageListSize);
            assertEquals(PurchaseOrderCanceled.class, eventClass);
            assertEquals(purchaseOrderId, eventId);
            assertEquals(INVALID, reasonCase);
            assertEquals(expectedInvalidValue, actualInvalidReason);
            assertEquals(1, orderCount);
        }
    }

    @Test
    @DisplayName("throw `CannotCancelDeliveredPurchaseOrder` rejection " +
            "upon an attempt to cancel delivered purchase order")
    void cannotCancelDeliveredPurchaseOrder() {
        dispatchCreatedCmd();
        dispatchDeliveredCmd();
        final CancelPurchaseOrder cancelCmd = cancelPOWithCustomReasonInstance();
        final Throwable t = assertThrows(Throwable.class,
                                         () -> dispatchCommand(aggregate,
                                                               envelopeOf(cancelCmd)));
        assertThat(Throwables.getRootCause(t),
                   instanceOf(CannotCancelDeliveredPurchaseOrder.class));
    }

    private void dispatchCreatedCmd() {
        final CreatePurchaseOrder createPOcmd = createPurchaseOrderInstance();
        dispatchCommand(aggregate, envelopeOf(createPOcmd));
    }

    private void dispatchDeliveredCmd() {
        final MarkPurchaseOrderAsDelivered markPOAsDelivered = markPurchaseOrderAsDeliveredInstance();
        dispatchCommand(aggregate, envelopeOf(markPOAsDelivered));
    }
}
