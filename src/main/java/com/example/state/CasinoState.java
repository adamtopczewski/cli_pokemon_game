package com.example.state;

import com.example.models.GameState;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;

public class CasinoState implements GameState{
    private final SceneManager manager;
    private Terminal terminal;


    CasinoState(SceneManager manager) {
        this.manager = manager;
    }

    @Override
    public void onEnter() throws IOException {
        this.terminal = this.manager.getTerminal();
    }

    @Override
    public void handleInput() throws IOException {
        KeyStroke keyStroke = this.terminal.pollInput();
        if (keyStroke == null) {
            return;
        }
        if (keyStroke.getCharacter().equals('Q') || keyStroke.getCharacter().equals('q')) {
            this.manager.stop();
        }
    }

    @Override
    public void render() throws IOException {

    }

    @Override
    public void onExit() throws IOException {
        this.manager.stop();
    }
}