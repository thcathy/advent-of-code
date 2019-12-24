package com.adventofcode.year2019;


import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Day3Part1 {
    Logger log = LoggerFactory.getLogger(Day3Part1.class);
    final static String inputFile = "2019/day3_1.txt";

    public static void main(String... args) throws IOException {
        Day3Part1 solution = new Day3Part1();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = minDistanceOfintersections(lines.get(0),lines.get(1));
        log.warn("What is the Manhattan distance = {}", result);
    }

    int minDistanceOfintersections(String wire1Input, String wire2Input) {
        var locations = getWireLocations(wire1Input);
        locations.retainAll(getWireLocations(wire2Input));
        return locations.stream()
                .mapToInt(Position::distanceFromCentral)
                .min().getAsInt();
    }

    HashSet<Position> getWireLocations(String inputs) {
        var from = new Position(0, 0);
        var locations = new HashSet<Position>();
        for (String input : inputs.split(",")) {
            var positions = extendWire(from, input);
            from = positions.get(positions.size()-1);
            locations.addAll(positions);
        }
        return locations;
    }

    List<Position> extendWire(Position from, String input) {
        char direction = input.charAt(0);
        int length = Integer.valueOf(input.substring(1));
        var locations = new ArrayList<Position>(length);
        for (int i = 0; i < length; i++) {
            from = movePosition(direction, from);
            locations.add(from);
        }
        return locations;
    }

    Position movePosition(char direction, Position from) {
        if (direction == 'R')
            return new Position(from.x+1, from.y);
        else if (direction == 'L')
            return new Position(from.x-1, from.y);
        else if (direction == 'U')
            return new Position(from.x, from.y+1);
        else if (direction == 'D')
            return new Position(from.x, from.y-1);
        return null;
    }

    @Test
    public void minDistanceOfintersections_testcases() {
        assertEquals(6, minDistanceOfintersections("R8,U5,L5,D3","U7,R6,D4,L4"));
        assertEquals(159, minDistanceOfintersections("R75,D30,R83,U83,L12,D49,R71,U7,L72","U62,R66,U55,R34,D71,R55,D58,R83"));
        assertEquals(135, minDistanceOfintersections("R98,U47,R26,D63,R33,U87,L62,D20,R33,U53,R51","U98,R91,D20,R16,D67,R40,U7,R15,U6,R7"));
    }

    @Test
    public void getWireLocations_testcases() {
        var wireLocations = getWireLocations("R8,U5,L5,D3");
        assertTrue(wireLocations.contains(new Position(1, 0 )));
        assertTrue(wireLocations.contains(new Position(8, 0 )));
        assertTrue(wireLocations.contains(new Position(8, 5 )));
        assertTrue(wireLocations.contains(new Position(3, 5 )));
        assertTrue(wireLocations.contains(new Position(3, 2 )));

        var wire2Locations = getWireLocations("U7,R6,D4,L4");
        assertTrue(wire2Locations.contains(new Position(0, 7 )));
        assertTrue(wire2Locations.contains(new Position(6, 7 )));
        assertTrue(wire2Locations.contains(new Position(2, 3 )));
    }

    class Position {
        int x = 0, y = 0;

        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        int distanceFromCentral() { return Math.abs(x) + Math.abs(y); }

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
}
