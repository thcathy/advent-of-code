package com.adventofcode.year2019;


import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.stream.IntStream;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Day4Part1 {
    Logger log = LoggerFactory.getLogger(Day4Part1.class);

    public static void main(String... args) throws IOException {
        Day4Part1 solution = new Day4Part1();
        solution.run();
    }

    void run() throws IOException {
        var result = totalPasswords(234208, 765869);
        log.warn("How many different passwords = {}", result);
    }

    long totalPasswords(int from, int to) {
        return IntStream.rangeClosed(from, to).mapToObj(String::valueOf)
                .filter(this::isPassword)
                .count();
    }

    boolean isPassword(String value) {
        boolean digitNeverDecrease = true, sameTwoAdjacentDigits = false;
        for (int i = 1; i < value.length(); i++) {
            if (value.charAt(i-1) == value.charAt(i)) sameTwoAdjacentDigits = true;
            if (value.charAt(i-1) > value.charAt(i)) digitNeverDecrease = false;
        }
        return digitNeverDecrease && sameTwoAdjacentDigits;
    }

    @Test
    public void program_testcases() {
        assertTrue(isPassword("111111"));
        assertFalse(isPassword("223450"));
        assertFalse(isPassword("123789"));
    }

}
