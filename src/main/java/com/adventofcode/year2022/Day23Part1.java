package com.adventofcode.year2022;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import static com.adventofcode.year2022.Day23Part1.Direction.*;
import static junit.framework.TestCase.assertEquals;

public class Day23Part1 {
    final static String inputFile = "2022/day23.txt";

    public static void main(String... args) throws IOException {
        Day23Part1 solution = new Day23Part1();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = totalEmptyGroundTiles(parseElvesPosition(lines));
        System.out.println("How many empty ground tiles does that rectangle contain? " + result);
    }

    int totalEmptyGroundTiles(Map<Integer, Position> elvesPosition) {
        var finalPositions = runRound(10, elvesPosition);

        var minX = Integer.MAX_VALUE; var minY = Integer.MAX_VALUE;
        var maxX = Integer.MIN_VALUE; var maxY = Integer.MIN_VALUE;
        for (var position : finalPositions.values()) {
            minX = Math.min(minX, position.x);
            maxX = Math.max(maxX, position.x);
            minY = Math.min(minY, position.y);
            maxY = Math.max(maxY, position.y);
        }
        int totalEmptyGroundTiles = 0;
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                if (!finalPositions.values().contains(new Position(x, y)))
                    totalEmptyGroundTiles++;
            }
        }
        return totalEmptyGroundTiles;
    }

    Map<Integer, Position> runRound(int numOfRound, Map<Integer, Position> elvesPosition) {
        for (int i = 0; i < numOfRound; i++) {
            elvesPosition = run1Round(elvesPosition, i % 4);
        }
        return elvesPosition;
    }

    Map<Integer, Position> run1Round(Map<Integer, Position> elvesPosition, int firstPurposeDirectionIndex) {
        var proposePositions = new HashMap<Position, List<Integer>>();
        for (var entry : elvesPosition.entrySet()) {
            var nextPosition = proposeNextPosition(elvesPosition.values(), entry.getValue(), firstPurposeDirectionIndex);
            var list = proposePositions.computeIfAbsent(nextPosition, (k) -> new ArrayList<>());
            list.add(entry.getKey());
        }

        var finalPositions = new HashMap<Integer, Position>();
        for (var entry : proposePositions.entrySet()) {
            if (entry.getValue().size() == 1) {
                finalPositions.put(entry.getValue().get(0), entry.getKey());
            } else {
                for (Integer i : entry.getValue()) {
                    finalPositions.put(i, elvesPosition.get(i));
                }
            }
        }
        return finalPositions;
    }

    Position proposeNextPosition(Collection<Position> elvesPosition, Position elf, int firstPurposeDirectionIndex) {
        if (elf.neighbors().noneMatch(elvesPosition::contains)) return elf;

        for (int x = 0; x < 4; x++) {
            Direction d = proposeDirections[(firstPurposeDirectionIndex + x) % 4];
            var neighbors = elf.getPositions(d);
            if (neighbors.stream().noneMatch(elvesPosition::contains))
                return neighbors.get(1);    // the middle position
        }

        return elf;
    }

    Map<Integer, Position> parseElvesPosition(List<String> inputs) {
        var elvesPosition = new HashMap<Integer, Position>();
        int index = 1;
        for (int y = 0; y < inputs.size(); y++) {
            var line = inputs.get(y);
            for (int x = 0; x < line.length(); x++) {
                if (line.charAt(x) == '#') {
                    elvesPosition.put(index, new Position(x, y));
                    index++;
                }

            }
        }
        return elvesPosition;
    }

    enum Direction { NORTH, SOUTH, WEST, EAST }

    Direction[] proposeDirections = new Direction[] { NORTH, SOUTH, WEST, EAST };

    record Position(int x, int y) {
        Stream<Position> neighbors() {
            return Stream.of(
                    new Position(x - 1, y - 1), new Position(x, y - 1), new Position(x + 1, y - 1),
                    new Position(x - 1, y), new Position(x + 1, y),
                    new Position(x - 1, y + 1), new Position(x, y + 1), new Position(x + 1, y + 1)
            );
        }

        List<Position> getPositions(Direction direction) {
            return switch (direction) {
                case NORTH -> List.of(new Position(x - 1, y - 1), new Position(x, y - 1), new Position(x + 1, y - 1));
                case SOUTH -> List.of(new Position(x - 1, y + 1), new Position(x, y + 1), new Position(x + 1, y + 1));
                case WEST -> List.of(new Position(x - 1, y - 1), new Position(x - 1, y), new Position(x - 1, y + 1));
                case EAST -> List.of(new Position(x + 1, y - 1), new Position(x + 1, y), new Position(x + 1, y + 1));
            };
        }
    }

    @Test
    public void unitTest() throws Exception {
        var lines1 = Files.readAllLines(Paths.get(ClassLoader.getSystemResource("2022/day23_test.txt").toURI()));
        var elvesPosition = parseElvesPosition(lines1);
        assertEquals(5, elvesPosition.size());
        assertEquals(25, totalEmptyGroundTiles(elvesPosition));

        var lines2 = Files.readAllLines(Paths.get(ClassLoader.getSystemResource("2022/day23_test2.txt").toURI()));
        var elvesPosition2 = parseElvesPosition(lines2);
        assertEquals(22, elvesPosition2.size());
        assertEquals(110, totalEmptyGroundTiles(elvesPosition2));
    }

}
