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

package javaclasses.mealorder.c.aggregate;

import com.google.common.annotations.VisibleForTesting;
import io.spine.Environment;

/**
 * The utility class representing service factory.
 * Used by {@code PurchaseOrderAggregate} upon the purchase
 * order creation.
 *
 * @author Yegor Udovchenko
 */
public class ServiceFactory {

    // TODO 2/20/2018[yegor.udovchenko]: Replace with implementation
    private static PurchaseOrderSender poSenderInstance = null;

    private ServiceFactory() {
        // Prevent instantiation of this utility class.
    }

    /**
     * Provides instance of {@link PurchaseOrderSender}
     *
     * @return {@code PurchaseOrderSender} default instance.
     */
    static PurchaseOrderSender getPurchaseOrderSender() {
        return poSenderInstance;
    }

    @VisibleForTesting
    public static void setPoSenderInstance(
            PurchaseOrderSender poSenderInstance) {
        if (Environment.getInstance()
                       .isTests()) {
            ServiceFactory.poSenderInstance = poSenderInstance;
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
