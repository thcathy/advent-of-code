package com.adventofcode.year2022;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Day9Part1 {
    final static String inputFile = "2022/day9.txt";

    public static void main(String... args) throws IOException {
        Day9Part1 solution = new Day9Part1();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = tailVisitedPosition(lines);
        System.out.println("How many positions does the tail of the rope visit at least once? " + result);
    }

    int tailVisitedPosition(List<String> strings) {
        var visited = new HashSet<Position>();
        var head = new Position(0, 0);
        var tail = head;
        visited.add(tail);

        for (String string : strings) {
            var inputs = string.split(" ");
            var direction = Direction.valueOf(inputs[0]);
            var steps = Integer.parseInt(inputs[1]);
            for (int i = 0; i < steps; i++) {
                head = move(head, direction);
                tail = moveTail(head, tail);
                visited.add(tail);
            }
        }

        return visited.size();
    }

    Position moveTail(Position head, Position tail) {
        if (isTouching(head, tail)) return tail;

        if (head.x != tail.x && head.y != tail.y) {
            var newX = head.x > tail.x ? tail.x + 1 : tail.x - 1;
            var newY = head.y > tail.y ? tail.y + 1 : tail.y - 1;
            return new Position(newX, newY);
        }

        if (head.x + 2 == tail.x) return new Position(tail.x-1, tail.y);
        if (head.x - 2 == tail.x) return new Position(tail.x+1, tail.y);
        if (head.y + 2 == tail.y) return new Position(tail.x, tail.y-1);
        if (head.y - 2 == tail.y) return new Position(tail.x, tail.y+1);

        throw new RuntimeException();
    }

    boolean isTouching(Position p1, Position p2) {
        if (Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y) <= 1) return true;
        return Math.abs(p1.x - p2.x) == 1 && Math.abs(p1.y - p2.y) == 1;
    }

    Position move(Position p, Direction d) {
        return switch (d) {
            case U -> new Position(p.x, p.y+1);
            case D -> new Position(p.x, p.y-1);
            case L -> new Position(p.x-1, p.y);
            case R -> new Position(p.x+1, p.y);
        };
    }

    record Position(int x, int y) {}

    enum Direction { U, D, L, R; }

    @Test
    public void unitTest() throws Exception {
        var lines = Files.readAllLines(Paths.get(ClassLoader.getSystemResource("2022/day9_test.txt").toURI()), Charset.defaultCharset());
        
        assertEquals(new Position(2, 0), moveTail(new Position(3, 0), new Position(1, 0)));
        assertEquals(new Position(2, 2), moveTail(new Position(3, 1), new Position(1, 3)));
        assertEquals(new Position(2, 2), moveTail(new Position(2, 3), new Position(1, 1)));
        assertEquals(new Position(2, 2), moveTail(new Position(4, 2), new Position(1, 1)));
        assertEquals(13, tailVisitedPosition(lines));
    }
}
