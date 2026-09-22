package com.example.ui;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;

public class GameTerminal {
    public com.googlecode.lanterna.terminal.Terminal terminal = null;
    private final DefaultTerminalFactory factory = new DefaultTerminalFactory();

    public Terminal getTerminal() throws IOException {
        terminal = factory.createTerminal();
        terminal.enterPrivateMode();
        terminal.clearScreen();
        return terminal;
    }

    @Deprecated
    public TerminalScreen getTerminalScreen() throws IOException {
        if (this.terminal == null) {
            return null;
        }
        return new TerminalScreen(this.terminal);
    }
}
