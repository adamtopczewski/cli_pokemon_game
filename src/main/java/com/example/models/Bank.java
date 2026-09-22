package com.example.models;

public class Bank {
    int balance;
    Player player;

    public Bank(int balance, Player player) {
        this.balance = balance;
        this.player = player;
    }

    public int getBalance() {
        return balance;
    }

    public int deposit(int amount) {
        if (amount <= 0) {
            return 0;
        }
        this.balance += amount;
        return amount;
    }

    public int withdraw(int amount) {
        if (amount <= 0) {
            return 0;
        }
        if (amount > this.balance) {
            return -1;
        }
        this.balance -= amount;
        return amount;
    }

}
