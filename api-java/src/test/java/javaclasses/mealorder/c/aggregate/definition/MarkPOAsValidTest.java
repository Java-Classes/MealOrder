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

import javaclasses.mealorder.PurchaseOrder;
import javaclasses.mealorder.PurchaseOrderStatus;
import javaclasses.mealorder.c.command.CreatePurchaseOrder;
import javaclasses.mealorder.c.command.MarkPurchaseOrderAsValid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.spine.server.aggregate.AggregateMessageDispatcher.dispatchCommand;
import static javaclasses.mealorder.PurchaseOrderStatus.*;
import static javaclasses.mealorder.testdata.TestPurchaseOrderCommandFactory.createPurchaseOrderWithInvalidOrdersInstance;
import static javaclasses.mealorder.testdata.TestPurchaseOrderCommandFactory.markPurchaseOrderAsValidInstance;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
    @DisplayName("set the purchase order status as valid")
    void markAsValid() {
        final CreatePurchaseOrder createPOcmd = createPurchaseOrderWithInvalidOrdersInstance(
                purchaseOrderId);
        dispatchCommand(aggregate, envelopeOf(createPOcmd));
        final MarkPurchaseOrderAsValid markAsValidCmd =
                markPurchaseOrderAsValidInstance(purchaseOrderId);
        dispatchCommand(aggregate, envelopeOf(markAsValidCmd));

        final PurchaseOrder state = aggregate.getState();
        assertEquals(state.getId(), markAsValidCmd.getId());
        assertEquals(state.getStatus(), VALID);
    }


}
