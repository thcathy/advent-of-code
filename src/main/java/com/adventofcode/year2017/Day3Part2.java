package com.adventofcode.year2017;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class Day3Part2 {
    Logger log = LoggerFactory.getLogger(Day3Part2.class);
    static int QUESTION_INPUT = 325489;

    public static void main(String... args) throws IOException {
        Day3Part2 solution = new Day3Part2();
        solution.run();
    }

    void run() {
        int result = valueLargerThan(QUESTION_INPUT);
        log.warn("What is the first actionValue written that is larger than your puzzle input? {}", result);
    }

    int valueLargerThan(int minValue) {
        var positionToValueMap = new HashMap<Position, Integer>();
        var value = 1;
        var position = new Position(0, 0);
        positionToValueMap.put(position, 1);
        Direction direction = Direction.Right;

        while (value < minValue) {
            var nextPosition = moveTo(position, direction);
            value = valueAtPosition(nextPosition, positionToValueMap);
            positionToValueMap.put(nextPosition, value);
            var turnedDirection = turnAntiClockwise(direction);
            if (!positionToValueMap.containsKey(moveTo(nextPosition, turnedDirection))) {
                direction = turnedDirection;
            }
            position = nextPosition;
        }
        return value;
    }

    private int valueAtPosition(Position position, Map<Position, Integer> valueMap) {
        return adjacentPositions(position)
                .mapToInt(pos -> valueMap.getOrDefault(pos, 0))
                .sum();
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
