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

import io.spine.time.LocalDate;
import javaclasses.mealorder.PurchaseOrderId;
import javaclasses.mealorder.UserId;
import javaclasses.mealorder.VendorId;
import javaclasses.mealorder.c.command.CancelPurchaseOrder;
import javaclasses.mealorder.c.command.CreatePurchaseOrder;
import javaclasses.mealorder.c.command.MarkPurchaseOrderAsDelivered;
import javaclasses.mealorder.c.command.MarkPurchaseOrderAsValid;
import javaclasses.mealorder.c.rejection.CannotCancelDeliveredPurchaseOrder;
import javaclasses.mealorder.c.rejection.CannotCreatePurchaseOrder;
import javaclasses.mealorder.c.rejection.CannotMarkPurchaseOrderAsDelivered;
import javaclasses.mealorder.c.rejection.CannotOverruleValidationOfNotInvalidPO;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.spine.time.Time.getCurrentTime;

/**
 * Utility class for working with {@link PurchaseOrderAggregateRejections}.
 *
 * @author Yegor Udovchenko
 */
class PurchaseOrderAggregateRejections {

    /** Prevents instantiation of this utility class. */
    private PurchaseOrderAggregateRejections() {
    }

    /**
     * Constructs and throws the {@link CannotCreatePurchaseOrder} rejection
     * according to the passed parameters.
     *
     * @param cmd the {@code CreatePurchaseOrder} command which thrown the rejection
     * @throws CannotCreatePurchaseOrder if the {@code CreatePurchaseOrder} command is invalid
     */
    static void cannotCreatePurchaseOrder(CreatePurchaseOrder cmd)
            throws CannotCreatePurchaseOrder {
        checkNotNull(cmd);
        final VendorId vendorId = cmd.getId()
                                     .getVendorId();
        final LocalDate poDate = cmd.getId()
                                    .getPoDate();
        final CannotCreatePurchaseOrder cannotCreatePurchaseOrder =
                new CannotCreatePurchaseOrder(vendorId, poDate, getCurrentTime());
        throw cannotCreatePurchaseOrder;
    }

    /**
     * Constructs and throws the {@link CannotMarkPurchaseOrderAsDelivered} rejection
     * according to the passed parameters.
     *
     * @param cmd the {@code MarkPurchaseOrderAsDelivered} command which thrown the rejection
     * @throws CannotMarkPurchaseOrderAsDelivered if the purchase order is not allowed to
     *                                            mark as delivered
     */
    static void cannotMarkPurchaseOrderAsDelivered(MarkPurchaseOrderAsDelivered cmd)
            throws CannotMarkPurchaseOrderAsDelivered {
        checkNotNull(cmd);
        final PurchaseOrderId purchaseOrderId = cmd.getId();
        final UserId userId = cmd.getWhoMarksAsDelivered();
        final CannotMarkPurchaseOrderAsDelivered cannotMarkPurchaseOrderAsDelivered =
                new CannotMarkPurchaseOrderAsDelivered(purchaseOrderId, userId, getCurrentTime());
        throw cannotMarkPurchaseOrderAsDelivered;
    }

    /**
     * Constructs and throws the {@link CannotCancelDeliveredPurchaseOrder} rejection
     * according to the passed parameters.
     *
     * @param cmd the {@code CancelPurchaseOrder} command which thrown the rejection
     * @throws CannotCancelDeliveredPurchaseOrder upon an attempt to mark delivered purchase
     *                                            order as canceled
     */
    static void cannotCancelDeliveredPurchaseOrder(CancelPurchaseOrder cmd)
            throws CannotCancelDeliveredPurchaseOrder {
        checkNotNull(cmd);
        final PurchaseOrderId purchaseOrderId = cmd.getId();
        final UserId userId = cmd.getUserId();
        final CannotCancelDeliveredPurchaseOrder cannotCancelDeliveredPurchaseOrder =
                new CannotCancelDeliveredPurchaseOrder(purchaseOrderId, userId, getCurrentTime());
        throw cannotCancelDeliveredPurchaseOrder;
    }

    /**
     * Constructs and throws the {@link CannotOverruleValidationOfNotInvalidPO} rejection
     * according to the passed parameters.
     *
     * @param cmd the {@code CancelPurchaseOrder} command which thrown the rejection
     * @throws CannotOverruleValidationOfNotInvalidPO upon an attempt to overrule validation of
     *                                                not invalid purchase order
     */
    static void cannotOverruleValidationOfNotInvalidPO(MarkPurchaseOrderAsValid cmd)
            throws CannotOverruleValidationOfNotInvalidPO {
        checkNotNull(cmd);
        final PurchaseOrderId purchaseOrderId = cmd.getId();
        final UserId userId = cmd.getUserId();
        final CannotOverruleValidationOfNotInvalidPO cannotOverruleValidationOfNotInvalidPO =
                new CannotOverruleValidationOfNotInvalidPO(purchaseOrderId,
                                                           userId,
                                                           getCurrentTime());
        throw cannotOverruleValidationOfNotInvalidPO;
    }
}
