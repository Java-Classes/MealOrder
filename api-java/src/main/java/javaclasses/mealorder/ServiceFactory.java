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

package javaclasses.mealorder;

import io.spine.Environment;

/**
 * The utility class representing service factory.
 * Used by {@code PurchaseOrderAggregate} upon creation of
 * a purchase order.
 *
 * @author Yegor Udovchenko
 */
public class ServiceFactory {

    // TODO 2/20/2018[yegor.udovchenko]: Replace with implementation
    private static PurchaseOrderSender poSenderInstance = null;

    /** Prevents instantiation of this utility class. */
    private ServiceFactory() {
    }

    /**
     * Provides instance of {@link PurchaseOrderSender}
     *
     * @return {@code PurchaseOrderSender} default instance.
     */
    public static PurchaseOrderSender getPurchaseOrderSender() {
        return poSenderInstance;
    }

    /**
     * Setter method is used to substitute {@code PurchaseOrderSender}
     * with a mock instance for tests. Applicable only in test runtime
     * environment.
     * Throws {@code UnsupportedOperationException} if called in
     * production runtime environment.
     *
     * @param poSenderInstance
     */
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
