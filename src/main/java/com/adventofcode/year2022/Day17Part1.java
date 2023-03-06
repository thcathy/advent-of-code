package com.adventofcode.year2022;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static com.adventofcode.year2022.Day17Part1.Direction.*;
import static org.junit.Assert.assertEquals;

public class Day17Part1 {
    final static String inputFile = "2022/day17.txt";
    final static int CHAMBER_WIDE = 7;
    final static int TOTAL_ROCKS = 2022;

    public static void main(String... args) throws IOException {
        Day17Part1 solution = new Day17Part1();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = findChamberHeight(lines.get(0));
        System.out.println("How many units tall will the tower of rocks be after 2022 rocks have stopped falling? " + result);
    }

    int findChamberHeight(String input) {
        var jetPattern = new JetPattern(input);
        var rockBuilder = new RockBuilder();
        var chamber = new Chamber(CHAMBER_WIDE);
        IntStream.range(0, TOTAL_ROCKS).forEach(i -> chamber.rockFallingUntilRest(rockBuilder, jetPattern));
        return chamber.highestRockY + 1;
    }

    record Position(int x, int y) {}
    enum Direction { DOWN, LEFT, RIGHT }

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
        int highestRockY = -1;

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
                    highestRockY = Math.max(highestRockY, rock.stream().mapToInt(p -> p.y).max().getAsInt());
                    return;
                } else {
                    rock = rockFellPositions;
                }
            }

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
        var jetPattern = new JetPattern(lines.get(0));
        assertEquals(RIGHT, jetPattern.next());

        var rockBuilder = new RockBuilder();
        var rock1 = rockBuilder.buildNext(new Position(0, 0));
        assertEquals(new Position(0, 0), rock1.get(0));
        assertEquals(new Position(1, 0), rock1.get(1));
        assertEquals(new Position(2, 0), rock1.get(2));
        assertEquals(new Position(3, 0), rock1.get(3));

        var rock2 = rockBuilder.buildNext(new Position(1, 3));
        assertEquals(new Position(2, 3), rock2.get(0));
        assertEquals(new Position(1, 4), rock2.get(1));
        assertEquals(new Position(2, 4), rock2.get(2));
        assertEquals(new Position(3, 4), rock2.get(3));
        assertEquals(new Position(2, 5), rock2.get(4));

        var rock3 = rockBuilder.buildNext(new Position(0, 10));
        assertEquals(new Position(0, 10), rock3.get(0));
        assertEquals(new Position(1, 10), rock3.get(1));
        assertEquals(new Position(2, 10), rock3.get(2));
        assertEquals(new Position(2, 11), rock3.get(3));
        assertEquals(new Position(2, 12), rock3.get(4));

        var rock4 = rockBuilder.buildNext(new Position(0, 0));
        assertEquals(new Position(0, 0), rock4.get(0));
        assertEquals(new Position(0, 1), rock4.get(1));
        assertEquals(new Position(0, 2), rock4.get(2));
        assertEquals(new Position(0, 3), rock4.get(3));

        var rock5 = rockBuilder.buildNext(new Position(0, 0));
        assertEquals(new Position(0, 0), rock5.get(0));
        assertEquals(new Position(1, 0), rock5.get(1));
        assertEquals(new Position(0, 1), rock5.get(2));
        assertEquals(new Position(1, 1), rock5.get(3));

        var rock6 = rockBuilder.buildNext(new Position(0, 0));
        assertEquals(rock1, rock6);

        var chamber = new Chamber(CHAMBER_WIDE);
        var jetPattern2 = new JetPattern(lines.get(0));
        var rockBuilder2 = new RockBuilder();
        chamber.rockFallingUntilRest(rockBuilder2, jetPattern2);
        assertEquals(4, chamber.rocks.size());
        chamber.rockFallingUntilRest(rockBuilder2, jetPattern2);
        assertEquals(9, chamber.rocks.size());

        assertEquals(3068, findChamberHeight(lines.get(0)));
    }

}
