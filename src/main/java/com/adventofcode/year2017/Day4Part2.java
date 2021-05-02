package com.adventofcode.year2017;


import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

public class Day4Part2 {
    Logger log = LoggerFactory.getLogger(Day4Part2.class);
    final static String inputFile = "2017/day4_1.txt";

    public static void main(String... args) throws IOException {
        Day4Part2 solution = new Day4Part2();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = lines.stream().filter(this::isValidPassPhrase).count();
        log.warn("How many passphrases are valid? {}", result);
    }

    boolean isValidPassPhrase(String input) {
        var phrases = new HashSet<String>();
        for (String phrase : input.split(" ")) {
            var chars = phrase.toCharArray();
            Arrays.sort(chars);
            if (!phrases.add(new String(chars)))
                return false;
        }
        return true;
    }

}
