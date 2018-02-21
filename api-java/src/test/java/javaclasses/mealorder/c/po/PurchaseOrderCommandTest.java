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

import com.google.protobuf.Message;
import io.spine.client.TestActorRequestFactory;
import io.spine.core.CommandEnvelope;
import io.spine.server.aggregate.AggregateCommandTest;
import javaclasses.mealorder.PurchaseOrderId;
import javaclasses.mealorder.c.po.PurchaseOrderAggregate;
import javaclasses.mealorder.PurchaseOrderSender;
import javaclasses.mealorder.ServiceFactory;

import static javaclasses.mealorder.testdata.TestValues.DATE;
import static javaclasses.mealorder.testdata.TestValues.VENDOR_ID;
import static org.mockito.Mockito.mock;

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

    @Override
    protected void setUp() {
        super.setUp();
        aggregate = aggregate().get();
        setSenderMock();
    }

    @Override
    protected PurchaseOrderAggregate createAggregate() {
        purchaseOrderId = createPurchaseOrderId();
        PurchaseOrderAggregate purchaseOrderAggregate = new PurchaseOrderAggregate(purchaseOrderId);
        return purchaseOrderAggregate;
    }

    CommandEnvelope envelopeOf(Message commandMessage) {
        return CommandEnvelope.of(requestFactory.command()
                                                .create(commandMessage));
    }

    private static PurchaseOrderId createPurchaseOrderId() {
        return PurchaseOrderId.newBuilder()
                              .setVendorId(VENDOR_ID)
                              .setPoDate(DATE)
                              .build();
    }

    private void setSenderMock() {
        final PurchaseOrderSender purchaseOrderSenderMock = mock(PurchaseOrderSender.class);
        ServiceFactory.setPoSenderInstance(purchaseOrderSenderMock);
    }
}
