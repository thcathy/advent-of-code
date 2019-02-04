package com.adventofcode.year2016;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import static org.junit.Assert.assertEquals;

public class Day6Part1 {
    Logger log = LoggerFactory.getLogger(Day6Part1.class);
    final static String inputFile = "2016/day6_1.txt";

    public static void main(String... args) throws IOException {
        Day6Part1 solution = new Day6Part1();
        solution.firstStar();
    }

    void firstStar() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = decodeSignal(lines);

        log.warn("First star - the error-corrected version of the message: {}", result);
    }

    String decodeSignal(List<String> inputs) {
        String result = "";
        int signalLength = inputs.get(0).length();
        for (int i=0; i<signalLength; i++) {
            result += mostCommonCharacters(inputs, i);
        }
        return result;
    }

    char mostCommonCharacters(List<String> inputs, int position) {
        HashMap<Character, Integer> charFrequency = new HashMap<>();
        inputs.stream().map(s -> s.charAt(position)).forEach(c -> addToMap(charFrequency, c));
        return mostCommonCharacter(charFrequency);
    }

    char mostCommonCharacter(HashMap<Character, Integer> charFrequency) {
        List<Map.Entry<Character, Integer>> letterEntries = new ArrayList(charFrequency.entrySet());
        Collections.sort(letterEntries, (o1, o2) -> o2.getValue() - o1.getValue());
        return letterEntries.get(0).getKey();
    }

    void addToMap(HashMap<Character, Integer> codes, int input) {
        char letter = (char) input;
        var count = codes.get(letter);
        if (count == null)
            codes.put(letter, 1);
        else
            codes.put(letter, ++count);
    }

    @Test
    public void test_decodeSignal() {
        List<String> inputs = new ArrayList<>(16);
        inputs.add("eedadn");
        inputs.add("drvtee");
        inputs.add("eandsr");
        inputs.add("raavrd");
        inputs.add("atevrs");
        inputs.add("tsrnev");
        inputs.add("sdttsa");
        inputs.add("rasrtv");
        inputs.add("nssdts");
        inputs.add("ntnada");
        inputs.add("svetve");
        inputs.add("tesnvt");
        inputs.add("vntsnd");
        inputs.add("vrdear");
        inputs.add("dvrsen");
        inputs.add("enarar");

        assertEquals("easter", decodeSignal(inputs));
    }

}