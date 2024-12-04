package com.adventofcode.year2024;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Day2Part2 {
    Logger log = LoggerFactory.getLogger(this.getClass());
    final static String inputFile = "2024/day2.txt";

    public static void main(String... args) throws IOException {
        new Day2Part2().run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var inputs = parseInput(lines);
        var result = inputs.stream().filter(this::isSafeOrDampened).count();
        log.warn("How many reports are safe? {}", result);
    }

    private List<Report> parseInput(List<String> lines) {
        return lines.stream().map(line ->
                        Arrays.stream(line.split("\\s+")).map(Integer::parseInt).collect(Collectors.toList())
                )
                .map(Report::new)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private boolean isSafeOrDampened(Report report) {
        return report.isSafe() || canBeDampened(report);
    }

    private boolean canBeDampened(Report report) {
        for (int i = 0; i < report.levels.size(); i++) {
            List<Integer> modifiedLevels = new ArrayList<>(report.levels);
            modifiedLevels.remove(i);
            Report modifiedReport = new Report(modifiedLevels);
            if (modifiedReport.isSafe()) {
                return true;
            }
        }
        return false;
    }

    static class Report {
        private List<Integer> levels;

        public Report(List<Integer> levels) {this.levels = levels;}

        public boolean isSafe() {
            return (isIncreasing() || isDecreasing()) && hasValidDifferences();
        }

        private boolean isIncreasing() {
            for (int i = 0; i < levels.size() - 1; i++) {
                if (levels.get(i) >= levels.get(i + 1)) {
                    return false;
                }
            }
            return true;
        }

        private boolean isDecreasing() {
            for (int i = 0; i < levels.size() - 1; i++) {
                if (levels.get(i) <= levels.get(i + 1)) {
                    return false;
                }
            }
            return true;
        }

        private boolean hasValidDifferences() {
            for (int i = 0; i < levels.size() - 1; i++) {
                int diff = Math.abs(levels.get(i) - levels.get(i + 1));
                if (diff < 1 || diff > 3) {
                    return false;
                }
            }
            return true;
        }
    }
}
