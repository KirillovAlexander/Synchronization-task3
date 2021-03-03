package ru.netology;

public class Waiter implements Runnable {
    public static final int SPEAK_WITH_CUSTOMER = 2000;
    public static final int TIME_TO_TRANSFER = 1000;
    private Restaurant restaurant;

    public Waiter(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    @Override
    public void run() {
        restaurant.waiterWork();
    }
}
