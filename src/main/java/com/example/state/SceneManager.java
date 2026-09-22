package com.example.state;

import com.example.models.Bank;
import com.example.models.GameState;
import com.example.models.Player;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

public class SceneManager {
    private final Deque<GameState> stateStack = new ArrayDeque<>();
    private final Terminal terminal;
    boolean isRunning = true;
    public Player player;
    public Bank bank;

    public SceneManager(Terminal terminal, Player player) {
        this.terminal = terminal;
        this.player = player;
        this.bank = new Bank(0, this.player);
        try {
            this.terminal.setCursorVisible(false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    Terminal getTerminal() {
        return this.terminal;
    }

    public void pushState(GameState state) throws IOException {
        stateStack.push(state);
        state.onEnter();
        this.terminal.flush();
    }

    public void popState() throws IOException {
        if (!stateStack.isEmpty()) {
            stateStack.pop().onExit();
        }
    }

    public void changeState(GameState state) throws IOException {
        this.popState();
        this.pushState(state);
    }

    public GameState getCurrentState() {
        return stateStack.peek();
    }

    public boolean isRunning() {
        return this.isRunning && !stateStack.isEmpty();
    }

    public void stop() throws IOException {
        this.terminal.close();
        this.isRunning = false;
    }
}