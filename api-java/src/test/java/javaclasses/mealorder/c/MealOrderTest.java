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
import com.google.protobuf.GeneratedMessageV3;
import io.spine.client.ActorRequestFactory;
import io.spine.client.TestActorRequestFactory;
import io.spine.core.Ack;
import io.spine.core.Command;
import io.spine.grpc.MemoizingObserver;
import io.spine.server.BoundedContext;
import io.spine.server.commandbus.CommandBus;
import io.spine.server.entity.Repository;
import javaclasses.mealorder.PurchaseOrder;
import javaclasses.mealorder.PurchaseOrderSender;
import javaclasses.mealorder.ServiceFactory;
import javaclasses.mealorder.c.po.PurchaseOrderAggregate;
import javaclasses.mealorder.q.FullMenuViewProjection;
import javaclasses.mealorder.q.projection.FullMenuView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.spine.protobuf.TypeConverter.toMessage;
import static javaclasses.mealorder.PurchaseOrderStatus.DELIVERED;
import static javaclasses.mealorder.testdata.TestOrderCommandFactory.addDishToOrderInstance;
import static javaclasses.mealorder.testdata.TestOrderCommandFactory.createOrderInstance;
import static javaclasses.mealorder.testdata.TestPurchaseOrderCommandFactory.createPurchaseOrderInstance;
import static javaclasses.mealorder.testdata.TestPurchaseOrderCommandFactory.markPurchaseOrderAsDeliveredInstance;
import static javaclasses.mealorder.testdata.TestValues.MENU_ID;
import static javaclasses.mealorder.testdata.TestValues.MENU_ID2;
import static javaclasses.mealorder.testdata.TestValues.PURCHASE_ORDER_ID;
import static javaclasses.mealorder.testdata.TestVendorCommandFactory.addVendorInstance;
import static javaclasses.mealorder.testdata.TestVendorCommandFactory.importMenuInstance2;
import static javaclasses.mealorder.testdata.TestVendorCommandFactory.importMenuInstance3;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

@DisplayName("MealOrder Integration Test")
class MealOrderTest {
    private final ActorRequestFactory requestFactory =
            TestActorRequestFactory.newInstance(getClass());

    private final BoundedContext boundedContext = BoundedContexts.create();
    private final CommandBus commandBus = boundedContext.getCommandBus();
    final MemoizingObserver<Ack> observer = MemoizingObserver.newInstance();

    private Command createCommand(GeneratedMessageV3 message) {
        return getRequestFactory().command()
                                  .create(toMessage(message));
    }

    @BeforeEach
    @DisplayName("Add vendor -> Import menu -> Set date range for menu ->" +
            " Create order -> Add dish to order -> Create purchase order ->" +
            " Mark purchase order as delivered ")
    public void setUp() {
        final PurchaseOrderSender purchaseOrderSenderMock = mock(PurchaseOrderSender.class);
        ServiceFactory.setPoSenderInstance(purchaseOrderSenderMock);

        final Command addVendor = createCommand(addVendorInstance());
        commandBus.post(addVendor, observer);

        final Command importMenu = createCommand(importMenuInstance2());
        commandBus.post(importMenu, observer);
        final Command importMenu3 = createCommand(importMenuInstance3());
        commandBus.post(importMenu3, observer);

        final Command createOrderCommand = createCommand(createOrderInstance());
        commandBus.post(createOrderCommand, observer);

        final Command addDishToOrderCommand = createCommand(addDishToOrderInstance());
        commandBus.post(addDishToOrderCommand, observer);

        final Command createPOCommand = createCommand(createPurchaseOrderInstance());
        commandBus.post(createPOCommand, observer);

        final Command markAsDeliveredCommand = createCommand(
                markPurchaseOrderAsDeliveredInstance());
        commandBus.post(markAsDeliveredCommand, observer);
    }

    @Test
    @DisplayName("Should change aggregates")
    void checkAggregate() {
        assertNull(observer.getError());

        final Optional<Repository> purchaseOrderRepository = boundedContext.findRepository(
                PurchaseOrder.class);

        assertTrue(purchaseOrderRepository.isPresent());
        Optional<PurchaseOrderAggregate> purchaseOrder = purchaseOrderRepository.get()
                                                                                .find(PURCHASE_ORDER_ID);

        assertTrue(purchaseOrder.isPresent());
        assertEquals(DELIVERED, purchaseOrder.get()
                                             .getState()
                                             .getStatus());
    }

    @Test
    @DisplayName("Should change full menu projections")
    void checkFullMenu() {
        final Repository fullMenuRepository = boundedContext.findRepository(FullMenuView.class)
                                                            .get();
        final FullMenuViewProjection fullMenu1 = (FullMenuViewProjection) fullMenuRepository.find(
                MENU_ID)
                                                                                            .get();
        final FullMenuViewProjection fullMenu2 = (FullMenuViewProjection) fullMenuRepository.find(
                MENU_ID2)
                                                                                            .get();
        assertNotEquals(fullMenu1.getState()
                                 .getMenuId(), fullMenu2.getState()
                                                        .getMenuId());
        assertEquals(fullMenu1.getState()
                              .getDishesByCategoryList(), fullMenu2.getState()
                                                                   .getDishesByCategoryList());
    }

    @Test
    @DisplayName("Should change calendar repository")
    void checkMenuCalendar() {
        final Repository fullMenuRepository = boundedContext.findRepository(FullMenuView.class)
                                                            .get();

    }
    private ActorRequestFactory getRequestFactory() {
        return requestFactory;
    }
}
