package ru.netology;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Restaurant {
    public static final long WORK_TIME = 1500000000L;

    private ReentrantLock locker;
    private Condition waitForFreeWaiter;
    private Condition customerIsWaitingForMeal;
    private Condition newOrderForCook;
    private Condition orderIsReadyToTransfer;
    private Condition newClient;
    private boolean isWork;

    List<Customer> customers = new ArrayList<>();
    List<Waiter> waiters = new ArrayList<>();

    public Restaurant(ReentrantLock locker) {
        this.locker = locker;
        this.waitForFreeWaiter = locker.newCondition();
        this.customerIsWaitingForMeal = locker.newCondition();
        this.newOrderForCook = locker.newCondition();
        this.newClient = locker.newCondition();
        this.orderIsReadyToTransfer = locker.newCondition();
        this.isWork = true;
    }

    public void setWork(boolean work) {
        this.isWork = work;
    }

    public void customerWork(Customer customer) {
        System.out.println(Thread.currentThread().getName() + " пришел в ресторан.");
        try {
            locker.lock();
            Thread.sleep(Customer.TIME_TO_TAKE_OFF_OUTERWEAR);
            customers.add(new Customer(this));
            newClient.signal();
            //Если нет свободных официантов - ждем
            while (waiters.size() == 0) {
                System.out.println(Thread.currentThread().getName() + " ожидает свободного официанта.");
                waitForFreeWaiter.await();
            }
            //забираем свободного официанта
            waiters.remove(0);
            //жду готового заказа
            customerIsWaitingForMeal.await();
            Thread.sleep(Customer.TIME_TO_EAT);
            System.out.println(Thread.currentThread().getName() + " покушал и покинул ресторан.");
            locker.unlock();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            if (locker.isLocked()) locker.unlock();
        }
    }

    public void waiterWork() {
        System.out.println(Thread.currentThread().getName() + " готов к работе.");
        while (isWork) {
            try {
                waiters.add(new Waiter(this));
                locker.lock();
                waitForFreeWaiter.signal();
                if (customers.size() == 0) {
                    System.out.println(Thread.currentThread().getName() + " ожидает клиента.");
                    newClient.await();
                }

                if (customers.size() > 0) {
                    customers.remove(0);
                    Thread.sleep(Waiter.SPEAK_WITH_CUSTOMER);
                    System.out.println(Thread.currentThread().getName() + " принимает заказ у клиента.");
                    Thread.sleep(Waiter.TIME_TO_TRANSFER);
                    System.out.println(Thread.currentThread().getName() + " передает заказ на кухню.");
                    newOrderForCook.signal();
                    Thread.sleep(Waiter.TIME_TO_TRANSFER);
                    orderIsReadyToTransfer.await();
                    System.out.println(Thread.currentThread().getName() + " приносит заказ с кухни.");
                    customerIsWaitingForMeal.signal();
                }

            } catch (InterruptedException ex) {
                ex.printStackTrace();
            } finally {
                if (locker.isLocked()) locker.unlock();
            }
        }
    }

    public void cookWork() {
        System.out.println(Thread.currentThread().getName() + " готов к работе, включает плиту.");
        while (isWork) {
            try {
                locker.lock();
                newOrderForCook.await();
                System.out.println(Thread.currentThread().getName() + " готовит блюдо.");
                Thread.sleep(Cook.TIME_TO_COOK);
                System.out.println(Thread.currentThread().getName() + " закончил готовить блюдо.");
                orderIsReadyToTransfer.signal();

            } catch (InterruptedException ex) {
                ex.printStackTrace();
            } finally {
                if (locker.isLocked()) locker.unlock();
            }
        }

    }
}
