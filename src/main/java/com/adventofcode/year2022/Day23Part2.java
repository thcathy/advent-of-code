package com.adventofcode.year2022;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import static com.adventofcode.year2022.Day23Part2.Direction.*;
import static junit.framework.TestCase.assertEquals;

public class Day23Part2 {
    final static String inputFile = "2022/day23.txt";

    public static void main(String... args) throws IOException {
        Day23Part2 solution = new Day23Part2();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = runUntilStable(parseElvesPosition(lines));
        System.out.println("What is the number of the first round where no Elf moves? " + result);
    }

    int runUntilStable(Map<Integer, Position> elvesPosition) {
        int numOfRound = 0;
        while (true) {
            var nextPositions = run1Round(elvesPosition, numOfRound % 4);
            if (match(nextPositions, elvesPosition)) return numOfRound + 1;
            elvesPosition = nextPositions;
            numOfRound++;
        }
    }

    boolean match(Map<Integer, Position> nextPositions, Map<Integer, Position> elvesPosition) {
        return nextPositions.entrySet().parallelStream().allMatch(e -> elvesPosition.get(e.getKey()).equals(e.getValue()));
    }

    Map<Integer, Position> run1Round(Map<Integer, Position> elvesPosition, int firstPurposeDirectionIndex) {
        var proposePositions = new ConcurrentHashMap<Position, List<Integer>>();
        elvesPosition.entrySet().parallelStream().forEach(entry -> {
            var nextPosition = proposeNextPosition(elvesPosition.values(), entry.getValue(), firstPurposeDirectionIndex);
            var list = proposePositions.computeIfAbsent(nextPosition, (k) -> new ArrayList<>());
            list.add(entry.getKey());
        });

        var finalPositions = new ConcurrentHashMap<Integer, Position>();
        proposePositions.entrySet().parallelStream().forEach(entry -> {
            if (entry.getValue().size() == 1) {
                finalPositions.put(entry.getValue().get(0), entry.getKey());
            } else {
                for (Integer i : entry.getValue()) {
                    finalPositions.put(i, elvesPosition.get(i));
                }
            }
        });
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
        var lines = Files.readAllLines(Paths.get(ClassLoader.getSystemResource("2022/day23_test2.txt").toURI()));
        var elvesPosition = parseElvesPosition(lines);
        assertEquals(22, elvesPosition.size());
        assertEquals(20, runUntilStable(elvesPosition));
    }

}
