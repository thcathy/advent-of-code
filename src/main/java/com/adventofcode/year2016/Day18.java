package com.adventofcode.year2016;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;

public class Day18 {
    Logger log = LoggerFactory.getLogger(Day18.class);
    MessageDigest messageDigest = MessageDigest.getInstance("MD5");

    public Day18() throws NoSuchAlgorithmException {}

    public static void main(String... args) throws Exception {
        Day18 solution = new Day18();
        solution.run();
    }

    void run() throws Exception {
       log.warn("First star - how many safe tiles are there? {}", numberOfSaveTiles("^^^^......^...^..^....^^^.^^^.^.^^^^^^..^...^^...^^^.^^....^..^^^.^.^^...^.^...^^.^^^.^^^^.^^.^..^.^", 40));
       log.warn("Second star - how many safe tiles are there? {}", numberOfSaveTiles("^^^^......^...^..^....^^^.^^^.^.^^^^^^..^...^^...^^^.^^....^..^^^.^.^^...^.^...^^.^^^.^^^^.^^.^..^.^", 400000));
    }

    int numberOfSaveTiles(String input, int totalRows) {
        var rows = new ArrayList<String>();
        rows.add(input);
        for (int i=0; i<totalRows-1; i++) {
            input = nextRow(input);
            rows.add(input);
        }

        return rows.stream()
                .map(s -> s.replaceAll("\\^", ""))
                .mapToInt(s -> s.length())
                .sum();
    }

    String nextRow(String previous) {
        final var input = '.' + previous + '.';
        return IntStream.range(0, input.length() - 2)
                .mapToObj(i -> getTile(input.charAt(i), input.charAt(i+1), input.charAt(i+2)))
                .collect(Collectors.joining());
    }

    String getTile(char left, char center, char right) {
        boolean isTrap =
                (left == '^' && center == '^' && right == '.') ||
                (left == '.' && center == '^' && right == '^') ||
                (left == '^' && center == '.' && right == '.') ||
                (left == '.' && center == '.' && right == '^');
        return isTrap ? "^" : ".";
    }

    @Test
    public void test_numberOfSaveTiles() {
        assertEquals(38, numberOfSaveTiles(".^^.^.^^^^", 10));
    }

    @Test
    public void test_getTile() {
        assertEquals(".", getTile('.', '.', '.'));
        assertEquals("^", getTile('.', '.', '^'));
        assertEquals("^", getTile('.', '^', '^'));
        assertEquals("^", getTile('^', '^', '.'));
        assertEquals("^", getTile('^', '.', '.'));
    }

    @Test
    public void test_nextRow() {
        assertEquals(".^^^^", nextRow("..^^."));
        assertEquals("^^..^", nextRow(".^^^^"));
        assertEquals("^.^^.^.^^.", nextRow("^^^...^..^"));
        assertEquals("^..^^^^.^^", nextRow("^^^^..^^^."));
    }

}