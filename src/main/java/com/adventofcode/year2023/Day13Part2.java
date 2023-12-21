package com.adventofcode.year2023;

import static com.adventofcode.year2023.Day13Part2.Orientation.HORIZONTAL;
import static com.adventofcode.year2023.Day13Part2.Orientation.VERTICAL;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Day13Part2 {
    Logger log = LoggerFactory.getLogger(Day13Part2.class);
    final static String inputFile = "2023/day13.txt";

    public static void main(String... args) throws IOException {
        Day13Part2 solution = new Day13Part2();
        solution.run();
    }
    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var puzzles = new Day13Part2().parseInput(lines);
        var result = totalMirrorValues(puzzles);
        log.warn("What number do you get after summarizing all of your notes? {}", result);
    }
    
    int totalMirrorValues(List<Puzzle> puzzles) {
        return puzzles.stream().map(this::findMirror).mapToInt(Mirror::value).sum();
    }
    
    Mirror findMirror(Puzzle puzzle) {
        for (int v = 1; v < puzzle.width; v++) {
            var mirror = new Mirror(VERTICAL, v);
            if (isMirror(puzzle.patterns, mirror)) 
                return mirror;
        }
        for (int h = 1; h < puzzle.height; h++) {
            var mirror = new Mirror(HORIZONTAL, h);
            if (isMirror(puzzle.patterns, mirror))
                return mirror;
        }
        throw new RuntimeException("cannot find mirror");
    }

    boolean isMirror(Map<Position, Character> patterns, Mirror mirror) {
        List<Position> positionOnFirstSide = patterns.entrySet().stream()
                                    .filter(e -> e.getKey().positionByOrientation(mirror.orientation) < mirror.location)
                                    .map(e -> e.getKey()).toList();
        var reflectionMismatch = positionOnFirstSide.stream().filter(position -> {
            var reflectPosition = position.reflectPosition(mirror);
            var charAtReflection = patterns.getOrDefault(reflectPosition, '?');
            return charAtReflection != patterns.get(position) && charAtReflection != '?';
        }).count();
        return reflectionMismatch == 1;
    }

    //region Data Objects

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

    //endregion

    //region Input Parsing

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

    //endregion

    @Test
    public void unitTest() throws IOException {        
        var lines = Resources.readLines(ClassLoader.getSystemResource("2023/day13_test.txt"), Charsets.UTF_8);
        var puzzles = new Day13Part2().parseInput(lines);
        var result = totalMirrorValues(puzzles);        
        Assert.assertEquals(400, result);
    }
}
