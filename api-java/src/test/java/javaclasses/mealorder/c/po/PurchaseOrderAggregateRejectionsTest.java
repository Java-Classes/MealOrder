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

import io.spine.test.Tests;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.spine.test.Tests.assertHasPrivateParameterlessCtor;
import static javaclasses.mealorder.c.po.PurchaseOrderAggregateRejections.cannotCancelDeliveredPurchaseOrder;
import static javaclasses.mealorder.c.po.PurchaseOrderAggregateRejections.cannotCreatePurchaseOrder;
import static javaclasses.mealorder.c.po.PurchaseOrderAggregateRejections.cannotMarkPurchaseOrderAsDelivered;
import static javaclasses.mealorder.c.po.PurchaseOrderAggregateRejections.cannotOverruleValidationOfNotInvalidPO;
import static javaclasses.mealorder.testdata.TestPurchaseOrderCommandFactory.cancelPOWithCustomReasonInstance;
import static javaclasses.mealorder.testdata.TestPurchaseOrderCommandFactory.createPurchaseOrderWithNotActiveOrdersInstance;
import static javaclasses.mealorder.testdata.TestPurchaseOrderCommandFactory.markPurchaseOrderAsDeliveredInstance;
import static javaclasses.mealorder.testdata.TestPurchaseOrderCommandFactory.markPurchaseOrderAsValidInstance;
import static javaclasses.mealorder.testdata.TestValues.PURCHASE_ORDER_ID;
import static javaclasses.mealorder.testdata.TestValues.USER_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Yegor Udovchenko
 */
@DisplayName("PurchaseOrderAggregateRejections should")
class PurchaseOrderAggregateRejectionsTest {

    @Test
    @DisplayName("have the private constructor")
    void havePrivateConstructor() {
        assertHasPrivateParameterlessCtor(PurchaseOrderAggregateRejections.class);
    }

    @Test
    @DisplayName("throw CannotCreatePurchaseOrder rejection")
    void throwCannotCreatePurchaseOrderRejection() {
        final CreatePurchaseOrder cmd = createPurchaseOrderWithNotActiveOrdersInstance();
        final CannotCreatePurchaseOrder rejection = assertThrows(CannotCreatePurchaseOrder.class,
                                                                 () -> cannotCreatePurchaseOrder(
                                                                         cmd));
        final VendorId actualVendorId = rejection.getMessageThrown()
                                           .getVendorId();
        final LocalDate actualDate = rejection.getMessageThrown()
                                        .getPurchaseOrderDate();
        assertEquals(PURCHASE_ORDER_ID.getPoDate(), actualDate);
        assertEquals(PURCHASE_ORDER_ID.getVendorId(), actualVendorId);
    }

    @Test
    @DisplayName("throw CannotMarkPurchaseOrderAsDelivered rejection")
    void throwCannotMarkPurchaseOrderAsDeliveredRejection() {
        final MarkPurchaseOrderAsDelivered cmd = markPurchaseOrderAsDeliveredInstance();
        final CannotMarkPurchaseOrderAsDelivered rejection =
                assertThrows(CannotMarkPurchaseOrderAsDelivered.class,
                             () -> cannotMarkPurchaseOrderAsDelivered(cmd));
        final PurchaseOrderId actual = rejection.getMessageThrown()
                                          .getPoId();
        final UserId actualUser = rejection.getMessageThrown()
                                     .getUserId();

        assertEquals(PURCHASE_ORDER_ID, actual);
        assertEquals(USER_ID, actualUser);
    }

    @Test
    @DisplayName("throw CannotCancelDeliveredPurchaseOrder rejection")
    void throwCannotCancelDeliveredPurchaseOrderRejection() {
        final CancelPurchaseOrder cmd = cancelPOWithCustomReasonInstance();
        final CannotCancelDeliveredPurchaseOrder rejection =
                assertThrows(CannotCancelDeliveredPurchaseOrder.class,
                             () -> cannotCancelDeliveredPurchaseOrder(cmd));
        final PurchaseOrderId actualId = rejection.getMessageThrown()
                                            .getPoId();
        final UserId actualUser = rejection.getMessageThrown()
                                     .getUserId();

        assertEquals(PURCHASE_ORDER_ID, actualId);
        assertEquals(USER_ID, actualUser);
    }

    @Test
    @DisplayName("throw CannotOverruleValidationOfNotInvalidPO rejection")
    void throwCannotOverruleValidationOfNotInvalidPORejection() {
        final MarkPurchaseOrderAsValid cmd = markPurchaseOrderAsValidInstance();
        final CannotOverruleValidationOfNotInvalidPO rejection =
                assertThrows(CannotOverruleValidationOfNotInvalidPO.class,
                             () -> cannotOverruleValidationOfNotInvalidPO(cmd));
        final PurchaseOrderId actualId = rejection.getMessageThrown()
                                            .getPoId();
        final UserId actualUser = rejection.getMessageThrown()
                                     .getUserId();

        assertEquals(PURCHASE_ORDER_ID, actualId);
        assertEquals(USER_ID, actualUser);
    }

    @Test
    @DisplayName("don't throw CannotCreatePurchaseOrder rejection for empty command ")
    void doNotThrowCannotCreatePurchaseOrderRejection() {
        assertThrows(NullPointerException.class, () -> cannotCreatePurchaseOrder(Tests.nullRef()));
    }

    @Test
    @DisplayName("don't throw CannotMarkPurchaseOrderAsDelivered rejection for empty command ")
    void doNotThrowCannotMarkPurchaseOrderAsDeliveredRejection() {
        assertThrows(NullPointerException.class, () -> cannotMarkPurchaseOrderAsDelivered(Tests.nullRef()));
    }

    @Test
    @DisplayName("don't throw CannotCancelDeliveredPurchaseOrder rejection for empty command ")
    void doNotThrowCannotCancelDeliveredPurchaseOrderRejection() {
        assertThrows(NullPointerException.class, () -> cannotCancelDeliveredPurchaseOrder(Tests.nullRef()));
    }

    @Test
    @DisplayName("don't throw CannotOverruleValidationOfNotInvalidPO rejection for empty command ")
    void doNotThrowCannotOverruleValidationOfNotInvalidPORejection() {
        assertThrows(NullPointerException.class, () -> cannotOverruleValidationOfNotInvalidPO(Tests.nullRef()));
    }
}
