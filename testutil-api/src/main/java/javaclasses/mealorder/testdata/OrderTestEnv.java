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

package javaclasses.mealorder.testdata;

import com.google.protobuf.Message;
import io.spine.core.EventContext;
import io.spine.core.Subscribe;
import io.spine.server.event.EventSubscriber;
import io.spine.server.rejection.RejectionSubscriber;
import javaclasses.mealorder.c.event.DishAddedToOrder;
import javaclasses.mealorder.c.event.OrderCanceled;
import javaclasses.mealorder.c.event.OrderCreated;
import javaclasses.mealorder.c.rejection.Rejections;

/**
 * @author Yurii Haidamaka
 */
public class OrderTestEnv {

    private OrderTestEnv() {
        // Prevent instantiation of this utility class.
    }

    public static class MenuNotAvailableSubscriber extends RejectionSubscriber {

        private static Rejections.MenuNotAvailable rejection = null;

        @Subscribe
        public void on(Rejections.MenuNotAvailable rejection) {
            this.rejection = rejection;
        }

        public static Rejections.MenuNotAvailable getRejection() {
            return rejection;
        }

        public static void clear() {
            rejection = null;
        }
    }

    public static class OrderAlreadyExistsSubscriber extends RejectionSubscriber {

        private static Rejections.OrderAlreadyExists rejection = null;

        @Subscribe
        public void on(Rejections.OrderAlreadyExists rejection) {
            this.rejection = rejection;
        }

        public static Rejections.OrderAlreadyExists getRejection() {
            return rejection;
        }

        public static void clear() {
            rejection = null;
        }
    }

    public static class DishVendorMismatchSubscriber extends RejectionSubscriber {

        private static Rejections.DishVendorMismatch rejection = null;

        @Subscribe
        public void on(Rejections.DishVendorMismatch rejection) {
            this.rejection = rejection;
        }

        public static Rejections.DishVendorMismatch getRejection() {
            return rejection;
        }

        public static void clear() {
            rejection = null;
        }
    }

    public static class CannotAddDishToNotActiveOrderSubscriber extends RejectionSubscriber {

        private static Rejections.CannotAddDishToNotActiveOrder rejection = null;

        @Subscribe
        public void on(Rejections.CannotAddDishToNotActiveOrder rejection) {
            this.rejection = rejection;
        }

        public static Rejections.CannotAddDishToNotActiveOrder getRejection() {
            return rejection;
        }

        public static void clear() {
            rejection = null;
        }
    }

    public static class OrderCreatedSubscriber extends EventSubscriber {

        private Message eventMessage;
        private EventContext eventContext;

        @Subscribe
        public void on(OrderCreated eventMsg, EventContext context) {
            this.eventMessage = eventMsg;
            this.eventContext = context;
        }

        public Message getEventMessage() {
            return eventMessage;
        }

        public EventContext getEventContext() {
            return eventContext;
        }
    }

    public static class DishAddedToOrderSubscriber extends EventSubscriber {

        private Message eventMessage;
        private EventContext eventContext;

        @Subscribe
        public void on(DishAddedToOrder eventMsg, EventContext context) {
            this.eventMessage = eventMsg;
            this.eventContext = context;
        }

        public Message getEventMessage() {
            return eventMessage;
        }

        public EventContext getEventContext() {
            return eventContext;
        }
    }


    public static class OrderCanceledSubscriber extends EventSubscriber {

        private Message eventMessage;
        private EventContext eventContext;

        @Subscribe
        public void on(OrderCanceled eventMsg, EventContext context) {
            this.eventMessage = eventMsg;
            this.eventContext = context;
        }

        public Message getEventMessage() {
            return eventMessage;
        }

        public EventContext getEventContext() {
            return eventContext;
        }
    }
}

