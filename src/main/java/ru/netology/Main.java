package ru.netology;

import java.util.concurrent.locks.ReentrantLock;

public class Main {
    public static void main(String[] args) {
        Restaurant restaurant = new Restaurant(new ReentrantLock());
        Cook cook = new Cook(restaurant);
        Waiter waiter = new Waiter(restaurant);
        Customer customer = new Customer(restaurant);
        ThreadGroup threadGroup = new ThreadGroup("Staff");

        new Thread(null, cook, "Повар 1").start();
        new Thread(null, cook, "Повар 2").start();
        new Thread(threadGroup, waiter, "Официант 1").start();
        new Thread(threadGroup, waiter, "Официант 2").start();
        new Thread(threadGroup, customer, "Клиент 1").start();
        new Thread(threadGroup, customer, "Клиент 2").start();
        new Thread(threadGroup, customer, "Клиент 3").start();
        new Thread(threadGroup, customer, "Клиент 4").start();

        try {
            Thread.sleep(Restaurant.WORK_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        restaurant.setWork(false);
    }
}
