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

import io.spine.time.LocalDate;
import javaclasses.mealorder.PurchaseOrderId;
import javaclasses.mealorder.UserId;
import javaclasses.mealorder.VendorId;
import javaclasses.mealorder.c.aggregate.aggregate.PurchaseOrderAggregate;
import javaclasses.mealorder.c.command.CancelPurchaseOrder;
import javaclasses.mealorder.c.command.CreatePurchaseOrder;
import javaclasses.mealorder.c.command.MarkPurchaseOrderAsDelivered;
import javaclasses.mealorder.c.command.MarkPurchaseOrderAsValid;
import javaclasses.mealorder.c.rejection.CannotCancelDeliveredPurchaseOrder;
import javaclasses.mealorder.c.rejection.CannotCreatePurchaseOrder;
import javaclasses.mealorder.c.rejection.CannotMarkCanceledPurchaseOrderAsDelivered;
import javaclasses.mealorder.c.rejection.CannotOverruleValidationOfNotInvalidPO;

import static io.spine.time.Time.getCurrentTime;

/**
 * Utility class for working with {@link PurchaseOrderAggregate} rejection.
 *
 * @author Yegor Udovchenko
 */
public class PurchaseOrderAggregateRejections {

    private PurchaseOrderAggregateRejections() {
        // Prevent instantiation of this utility class.
    }

    /**
     * Constructs and throws the {@link CannotCreatePurchaseOrder} rejection
     * according to the passed parameters.
     *
     * @param cmd the {@code CreatePurchaseOrder} command which thrown the rejection
     * @throws CannotCreatePurchaseOrder the rejection to throw
     */
    public static void throwCannotCreatePurchaseOrder(CreatePurchaseOrder cmd)
            throws CannotCreatePurchaseOrder {
        final VendorId vendorId = cmd.getId()
                                     .getVendorId();
        final LocalDate poDate = cmd.getId()
                                    .getPoDate();

        throw new CannotCreatePurchaseOrder(vendorId, poDate, getCurrentTime());
    }

    /**
     * Constructs and throws the {@link CannotMarkCanceledPurchaseOrderAsDelivered} rejection
     * according to the passed parameters.
     *
     * @param cmd the {@code MarkPurchaseOrderAsDelivered} command which thrown the rejection
     * @throws CannotMarkCanceledPurchaseOrderAsDelivered the rejection to throw
     */
    public static void throwCannotMarkCanceledPurchaseOrderAsDelivered(
            MarkPurchaseOrderAsDelivered cmd) throws CannotMarkCanceledPurchaseOrderAsDelivered {
        final PurchaseOrderId purchaseOrderId = cmd.getId();
        final UserId userId = cmd.getWhoMarksAsDelivered();

        throw new CannotMarkCanceledPurchaseOrderAsDelivered(purchaseOrderId, userId,
                                                             getCurrentTime());
    }

    /**
     * Constructs and throws the {@link CannotCancelDeliveredPurchaseOrder} rejection
     * according to the passed parameters.
     *
     * @param cmd the {@code CancelPurchaseOrder} command which thrown the rejection
     * @throws CannotCancelDeliveredPurchaseOrder the rejection to throw
     */
    public static void throwCannotCancelDeliveredPurchaseOrder(
            CancelPurchaseOrder cmd) throws CannotCancelDeliveredPurchaseOrder {
        final PurchaseOrderId purchaseOrderId = cmd.getId();
        final UserId userId = cmd.getUserId();

        throw new CannotCancelDeliveredPurchaseOrder(purchaseOrderId, userId, getCurrentTime());
    }

    /**
     * Constructs and throws the {@link CannotOverruleValidationOfNotInvalidPO} rejection
     * according to the passed parameters.
     *
     * @param cmd the {@code CancelPurchaseOrder} command which thrown the rejection
     * @throws CannotOverruleValidationOfNotInvalidPO the rejection to throw
     */
    public static void throwCannotOverruleValidationOfNotInvalidPO(
            MarkPurchaseOrderAsValid cmd) throws CannotOverruleValidationOfNotInvalidPO {
        final PurchaseOrderId purchaseOrderId = cmd.getId();
        final UserId userId = cmd.getUserId();

        throw new CannotOverruleValidationOfNotInvalidPO(purchaseOrderId, userId, getCurrentTime());
    }
}
