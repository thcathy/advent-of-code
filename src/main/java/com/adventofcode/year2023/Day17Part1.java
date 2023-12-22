package com.adventofcode.year2023;

import static com.adventofcode.year2023.Day17Part1.Direction.Down;
import static com.adventofcode.year2023.Day17Part1.Direction.Left;
import static com.adventofcode.year2023.Day17Part1.Direction.Right;
import static com.adventofcode.year2023.Day17Part1.Direction.Up;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Day17Part1 {
    Logger log = LoggerFactory.getLogger(Day17Part1.class);
    final static String inputFile = "2023/day17.txt";

    public static void main(String... args) throws IOException {
        Day17Part1 solution = new Day17Part1();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        // var puzzle = new Day17Part1().parseInput(lines);
        // var result = totalEnergized(puzzle);
        // log.warn("What is the least heat loss it can incur? {}", result);
    }

    int minHeatLoss(Puzzle puzzle) {
        var queue = new PriorityQueue<MovementState>(Comparator.comparingInt(s -> s.heatLoss));
        var minHeatLossMap = new HashMap<Position, Integer>();
        queue.add(new MovementState(0, new Position(0, 0), Left, 0));
        queue.add(new MovementState(0, new Position(0, 0), Down, 0));

        while (!queue.isEmpty()) {
            var state = queue.poll();
            if (puzzle.isEnding(state.position)) return state.heatLoss;


        }
        return 0;
    }

    //region Data Objects

    record MovementState(int heatLoss, Position position, Direction direction, int sameDirectionStep) {
        Stream<MovementState> allPossibleNextState(Map<Position, Character> map) {
            var moveUpPosition = position.move(Up);
            var moreLoss = map.get(moveUpPosition);
            var moveUpState = new MovementState(heatLoss + moreLoss, moveUpPosition, Up, sameDirectionStep + (direction == Up) ? 1 : 0);
            
        }
    }

    record Puzzle(Map<Position, Character> map, int width, int height) {
        boolean isValid(Position position) {
            return position.x >= 0 && position.x < width && position.y >= 0 && position.y < height;
        }

        boolean isEnding(Position position) {
            return position.x == width - 1 && position.y == height - 1;
        }
    }

    record Position(int x, int y) {
        Position move(Direction direction) {
            return switch (direction) {
                case Up -> new Position(x, y-1);
                case Down -> new Position(x, y+1);
                case Left -> new Position(x-1, y);
                case Right -> new Position(x+1, y);
            };
        }
    }

    enum Direction { Up, Down, Left, Right }
    
    //endregion

    //region Input Parsing

    Puzzle parseInput(List<String> inputs) {
        var height = inputs.size();
        var width = inputs.get(0).length();
        var map = new HashMap<Position, Character>();
        for (int y = 0; y < height; y++) {
            var line = inputs.get(y);
            for (int x = 0; x < width; x++) {                
                map.put(new Position(x, y), line.charAt(x));
            }
        }
        return new Puzzle(map, width, height);
    }

    //endregion

    @Test
    public void unitTest() throws IOException {        
        var lines = Resources.readLines(ClassLoader.getSystemResource("2023/day17_test.txt"), Charsets.UTF_8);
        var puzzle = new Day17Part1().parseInput(lines);
        var result = minHeatLoss(puzzle);
        Assert.assertEquals(46, result);
    }
}
