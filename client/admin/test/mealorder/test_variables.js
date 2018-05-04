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
    window.MealOrderAdminAppTest = window.MealOrderAdminAppTest || {};

    MealOrderAdminAppTest.vendors = [
        {
            title: 'Пюре',
            phone: '050-12-45-876',
            email: 'smashpotato@gmail.com',
            deadline: '11:00'
        }, {
            title: 'Позитив',
            phone: '050-14-88-322',
            email: 'Positiv@gmail.com',
            deadline: '10:00'
        }];
    MealOrderAdminAppTest.monthlySpendings = {
        date: new Date("2018-05-01"), users: [
            {name: 'Стив Джобс', amount: 100},
            {name: 'Майкл Скофилд', amount: 200},
            {name: 'Вова Бессонов', amount: 938},
            {name: 'Андрей Жданов', amount: 3567},
            {name: 'Яна Смешарикова', amount: 100}]
    };
}
