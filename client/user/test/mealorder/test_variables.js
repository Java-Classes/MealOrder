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
{
    window.MealOrderAppTest = {};

    const arrayOfDates = [{
        date: {
            year: '2018',
            month: '03',
            day: '21',
        },
        dayOfTheWeek: 'Ср',
        hasMenu: true
    },
        {
            date: {
                year: '2018',
                month: '03',
                day: '22',
            },
            dayOfTheWeek: 'Чт',
            hasMenu: true
        },
        {
            date: {
                year: '2018',
                month: '03',
                day: '23',
            },
            dayOfTheWeek: 'Пт',
            hasMenu: true
        },
        {
            date: {
                year: '2018',
                month: '03',
                day: '24',
            },
            dayOfTheWeek: 'Сб',
            hasMenu: false
        },
        {
            date: {
                year: '2018',
                month: '03',
                day: '25',
            },
            dayOfTheWeek: 'Вс',
            hasMenu: false
        }
        , {
            date: {
                year: '2018',
                month: '03',
                day: '26',
            },
            dayOfTheWeek: 'Пн',
            hasMenu: true
        }
        , {
            date: {
                year: '2018',
                month: '03',
                day: '27',
            },
            dayOfTheWeek: 'Вт',
            hasMenu: true
        }
        , {
            date: {
                year: '2018',
                month: '03',
                day: '28',
            },
            dayOfTheWeek: 'Ср',
            hasMenu: true
        }];

    MealOrderAppTest.initialMenu = {
        calendar: arrayOfDates,
    };
}
