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

package javaclasses.mealorder.c.order;

import com.google.common.base.Optional;
import io.spine.time.LocalDate;
import javaclasses.mealorder.Dish;
import javaclasses.mealorder.DishId;
import javaclasses.mealorder.LocalDateComparator;
import javaclasses.mealorder.Menu;
import javaclasses.mealorder.MenuDateRange;
import javaclasses.mealorder.MenuId;
import javaclasses.mealorder.OrderId;
import javaclasses.mealorder.c.command.CreateOrder;
import javaclasses.mealorder.c.command.RemoveDishFromOrder;
import javaclasses.mealorder.c.rejection.CannotRemoveMissingDish;
import javaclasses.mealorder.c.rejection.MenuNotAvailable;
import javaclasses.mealorder.c.vendor.VendorAggregate;
import javaclasses.mealorder.c.vendor.VendorRepository;

import java.util.Comparator;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static javaclasses.mealorder.c.order.OrderAggregateRejections.CreateOrderRejections.menuNotAvailable;
import static javaclasses.mealorder.c.order.OrderAggregateRejections.RemoveDishFromOrderRejections.cannotRemoveMissingDish;

/**
 * Utility class that contains static methods that operate on order aggregate.
 *
 * @author Vlad Kozachenko
 */
class Orders {

    /**
     * Prevent instantiation of this utility class.
     */
    private Orders() {
    }

    /**
     * Checks whether the menu is available on the date of the order.
     *
     * @param range     menu date range to check
     * @param orderDate order date to check
     * @return boolean true if there is a menu on the order date
     */
    static boolean checkRangeIncludesDate(MenuDateRange range, LocalDate orderDate) {
        checkNotNull(range);
        checkNotNull(orderDate);
        final Comparator<LocalDate> comparator = new LocalDateComparator();

        return comparator.compare(range.getRangeStart(), orderDate) <= 0 &&
                comparator.compare(range.getRangeEnd(), orderDate) >= 0;
    }

    /**
     * Finds vendor for order.
     *
     * @param orderId ID of the order for which vendor have to be found.
     * @return Optional of {@code Vendor}.
     */
    static Optional<VendorAggregate> getVendorAggregateForOrder(OrderId orderId) throws
                                                                                 MenuNotAvailable {
        checkNotNull(orderId);
        final VendorRepository vendorRepository = VendorRepository.getRepository();

        final Optional<VendorAggregate> vendor = vendorRepository.find(orderId.getVendorId());

        return vendor;
    }

    /**
     * Checks whether the menu is available on the date of the order.
     *
     * @param cmd command that contains order ID and menu ID that may be checked.
     * @throws MenuNotAvailable if vendor or menu doesn't exist or
     *                          if the menu date range doesn't include order date.
     */
    static void checkMenuAvailability(CreateOrder cmd) throws MenuNotAvailable {
        checkNotNull(cmd);
        final OrderId orderId = cmd.getOrderId();
        final MenuId menuId = cmd.getMenuId();
        final Optional<VendorAggregate> vendor = getVendorAggregateForOrder(orderId);
        if (!vendor.isPresent()) {
            throw menuNotAvailable(cmd);
        }
        final VendorAggregate vendorAggregateForOrder = vendor.get();
        final List<Menu> menus = vendorAggregateForOrder.getState()
                                                        .getMenuList();

        final java.util.Optional<Menu> menu = menus.stream()
                                                   .filter(m -> menuId.equals(m.getId()))
                                                   .findFirst();

        final LocalDate orderDate = orderId.getOrderDate();
        if (!menu.isPresent() || !checkRangeIncludesDate(menu.get()
                                                             .getMenuDateRange(),
                                                         orderDate)) {
            throw menuNotAvailable(cmd);
        }
    }

    static Dish getDishFromOrder(RemoveDishFromOrder cmd, DishId dishId,
                                 List<Dish> dishesList) throws CannotRemoveMissingDish {
        java.util.Optional<Dish> dish = dishesList.stream()
                                                  .filter(d -> d.getId()
                                                                .equals(dishId))
                                                  .findFirst();

        if (!dish.isPresent()) {
            throw cannotRemoveMissingDish(cmd);
        }
        return dish.get();
    }
}
