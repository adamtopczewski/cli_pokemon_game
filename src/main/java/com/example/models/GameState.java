package com.example.models;

import java.io.IOException;

public interface GameState {
    void onEnter() throws IOException;
    void handleInput() throws IOException;
    void render() throws IOException;
    void onExit() throws IOException;
}