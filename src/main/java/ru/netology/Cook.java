package ru.netology;

public class Cook implements Runnable {
    public static final int TIME_TO_COOK = 3000;
    private Restaurant restaurant;

    public Cook(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    @Override
    public void run() {
        restaurant.cookWork();
    }
}
