package com.adventofcode.year2016;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class Day7Part1 {
    Logger log = LoggerFactory.getLogger(Day7Part1.class);
    final static String inputFile = "2016/day7_1.txt";

    public static void main(String... args) throws IOException {
        Day7Part1 solution = new Day7Part1();
        solution.firstStar();
    }

    void firstStar() throws IOException {
       var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
       var result = lines.stream()
                        .filter(this::isSupportTLS)
                        .count();

       log.warn("First star - How many IPs in your puzzle input support TLS: {}", result);
    }

    boolean isSupportTLS(String input) {
        input = input.replaceAll("\\[", " \\[");
        input = input.replaceAll("]", "] ");
        var codes = input.split(" ");

        boolean anySupernetContainABBA = Arrays.stream(codes)
                                        .filter(s -> !s.endsWith("]"))
                                        .anyMatch(this::isContainABBA);
        boolean anyHypernetContainABBA = Arrays.stream(codes)
                                            .filter(s -> s.endsWith("]"))
                                            .anyMatch(this::isContainABBA);

        return anySupernetContainABBA && !anyHypernetContainABBA;
    }

    boolean isContainABBA(String input) {
        log.debug("test {}", input);
        int index = 4;
        while (index <= input.length()) {
            if (isAutoBridgeBypassAnnotation(input.substring(index-4, index)))
                return true;
            index++;
        }
        return false;
    }

    boolean isAutoBridgeBypassAnnotation(String input) {
        return input.charAt(0) != input.charAt(1)
                && input.charAt(0) == input.charAt(3)
                && input.charAt(1) == input.charAt(2);
    }

    @Test
    public void test_isSupportTLS() {
        assertTrue(isSupportTLS("abba[mnop]qrst"));
        assertTrue(isSupportTLS("ioxxoj[asdfgh]zxcvbn"));
        assertFalse(isSupportTLS("abcd[bddb]xyyx"));
        assertFalse(isSupportTLS("aaaa[qwer]tyui"));
    }

}