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

package javaclasses.mealorder.q.projection;

import javaclasses.mealorder.c.event.MenuImported;
import javaclasses.mealorder.q.FullMenuViewProjection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.spine.server.projection.ProjectionEventDispatcher.dispatch;
import static javaclasses.mealorder.testdata.TestMealOrderEventFactory.VendorEvents.menuImportedInstance;
import static javaclasses.mealorder.testdata.TestValues.MENU_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FullMenuViewProjectionTest extends ProjectionTest {
    private FullMenuViewProjection projection;

    @BeforeEach
    void setUp() {
        projection = new FullMenuViewProjection(MENU_ID);
    }

    @Nested
    @DisplayName("MenuImported event should be interpreted by FullMenuViewProjection")
    class MenuImportedEvent {

        @Test
        @DisplayName("Should set dates with menus")
        void addView() {
            final MenuImported menuImported = menuImportedInstance();
            dispatch(projection, createEvent(menuImported));

            assertEquals(menuImported.getMenuId(), projection.getState()
                                                             .getMenuId());
            assertEquals(menuImported.getMenuDateRange(), projection.getState()
                                                                    .getMenuDateRange());
            assertEquals(3, projection.getState()
                                      .getDishesByCategoryList()
                                      .size());

        }
    }
}
