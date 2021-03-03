package ru.netology;

public class Customer implements Runnable {
    public static final int TIME_TO_EAT = 2000;
    public static final int TIME_TO_TAKE_OFF_OUTERWEAR = 2000;
    private Restaurant restaurant;

    public Customer(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    @Override
    public void run() {
        restaurant.customerWork(this);
    }
}
