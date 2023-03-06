package com.adventofcode.year2022;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.IntStream;

import static com.adventofcode.year2022.Day17Part2.Direction.*;
import static org.junit.Assert.assertEquals;

public class Day17Part2 {
    final static String inputFile = "2022/day17.txt";
    final static int CHAMBER_WIDE = 7;
    final static long TOTAL_ROCKS = 1000000000000L;

    public static void main(String... args) throws IOException {
        Day17Part2 solution = new Day17Part2();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = findChamberHeight(lines.get(0), TOTAL_ROCKS);
        System.out.println("How tall will the tower be after 1000000000000 rocks have stopped? " + result);
    }

    long findChamberHeight(String input, long totalRock) {
        var jetPattern = new JetPattern(input);
        var rockBuilder = new RockBuilder();
        var chamber = new Chamber(CHAMBER_WIDE);
        var indexes = new HashMap<State, Long>();
        var heightMap = new HashMap<Long, Long>();

        for (long i = 1; i <= totalRock; i++) {
            chamber.rockFallingUntilRest(rockBuilder, jetPattern);
            var state = new State(jetPattern.index, rockBuilder.index, chamber.getRockBitSet());

            // when a loop is found
            if (indexes.containsKey(state)) {
                return calculateFinalHeight(chamber, indexes, heightMap, i, state);
            }

            indexes.put(state, i);
            heightMap.put(i, chamber.highestRockY);
        }
        return chamber.highestRockY + 1;
    }

    private static long calculateFinalHeight(Chamber chamber, HashMap<State, Long> indexes, HashMap<Long, Long> heightMap, long i, State state) {
        var previousStep = indexes.get(state);
        var previousHeight = heightMap.get(previousStep);
        var stepPerLoop = i - previousStep;
        var heightPerLoop = chamber.highestRockY - previousHeight;
        var remainingLoop = (TOTAL_ROCKS - i) / stepPerLoop;
        var remainingSteps = (TOTAL_ROCKS - i) % stepPerLoop;
        var remainingHeight = heightMap.get(previousStep + remainingSteps) - previousHeight;
        return chamber.highestRockY + remainingLoop * heightPerLoop + remainingHeight + 1;
    }

    record Position(long x, long y) {}
    enum Direction { DOWN, LEFT, RIGHT }
    record State(int jetIndex, int rockIndex, BitSet rocksPattern) {}

    class JetPattern {
        int index = 0;
        final List<Direction> directions;

        public JetPattern(String pattern) {
            directions = pattern.chars().mapToObj(c -> c == '>' ? RIGHT : LEFT).toList();
        }

        Direction next() {
            var direction = directions.get(index);
            index = (index + 1 == directions.size()) ? 0 : index + 1;
            return direction;
        }
    }

    class RockBuilder {
        int index = 0;
        List<Position> buildNext(Position bottomLeft) {
            var rock = new ArrayList<Position>();
            if (index == 0) {
                IntStream.rangeClosed(0, 3).forEach(x -> rock.add(new Position(bottomLeft.x + x, bottomLeft.y)));
            } else if (index == 1) {
                rock.add(new Position(bottomLeft.x + 1, bottomLeft.y));
                IntStream.rangeClosed(0, 2).forEach(x -> rock.add(new Position(bottomLeft.x + x, bottomLeft.y + 1)));
                rock.add(new Position(bottomLeft.x + 1, bottomLeft.y + 2));
            } else if (index == 2) {
                IntStream.rangeClosed(0, 2).forEach(x -> rock.add(new Position(bottomLeft.x + x, bottomLeft.y)));
                rock.add(new Position(bottomLeft.x + 2, bottomLeft.y + 1));
                rock.add(new Position(bottomLeft.x + 2, bottomLeft.y + 2));
            } else if (index == 3) {
                IntStream.rangeClosed(0, 3).forEach(y -> rock.add(new Position(bottomLeft.x, bottomLeft.y + y)));
            } else if (index == 4) {
                IntStream.rangeClosed(0, 1).forEach(x -> rock.add(new Position(bottomLeft.x + x, bottomLeft.y)));
                IntStream.rangeClosed(0, 1).forEach(x -> rock.add(new Position(bottomLeft.x + x, bottomLeft.y + 1)));
            }
            index = (index == 4) ? 0 : index + 1;
            return rock;
        }
    }

    class Chamber {
        final int wide;
        Set<Position> rocks = new HashSet<>();
        long highestRockY = -1;

        public Chamber(int wide) {
            this.wide = wide;
        }

        public boolean isValidPositions(List<Position> rockJettedPosition) {
            return rockJettedPosition.stream().noneMatch(p -> p.x < 0)
                    && rockJettedPosition.stream().noneMatch(p -> p.x >= wide)
                    && rockJettedPosition.stream().noneMatch(p -> rocks.contains(p));
        }

        public boolean isStop(List<Position> rockFellPositions) {
            return rockFellPositions.stream().anyMatch(p -> p.y < 0)
                    || rockFellPositions.stream().anyMatch(p -> rocks.contains(p));
        }

        void rockFallingUntilRest(RockBuilder rockBuilder, JetPattern jetPattern) {
            var rock = rockBuilder.buildNext(new Position(2, highestRockY + 4));
            while (true) {
                var rockJettedPositions = moveRock(rock, jetPattern.next());
                rock = isValidPositions(rockJettedPositions) ? rockJettedPositions : rock;

                var rockFellPositions = moveRock(rock, DOWN);
                if (isStop(rockFellPositions)) {
                    rocks.addAll(rock);
                    highestRockY = Math.max(highestRockY, rock.stream().mapToLong(p -> p.y).max().getAsLong());
                    return;
                } else {
                    rock = rockFellPositions;
                }
            }

        }

        // Return the top 8 rows as bits
        public BitSet getRockBitSet() {
            var bits = new BitSet();
            int index = 0;
            for (long y = highestRockY; y > highestRockY - 8; y--) {
                for (int x = 0; x < wide; x++) {
                    bits.set(index, rocks.contains(new Position(x, y)));
                    index++;
                }
            }
            return bits;
        }
    }

    List<Position> moveRock(List<Position> rock, Direction direction) {
        return switch (direction) {
            case DOWN -> rock.stream().map(p -> new Position(p.x, p.y - 1)).toList();
            case LEFT -> rock.stream().map(p -> new Position(p.x - 1, p.y)).toList();
            case RIGHT -> rock.stream().map(p -> new Position(p.x + 1, p.y)).toList();
        };
    }

    @Test
    public void unitTest() throws Exception {
        var lines = Files.readAllLines(Paths.get(ClassLoader.getSystemResource("2022/day17_test.txt").toURI()));
        assertEquals(1514285714288L, findChamberHeight(lines.get(0), TOTAL_ROCKS));
    }

}
