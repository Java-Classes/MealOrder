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
    window.MealOrderAppTest = window.MealOrderAppTest || {};

    MealOrderAppTest.initialMenu = [{
        vendor: 'Пюре',
        date: new Date('2018-03-22'),
        categories: [{
            categoryName: 'Первое блюдо', dishes: [
                {title: 'Суп гречневый', price: 35},
                {title: 'Солянка', price: 40},
                {title: 'Суп гороховый', price: 140},
                {title: 'Уха', price: 35},
                {title: 'Суп-пюре грибной', price: 40}]
        }
            , {
                categoryName: 'Второе блюдо', dishes: [
                    {title: 'Утиные крылышки', price: 40},
                    {title: 'Куриные крылышки', price: 45}]
            }
        ]
    }];

    MealOrderAppTest.ininitalOrder = [{
        date: new Date('2018-03-21'),
        order: [{
            vendor: "Пюре",
            dishes: [
                {title: 'Борщ украинский', price: 35, quantity: 1},
                {title: 'Солянка', price: 40, quantity: 1},
                {title: 'Суп гороховый', price: 40, quantity: 1},
                {title: 'Уха', price: 35, quantity: 1},
                {title: 'Суп-пюре грибной', price: 40, quantity: 1},
                {title: 'Суп куриный', price: 40, quantity: 1},]
        }, {
            vendor: 'Позитив',
            dishes: [{title: 'Суп куриный', price: 35, quantity: 1}, {
                title: 'Стейк из лосося',
                price: 35,
                quantity: 1
            }]
        }]
    }];

    MealOrderAppTest.ininitalHistory = [{
        date: new Date('2018-03-20'),
        order: [{
            vendor: "Пюре",
            dishes: [
                {title: 'Борщ украинский', price: 35, quantity: 1},
                {title: 'Солянка', price: 40, quantity: 1},
                {title: 'Суп гороховый', price: 40, quantity: 1},
                {title: 'Уха', price: 35, quantity: 1},
                {title: 'Суп-пюре грибной', price: 40, quantity: 1},
                {title: 'Суп куриный', price: 40, quantity: 1},]
        }, {
            vendor: 'Позитив',
            dishes: [{title: 'Суп куриный', price: 35, quantity: 1}, {
                title: 'Стейк из лосося',
                price: 35,
                quantity: 1
            }]
        }]
    }];
}