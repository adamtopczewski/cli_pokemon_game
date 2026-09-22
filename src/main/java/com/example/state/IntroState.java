package com.example.state;

import com.example.models.GameState;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;

public class IntroState implements GameState {
    private final SceneManager manager;
    private TextGraphics textGraphics;
    private Terminal terminal;

    public IntroState(SceneManager manager) {
        this.manager = manager;
    }

    @Override
    public void onEnter() throws IOException {
        this.terminal = this.manager.getTerminal();
        this.textGraphics = this.terminal.newTextGraphics();
    }

    @Override
    public void handleInput() throws IOException {
        while (true) {
            KeyStroke keyStroke = this.terminal.readInput();
            if (keyStroke.getKeyType() == KeyType.Enter) {
                this.manager.changeState(new TownState(this.manager));
                break;
            } else if (keyStroke.getCharacter().equals('Q') || keyStroke.getCharacter().equals('q')) {
                this.manager.stop();
                break;
            }
        }
    }

    @Override
    public void render() throws IOException {
        int renderOffset = 4;
        String INTRO_MESSAGE = "***********************************\n*** Welcome to the Pokemon game ***\n***********************************";
        String INSTRUCTIONS = "Press 'Enter' to continue or press 'Q' to exit program.";
        int introLine = 0;
        for (String line : INTRO_MESSAGE.split("\n")) {
            this.textGraphics.putString(renderOffset, renderOffset + introLine, line);
            introLine++;
        }
        this.textGraphics.putString(renderOffset, this.terminal.getTerminalSize().getRows() - renderOffset, INSTRUCTIONS);
        this.terminal.flush();
    }

    @Override
    public void onExit() throws IOException {
        this.terminal.clearScreen();
    }
}