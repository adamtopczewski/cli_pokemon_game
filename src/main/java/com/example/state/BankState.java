package com.example.state;

import com.example.models.Bank;
import com.example.models.GameState;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.Terminal;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;

public class BankState implements GameState {
    private final SceneManager manager;
    private Terminal terminal;
    private final Bank bank;
    private TextGraphics textGraphics;
    private Runnable onExitCallBack;
    private int isInputMode = 0;
    private final Deque<Integer> inputBuffer = new ArrayDeque<>();
    private int columnOffset = 2;
    private int inputOffset = 2;

    BankState(SceneManager manager, Bank bank, Runnable onExitCallBack) {
        this.manager = manager;
        this.bank = bank;
        this.onExitCallBack = onExitCallBack;
    }

    @Override
    public void onEnter() throws IOException {
        terminal = manager.getTerminal();
        textGraphics = terminal.newTextGraphics();
        inputBuffer.add(0);
        terminal.flush();
    }

    @Override
    public void handleInput() throws IOException {
        KeyStroke keyStroke = terminal.pollInput();
        if (keyStroke == null || keyStroke.getCharacter() == null) {
            return;
        }

        if (this.isInputMode == 0) {
            switch (keyStroke.getCharacter()){
                case '1':
                    isInputMode = 1;
                    textGraphics.putString(
                            1,
                            terminal.getTerminalSize().getRows() - 6 ,
                            "How much money would you like to deposit?"
                    );
                    break;
                case '2':
                    isInputMode = 2;
                    textGraphics.putString(
                            1,
                            terminal.getTerminalSize().getRows() - 6 ,
                            "How much money would you like to withdraw?"
                    );
                    break;
                case '3':
                    manager.popState();
                    break;
            }
        } else {
            handleInputMode(keyStroke, isInputMode);
        }
    }

    @Override
    public void render() throws IOException {
        printIntro();
        printVirtualCursor();
        terminal.flush();
    }

    @Override
    public void onExit() throws IOException {
        this.isInputMode = 0;
        terminal.setCursorVisible(false);
        terminal.flush();
        if (onExitCallBack != null) {
            onExitCallBack.run();
        }
    }

    private void printIntro() throws IOException {
        textGraphics.setBackgroundColor(TextColor.ANSI.BLACK);
        textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
        textGraphics.putString(columnOffset, 0, "======== BANK ========");
        textGraphics.putString(columnOffset, 3, "---- Your current balance is: " + bank.getBalance() + "$.");
        textGraphics.putString(columnOffset, 4, "---- You have: " + this.manager.player.getMoney() + "$ on hand.");
        textGraphics.putString(columnOffset, 6, "Would you like to:");
        textGraphics.putString(columnOffset, 7, "1. Deposit");
        textGraphics.putString(columnOffset, 8, "2. Withdraw");
        textGraphics.putString(columnOffset, 9, "3. Exit");
    }

    private void printVirtualCursor() throws IOException {
        TerminalSize size = terminal.getTerminalSize();
        textGraphics = terminal.newTextGraphics();
        if (isInputMode == 0) {
            this.clearRow(size.getRows() - inputOffset);
//            textGraphics.fillRectangle(new TerminalPosition(0, terminal.getTerminalSize().getRows() - 6), new TerminalSize(terminal.getTerminalSize().getColumns(), terminal.getTerminalSize().getRows()), ' ');
            return;
        }
        textGraphics.setBackgroundColor(TextColor.ANSI.WHITE);
        textGraphics.setForegroundColor(TextColor.ANSI.BLACK);
        textGraphics.putString(columnOffset, size.getRows() - inputOffset, parseInputBuffer());
    }

    private void handleInputMode(KeyStroke keyStroke, int isInputMode) throws IOException {
        Character ch = keyStroke.getCharacter();
        KeyType specialKey = keyStroke.getKeyType();
        // TODO handle delete input
        // TODO handle proceed logic
        // TODO refactor
        if (specialKey != null && specialKey != KeyType.Character) {
            switch (specialKey) {
                case Enter ->   {
                    int inputAmount = parseInt(parseInputBuffer());
                    int loggingOffset = 2;
                    TerminalSize size = terminal.getTerminalSize();
                    textGraphics = terminal.newTextGraphics();
                    switch (isInputMode) {
                        // TODO enum for isInputMode for better readability
                        case 1:
                            if (inputAmount > this.manager.player.getMoney()) {
                                textGraphics.putString(
                                        columnOffset,
                                        size.getRows() - inputOffset - loggingOffset,
                                        "Insufficient funds.");
                                break;
                            }
                            int depositedAmount = this.bank.deposit(inputAmount);
                            if (depositedAmount == 0) {
                                textGraphics.putString(
                                        columnOffset,
                                        size.getRows() - inputOffset + loggingOffset,
                                        "Please provide amount you want to deposit first.");
                            } else {
                                textGraphics.putString(
                                        columnOffset,
                                        size.getRows() - inputOffset + loggingOffset,
                                        "You've deposited " + depositedAmount + "$ into your account!");
                                this.manager.player.setMoney(this.manager.player.getMoney() - depositedAmount);
                            }
                            break;
                        case 2:
                            int withdrawAmount = this.bank.withdraw(inputAmount);
                            if (withdrawAmount < 0) {
                                textGraphics.putString(
                                        columnOffset,
                                        (size.getRows() - inputOffset) - loggingOffset,
                                        "Withdraw amount exceeds account funds.");
                            } else if (withdrawAmount == 0) {
                                textGraphics.putString(
                                        columnOffset,
                                        (size.getRows() - inputOffset) - loggingOffset,
                                        "Please provide amount you want to withdraw first.");
                            }  else {
                                textGraphics.putString(
                                        columnOffset,
                                        (size.getRows() - inputOffset) - loggingOffset,
                                        "You've withdrew " + withdrawAmount + "$ from your account!");
                                this.manager.player.setMoney(this.manager.player.getMoney() + withdrawAmount);
                            }
                            break;
                    }
                    this.inputBuffer.clear();
                    this.inputBuffer.add(0);
                    printVirtualCursor();
                    this.isInputMode = 0;
                }
                case Escape -> {
                    this.inputBuffer.clear();
                    this.inputBuffer.add(0);
                    this.isInputMode = 0;
                }
            }
            return;
        }

        if (!Character.isDigit(ch)) {
            return;
        }
        if (this.inputBuffer.size() == 1 && this.inputBuffer.getFirst() == 0) {
            if (ch.equals('0')) {
                return;
            } else {
                this.inputBuffer.clear();
            }
        }

        this.inputBuffer.add(Integer.parseInt(keyStroke.getCharacter().toString()));
    }

    private String parseInputBuffer() {
        return this.inputBuffer.stream().map(String::valueOf).collect(Collectors.joining());
    }

    private int getInputAmount() {
        return parseInt(parseInputBuffer());
    }

    private void clearRow (int row) throws IOException {
        textGraphics.fillRectangle(new TerminalPosition(0, row), new TerminalSize(this.terminal.getTerminalSize().getColumns(), row), ' ');
    }
}