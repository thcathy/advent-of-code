package com.adventofcode.year2018;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Day2Part2 {
    Logger log = LoggerFactory.getLogger(Day2Part2.class);
    final static String inputFile = "2018/day2.txt";

    public static void main(String... args) throws IOException {
        Day2Part2 solution = new Day2Part2();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = correctBoxIdUnionCharacters(lines);
        log.warn("What is the checksum for your list of box IDs? {}", result);
    }
    
    String correctBoxIdUnionCharacters(List<String> inputs) {
        for (String s1 : inputs) {
            for (String s2 : inputs) {
                if (!s1.equals(s2) && isExactOneCharacterDiff(s1, s2)) {
                    return removeDiff(s1, s2);
                }
            }
        }
        throw new RuntimeException("cannot find correct boxes");
    }
   

    String removeDiff(String s1, String s2) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s1.length(); i++) {
            if (s1.charAt(i) == s2.charAt(i)) {
                sb.append(s1.charAt(i));
            }
        }
        return sb.toString();
    }

    boolean isExactOneCharacterDiff(String s1, String s2) {
        boolean hasDiff = false;
        for (int i = 0; i < s1.length(); i++) {
            if (s1.charAt(i) != s2.charAt(i)) {
                if (hasDiff)
                    return false;
                else
                    hasDiff = true;
            }
        }
        return hasDiff;
    }

    @Test
    public void unitTest() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource("2018/day2_test2.txt"), Charsets.UTF_8);
        assertEquals("fgij", correctBoxIdUnionCharacters(lines));
    }
}
