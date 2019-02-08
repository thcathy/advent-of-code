package com.adventofcode.year2016;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.common.primitives.Booleans;

import static com.adventofcode.year2016.Day8.Action.RECTANGLE_ON;
import static com.adventofcode.year2016.Day8.Action.ROTATE_COLUMN;
import static com.adventofcode.year2016.Day8.Action.ROTATE_ROW;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

public class Day8 {
    Logger log = LoggerFactory.getLogger(Day8.class);
    final static String inputFile = "2016/day8_1.txt";

    public static void main(String... args) throws IOException {
        Day8 solution = new Day8();
        solution.run();
    }

    void run() throws IOException {
      var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);

      var screen = new Screen(50, 6);
      lines.stream()
           .map(this::parseCommand)
           .forEach(c -> screen.applyCommand(c));

      log.warn("First star - how many pixels should be lit? {}", screen.totalPixelOn());

      log.warn("Second star - what code is the screen trying to display?");
        IntStream.range(0, screen.pixel[0].length).forEach(y -> {
            IntStream.range(0, screen.pixel.length).forEach(x ->
                    System.out.print(screen.pixel[x][y] ? 'X' : ' ')
            );
            System.out.println();
        });
    }

    Command parseCommand(String input) {
        if (input.startsWith("rect ")) {
            var params = input.split(" ")[1].split("x");
            return new Command(RECTANGLE_ON, List.of(Integer.valueOf(params[0]), Integer.valueOf(params[1])));
        } else if (input.startsWith("rotate column x=")) {
            var params = input.split("=")[1].split(" by ");
            return new Command(ROTATE_COLUMN, List.of(Integer.valueOf(params[0]), Integer.valueOf(params[1])));
        }  else if (input.startsWith("rotate row y=")) {
            var params = input.split("=")[1].split(" by ");
            return new Command(ROTATE_ROW, List.of(Integer.valueOf(params[0]), Integer.valueOf(params[1])));
        }
        return null;
    }

    class Screen {
        boolean[][] pixel;

        public Screen(int x, int y) {
            pixel = new boolean[x][y];
        }

        void applyCommand(Command command) {
            switch (command.action) {
                case RECTANGLE_ON:
                    turnOnPixel(command.params);
                    break;
                case ROTATE_COLUMN:
                    rotateColumn(command.params);
                    break;
                case ROTATE_ROW:
                    rotateRow(command.params);
                    break;
            }
        }

        void rotateRow(List<Integer> params) {
            int y = params.get(0), shift = params.get(1);
            boolean[] values = new boolean[pixel.length];
            for (int x = 0; x < pixel.length; x++)
                values[x] = pixel[x][y];

            for (int x = 0; x < values.length; x++) {
                int pos = (x + shift) % values.length;
                pixel[pos][y] = values[x];
            }
        }

        void rotateColumn(List<Integer> params) {
            int x = params.get(0), shift = params.get(1);
            boolean[] values = Arrays.copyOf(pixel[x], pixel[x].length);

            for (int y = 0; y < values.length; y++) {
                int pos = (y + shift) % values.length;
                pixel[x][pos] = values[y];
            }
        }

        void turnOnPixel(List<Integer> params) {
            IntStream.range(0, params.get(0)).forEach(x ->
                IntStream.range(0, params.get(1)).forEach(y ->
                    pixel[x][y] = true
                )
            );
        }

        long totalPixelOn() {
            return Arrays.stream(pixel)
                    .flatMap(y -> Booleans.asList(y).stream())
                    .filter(v -> v)
                    .count();
        }
    }

    class Command {
        Action action;
        List<Integer> params;

        public Command(Action action, List<Integer> params) {
            this.action = action;
            this.params = params;
        }
    }

    enum Action { RECTANGLE_ON, ROTATE_COLUMN, ROTATE_ROW }

    @Test
    public void test_applyCommandInSequence() {
        var screen = new Screen(7, 3);
        screen.applyCommand(parseCommand("rect 3x2"));
        assertTrue(screen.pixel[0][0]);
        assertTrue(screen.pixel[1][0]);
        assertTrue(screen.pixel[2][0]);
        assertTrue(screen.pixel[0][1]);
        assertTrue(screen.pixel[1][1]);
        assertTrue(screen.pixel[2][1]);
        assertEquals(6, screen.totalPixelOn());

        screen.applyCommand(parseCommand("rotate column x=1 by 1"));
        assertTrue(screen.pixel[0][0]);
        assertFalse(screen.pixel[1][0]);
        assertTrue(screen.pixel[2][0]);
        assertTrue(screen.pixel[0][1]);
        assertTrue(screen.pixel[1][1]);
        assertTrue(screen.pixel[2][1]);
        assertTrue(screen.pixel[1][2]);
        assertEquals(6, screen.totalPixelOn());

        screen.applyCommand(parseCommand("rotate row y=0 by 4"));
        assertFalse(screen.pixel[0][0]);
        assertFalse(screen.pixel[1][0]);
        assertFalse(screen.pixel[2][0]);
        assertTrue(screen.pixel[4][0]);
        assertTrue(screen.pixel[6][0]);
        assertTrue(screen.pixel[0][1]);
        assertTrue(screen.pixel[1][1]);
        assertTrue(screen.pixel[2][1]);
        assertTrue(screen.pixel[1][2]);
        assertEquals(6, screen.totalPixelOn());

        screen.applyCommand(parseCommand("rotate column x=1 by 1"));
        assertFalse(screen.pixel[0][0]);
        assertTrue(screen.pixel[1][0]);
        assertFalse(screen.pixel[2][0]);
        assertTrue(screen.pixel[4][0]);
        assertTrue(screen.pixel[6][0]);
        assertTrue(screen.pixel[0][1]);
        assertFalse(screen.pixel[1][1]);
        assertTrue(screen.pixel[2][1]);
        assertTrue(screen.pixel[1][2]);
        assertEquals(6, screen.totalPixelOn());
    }

    @Test
    public void test_parseCommand() {
        Command result = parseCommand("rect 3x2");
        assertEquals(RECTANGLE_ON, result.action);
        assertEquals(3, result.params.get(0).intValue());
        assertEquals(2, result.params.get(1).intValue());

        result = parseCommand("rotate column x=1 by 1");
        assertEquals(ROTATE_COLUMN, result.action);
        assertEquals(1, result.params.get(0).intValue());
        assertEquals(1, result.params.get(1).intValue());

        result = parseCommand("rotate row y=0 by 4");
        assertEquals(ROTATE_ROW, result.action);
        assertEquals(0, result.params.get(0).intValue());
        assertEquals(4, result.params.get(1).intValue());
    }

}