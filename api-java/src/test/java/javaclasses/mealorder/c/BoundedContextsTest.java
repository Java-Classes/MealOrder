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

package javaclasses.mealorder.c;

import com.google.common.base.Optional;
import io.spine.server.BoundedContext;
import io.spine.server.entity.Repository;
import io.spine.server.storage.StorageFactory;
import io.spine.server.storage.memory.InMemoryStorageFactory;
import io.spine.test.Tests;
import javaclasses.mealorder.Order;
import javaclasses.mealorder.PurchaseOrder;
import javaclasses.mealorder.Vendor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.spine.test.Tests.assertHasPrivateParameterlessCtor;
import static javaclasses.mealorder.c.BoundedContexts.createBoundedContext;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Yurii Haidamaka
 */
@DisplayName("`BoundedContexts` should")
class BoundedContextsTest {

    @Test
    @DisplayName("have the private parameterless constructor")
    void havePrivateCtor() {
        assertHasPrivateParameterlessCtor(BoundedContexts.class);
    }

    @Test
    @DisplayName("not create `BoundedContext` without a `StorageFactory`")
    void notCreateBoundedContextWithoutStorageFactory() {
        assertThrows(NullPointerException.class, () -> createBoundedContext(Tests.nullRef()));
    }

    @Test
    @DisplayName("create `BoundedContext` with the `InMemoryStorageFactory`")
    void createBoundedContextWithInMemoryStorageFactory() {
        BoundedContext boundedContext = BoundedContexts.create();

        assertEquals(InMemoryStorageFactory.class, boundedContext.getStorageFactory()
                                                                 .getClass());
    }

    @Test
    @DisplayName("create `BoundedContext` with a given `StorageFactory` ")
    void createBoundedContextWithStorageFactory() {
        final StorageFactory inMemoryFactory =
                InMemoryStorageFactory.newInstance(
                        BoundedContext.newName("MealOrderBoundedContext"), false);

        BoundedContext boundedContext = BoundedContexts.createBoundedContext(inMemoryFactory);

        assertEquals(inMemoryFactory, boundedContext.getStorageFactory());
    }

    @Test
    @DisplayName("create `BoundedContext` with `VendorRepository`, `OrderRepository` and `PurchaseOrderRepository`")
    void createBoundedContextWithoutVendorRepository() {
        final StorageFactory inMemoryFactory =
                InMemoryStorageFactory.newInstance(
                        BoundedContext.newName("MealOrderBoundedContext"), false);

        final BoundedContext boundedContext = BoundedContexts.create(inMemoryFactory);

        final Optional<Repository> vendorRepository = boundedContext.findRepository(Vendor.class);
        final Optional<Repository> poRepository = boundedContext.findRepository(
                PurchaseOrder.class);
        final Optional<Repository> orderRepository = boundedContext.findRepository(Order.class);

        assertTrue(vendorRepository.isPresent());
        assertTrue(poRepository.isPresent());
        assertTrue(orderRepository.isPresent());
    }
}
