package com.adventofcode.year2016;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;

import static com.adventofcode.year2016.Day1Part1.Direction.NORTH;

public class Day1Part1 {
    Logger log = LoggerFactory.getLogger(Day1Part1.class);

    public static void main(String... args) {
        Day1Part1 solution = new Day1Part1();
        solution.firstStar();
    }

    void firstStar() {
        var input = "L4, L1, R4, R1, R1, L3, R5, L5, L2, L3, R2, R1, L4, R5, R4, L2, R1, R3, L5, R1, L3, L2, R5, L4, L5, R1, R2, L1, R5, L3, R2, R2, L1, R5, R2, L1, L1, R2, L1, R1, L2, L2, R4, R3, R2, L3, L188, L3, R2, R54, R1, R1, L2, L4, L3, L2, R3, L1, L1, R3, R5, L1, R5, L1, L1, R2, R4, R4, L5, L4, L1, R2, R4, R5, L2, L3, R5, L5, R1, R5, L2, R4, L2, L1, R4, R3, R4, L4, R3, L4, R78, R2, L3, R188, R2, R3, L2, R2, R3, R1, R5, R1, L1, L1, R4, R2, R1, R5, L1, R4, L4, R2, R5, L2, L5, R4, L3, L2, R1, R1, L5, L4, R1, L5, L1, L5, L1, L4, L3, L5, R4, R5, R2, L5, R5, R5, R4, R2, L1, L2, R3, R5, R5, R5, L2, L1, R4, R3, R1, L4, L2, L3, R2, L3, L5, L2, L2, L1, L2, R5, L2, L2, L3, L1, R1, L4, R2, L4, R3, R5, R3, R4, R1, R5, L3, L5, L5, L3, L2, L1, R3, L4, R3, R2, L1, R3, R1, L2, R4, L3, L3, L3, L1, L2";
        int result = calculateDistance(input);
        log.warn("First star - distance = {}", result);
    }

    int calculateDistance(String input) {
        log.info("Start moving from origin, input: {}", input);
        var position = new Position();
        for (var movement : input.split(",")) {
            movement = movement.trim();
            position.turn(movement.charAt(0))
                    .move(Integer.parseInt(movement.substring(1)));
            log.info("Position after movement ({}) = {}", movement, position);
        }
        return distanceFromOrigin(position);
    }

    int distanceFromOrigin(Position p) {
        return Math.abs(p.x) + Math.abs(p.y);
    }

    class Position {
        int x = 0;
        int y = 0;
        Direction direction = NORTH;

        Position turn(char input) {
            direction = input == 'L' ? direction.left : direction.right;
            return this;
        }

        Position move(int step) {
            switch (direction) {
                case NORTH:
                    y += step;
                    break;
                case EAST:
                    x += step;
                    break;
                case SOUTH:
                    y -= step;
                    break;
                case WEST:
                    x -= step;
                    break;
            }
            return this;
        }

        @Override
        public String toString() {
            return MessageFormat.format("({0},{1},{2})", x, y, direction);
        }

    }

    enum Direction {
        NORTH, EAST, SOUTH, WEST;

        Direction left;
        Direction right;

        static {
            NORTH.right = EAST;
            NORTH.left = WEST;
            EAST.right = SOUTH;
            EAST.left = NORTH;
            SOUTH.right = WEST;
            SOUTH.left = EAST;
            WEST.right = NORTH;
            WEST.left = SOUTH;
        }
    }

    @Test
    public void testcases() {
        var input = "R2, L3";
        Assert.assertEquals(5, new Day1Part1().calculateDistance(input));

        input = "R2, R2, R2";
        Assert.assertEquals(2, new Day1Part1().calculateDistance(input));

        input = "R5, L5, R5, R3";
        Assert.assertEquals(12, new Day1Part1().calculateDistance(input));
    }
}