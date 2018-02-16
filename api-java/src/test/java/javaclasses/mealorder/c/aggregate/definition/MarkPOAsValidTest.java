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
import javaclasses.mealorder.c.command.MarkPurchaseOrderAsValid;
import javaclasses.mealorder.c.event.PurchaseOrderSent;
import javaclasses.mealorder.c.event.PurchaseOrderValidationOverruled;
import javaclasses.mealorder.c.rejection.CannotOverruleValidationOfNotInvalidPO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.spine.server.aggregate.AggregateMessageDispatcher.dispatchCommand;
import static javaclasses.mealorder.PurchaseOrderStatus.SENT;
import static javaclasses.mealorder.testdata.TestPurchaseOrderCommandFactory.createPurchaseOrderWithInvalidOrdersInstance;
import static javaclasses.mealorder.testdata.TestPurchaseOrderCommandFactory.markPurchaseOrderAsValidInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Yegor Udovchenko
 */
@DisplayName("MarkPurchaseOrderAsValid command should be interpreted by PurchaseOrderAggregate and")
public class MarkPOAsValidTest extends PurchaseOrderCommandTest<MarkPurchaseOrderAsValid> {
    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    @Test
    @DisplayName("set the purchase order status to 'SENT'")
    void markAsValid() {
        dispatchCreatedWithInvalidStateCmd();

        final MarkPurchaseOrderAsValid markAsValidCmd = markPurchaseOrderAsValidInstance();

        dispatchCommand(aggregate, envelopeOf(markAsValidCmd));
        final PurchaseOrder state = aggregate.getState();

        assertEquals(purchaseOrderId, state.getId());
        assertEquals(SENT, state.getStatus());
    }

    @Test
    @DisplayName("produce PurchaseOrderValidationOverruled and event")
    void producePairOFEvent() {
        dispatchCreatedWithInvalidStateCmd();

        final MarkPurchaseOrderAsValid markAsValidCmd = markPurchaseOrderAsValidInstance();
        final List<? extends Message> messageList = dispatchCommand(aggregate,
                                                                    envelopeOf(markAsValidCmd));

        assertNotNull(aggregate.getId());
        assertEquals(2, messageList.size());
        assertEquals(PurchaseOrderValidationOverruled.class, messageList.get(0)
                                                                        .getClass());
        assertEquals(PurchaseOrderSent.class, messageList.get(1)
                                                         .getClass());
        final PurchaseOrderValidationOverruled poValidationOverruled =
                (PurchaseOrderValidationOverruled) messageList.get(0);
        final PurchaseOrderSent purchaseOrderSent = (PurchaseOrderSent) messageList.get(1);

        assertEquals(purchaseOrderId, poValidationOverruled.getId());
        assertEquals(purchaseOrderId, purchaseOrderSent.getPurchaseOrder()
                                                       .getId());
    }

    @Test
    @DisplayName("throw CannotOverruleValidationOfNotInvalidPO rejection " +
            "upon an attempt to mark PO with not invalid state")
    void cannotOverrulePurchaseOrderValidation() {
        final MarkPurchaseOrderAsValid markAsValidCmd = markPurchaseOrderAsValidInstance();

        Throwable t = assertThrows(Throwable.class,
                                   () -> dispatchCommand(aggregate,
                                                         envelopeOf(markAsValidCmd)));
        assertThat(Throwables.getRootCause(t),
                   instanceOf(CannotOverruleValidationOfNotInvalidPO.class));
    }

    private void dispatchCreatedWithInvalidStateCmd() {
        final CreatePurchaseOrder createPOcmd = createPurchaseOrderWithInvalidOrdersInstance();
        dispatchCommand(aggregate, envelopeOf(createPOcmd));
    }
}
