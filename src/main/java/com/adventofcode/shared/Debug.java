package com.adventofcode.shared;

import java.util.Set;

public class Debug {
    static public void print(int width, int height, Set<Position> position) {
        System.out.println(">>> debug");
        for (int y = 0 ; y < height; y++) {
            for (int x = 0; x < width; x++) {
                var c = position.contains(new Position(x, y)) ? 'X' : '.';
                System.out.print(c);
            }
            System.out.println();
        }
        System.out.println(">>>>>>>>>");
    }
}
