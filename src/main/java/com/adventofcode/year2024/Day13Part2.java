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
        int totalPrizes = 0;
        long totalTokens = 0;

        for (Machine machine : machines) {
            var result = calculateMinimumTokens(machine);
            if (result[0] != Long.MAX_VALUE) {
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
            long prizeX = Long.parseLong(prizeParts[0].split("=")[1]) + PRIZE_OFFSET;
            long prizeY = Long.parseLong(prizeParts[1].split("=")[1]) + PRIZE_OFFSET;

            machines.add(new Machine(aX, aY, bX, bY, prizeX, prizeY));
        }

        return machines;
    }

    private long[] calculateMinimumTokens(Machine machine) {
        long minTokens = Long.MAX_VALUE;

        long gcdX = gcd(machine.aX, machine.bX);
        long gcdY = gcd(machine.aY, machine.bY);

        if (machine.prizeX % gcdX == 0 && machine.prizeY % gcdY == 0) {
            long[] xSolution = findSolution(machine.aX, machine.bX, machine.prizeX);
            long[] ySolution = findSolution(machine.aY, machine.bY, machine.prizeY);

            // Calculate costs and find the minimum
            for (long k = 0; k <= 100000000; k++) { // Adjust the range based on gcd scaling
                long aPresses = xSolution[0] + k * (machine.bX / gcdX);
                long bPresses = ySolution[0] + k * (machine.bY / gcdY);

                if (aPresses >= 0 && bPresses >= 0) {
                    long cost = aPresses * 3 + bPresses;
                    minTokens = Math.min(minTokens, cost);
                }
            }
        }

        return new long[]{minTokens};
    }

    private long[] findSolution(int a, int b, long c) {
        long gcd = gcd(a, b);
        if (c % gcd != 0) return new long[]{0, 0}; // No solution

        // Extended Euclidean Algorithm to find one solution
        long x0 = 0, y0 = 0;
        long[] extGcd = extendedGCD(a, b);
        x0 = extGcd[0] * (c / gcd);
        y0 = extGcd[1] * (c / gcd);

        return new long[]{x0, y0};
    }

    private long[] extendedGCD(int a, int b) {
        if (b == 0) return new long[]{1, 0}; // ax + by = gcd(a, b)
        long[] next = extendedGCD(b, a % b);
        return new long[]{next[1], next[0] - (a / b) * next[1]};
    }

    private long gcd(int a, int b) {
        while (b != 0) {
            long temp = b;
            b = a % b;
            a = (int) temp;
        }
        return a;
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
