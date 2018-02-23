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
import javaclasses.mealorder.c.command.CreatePurchaseOrder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static io.spine.test.Tests.assertHasPrivateParameterlessCtor;
import static javaclasses.mealorder.c.po.PurchaseOrders.findInvalidOrders;
import static javaclasses.mealorder.c.po.PurchaseOrders.hasInvalidOrders;
import static javaclasses.mealorder.c.po.PurchaseOrders.isAllowedPurchaseOrderCreation;
import static javaclasses.mealorder.testdata.TestValues.PURCHASE_ORDER_ID;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Yegor Udovchenko
 */
@DisplayName("PurchaseOrders should")
class PurchaseOrdersTest {

    @Test
    @DisplayName("have the private constructor")
    void havePrivateConstructor() {
        assertHasPrivateParameterlessCtor(PurchaseOrders.class);
    }

    @Test
    @DisplayName("return false for empty list of dishes")
    void returnFalseForEmptyList() {
        CreatePurchaseOrder createPurchaseOrder = CreatePurchaseOrder.newBuilder()
                                                                     .setId(PURCHASE_ORDER_ID)
                                                                     .addAllOrder(new ArrayList<>())
                                                                     .build();
        assertFalse(isAllowedPurchaseOrderCreation(createPurchaseOrder));
    }

    @Test
    @DisplayName("don't check Purchase Order creation without a command")
    void doNotCheckPurchaseOrderCreationWithoutCommand() {
        assertThrows(NullPointerException.class,
                     () -> isAllowedPurchaseOrderCreation(Tests.nullRef()));
    }

    @Test
    @DisplayName("don't find invalid orders without orders")
    void doNotFindInvalidOrdersWithoutOrders() {
        assertThrows(NullPointerException.class, () -> findInvalidOrders(Tests.nullRef()));
    }

    @Test
    @DisplayName("don't check invalid orders without orders")
    void doNotCheckInvalidOrdersWithoutOrders() {
        assertThrows(NullPointerException.class, () -> hasInvalidOrders(Tests.nullRef()));
    }
}
