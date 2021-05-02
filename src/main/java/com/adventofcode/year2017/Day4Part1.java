package com.adventofcode.year2017;


import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashSet;

public class Day4Part1 {
    Logger log = LoggerFactory.getLogger(Day4Part1.class);
    final static String inputFile = "2017/day4_1.txt";

    public static void main(String... args) throws IOException {
        Day4Part1 solution = new Day4Part1();
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
            if (!phrases.add(phrase))
                return false;
        }
        return true;
    }

}
