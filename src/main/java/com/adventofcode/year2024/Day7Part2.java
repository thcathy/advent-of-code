package com.adventofcode.year2024;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class Day7Part2 {
    private static final Logger log = LoggerFactory.getLogger(Day7Part2.class);
    private static final String INPUT_FILE = "2024/day7.txt";

    public static void main(String... args) throws IOException {
        new Day7Part2().run();
    }

    private void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(INPUT_FILE), Charsets.UTF_8);
        long totalSum = calculateTotalCalibration(lines);
        log.warn("Total calibration result: {}", totalSum);
    }

    private long calculateTotalCalibration(List<String> lines) {
        return lines.stream()
                .map(this::parseEquation)
                .filter(equation -> evaluateExpressions(equation, 1, equation.numbers[0]))
                .mapToLong(Equation::testValue)
                .sum();
    }

    private Equation parseEquation(String line) {
        var parts = line.split(": ");
        long testValue = Long.parseLong(parts[0]);
        String[] numbers = parts[1].split(" ");
        long[] numberArray = new long[numbers.length];

        for (int i = 0; i < numbers.length; i++) {
            numberArray[i] = Long.parseLong(numbers[i]);
        }

        return new Equation(testValue, numberArray);
    }

    private boolean evaluateExpressions(Equation equation, int index, long current) {
        var numbers = equation.numbers;
        if (index == numbers.length) {
            return current == equation.testValue;
        }

        long nextNumber = numbers[index];
        boolean addResult = evaluateExpressions(equation, index + 1, current + nextNumber);
        boolean multiplyResult = evaluateExpressions(equation, index + 1, current * nextNumber);

        long concatenatedValue = Long.parseLong(current + "" + nextNumber);
        boolean concatResult = evaluateExpressions(equation, index + 1, concatenatedValue);

        return addResult || multiplyResult || concatResult;
    }

    private record Equation(long testValue, long[] numbers) {}
}
