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
import javaclasses.mealorder.c.command.CancelPurchaseOrder;
import javaclasses.mealorder.c.command.CreatePurchaseOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.spine.server.aggregate.AggregateMessageDispatcher.dispatchCommand;
import static javaclasses.mealorder.PurchaseOrderStatus.CANCELED;
import static javaclasses.mealorder.testdata.TestPurchaseOrderCommandFactory.cancelPurchaseOrderInstance;
import static javaclasses.mealorder.testdata.TestPurchaseOrderCommandFactory.createPurchaseOrderInstance;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Yegor Udovchenko
 */
@DisplayName("CancelPurchaseOrder command should be interpreted by PurchaseOrderAggregate and")
public class CancelPurchaseOrderTest extends PurchaseOrderCommandTest<CancelPurchaseOrder> {
    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    @Test
    @DisplayName("set the purchase order status to 'CANCELED'")
    void cancelPurchaseOrder() {
        setUpCreatedState();
        final CancelPurchaseOrder cancelCmd =
                cancelPurchaseOrderInstance(purchaseOrderId);
        dispatchCommand(aggregate, envelopeOf(cancelCmd));

        PurchaseOrder state = aggregate.getState();

        assertEquals(purchaseOrderId, state.getId());
        assertEquals(CANCELED, state.getStatus());
    }

    private void setUpCreatedState() {
        final CreatePurchaseOrder createPOcmd = createPurchaseOrderInstance(
                purchaseOrderId);
        dispatchCommand(aggregate, envelopeOf(createPOcmd));
    }
}
