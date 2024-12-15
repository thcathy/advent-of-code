package com.adventofcode.year2024;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class Day13Part1 {
    private static final Logger log = LoggerFactory.getLogger(Day13Part1.class);
    private static final String INPUT_FILE = "2024/day13.txt";

    public static void main(String... args) throws IOException {
        new Day13Part1().run();
    }

    private void run() throws IOException {
        List<Machine> machines = parseInput();
        int totalPrizes = 0;
        int totalTokens = 0;

        for (Machine machine : machines) {
            var result = calculateMinimumTokens(machine);
            if (result[0] != Integer.MAX_VALUE) {
                totalPrizes++;
                totalTokens += result[0];
            }
        }

        log.warn("Total prizes won: {}", totalPrizes);
        log.warn("Minimum tokens spent: {}", totalTokens);
    }

    private List<Machine> parseInput() throws IOException {
        List<String> lines = Resources.readLines(ClassLoader.getSystemResource(INPUT_FILE), Charsets.UTF_8);
        List<Machine> machines = new ArrayList<>();

        for (int i = 0; i < lines.size(); i += 4) {
            String buttonALine = lines.get(i);
            String buttonBLine = lines.get(i + 1);
            String prizeLine = lines.get(i + 2);

            String[] aParts = buttonALine.split(": ")[1].split(", ");
            int aX = Integer.parseInt(aParts[0].split("\\+")[1]);
            int aY = Integer.parseInt(aParts[1].split("\\+")[1]);

            String[] bParts = buttonBLine.split(": ")[1].split(", ");
            int bX = Integer.parseInt(bParts[0].split("\\+")[1]);
            int bY = Integer.parseInt(bParts[1].split("\\+")[1]);

            String[] prizeParts = prizeLine.split(": ")[1].split(", ");
            int prizeX = Integer.parseInt(prizeParts[0].split("=")[1]);
            int prizeY = Integer.parseInt(prizeParts[1].split("=")[1]);

            // Create a new Machine object and add to the list
            machines.add(new Machine(aX, aY, bX, bY, prizeX, prizeY));
        }

        return machines;
    }

    private int[] calculateMinimumTokens(Machine machine) {
        int minTokens = Integer.MAX_VALUE;

        for (int aPresses = 0; aPresses <= 100; aPresses++) {
            for (int bPresses = 0; bPresses <= 100; bPresses++) {
                int posX = aPresses * machine.aX + bPresses * machine.bX;
                int posY = aPresses * machine.aY + bPresses * machine.bY;

                if (posX == machine.prizeX && posY == machine.prizeY) {
                    int cost = aPresses * 3 + bPresses;
                    minTokens = Math.min(minTokens, cost);
                }
            }
        }

        return new int[]{minTokens};
    }

    private static class Machine {
        int aX, aY, bX, bY;
        int prizeX, prizeY;

        Machine(int aX, int aY, int bX, int bY, int prizeX, int prizeY) {
            this.aX = aX;
            this.aY = aY;
            this.bX = bX;
            this.bY = bY;
            this.prizeX = prizeX;
            this.prizeY = prizeY;
        }
    }
}
