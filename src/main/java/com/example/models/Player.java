package com.example.models;

public class Player {
    public Position playerPosition;
    public double money = 150;
    public int level = 1;



    public Player(int posX, int posY) {
        this.playerPosition = new Position(posX, posY);
    }


    public void setPosition(Position playerPosition) {
        this.playerPosition.posX = playerPosition.posX;
        this.playerPosition.posY = playerPosition.posY;
    }

    public Position getPosition() {
        return this.playerPosition;
    }

    public double getMoney() {
        return money;
    }

    // Może zarówno setMoney jak i setLevel powinno być prywatne,
    // a stworzenie dodatkowych metod do konkretnej opracji i.e. wypłata pieniędzy mogłoby byc prawidłowe.
    // Na potrzeby prototypu pozostawiam publiczne.
    public void setMoney(double money) {
        this.money = money;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void levelUp() {
        this.level++;
    }

    // getPokemon
    // getSinglePokemon
    // addPokemon
    // removePokemon
    // clearPokemon
}

// TODO
//    public double money = 0.00;
//    public int exp = 0;
//    public int level = 1;
//    public String[] pokemon = {};

