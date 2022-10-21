package com.adventofcode.year2018;


import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.Test;

public class Day9Part2 {
    final static int totalPlayers = 459;
    final static int lastMarble = 7132000;
        
    public static void main(String... args) throws IOException {
        Day9Part2 solution = new Day9Part2();
        solution.run();
    }

    void run() throws IOException {        
        var result = highestScore(totalPlayers, lastMarble);
        System.out.println("What is the winning Elf's score? " + result);
    }

    long highestScore(int totalPlayers, int lastMarble) {
        List<Player> players = IntStream.rangeClosed(1, totalPlayers).mapToObj(v -> new Player()).toList();
        Marble currentMarble = new Marble(0);
        currentMarble.left = currentMarble;
        currentMarble.right = currentMarble;

        for (int i=1; i<=lastMarble; i++) {
            currentMarble = placing(new Marble(i), players.get((i-1)%players.size()), currentMarble);
        }

        return players.stream().mapToLong(p -> p.score).max().getAsLong();
    }

    Marble placing(Marble newMarble, Player player, Marble currentMarble) {        
        if (newMarble.value % 23 != 0) {
            return normalPlacing(newMarble, currentMarble);
        } else {                  
            var removedMarble = removeMarble(currentMarble);
            player.score += newMarble.value + removedMarble.value;
            return removedMarble.right;
        }
    }

    Marble removeMarble(Marble currentMarble) {
        for (int i=0; i<7; i++) {
            currentMarble = currentMarble.left;
        }        
        currentMarble.left.right = currentMarble.right;
        currentMarble.right.left = currentMarble.left;
        return currentMarble;
    }

    Marble normalPlacing(Marble newMarble, Marble currentMarble) {
        var leftMarbleOfNew = currentMarble.right;
        var rightMarbleOfNew = leftMarbleOfNew.right;
        leftMarbleOfNew.right = newMarble;
        rightMarbleOfNew.left = newMarble;
        newMarble.left = leftMarbleOfNew;
        newMarble.right = rightMarbleOfNew;
        return newMarble;
    }

    class Player {
        long score = 0;
    }

    class Marble {
        Marble left, right;
        final int value;

        Marble(int value) { this.value = value; }
    }
        
    @Test
    public void unitTest() throws IOException {        
        assertEquals(32, highestScore(9, 25));
        assertEquals(8317, highestScore(10, 1618));
        assertEquals(146373, highestScore(13, 7999));        
    }
}
