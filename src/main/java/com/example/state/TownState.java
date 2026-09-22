package com.example.state;

import com.example.enums.GameSymbol;
import com.example.models.GameState;
import com.example.models.Position;
import com.example.utils.TownMap;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;
import java.util.ArrayList;

public class TownState implements GameState {
    private final SceneManager manager;
    private final TownMap townMap = new TownMap("map.txt");
    private Terminal terminal;
    private boolean isPaused = false;

    TownState(SceneManager manager) {
        this.manager = manager;
    }

    @Override
    public void onEnter() throws IOException {
        this.isPaused = false;
        this.terminal = this.manager.getTerminal();
        this.terminal.flush();
        this.townMap.generateTownMap();
        this.renderTown();
        int[] playerPositionCords = this.townMap.findFirstPosition(GameSymbol.EMPTY_SPACE.getSymbol());
        Position playerPosition = new Position(playerPositionCords[0], playerPositionCords[1]);

        this.manager.player.setPosition(playerPosition);
    }

    @Override
    public void handleInput() throws IOException {
        // Zamiana na readInput będzie blokować egezkucję programu i na dobrą sprawę zmieni grę w turówkę
        if (isPaused) return;
        KeyStroke keyStroke = this.terminal.pollInput();
        if (keyStroke == null || keyStroke.getCharacter() == null) {
            return;
        }
        if (keyStroke.getCharacter().equals('Q') || keyStroke.getCharacter().equals('q')) {
            this.manager.stop();
            return;
        }

        Position playerPosition = this.manager.player.getPosition();
        Position newPlayerPosition = null;
        char lookAhead = 0;
        // TODO refactor + enum
        switch (keyStroke.getCharacter()) {
            case 'w':
                newPlayerPosition = new Position(playerPosition.posX, playerPosition.posY - 1);
                lookAhead = this.townMap.getSymbolAt(newPlayerPosition.posX, newPlayerPosition.posY);
                break;
            case 's':
                newPlayerPosition = new Position(playerPosition.posX, playerPosition.posY + 1);
                lookAhead = this.townMap.getSymbolAt(newPlayerPosition.posX, newPlayerPosition.posY);
                break;
            case 'a':
                newPlayerPosition = new Position(playerPosition.posX - 1, playerPosition.posY);
                lookAhead = this.townMap.getSymbolAt(newPlayerPosition.posX, newPlayerPosition.posY);
                break;
            case 'd':
                newPlayerPosition = new Position(playerPosition.posX + 1, playerPosition.posY);
                lookAhead = this.townMap.getSymbolAt(newPlayerPosition.posX, newPlayerPosition.posY);
                break;
            default:
                break;
        }
        if (lookAhead == 0) {
            return;
        }

        // TODO refactor
        if (lookAhead == GameSymbol.EMPTY_SPACE.getSymbol()) {
            this.manager.player.setPosition(newPlayerPosition);
        }

        if (lookAhead == Character.toLowerCase(GameSymbol.BANK.getSymbol())) {
            this.isPaused = true;
            this.terminal.clearScreen();
            this.manager.pushState(new BankState(this.manager, this.manager.bank, () -> {
                try {
                    unpause();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }));
        }

        if (lookAhead == Character.toLowerCase(GameSymbol.HOSPITAL.getSymbol())) {
            this.manager.stop();
        }

        if (lookAhead == Character.toLowerCase(GameSymbol.CASINO.getSymbol())) {
            this.manager.stop();
        }

        if (lookAhead == Character.toLowerCase(GameSymbol.GYM.getSymbol())) {
            this.manager.stop();
        }
    }

    @Override
    public void render() throws IOException {
        if (this.isPaused) return;
        this.renderTown();
        this.renderPlayer();
    }

    @Override
    public void onExit() throws IOException {
        this.manager.stop();
    }

    private void renderTown() throws IOException {
        ArrayList<ArrayList<Character>> townGrid = this.townMap.getTownGrid();
        int row = 0;
        int column = 0;
        TextGraphics textGraphics = this.terminal.newTextGraphics();
        for (ArrayList<Character> charRow : townGrid) {
            for (Character ch : charRow) {
                textGraphics.putString(column, row, ch.toString());
                column++;
            }
            column = 0;
            row++;
        }
        this.terminal.flush();
    }

    private void renderPlayer() throws IOException {
        TextGraphics playerGraphics = this.terminal.newTextGraphics();
        Position playerPosition = this.manager.player.getPosition();
        playerGraphics.setCharacter(playerPosition.posX, playerPosition.posY, GameSymbol.PLAYER.getSymbol());
        this.terminal.flush();
    }

    public void unpause() throws IOException {
        this.isPaused = false;
        this.terminal.clearScreen();
    }
}