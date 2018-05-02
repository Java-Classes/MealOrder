//
// Copyright 2018, TeamDev Ltd. All rights reserved.
//
// Redistribution and use in source and/or binary forms, with or without
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

package javaclasses.mealorder.c.vendor;

import io.spine.server.aggregate.AggregateRepository;
import javaclasses.mealorder.VendorId;

/**
 * Repository for the {@link VendorAggregate}.
 *
 * <p>This class is singleton.
 *
 * @author Yurii Haidamaka
 */

public class VendorRepository extends AggregateRepository<VendorId, VendorAggregate> {

    /**
     * Returns instance of the VendorRepository
     */
    public static VendorRepository getRepository() {
        return VendorRepositorySingleton.INSTANCE.value;
    }

    public static VendorRepository createRepository() {
        VendorRepositorySingleton.INSTANCE.value = new VendorRepository();
        return VendorRepositorySingleton.INSTANCE.value;
    }
    private enum VendorRepositorySingleton {
        INSTANCE;
        public VendorRepository value = new VendorRepository();
    }
}
