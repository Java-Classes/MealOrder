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

package javaclasses.mealorder.c.context;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import io.spine.server.BoundedContext;
import io.spine.server.event.EventBus;
import io.spine.server.storage.StorageFactory;
import io.spine.server.storage.memory.InMemoryStorageFactory;
import javaclasses.mealorder.c.repository.OrderRepository;
import javaclasses.mealorder.c.repository.PurchaseOrderRepository;
import javaclasses.mealorder.c.repository.VendorRepository;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.spine.util.Exceptions.newIllegalStateException;

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

    private BoundedContexts() {
        // Disable instantiation from outside.
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
     * @param storageFactory the storage factory to use
     * @return the bounded context created with the storage factory
     */
    public static BoundedContext create(StorageFactory storageFactory) {
        checkNotNull(storageFactory);

        final VendorRepository vendorRepo = new VendorRepository();
        final OrderRepository orderRepository = new OrderRepository();
        final PurchaseOrderRepository purchaseOrderRepository = new PurchaseOrderRepository();

        final EventBus.Builder eventBus = createEventBus(storageFactory);

        final BoundedContext boundedContext = createBoundedContext(eventBus);

        boundedContext.register(vendorRepo);
        boundedContext.register(orderRepository);
        boundedContext.register(purchaseOrderRepository);
        return boundedContext;
    }

    private static EventBus.Builder createEventBus(StorageFactory storageFactory) {

        final EventBus.Builder eventBus = EventBus.newBuilder()
                                                  .setStorageFactory(storageFactory);
        return eventBus;
    }

    @VisibleForTesting
    static BoundedContext createBoundedContext(EventBus.Builder eventBus) {
        checkNotNull(eventBus);
        final Optional<StorageFactory> storageFactory = eventBus.getStorageFactory();
        if (!storageFactory.isPresent()) {
            throw newIllegalStateException("EventBus does not specify a StorageFactory.");
        }
        return BoundedContext.newBuilder()
                             .setStorageFactorySupplier(storageFactory::get)
                             .setName(NAME)
                             .setEventBus(eventBus)
                             .build();
    }
}
