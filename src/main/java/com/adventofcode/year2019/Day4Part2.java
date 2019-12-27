package com.adventofcode.year2019;


import java.io.IOException;
import java.util.stream.IntStream;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Day4Part2 {
    Logger log = LoggerFactory.getLogger(Day4Part2.class);

    public static void main(String... args) throws IOException {
        Day4Part2 solution = new Day4Part2();
        solution.run();
    }

    void run() throws IOException {
        var result = totalPasswords(128392, 643281);
        log.warn("How many different passwords = {}", result);
    }

    long totalPasswords(int from, int to) {
        return IntStream.rangeClosed(from, to).mapToObj(String::valueOf)
                .filter(this::isPassword)
                .count();
    }

    boolean isPassword(String value) {
        boolean digitNeverDecrease = true;
        boolean exactSameTwoAdjacentDigits = false;

        for (int i = 1; i < value.length(); i++) {
            if (isExactSameTwoAdjacentDigits(value, i))   exactSameTwoAdjacentDigits = true;
            if (value.charAt(i-1) > value.charAt(i))    digitNeverDecrease = false;
        }

        return digitNeverDecrease && exactSameTwoAdjacentDigits;
    }

    private boolean isExactSameTwoAdjacentDigits(String value, int i) {
        if (value.charAt(i-1) != value.charAt(i)) return false;
        if (i >= 2 && value.charAt(i-2) == value.charAt(i-1)) return false;
        if (i < value.length() - 1 && value.charAt(i) == value.charAt(i+1)) return false;
        return true;
    }

    @Test
    public void program_testcases() {
        assertTrue(isPassword("112233"));
        assertTrue(isPassword("111122"));
        assertFalse(isPassword("123444"));
    }
}
