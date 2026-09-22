package com.example.utils;

import com.example.enums.GameSymbol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class TownMap {
    private final InputStream inputStream;
    private ArrayList<ArrayList<Character>> town;

    public TownMap(String resourceName) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        this.inputStream = classLoader.getResourceAsStream(resourceName);
    }

    public void generateTownMap() {
        ArrayList<ArrayList<Character>> town = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))){
            String line;
            while ((line = reader.readLine()) != null) {
                ArrayList<Character> row = line.chars()
                                .mapToObj(c -> (char) c)
                                        .collect(Collectors.toCollection(ArrayList::new));

                town.add(row);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.town = town;
    }

    public ArrayList<ArrayList<Character>> getTownGrid() {
        return this.town;
    }

    public Character getSymbolAt(int x, int y) {
        return this.town.get(y).get(x);
    }

    public int[] findFirstPosition(char target) {
        for (int row = 0; row < this.town.size(); row++) {
            int col = town.get(row).indexOf(target);
            if (col != -1) {
                return new int[] {col, row};
            }
        }
        return null;
    }
}
