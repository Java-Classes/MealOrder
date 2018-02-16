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

import io.spine.net.EmailAddress;
import javaclasses.mealorder.PurchaseOrder;

/**
 * Utility class managing the sending process of a purchase order
 * to the vendor.
 *
 * @author Yegor Udovchenko
 */
public class PurchaseOrderSender {

    /**
     * Creates the Spreadsheet from the orders list of the purchase order.
     * Sends it to vendor email address.
     *
     * @param purchaseOrder purchase order to form spreadsheet and send
     * @param senderEmail   the email of the sender
     * @param vendorEmail   the email of the vendor
     * @return 'true' if the purchase order sent successfully,
     * 'false' if sending errors occurred
     */
    public boolean formAndSendPurchaseOrder(PurchaseOrder purchaseOrder,
                                            EmailAddress senderEmail,
                                            EmailAddress vendorEmail) {
        // TODO 2/16/2018[yegor.udovchenko]: implement method.
        return true;
    }
}
