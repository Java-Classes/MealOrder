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

package javaclasses.mealorder;

import io.spine.time.LocalDate;

import java.util.Comparator;

import static java.lang.String.format;

/**
 * @author Yurii Haidamaka
 */
public class LocalDateComparator implements Comparator<LocalDate> {

    /**
     * Compares two LocalDate arguments for order.
     *
     * <p> Returns a negative integer, zero, or a positive integer
     * as the first argument is less than, equal to, or greater
     * than the second.
     *
     * @param o1 the first LocalDate object to be compared
     * @param o2 the second LocalDate object to be compared
     * @return a negative integer, zero, or a positive integer as the
     * first date is less than, equal to, or greater than the
     * second.
     */
    @Override
    public int compare(LocalDate o1, LocalDate o2) {
        final String o1String =
                format("%04d%02d%02d", o1.getYear(), o1.getMonth().getNumber(), o1.getDay());
        final String o2String =
                format("%04d%02d%02d", o2.getYear(), o2.getMonth().getNumber(), o2.getDay());

        return o1String.compareTo(o2String);
    }
}
