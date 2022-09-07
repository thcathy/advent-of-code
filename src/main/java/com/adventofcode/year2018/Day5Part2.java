package com.adventofcode.year2018;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Day5Part2 {
    final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    Logger log = LoggerFactory.getLogger(Day5Part2.class);
    final static String inputFile = "2018/day5.txt";
    
    public static void main(String... args) throws IOException {
        Day5Part2 solution = new Day5Part2();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = shortestResultPolymer(lines.get(0));
        log.warn("What is the length of the shortest polymer you can produce? {}", result);
    }
    
    int shortestResultPolymer(String polymer) {
        int minPolymerLength = Integer.MAX_VALUE;
        Set<Character> types = unitTypes(polymer);
        for (char type : types) {
            minPolymerLength = Math.min(minPolymerLength, remainUnits(removeAllUnitType(polymer, type)));
        }
        return minPolymerLength;
    }

    String removeAllUnitType(String polymer, char type) {
        return polymer.replaceAll(String.valueOf(Character.toLowerCase(type)), "")
                .replaceAll(String.valueOf(Character.toUpperCase(type)), "");
    }

    int remainUnits(String polymer) {
        Deque<Character> result = new LinkedList<>();
        for (char unit : polymer.toCharArray()) {
            if (result.size() > 0 && canReact(result.peekLast(), unit)) {
                result.pollLast();
                continue;
            }
            result.add(unit);
        }
        return result.size();
    }
    
    Set<Character> unitTypes(String polymer) {        
       return polymer.chars().mapToObj(i->(char)i)
                .map(c -> Character.toLowerCase(c))
                .collect(Collectors.toSet());
    }

    boolean canReact(char unit1, char unit2) {
        return (Character.isLowerCase(unit1) && Character.toUpperCase(unit1) == unit2) 
                || (Character.isUpperCase(unit1) && Character.toLowerCase(unit1) == unit2);
    }

    @Test
    public void unitTest() throws IOException {        
        assertEquals(4, shortestResultPolymer("dabAcCaCBAcCcaDA"));        
    }
}
