//
// Copyright 2018, TeamDev Ltd. All rights reserved.
//
// Redistribution and use in source and/or binary gorms, with or without
// modification, must retain the above copyright notice and the following
// disclaimer.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
// "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
// LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
// A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
// OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
// SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
// LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
// DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
// THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//

package javaclasses.mealorder.c;

import io.spine.server.BoundedContext;
import io.spine.server.storage.StorageFactory;
import io.spine.server.storage.memory.InMemoryStorageFactory;
import javaclasses.mealorder.c.order.OrderRepository;
import javaclasses.mealorder.c.po.PurchaseOrderRepository;
import javaclasses.mealorder.c.vendor.VendorRepository;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Utilities for creation the {@link BoundedContext} instances.
 *
 * @author Yurii Haidamaka
 */
public final class BoundedContexts {

    /** The default name of the {@code BoundedContext}. */
    private static final String NAME = "MealOrderBoundedContext";

    private static final StorageFactory IN_MEMORY_FACTORY =
            InMemoryStorageFactory.newInstance(BoundedContext.newName(NAME), false);

    /**
     * Disable instantiation from outside.
     */
    private BoundedContexts() {

    }

    /**
     * Creates the {@link BoundedContext} instance
     * using {@code InMemoryStorageFactory} for a single tenant.
     *
     * @return the {@link BoundedContext} instance
     */
    public static BoundedContext create() {
        final BoundedContext result = create(IN_MEMORY_FACTORY);
        return result;
    }

    /**
     * Creates a new instance of the {@link BoundedContext}
     * using the specified {@link StorageFactory}.
     *
     * Registers repository for vendor, order and purchase order aggregates.
     *
     * @param storageFactory the storage factory to use
     * @return the bounded context created with the storage factory and repositories
     */
    public static BoundedContext create(StorageFactory storageFactory) {
        checkNotNull(storageFactory);
        final VendorRepository vendorRepository = VendorRepository.getRepository();

        final OrderRepository orderRepository = new OrderRepository();
        final PurchaseOrderRepository purchaseOrderRepository = new PurchaseOrderRepository();

        final BoundedContext boundedContext = createBoundedContext(storageFactory);

        boundedContext.register(vendorRepository);
        boundedContext.register(orderRepository);
        boundedContext.register(purchaseOrderRepository);
        return boundedContext;
    }

    /**
     * Creates a new instance of the {@link BoundedContext}
     * using the specified {@link StorageFactory}.
     *
     * @param storageFactory the storage factory to use
     * @return the bounded context created with the storage factory
     */
    public static BoundedContext createBoundedContext(StorageFactory storageFactory) {
        checkNotNull(storageFactory);
        return BoundedContext.newBuilder()
                             .setStorageFactorySupplier(() -> storageFactory)
                             .setName(NAME)
                             .build();
    }
}
