package com.adventofcode.year2016;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class Day7Part2 {
    Logger log = LoggerFactory.getLogger(Day7Part2.class);
    final static String inputFile = "2016/day7_1.txt";

    public static void main(String... args) throws IOException {
        Day7Part2 solution = new Day7Part2();
        solution.secondStar();
    }

    void secondStar() throws IOException {
       var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
       var result = lines.stream()
                        .filter(this::isSupportSSL)
                        .count();

       log.warn("Second star - How many IPs in your puzzle input support SSL: {}", result);
    }

    boolean isSupportSSL(String input) {
        input = input.replaceAll("\\[", " \\[");
        input = input.replaceAll("]", "] ");
        var codes = input.split(" ");

        var ABAPatterns = Arrays.stream(codes)
                            .filter(s -> !s.endsWith("]"))
                            .flatMap(this::findAllABA);
        var hypernetSequences = Arrays.stream(codes)
                                            .filter(s -> s.endsWith("]"))
                                            .collect(Collectors.toList());
        return ABAPatterns.anyMatch(pattern ->
                hypernetSequences.stream().anyMatch(hypernet -> hypernet.contains(convertToBAB(pattern)))
        );
    }

    CharSequence convertToBAB(String pattern) {
        return new StringBuilder()
                .append(pattern.charAt(1))
                .append(pattern.charAt(0))
                .append(pattern.charAt(1));
    }

    Stream<String> findAllABA(String input) {
        List<String> ABAPatterns = new ArrayList<>();
        int index = 3;
        while (index <= input.length()) {
            var subString = input.substring(index-3, index);
            if (isABA(subString))
                ABAPatterns.add(subString);
            index++;
        }
        return ABAPatterns.stream();
    }

    boolean isABA(String input) {
        return input.charAt(0) != input.charAt(1) && input.charAt(0) == input.charAt(2);
    }

    @Test
    public void test_isSupportSSL() {
        assertTrue(isSupportSSL("aba[bab]xyz"));
        assertTrue(isSupportSSL("aaa[kek]eke"));
        assertTrue(isSupportSSL("zazbz[bzb]cdb"));
        assertFalse(isSupportSSL("xyx[xyx]xyx"));
    }

}