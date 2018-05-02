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
import io.spine.time.MonthOfYear;
import javaclasses.mealorder.LocalMonth;
import javaclasses.mealorder.MonthlySpendingsReportId;
import javaclasses.mealorder.OrderListId;
import javaclasses.mealorder.PurchaseOrder;
import javaclasses.mealorder.PurchaseOrderSender;
import javaclasses.mealorder.PurchaseOrderStatus;
import javaclasses.mealorder.ServiceFactory;
import javaclasses.mealorder.VendorListId;
import javaclasses.mealorder.c.po.PurchaseOrderAggregate;
import javaclasses.mealorder.q.FullMenuViewProjection;
import javaclasses.mealorder.q.MenuCalendarItem;
import javaclasses.mealorder.q.MenuCalendarViewProjection;
import javaclasses.mealorder.q.MenuListViewProjection;
import javaclasses.mealorder.q.MonthlySpendingsReportViewProjection;
import javaclasses.mealorder.q.OrderListViewProjection;
import javaclasses.mealorder.q.PurchaseOrderDetailsByDishViewProjection;
import javaclasses.mealorder.q.PurchaseOrderDetailsByUserViewProjection;
import javaclasses.mealorder.q.PurchaseOrderItemViewProjection;
import javaclasses.mealorder.q.VendorListViewProjection;
import javaclasses.mealorder.q.projection.FullMenuView;
import javaclasses.mealorder.q.projection.MenuCalendarView;
import javaclasses.mealorder.q.projection.MenuListView;
import javaclasses.mealorder.q.projection.MonthlySpendingsReportView;
import javaclasses.mealorder.q.projection.OrderListView;
import javaclasses.mealorder.q.projection.PurchaseOrderDetailsByDishView;
import javaclasses.mealorder.q.projection.PurchaseOrderDetailsByUserView;
import javaclasses.mealorder.q.projection.PurchaseOrderItemView;
import javaclasses.mealorder.q.projection.VendorListView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.List;

