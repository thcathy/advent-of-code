package com.adventofcode.year2022;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class Day25Part1 {
    final static String inputFile = "2022/day25.txt";

    public static void main(String... args) throws IOException {
        Day25Part1 solution = new Day25Part1();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = snafuSupplyToConsole(lines);
        System.out.println("What SNAFU number do you supply to Bob's console? " + result);
    }

    public static String decimalToSnafu(long n) {
        StringBuilder sb = new StringBuilder();
        while (n > 0) {
            long rem = n % 5;
            n = n / 5;
            if (rem == 4) {
                sb.append('-');
                n++;
            } else if (rem == 3) {
                sb.append('=');
                n++;
            } else {
                sb.append(rem);
            }
        }
        return sb.reverse().toString();
    }

    public static long snafuToDecimal(String snafu) {
        long result = 0;
        long powerOfFive = 1;

        for (int i = snafu.length() - 1; i >= 0; i--) {
            result += switch (snafu.charAt(i)) {
                case '2' -> 2 * powerOfFive;
                case '1' -> powerOfFive;
                case '-' -> -1 * powerOfFive;
                case '=' -> -2 * powerOfFive;
                default -> 0;
            };

            powerOfFive *= 5;
        }

        return result;
    }

    String snafuSupplyToConsole(List<String> inputs) {
        var decimal = inputs.stream().mapToLong(Day25Part1::snafuToDecimal).sum();
        return decimalToSnafu(decimal);
    }

    @Test
    public void unitTest() throws Exception {
        assertEquals(1, snafuToDecimal("1"));
        assertEquals(4, snafuToDecimal("1-"));
        assertEquals(3, snafuToDecimal("1="));
        assertEquals(12345, snafuToDecimal("1-0---0"));
        assertEquals(314159265, snafuToDecimal("1121-1110-1=0"));

        assertEquals("10", decimalToSnafu(5));
        assertEquals("11", decimalToSnafu(6));
        assertEquals("2=", decimalToSnafu(8));
        assertEquals("1=", decimalToSnafu(3));
        assertEquals("1=-0-2", decimalToSnafu(1747));

        var lines = Files.readAllLines(Paths.get(ClassLoader.getSystemResource("2022/day25_test.txt").toURI()));
        assertEquals("2=-1=0", snafuSupplyToConsole(lines));
    }

}
