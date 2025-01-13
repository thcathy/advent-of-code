package com.adventofcode.year2024;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class Day16Part1 {
    private static final Logger log = LoggerFactory.getLogger(Day16Part1.class);
    private static final String INPUT_FILE = "2024/day16.txt";

    private static final char WALL = '#';
    private static final char START = 'S';
    private static final char END = 'E';
    private static final int[] dirX = {0, 1, 0, -1}; // East, South, West, North
    private static final int[] dirY = {1, 0, -1, 0};

    public static void main(String... args) throws IOException {
        new Day16Part1().run();
    }

    private void run() throws IOException {
        var input = parseInput();
        var startPos = findPosition(input.map, START);
        var endPos = findPosition(input.map, END);
        int minScore = findMinimumScore(input.map, startPos[0], startPos[1], endPos[0], endPos[1]);
        log.info("Minimum score to reach the end: {}", minScore);
    }

    private Input parseInput() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(INPUT_FILE), Charsets.UTF_8);
        char[][] map = new char[lines.size()][];
        for (int i = 0; i < lines.size(); i++) {
            map[i] = lines.get(i).toCharArray();
        }
        return new Input(map);
    }

    private record Input(char[][] map) {}

    private int[] findPosition(char[][] map, char target) {
        int[] position = new int[2];

        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (map[i][j] == target) {
                    position[0] = i;
                    position[1] = j;
                    return position;
                }
            }
        }
        return new int[]{-1, -1};
    }

    private int findMinimumScore(char[][] map, int startX, int startY, int endX, int endY) {
        PriorityQueue<State> queue = new PriorityQueue<>(Comparator.comparingInt(s -> s.score));
        HashMap<VisitedState, Integer> visited = new HashMap<>();
        queue.add(new State(startX, startY, 0, 0)); // face east
        visited.put(new VisitedState(startX, startY, 0), 0);
        var minScore = Integer.MAX_VALUE;

        while (!queue.isEmpty()) {
            State current = queue.poll();

            // Check if we reached the end
            if (current.x == endX && current.y == endY) {
                minScore = Math.min(minScore, current.score);
            }

            // Move forward
            var newX = current.x + dirX[current.direction];
            var newY = current.y + dirY[current.direction];
            if (isValidMove(map, newX, newY)) {
                var newScore = current.score + 1;
                VisitedState newVisitedState = new VisitedState(newX, newY, current.direction);
                if (!visited.containsKey(newVisitedState) || newScore < visited.get(newVisitedState)) {
                    visited.put(newVisitedState, newScore);
                    queue.add(new State(newX, newY, current.direction, newScore));
                }
            }

            // Or Turn
            var newScore = current.score + 1000;
            var newDirectionCW = (current.direction + 1) % dirX.length;
            var newVisitedStateCW = new VisitedState(current.x, current.y, newDirectionCW);

            if (!visited.containsKey(newVisitedStateCW) || newScore < visited.get(newVisitedStateCW)) {
                visited.put(newVisitedStateCW, newScore);
                queue.add(new State(current.x, current.y, newDirectionCW, newScore));
            }
            var newDirectionCCW = (current.direction + 3) % dirX.length;
            var newVisitedStateCCW = new VisitedState(current.x, current.y, newDirectionCCW);
            if (!visited.containsKey(newVisitedStateCCW) || newScore < visited.get(newVisitedStateCCW)) {
                visited.put(newVisitedStateCCW, newScore);
                queue.add(new State(current.x, current.y, newDirectionCCW, newScore));
            }
        }

        return minScore;
    }

    private boolean isValidMove(char[][] map, int x, int y) {
        return x >= 0 && y >= 0 && x < map.length && y < map[0].length && map[x][y] != WALL;
    }

    private record State(int x, int y, int direction, int score) {}
    private record VisitedState(int x, int y, int direction) {}
}
