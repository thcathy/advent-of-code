package com.adventofcode.year2024;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class Day16Part2 {
    private static final Logger log = LoggerFactory.getLogger(Day16Part2.class);
    private static final String INPUT_FILE = "2024/day16.txt";
    private static final char WALL = '#';
    private static final char START = 'S';
    private static final char END = 'E';
    private static final int[] dirX = {0, 1, 0, -1}; // East, South, West, North
    private static final int[] dirY = {1, 0, -1, 0};

    public static void main(String... args) throws IOException {
        new Day16Part2().run();
    }

    private void run() throws IOException {
        var input = parseInput();
        var startPos = findPosition(input.map, START);
        var endPos = findPosition(input.map, END);
        var states = statesReachedTheEnd(input.map, startPos, endPos);
        var minScore = states.stream().mapToInt(s -> s.score).min().orElseThrow();
        var uniqueCount = states.stream().filter(s -> s.score == minScore)
                .flatMap(s -> s.path.stream()).distinct().count();
        log.info("Number of unique positions across all best paths: {}", uniqueCount);
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
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (map[i][j] == target) {
                    return new int[]{i, j};
                }
            }
        }
        return new int[]{-1, -1};
    }

    private List<State> statesReachedTheEnd(char[][] map, int[] startPos, int[] endPos) {
        var queue = new PriorityQueue<State>(Comparator.comparingInt(s -> s.score));
        var visited = new HashMap<VisitedState, Integer>();
        var statesReachedTheEnd = new ArrayList<State>();
        var minScore = Integer.MAX_VALUE;
        queue.add(new State(startPos[0], startPos[1], 0, 0, List.of(startPos[0] + "," + startPos[1])));
        visited.put(new VisitedState(startPos[0], startPos[1], 0), 0);

        while (!queue.isEmpty()) {
            State current = queue.poll();
            if (current.x == endPos[0] && current.y == endPos[1]) { // Check if reach the end position
                statesReachedTheEnd.add(current);
                minScore = Math.min(current.score, minScore);
            }

            // Move forward
            int newX = current.x + dirX[current.direction];
            int newY = current.y + dirY[current.direction];
            if (isValidMove(map, newX, newY)) {
                int newScore = current.score + 1;
                VisitedState newVisitedState = new VisitedState(newX, newY, current.direction);
                List<String> newPath = new ArrayList<>(current.path);
                newPath.add(newX + "," + newY);

                if (!visited.containsKey(newVisitedState) || newScore <= visited.get(newVisitedState)) {
                    visited.put(newVisitedState, newScore);
                    queue.add(new State(newX, newY, current.direction, newScore, newPath));
                }
            }

            // Turn
            int cwDirection = (current.direction + 1) % dirX.length;
            int newScore = current.score + 1000;
            VisitedState cwVisitedState = new VisitedState(current.x, current.y, cwDirection);
            if (!visited.containsKey(cwVisitedState) || newScore <= visited.get(cwVisitedState)) {
                visited.put(cwVisitedState, newScore);
                queue.add(new State(current.x, current.y, cwDirection, newScore, current.path));
            }
            int ccwDirection = (current.direction + 3) % dirX.length;
            VisitedState ccwVisitedState = new VisitedState(current.x, current.y, ccwDirection);
            if (!visited.containsKey(ccwVisitedState) || newScore <= visited.get(ccwVisitedState)) {
                visited.put(ccwVisitedState, newScore);
                queue.add(new State(current.x, current.y, ccwDirection, newScore, current.path));
            }
        }

        return statesReachedTheEnd;
    }

    private boolean isValidMove(char[][] map, int x, int y) {
        return x >= 0 && y >= 0 && x < map.length && y < map[0].length && map[x][y] != WALL;
    }

    private record State(int x, int y, int direction, int score, List<String> path) {}

    private record VisitedState(int x, int y, int direction) {}

}
