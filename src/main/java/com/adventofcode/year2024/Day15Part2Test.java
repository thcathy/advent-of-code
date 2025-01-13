package com.adventofcode.year2024;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class Day15Part2Test {
    @Test
    public void testSimulateRobotMoveLeft() {
        // Initial map
        char[][] initialMap = {
                "####################".toCharArray(),
                "##....[]....[]..[]##".toCharArray(),
                "##............[]..##".toCharArray(),
                "##..[][]....[]..[]##".toCharArray(),
                "##....[]@.....[]..##".toCharArray(),
                "##[]##....[]......##".toCharArray(),
                "##[]....[]....[]..##".toCharArray(),
                "##..[][]..[]..[][]##".toCharArray(),
                "##........[]......##".toCharArray(),
                "####################".toCharArray()
        };

        // Expected map after move '<'
        char[][] expectedMap = {
                "####################".toCharArray(),
                "##....[]....[]..[]##".toCharArray(),
                "##............[]..##".toCharArray(),
                "##..[][]....[]..[]##".toCharArray(),
                "##...[]@......[]..##".toCharArray(),
                "##[]##....[]......##".toCharArray(),
                "##[]....[]....[]..##".toCharArray(),
                "##..[][]..[]..[][]##".toCharArray(),
                "##........[]......##".toCharArray(),
                "####################".toCharArray()
        };

        // Create an instance of Day15Part2
        Day15Part2 day15Part2 = new Day15Part2();

        // Simulate the move '<'
        day15Part2.simulateRobot(initialMap, "<");

        // Compare the resulting map with the expected map
        assertArrayEquals(expectedMap, initialMap);
    }

    @Test
    public void testSimulateRobotMoveUp() {
        // Initial map
        char[][] initialMap = {
                "####################".toCharArray(),
                "##....[]....[]..[]##".toCharArray(),
                "##............[]..##".toCharArray(),
                "##..[][]....[]..[]##".toCharArray(),
                "##...[].......[]..##".toCharArray(),
                "##[]##....[]......##".toCharArray(),
                "##[]......[]..[]..##".toCharArray(),
                "##..[][]..@[].[][]##".toCharArray(),
                "##........[]......##".toCharArray(),
                "####################".toCharArray()
        };

        // Expected map after move '^'
        char[][] expectedMap = {
                "####################".toCharArray(),
                "##....[]....[]..[]##".toCharArray(),
                "##............[]..##".toCharArray(),
                "##..[][]....[]..[]##".toCharArray(),
                "##...[]...[]..[]..##".toCharArray(),
                "##[]##....[]......##".toCharArray(),
                "##[]......@...[]..##".toCharArray(),
                "##..[][]...[].[][]##".toCharArray(),
                "##........[]......##".toCharArray(),
                "####################".toCharArray()
        };

        // Create an instance of Day15Part2
        Day15Part2 day15Part2 = new Day15Part2();

        // Simulate the move '^'
        day15Part2.simulateRobot(initialMap, "^");

        // Compare the resulting map with the expected map
        assertArrayEquals(expectedMap, initialMap);
    }

    @Test
    public void testSimulateRobotMoveDown() {
        // Initial map setup
        char[][] initialMap = {
                "####################".toCharArray(),
                "##[]..[]......[][]##".toCharArray(),
                "##[]..........@[].##".toCharArray(),
                "##..........[][][]##".toCharArray(),
                "##...........[][].##".toCharArray(),
                "##..##[]..[]......##".toCharArray(),
                "##...[]...[]..[]..##".toCharArray(),
                "##.....[]..[].[][]##".toCharArray(),
                "##........[]......##".toCharArray(),
                "####################".toCharArray()
        };

        // Expected map after move 'v'
        char[][] expectedMap = {
                "####################".toCharArray(),
                "##[]..[]......[][]##".toCharArray(),
                "##[]...........[].##".toCharArray(),
                "##..........[]@.[]##".toCharArray(),
                "##............[]..##".toCharArray(),
                "##..##[]..[].[][].##".toCharArray(),
                "##...[]...[]..[]..##".toCharArray(),
                "##.....[]..[].[][]##".toCharArray(),
                "##........[]......##".toCharArray(),
                "####################".toCharArray()
        };

        // Create an instance of Day15Part2
        Day15Part2 day15Part2 = new Day15Part2();

        // Simulate the move 'v'
        day15Part2.simulateRobot(initialMap, "v");

        // Compare the resulting map with the expected map
        assertArrayEquals(expectedMap, initialMap);
    }

    @Test
    public void testSimulateRobotMoveDown2() {
        // Initial map setup
        char[][] initialMap = {
                "####################".toCharArray(),
                "##[]..[]......[][]##".toCharArray(),
                "##[]........@..[].##".toCharArray(),
                "##..........[][][]##".toCharArray(),
                "##...........[][].##".toCharArray(),
                "##..##[]..[]......##".toCharArray(),
                "##...[]...[]..[]..##".toCharArray(),
                "##.....[]..[].[][]##".toCharArray(),
                "##........[]......##".toCharArray(),
                "####################".toCharArray()
        };

        // Expected map after move 'v'
        char[][] expectedMap = {
                "####################".toCharArray(),
                "##[]..[]......[][]##".toCharArray(),
                "##[]...........[].##".toCharArray(),
                "##..........@.[][]##".toCharArray(),
                "##..........[].[].##".toCharArray(),
                "##..##[]..[].[]...##".toCharArray(),
                "##...[]...[]..[]..##".toCharArray(),
                "##.....[]..[].[][]##".toCharArray(),
                "##........[]......##".toCharArray(),
                "####################".toCharArray()
        };

        // Create an instance of Day15Part2
        Day15Part2 day15Part2 = new Day15Part2();

        // Simulate the move 'v'
        day15Part2.simulateRobot(initialMap, "v");

        // Compare the resulting map with the expected map
        assertArrayEquals(expectedMap, initialMap);
    }
}
