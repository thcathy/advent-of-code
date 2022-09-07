package com.adventofcode.year2018;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Deque;
import java.util.LinkedList;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Day5Part1 {
    final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    Logger log = LoggerFactory.getLogger(Day5Part1.class);
    final static String inputFile = "2018/day5.txt";
    
    public static void main(String... args) throws IOException {
        Day5Part1 solution = new Day5Part1();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);        
        var result =  remainUnits(lines.get(0));
        log.warn("How many units remain after fully reacting the polymer you scanned? {}", result);
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

    boolean canReact(char unit1, char unit2) {
        return (Character.isLowerCase(unit1) && Character.toUpperCase(unit1) == unit2) 
                || (Character.isUpperCase(unit1) && Character.toLowerCase(unit1) == unit2);
    }

    @Test
    public void unitTest() throws IOException {        
        assertEquals(10, remainUnits("dabAcCaCBAcCcaDA"));        
    }
}
