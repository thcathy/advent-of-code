package com.adventofcode.year2024;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class Day13Part2 {
    private static final Logger log = LoggerFactory.getLogger(Day13Part2.class);
    private static final String INPUT_FILE = "2024/day13.txt";
    private static final long PRIZE_OFFSET = 10000000000000L;

    public static void main(String... args) throws IOException {
        new Day13Part2().run();
    }

    private void run() throws IOException {
        List<Machine> machines = parseInput();
        long totalTokens = 0;

        for (Machine machine : machines) {
            var result = calculateMinimumTokens(machine);
            if (result != Long.MAX_VALUE) {
                totalTokens += result;
            }
        }

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
            long prizeX = Long.parseLong(prizeParts[0].split("=")[1]) + PRIZE_OFFSET;
            long prizeY = Long.parseLong(prizeParts[1].split("=")[1]) + PRIZE_OFFSET;

            // Create a new Machine object and add to the list
            machines.add(new Machine(aX, aY, bX, bY, prizeX, prizeY));
        }

        return machines;
    }

    private long calculateMinimumTokens(Machine machine) {
        long minTokens = Long.MAX_VALUE;

        // Solve the system of equations:
        // aPresses * aX + bPresses * bX = prizeX
        // aPresses * aY + bPresses * bY = prizeY
        long determinant = (long) machine.aX * machine.bY - (long) machine.aY * machine.bX;
        long aPresses = (machine.prizeX * machine.bY - machine.prizeY * machine.bX) / determinant;
        long bPresses = (machine.aX * machine.prizeY - machine.aY * machine.prizeX) / determinant;

        // Check if the solution is valid
        if (aPresses >= 0 && bPresses >= 0 &&
                aPresses * machine.aX + bPresses * machine.bX == machine.prizeX &&
                aPresses * machine.aY + bPresses * machine.bY == machine.prizeY) {
            minTokens = aPresses * 3 + bPresses;
        }

        return minTokens;
    }

    private static class Machine {
        int aX, aY, bX, bY;
        long prizeX, prizeY;

        Machine(int aX, int aY, int bX, int bY, long prizeX, long prizeY) {
            this.aX = aX;
            this.aY = aY;
            this.bX = bX;
            this.bY = bY;
            this.prizeX = prizeX;
            this.prizeY = prizeY;
        }
    }
}
