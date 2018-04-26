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

package javaclasses.mealorder.q;

import io.spine.time.LocalDate;
import javaclasses.mealorder.CategoryName;
import javaclasses.mealorder.Dish;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class Projections {

    /**
     * The {@code private} constructor prevents the utility class instantiation.
     */
    private Projections() {
    }

    static List<java.time.LocalDate> getDatesBetween(
            LocalDate startDate, LocalDate endDate) {
        java.time.LocalDate start = java.time.LocalDate.of(startDate.getYear(),
                                                           startDate.getMonthValue(),
                                                           startDate.getDay());
        java.time.LocalDate end = java.time.LocalDate.of(endDate.getYear(), endDate.getMonthValue(),
                                                         endDate.getDay());
        long numOfDaysBetween = ChronoUnit.DAYS.between(start, end);
        final List<java.time.LocalDate> collect = IntStream.iterate(0, i -> i + 1)
                                                           .limit(numOfDaysBetween)
                                                           .mapToObj(i -> start.plusDays(i))
                                                           .collect(Collectors.toList());
        return collect;
    }

    static LocalDate toLocalDate(java.time.LocalDate date) {
        return LocalDate.newBuilder()
                        .setDay(date.getDayOfMonth())
                        .setMonthValue(date.getMonthValue())
                        .setYear(date.getYear())
                        .build();
    }

    static List<DishesByCategory> getAllDishesByCategories(
            List<Dish> dishList) {
        final List<DishesByCategory> dishesByCategoryList = new ArrayList<>();
        dishList.forEach(dish -> {
            final int index = categoryIndex(dishesByCategoryList, dish.getCategory());
            final DishItem newDish = DishItem.newBuilder()
                                             .setId(dish.getId())
                                             .setName(dish.getName())
                                             .setPrice(dish.getPrice())
                                             .build();
            if (index == -1) {
                final CategoryName category = CategoryName.newBuilder()
                                                          .setValue(dish.getCategory())
                                                          .build();

                final DishesByCategory dishesByCategory = DishesByCategory.newBuilder()
                                                                          .setCategory(category)
                                                                          .addDishes(newDish)
                                                                          .build();
                dishesByCategoryList.add(dishesByCategory);
            } else {
                final DishesByCategory newDishesByCategory = DishesByCategory.newBuilder(
                        dishesByCategoryList.get(index))
                                                                             .addDishes(newDish)
                                                                             .build();
                dishesByCategoryList.set(index, newDishesByCategory);
            }
        });
        return dishesByCategoryList;
    }

    static int categoryIndex(List<DishesByCategory> dishByCategory, String category) {
        final OptionalInt optionalInt = IntStream.range(0, dishByCategory.size())
                                                 .filter(i -> dishByCategory.get(i)
                                                                            .getCategory()
                                                                            .getValue()
                                                                            .equals(category))
                                                 .findFirst();
        return optionalInt.isPresent() ? optionalInt.getAsInt() : -1;
    }
}
