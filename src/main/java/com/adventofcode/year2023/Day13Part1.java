package com.adventofcode.year2023;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.adventofcode.year2023.Day13Part1.Orientation.HORIZONTAL;
import static com.adventofcode.year2023.Day13Part1.Orientation.VERTICAL;

import java.io.IOException;
import java.net.http.HttpClient.Version;
import java.util.*;
import java.util.stream.IntStream;

import javax.swing.text.Position;

public class Day13Part1 {
    Logger log = LoggerFactory.getLogger(Day13Part1.class);
    final static String inputFile = "2023/day13.txt";

    public static void main(String... args) throws IOException {
        Day13Part1 solution = new Day13Part1();
        solution.run();
    }
    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        // var question = new Day13Part1().parseInput(lines);
        // var result = question.totalArrangements();
        // log.warn("What is the sum of those counts? {}", result);
    }

    List<Puzzle> parseInput(List<String> inputs) {
        return splitByEmptyLine(inputs).stream().map(this::parsePuzzle).toList();        
    }

    Puzzle parsePuzzle(List<String> inputs) {
        var heigth = inputs.size();
        var width = inputs.get(0).length();        
        var patterns = new HashMap<Position, Character>();
        for (int y = 0; y < heigth; y++) {
            var line = inputs.get(y);
            for (int x = 0; x < width; x++) {
                patterns.put(new Position(x, y), line.charAt(x));
            }
        }
        return new Puzzle(patterns, heigth, width);
    }
    
    Mirror findMirror(Puzzle puzzle) {
        for (int v = 2; v < puzzle.width - 1; v++) {
            var mirror = new Mirror(VERTICAL, v);
            if (isMirror(puzzle.patterns, mirror))
                return mirror;
        }
        for (int h = 2; h < puzzle.height - 1; h++) {
            var mirror = new Mirror(HORIZONTAL, h);
            if (isMirror(puzzle.patterns, mirror))
                return mirror;
        }
        throw new RuntimeException("cannot find mirror");
    }

    boolean isMirror(Map<Position, Character> patterns, Mirror mirror) {
        List<Position> rocksOnOneSide = patterns.entrySet().stream()
                                    .filter(e -> e.getValue() == '#')
                                    .filter(e -> e.getKey().positionByOrientation(mirror.orientation) < mirror.location)
                                    .map(e -> e.getKey()).toList();        
        return rocksOnOneSide.stream().allMatch(r -> {
            var reflectPosition = r.reflectPosition(mirror);
            return patterns.getOrDefault(reflectPosition, '.') == '#';
        });
    }

    List<List<String>> splitByEmptyLine(List<String> input) {
        List<List<String>> output = new ArrayList<>();
        List<String> currentList = new ArrayList<>();
        for (String line : input) {
            if (line.isEmpty()) {
                output.add(currentList);
                currentList = new ArrayList<>();
            } else {
                currentList.add(line);
            }
        }
        output.add(currentList);
        return output;
    }

    // Data objects

    record Puzzle(Map<Position, Character> patterns, int height, int width) {}

    record Position(int x, int y) {
        Position reflectPosition(Mirror mirror) {
            if (mirror.orientation == VERTICAL) {
                return new Position(mirror.location + mirror.location - x - 1, y);
            } else
                return new Position(x, mirror.location + mirror.location - y - 1);
        }

        int positionByOrientation(Orientation orientation) {
            return orientation == VERTICAL ? x : y;
        }
    }
            
    record Mirror(Orientation orientation, int location) {
        int value() {
            return orientation == VERTICAL ? location : location * 100;
        }
    }

    enum Orientation { VERTICAL, HORIZONTAL }

    // Data objects (END)

    @Test
    public void unitTest() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource("2023/day13_test.txt"), Charsets.UTF_8);
        var puzzles = new Day13Part1().parseInput(lines);
        Assert.assertEquals(5, findMirror(puzzles.get(0)).value());
    }
}
