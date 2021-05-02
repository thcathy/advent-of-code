package com.adventofcode.year2017;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Objects;

import static com.adventofcode.year2017.Day3Part1.Direction.Right;

public class Day3Part1 {
    Logger log = LoggerFactory.getLogger(Day3Part1.class);
    static int QUESTION_INPUT = 325489;

    public static void main(String... args) throws IOException {
        Day3Part1 solution = new Day3Part1();
        solution.run();
    }

    void run() {
        Position memoryPosition = calculateMemoryPosition(QUESTION_INPUT);
        int result = memoryPosition.manhattanDistanceFromOrigin();
        log.warn("How many steps are required to carry the data from the square identified in your puzzle input all the way to the access port? {}", result);
    }

    Position calculateMemoryPosition(int finalAddress) {
        var memoryPositions = new HashSet<Position>();
        var memoryAddress = 1;
        var position = new Position(0, 0);
        memoryPositions.add(position);
        Direction direction = Right;

        while (memoryAddress != finalAddress) {
            var next = moveTo(position, direction);
            memoryPositions.add(next);
            memoryAddress++;
            var turnedDirection = turnAntiClockwise(direction);
            if (!memoryPositions.contains(moveTo(next, turnedDirection))) {
                direction = turnedDirection;
            }
            position = next;
        }
        return position;
    }

    Direction turnAntiClockwise(Direction direction) {
        switch (direction) {
            case Up:
                return Direction.Left;
            case Down:
                return Direction.Right;
            case Left:
                return Direction.Down;
            case Right:
                return Direction.Up;
        }
        throw new RuntimeException();
    }

    Position moveTo(Position position, Direction direction) {
        switch (direction) {
            case Up:
                return new Position(position.x, position.y-1);
            case Down:
                return new Position(position.x, position.y+1);
            case Left:
                return new Position(position.x-1, position.y);
            case Right:
                return new Position(position.x+1, position.y);
        }
        throw new RuntimeException();
    }

    enum Direction { Up, Down, Left, Right }

    static class Position {
        int x = 0, y = 0;

        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        int manhattanDistanceFromOrigin() { return Math.abs(x) + Math.abs(y); }

        @Override
        public String toString() { return MessageFormat.format("({0},{1})", x, y); }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Position position = (Position) o;
            return x == position.x && y == position.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }
}
