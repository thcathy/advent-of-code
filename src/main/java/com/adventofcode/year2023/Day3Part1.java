package com.adventofcode.year2023;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Day3Part1 {
    Logger log = LoggerFactory.getLogger(Day3Part1.class);
    final static String inputFile = "2023/day3.txt";

    public static void main(String... args) throws IOException {
        Day3Part1 solution = new Day3Part1();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = sumOfPartNumbers(parseMap(lines));
        log.warn("What is the sum of all of the part numbers in the engine schematic? {}", result);
    }

    int sumOfPartNumbers(TheMap map) {
        return parseNumbers(map).stream()
            .filter(n -> isPartNumber(map, n))
            .mapToInt(n -> n.value)
            .sum();
    }

    List<Number> parseNumbers(TheMap map) {
        var allNumbers = new ArrayList<Number>();
        var numberPositions = new ArrayList<Position>();
        String digits = "";
        for (int y = 0; y < map.height; y++) {
            for (int x = 0; x < map.width; x++) {
                char c = map.chars[y][x];
                if (Character.isDigit(c)) {
                    numberPositions.add(new Position(x, y));
                    digits += c;
                } else {
                    if (!numberPositions.isEmpty()) {
                        allNumbers.add(new Number(new ArrayList<>(numberPositions), Integer.parseInt(digits)));
                        numberPositions.clear();
                        digits = "";
                    }
                }
            }
            if (!numberPositions.isEmpty()) {
                allNumbers.add(new Number(new ArrayList<>(numberPositions), Integer.parseInt(digits)));
                numberPositions.clear();
                digits = "";
            }
        }
        return allNumbers;
    }

    boolean isPartNumber(TheMap map, Number number) {
        for (var position : number.positions) {
            if (adjacentPositions(position).anyMatch(p -> isSymbol(p, map.chars)))
                return true;
        }
        return false;
    }

    boolean isSymbol(Position pos, char[][] map) {
        if (pos.x < 0 || pos.y < 0) {
            return false;
        } else if (pos.x >= map[0].length || pos.y >= map.length) {
            return false;
        }
        var c = map[pos.y][pos.x];
        if (Character.isDigit(c) || c == '.') {
            return false;
        }
        return true;
    }

    Stream<Position> adjacentPositions(Position position) {
        return Stream.of(
            new Position(position.x-1, position.y-1),
            new Position(position.x-1, position.y),
            new Position(position.x-1, position.y+1),
            new Position(position.x, position.y+1),
            new Position(position.x, position.y-1),
            new Position(position.x+1, position.y-1),
            new Position(position.x+1, position.y),
            new Position(position.x+1, position.y+1)
        );
    }

    TheMap parseMap(List<String> inputs) {        
        char[][] chars = new char[inputs.size()][inputs.get(0).length()];
        for (int y = 0; y < inputs.size(); y++) {
            String line = inputs.get(y);
            for (int x = 0; x < line.length(); x++) {
                chars[y][x] = line.charAt(x);
            }
        }
        return new TheMap(chars, inputs.get(0).length(), inputs.size());
    }

    record Number(List<Position> positions, int value) {}

    record TheMap(char[][] chars, int width, int height) {}

    record Position(int x, int y) {}

    enum Direction { Up, Down, Left, Right }
    
    @Test
    public void unitTest() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource("2023/day3_test.txt"), Charsets.UTF_8);
        var map = parseMap(lines);
        var partNumbers = parseNumbers(map).stream().filter(n -> isPartNumber(map, n)).toList();
        assertEquals(8, partNumbers.size());
        assertEquals(4361, sumOfPartNumbers(map));
    }
}
