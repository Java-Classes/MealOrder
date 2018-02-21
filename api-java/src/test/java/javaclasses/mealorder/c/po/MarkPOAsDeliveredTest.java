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
import javaclasses.mealorder.c.command.CreatePurchaseOrder;
import javaclasses.mealorder.c.command.MarkPurchaseOrderAsDelivered;
import javaclasses.mealorder.c.event.PurchaseOrderDelivered;
import javaclasses.mealorder.c.rejection.CannotMarkPurchaseOrderAsDelivered;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.spine.server.aggregate.AggregateMessageDispatcher.dispatchCommand;
import static javaclasses.mealorder.PurchaseOrderStatus.DELIVERED;
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
@DisplayName("MarkPurchaseOrderAsDelivered command should be interpreted by PurchaseOrderAggregate and")
public class MarkPOAsDeliveredTest extends PurchaseOrderCommandTest<MarkPurchaseOrderAsDelivered> {
    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    @Test
    @DisplayName("set the purchase order status to 'DELIVERED'")
    void markAsDelivered() {
        dispatchCreatedCmd();
        final MarkPurchaseOrderAsDelivered markAsDeliveredCmd =
                markPurchaseOrderAsDeliveredInstance();
        dispatchCommand(aggregate, envelopeOf(markAsDeliveredCmd));
        final PurchaseOrder state = aggregate.getState();
        final PurchaseOrderId actualId = state.getId();
        final PurchaseOrderStatus actualStatus = state.getStatus();

        assertEquals(purchaseOrderId, actualId);
        assertEquals(DELIVERED, actualStatus);
    }

    @Test
    @DisplayName("produce PurchaseOrderDelivered event")
    void producePurchaseOrderDeliveredEvent() {
        dispatchCreatedCmd();
        final MarkPurchaseOrderAsDelivered markAsDeliveredCmd =
                markPurchaseOrderAsDeliveredInstance();
        final List<? extends Message> messageList = dispatchCommand(aggregate,
                                                                    envelopeOf(markAsDeliveredCmd));
        assertNotNull(aggregate.getId());
        assertEquals(1, messageList.size());
        assertEquals(PurchaseOrderDelivered.class, messageList.get(0)
                                                              .getClass());
        final PurchaseOrderDelivered poDelivered = (PurchaseOrderDelivered) messageList.get(0);
        final PurchaseOrderId actualId = poDelivered.getId();

        assertEquals(purchaseOrderId, actualId);
    }

    @Test
    @DisplayName("throw CannotMarkPurchaseOrderAsDelivered rejection " +
            "upon an attempt to mark PO with not sent state as delivered")
    void cannotMarkPurchaseOrderAsDelivered() {
        final MarkPurchaseOrderAsDelivered markAsDeliveredCmd =
                markPurchaseOrderAsDeliveredInstance();
        final Throwable t = assertThrows(Throwable.class,
                                         () -> dispatchCommand(aggregate,
                                                               envelopeOf(markAsDeliveredCmd)));
        assertThat(Throwables.getRootCause(t),
                   instanceOf(CannotMarkPurchaseOrderAsDelivered.class));
    }

    private void dispatchCreatedCmd() {
        final CreatePurchaseOrder createPOcmd = createPurchaseOrderInstance();
        dispatchCommand(aggregate, envelopeOf(createPOcmd));
    }
}
