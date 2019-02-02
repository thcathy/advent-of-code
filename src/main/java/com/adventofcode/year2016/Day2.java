package com.adventofcode.year2016;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Day2 {
    Logger log = LoggerFactory.getLogger(Day2.class);
    final static String inputFile = "2016/day2_1.txt";

    public static void main(String... args) throws IOException {
        Day2 solution = new Day2();
        solution.firstStar();
        solution.secondStar();
    }

    void firstStar() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var keypad = createFirstKeypad();
        var codes = lines.stream()
                .map(s -> keypad.getKey(s))
                .collect(Collectors.toList());

        log.warn("First star code: {}", codes);
    }

    void secondStar() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var keypad = createSecondKeypad();
        var codes = lines.stream()
                .map(s -> keypad.getKey(s))
                .collect(Collectors.toList());

        log.warn("Second star code: {}", codes);
    }

    public Keypad createFirstKeypad() {
        return new Keypad(firstKeypadValues(), new Position(1,1));
    }

    public Keypad createSecondKeypad() {
        return new Keypad(secondKeypadValues(), new Position(2,0));
    }

    class Keypad {
        Position position;
        Map<Position, Character> values;

        public Keypad(Map<Position, Character> values, Position startingPosition) {
            this.values = values;
            this.position = startingPosition;
        }

        public Character getKey(String input) {
            input.chars().mapToObj(c -> (char) c)
                    .forEach(c -> position = move(c));
            return values.get(position);
        }

        Position move(char c) {
            int x = position.x;
            int y = position.y;

            if (c == 'U') {
                x--;
            } else if (c == 'D') {
                x++;
            } else if (c == 'L') {
                y--;
            } else if (c == 'R') {
                y++;
            }

            var newPosition = new Position(x, y);
            if (!values.containsKey(newPosition)) newPosition = position;

            log.debug("move {} from {} to {}", c, position, newPosition);
            return newPosition;
        }
    }

    class Position {
        int x = 0, y = 0;

        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return MessageFormat.format("({0},{1})", x, y);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;

            if (o == null || getClass() != o.getClass()) return false;

            Position position = (Position) o;

            return new EqualsBuilder()
                    .append(x, position.x)
                    .append(y, position.y)
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37)
                    .append(x)
                    .append(y)
                    .toHashCode();
        }
    }

    public Map<Position, Character> firstKeypadValues() {
        return Map.of(
                new Position(0, 0), '1',
                new Position(0, 1), '2',
                new Position(0, 2), '3',
                new Position(1, 0), '4',
                new Position(1, 1), '5',
                new Position(1, 2), '6',
                new Position(2, 0), '7',
                new Position(2, 1), '8',
                new Position(2, 2), '9'
        );
    }

    public Map<Position, Character> secondKeypadValues() {
        var map = new HashMap<Position, Character>();
        map.put(new Position(0, 2), '1');
        map.put(new Position(1, 1), '2');
        map.put(new Position(1, 2), '3');
        map.put(new Position(1, 3), '4');
        map.put(new Position(2, 0), '5');
        map.put(new Position(2, 1), '6');
        map.put(new Position(2, 2), '7');
        map.put(new Position(2, 3), '8');
        map.put(new Position(2, 4), '9');
        map.put(new Position(3, 1), 'A');
        map.put(new Position(3, 2), 'B');
        map.put(new Position(3, 3), 'C');
        map.put(new Position(4, 2), 'D');
        return map;
    }

    @Test
    public void firstStartTestcase() {
        Keypad keypad = createFirstKeypad();
        Assert.assertEquals('1', keypad.getKey("ULL").charValue());
        Assert.assertEquals('9', keypad.getKey("RRDDD").charValue());
        Assert.assertEquals('8', keypad.getKey("LURDL").charValue());
        Assert.assertEquals('5', keypad.getKey("UUUUD").charValue());
    }

    @Test
    public void secondStarTestcase() {
        Keypad keypad = createSecondKeypad();
        Assert.assertEquals('5', keypad.getKey("ULL").charValue());
        Assert.assertEquals('D', keypad.getKey("RRDDD").charValue());
        Assert.assertEquals('B', keypad.getKey("LURDL").charValue());
        Assert.assertEquals('3', keypad.getKey("UUUUD").charValue());
    }
}