import static io.spine.protobuf.TypeConverter.toMessage;
import static javaclasses.mealorder.PurchaseOrderStatus.DELIVERED;
import static javaclasses.mealorder.testdata.TestOrderCommandFactory.addDishToOrderInstance;
import static javaclasses.mealorder.testdata.TestOrderCommandFactory.createOrderInstance;
import static javaclasses.mealorder.testdata.TestPurchaseOrderCommandFactory.createPurchaseOrderInstance;
import static javaclasses.mealorder.testdata.TestPurchaseOrderCommandFactory.markPurchaseOrderAsDeliveredInstance;
import static javaclasses.mealorder.testdata.TestValues.DATE;
import static javaclasses.mealorder.testdata.TestValues.MENU_ID;
import static javaclasses.mealorder.testdata.TestValues.MENU_ID2;
import static javaclasses.mealorder.testdata.TestValues.PURCHASE_ORDER_ID;
import static javaclasses.mealorder.testdata.TestValues.USER_ID;
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
    private ActorRequestFactory requestFactory;
    private CommandBus commandBus;
    private BoundedContext boundedContext;
    private MemoizingObserver<Ack> observer;

    private Command createCommand(GeneratedMessageV3 message) {
        return getRequestFactory().command()
                                  .create(toMessage(message));
    }

    @DisplayName("Add vendor -> Import menu -> Set date range for menu ->" +
            " Create order -> Add dish to order -> Create purchase order ->" +
            " Mark purchase order as delivered ")
    @BeforeEach
    public void setUp() {
        requestFactory =
                TestActorRequestFactory.newInstance(getClass());
        observer = MemoizingObserver.newInstance();
        boundedContext = BoundedContexts.create();
        commandBus = boundedContext.getCommandBus();

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
        final Repository menuCalendarViewRepository = boundedContext.findRepository(
                MenuCalendarView.class)
                                                                    .get();
        final MenuCalendarViewProjection menuCalendarViewProjection = (MenuCalendarViewProjection) menuCalendarViewRepository.find(
                MenuCalendarViewProjection.ID)
                                                                                                                             .get();
        final List<MenuCalendarItem> calendarItemList = menuCalendarViewProjection.getState()
                                                                                  .getCalendarItemList();
        assertEquals(33, calendarItemList.size());
        assertEquals(13, calendarItemList.get(0)
                                         .getDate()
                                         .getDay());
    }

    @Test
    @DisplayName("Should change menu list")
    void checkMenuList() {
        final Repository menuListViewRepository = boundedContext.findRepository(
                MenuListView.class)
                                                                .get();

        final MenuListViewProjection menuListViewProjection = (MenuListViewProjection) menuListViewRepository.find(
                MenuListViewProjection.ID)
                                                                                                             .get();
        assertEquals(33, menuListViewProjection.getState()
                                               .getMenuList()
                                               .size());
    }

    @Test
    @DisplayName("Should change monthly spendings")
    void checkMonthlySpendings() {
        final Repository monthlySpendingsRepository = boundedContext.findRepository(
                MonthlySpendingsReportView.class)
                                                                    .get();

        final LocalMonth localMonth = LocalMonth.newBuilder()
                                                .setYear(2019)
                                                .setMonth(MonthOfYear.FEBRUARY)
                                                .build();
        final MonthlySpendingsReportViewProjection spendingsProjection = (MonthlySpendingsReportViewProjection) monthlySpendingsRepository.find(
                MonthlySpendingsReportId.newBuilder()
                                        .setMonth(localMonth)
                                        .build())
                                                                                                                                          .get();
        spendingsProjection.getId();
        assertEquals("user@example.com", spendingsProjection.getState()
                                                            .getUserSpending(0)
                                                            .getId()
                                                            .getEmail()
                                                            .getValue());
        assertEquals(56, spendingsProjection.getState()
                                            .getUserSpending(0)
                                            .getAmount()
                                            .getAmount());
    }

    @Test
    @DisplayName("Should change order view")
    void checkOrderView() {
        final Repository orderListViewRepository = boundedContext.findRepository(
                OrderListView.class)
                                                                 .get();

        final OrderListViewProjection orderListViewProjection = (OrderListViewProjection) orderListViewRepository.find(
                OrderListId.newBuilder()
                           .setUserId(USER_ID)
                           .setOrderDate(DATE)
                           .build())
                                                                                                                 .get();
        assertEquals("dishName1", orderListViewProjection.getState()
                                                         .getOrder(0)
                                                         .getDish(0)
                                                         .getName());
    }

    @Test
    @DisplayName("Should change po by dish view")
    void checkPoByDish() {
        final Repository poDetailsByDishViewRepository = boundedContext.findRepository(
                PurchaseOrderDetailsByDishView.class)
                                                                       .get();
        final Iterator iterator = poDetailsByDishViewRepository.iterator(input -> true);
        for (Iterator it = iterator; it.hasNext(); ) {
            final Object o = it.next();
        }
        final PurchaseOrderDetailsByDishViewProjection poDetailsByDishViewProjection = (PurchaseOrderDetailsByDishViewProjection) poDetailsByDishViewRepository.find(
                PURCHASE_ORDER_ID)
                                                                                                                                                               .get();
        assertEquals(PurchaseOrderStatus.DELIVERED, poDetailsByDishViewProjection.getState()
                                                                                 .getPurchaseOrderStatus());
        assertEquals("dishName1", poDetailsByDishViewProjection.getState()
                                                               .getDishList()
                                                               .get(0)
                                                               .getName());
    }

    @Test
    @DisplayName("Should change po by user view")
    void checkPoByUser() {
        final Repository poDetailsByUserViewRepository = boundedContext.findRepository(
                PurchaseOrderDetailsByUserView.class)
                                                                       .get();
        final PurchaseOrderDetailsByUserViewProjection poDetailsByUserViewProjection = (PurchaseOrderDetailsByUserViewProjection) poDetailsByUserViewRepository.find(
                PURCHASE_ORDER_ID)
                                                                                                                                                               .get();
        assertEquals(1, poDetailsByUserViewProjection.getState()
                                                     .getOrderList()
                                                     .size());
        assertEquals("user@example.com", poDetailsByUserViewProjection.getState()
                                                                      .getOrderList()
                                                                      .get(0)
                                                                      .getId()
                                                                      .getEmail()
                                                                      .getValue());
        assertEquals(1, poDetailsByUserViewProjection.getState()
                                                     .getOrderList()
                                                     .get(0)
                                                     .getDishList()
                                                     .size());
    }

    @Test
    @DisplayName("Should change po item view")
    void checkPoItemView() {
        final Repository poItemViewRepository = boundedContext.findRepository(
                PurchaseOrderItemView.class)
                                                              .get();
        final PurchaseOrderItemViewProjection poItemViewProjection = (PurchaseOrderItemViewProjection) poItemViewRepository.find(
                PURCHASE_ORDER_ID)
                                                                                                                           .get();
        assertEquals(DELIVERED, poItemViewProjection.getState()
                                                    .getPurchaseOrderStatus());
        assertEquals("vendor:value: \"VendorName1\"", poItemViewProjection.getState()
                                                                          .getId()
                                                                          .getVendorId()
                                                                          .getValue()
                                                                          .trim());
    }

    @Test
    @DisplayName("Should change vendor list view")
    void checkVendorList() {
        final Repository vendorListViewRepository = boundedContext.findRepository(
                VendorListView.class)
                                                                  .get();
        final VendorListId vendorListId = VendorListViewProjection.ID;
        final VendorListViewProjection vendorListViewProjection = (VendorListViewProjection) vendorListViewRepository.find(
                vendorListId)
                                                                                                                     .get();
        assertEquals("VendorListViewProjectionSingleton", vendorListViewProjection.getId()
                                                                                  .getValue());
        assertEquals(2, vendorListViewProjection.getState()
                                                .getVendorList()
                                                .get(0)
                                                .getPhoneNumberList()
                                                .size());
    }

    private ActorRequestFactory getRequestFactory() {
        return requestFactory;
    }
}
