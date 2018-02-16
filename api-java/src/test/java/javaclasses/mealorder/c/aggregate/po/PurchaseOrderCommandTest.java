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

package javaclasses.mealorder.c.aggregate.po;

import com.google.protobuf.Message;
import io.spine.client.TestActorRequestFactory;
import io.spine.core.CommandEnvelope;
import io.spine.net.EmailAddress;
import io.spine.server.aggregate.AggregateCommandTest;
import javaclasses.mealorder.PurchaseOrder;
import javaclasses.mealorder.PurchaseOrderId;
import javaclasses.mealorder.c.aggregate.PurchaseOrderAggregate;
import javaclasses.mealorder.c.aggregate.PurchaseOrderSender;

import static javaclasses.mealorder.testdata.TestPurchaseOrderCommandFactory.DATE;
import static javaclasses.mealorder.testdata.TestPurchaseOrderCommandFactory.VENDOR_ID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * The parent class for the {@link PurchaseOrderAggregate} test classes.
 * Provides the common methods for testing.
 *
 * @author Yegor Udovchenko
 */
abstract class PurchaseOrderCommandTest<C extends Message>
        extends AggregateCommandTest<C, PurchaseOrderAggregate> {
    private final TestActorRequestFactory requestFactory =
            TestActorRequestFactory.newInstance(getClass());
    PurchaseOrderAggregate aggregate;
    PurchaseOrderId purchaseOrderId;
    private PurchaseOrderSender purchaseOrderSenderMock;

    @Override
    protected void setUp() {
        if (purchaseOrderSenderMock == null) {
            setSenderMockWithTrueAnswer();
        }
        super.setUp();
        aggregate = aggregate().get();
    }

    @Override
    protected PurchaseOrderAggregate createAggregate() {
        purchaseOrderId = createPurchaseOrderId();
        return new PurchaseOrderAggregate(purchaseOrderId, purchaseOrderSenderMock);
    }

    protected void setSenderMockWithFalseAnswer() {
        purchaseOrderSenderMock = mock(PurchaseOrderSender.class);
        when(purchaseOrderSenderMock.formAndSendPurchaseOrder(any(PurchaseOrder.class),
                                                              any(EmailAddress.class),
                                                              any(EmailAddress.class)))
                .thenReturn(false);
    }

    CommandEnvelope envelopeOf(Message commandMessage) {
        return CommandEnvelope.of(requestFactory.command()
                                                .create(commandMessage));
    }

    private void setSenderMockWithTrueAnswer() {
        purchaseOrderSenderMock = mock(PurchaseOrderSender.class);
        when(purchaseOrderSenderMock.formAndSendPurchaseOrder(any(PurchaseOrder.class),
                                                              any(EmailAddress.class),
                                                              any(EmailAddress.class)))
                .thenReturn(true);
    }

    private static PurchaseOrderId createPurchaseOrderId() {
        return PurchaseOrderId.newBuilder()
                              .setVendorId(VENDOR_ID)
                              .setPoDate(DATE)
                              .build();
    }
}