package com.adventofcode.year2023;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Day14Part1 {
    Logger log = LoggerFactory.getLogger(Day14Part1.class);
    final static String inputFile = "2023/day14.txt";

    public static void main(String... args) throws IOException {
        Day14Part1 solution = new Day14Part1();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var puzzle = new Day14Part1().parseInput(lines);
        var result = totalLoad(allRockSlideToNorth(puzzle), puzzle.height);
        log.warn("What is the total load on the north support beams? {}", result);
    }

    Set<Position> allRockSlideToNorth(Puzzle puzzle) {
        var slidedRocks = new HashSet<Position>();
        for (int y = 0; y < puzzle.height; y++) {
            for (int x = 0; x < puzzle.width; x++) {
                var position = new Position(x, y);
                if (puzzle.patterns.get(position) == 'O') {
                    slidedRocks.add(slideNorth(puzzle.patterns, slidedRocks, position));
                }
            }
        }
        return slidedRocks;
    }

    Position slideNorth(Map<Position, Character> map, Set<Position> slidedRocks, Position rock) {
        for (int y = rock.y - 1; y >= 0; y--) {
            var northernPosition = new Position(rock.x, y);            
            if (map.get(northernPosition) == '#' || slidedRocks.contains(northernPosition))
                return new Position(rock.x, y + 1);
        }
        return new Position(rock.x, 0);
    }
    
    long totalLoad(Set<Position> rocks, int height) {
        return rocks.stream().mapToLong(r -> (long) height - r.y).sum();
    }

    //region Data Objects

    record Puzzle(Map<Position, Character> patterns, int width, int height) {}

    record Position(int x, int y) {}
    
    //endregion

    //region Input Parsing

    Puzzle parseInput(List<String> inputs) {
        var heigth = inputs.size();
        var width = inputs.get(0).length();
        var patterns = new HashMap<Position, Character>();
        for (int y = 0; y < heigth; y++) {
            var line = inputs.get(y);
            for (int x = 0; x < width; x++) {
                patterns.put(new Position(x, y), line.charAt(x));
            }
        }
        return new Puzzle(patterns, width, heigth);
    }

    //endregion

    @Test
    public void unitTest() throws IOException {        
        var lines = Resources.readLines(ClassLoader.getSystemResource("2023/day14_test.txt"), Charsets.UTF_8);
        var puzzle = new Day14Part1().parseInput(lines);
        var result = totalLoad(allRockSlideToNorth(puzzle), puzzle.height);
        Assert.assertEquals(136, result);
    }
}
