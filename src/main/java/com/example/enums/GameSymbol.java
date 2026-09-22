package com.example.enums;

public enum GameSymbol {
    PLAYER('@'),
    HOSPITAL('H'),
    BANK('B'),
    CASINO('C'),
    GYM('G'),
    FENCE('#'),
    EMPTY_SPACE('◦');

    private final char symbol;
    GameSymbol(char symbol) {
        this.symbol = symbol;
    }

    public char getSymbol() {
        return symbol;
    }
}
