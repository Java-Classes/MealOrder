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

import com.google.common.base.Throwables;
import io.spine.Environment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.spine.test.Tests.assertHasPrivateParameterlessCtor;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

/**
 * @author Yegor Udovchenko
 */
@DisplayName("ServiceFactory should")
class ServiceFactoryTest {

    @Test
    @DisplayName("have the private constructor")
    void havePrivateConstructor() {
        assertHasPrivateParameterlessCtor(ServiceFactory.class);
    }

    @Test
    @DisplayName("return mock instance in test mode")
    void returnsInTest() {
        ServiceFactory.setPoSenderInstance(mock(PurchaseOrderSender.class));
        assertThat(ServiceFactory.getPurchaseOrderSender()
                                 .getClass(), instanceOf(Class.class));
    }

    @Test
    @DisplayName("throw UnsupportedOperationException upon an attempt " +
            "to set Purchase Order instance in production")
    void setPOSenderInstanceInProduction() {
        Environment.getInstance()
                   .setToProduction();
        Throwable t = assertThrows(Throwable.class,
                                   () -> ServiceFactory.setPoSenderInstance(
                                           mock(PurchaseOrderSender.class)));

        assertThat(Throwables.getRootCause(t), instanceOf(UnsupportedOperationException.class));
        Environment.getInstance()
                   .reset();
    }
}
