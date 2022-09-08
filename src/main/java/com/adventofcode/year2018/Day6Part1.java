package com.adventofcode.year2018;


import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Day6Part1 {
    final static String inputFile = "2018/day6.txt";
    
    public static void main(String... args) throws IOException {
        Day6Part1 solution = new Day6Part1();
        solution.run();
    }

    void run() throws IOException {
    //    var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
    //    var result = travel(parseMap(lines));
    //    System.out.println("What letters will it see (in the order it would see them) if it follows the path? " + result);
    }

    // Map<Integer, Point> parseMap(List<String> inputs) {
        
    // }
    
    int distance(Point a, Point b) { return Math.abs(a.x - b.x) + Math.abs(a.y - b.y); }

    record Point(int x, int y) {}
    
    @Test
    public void unitTest() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource("2018/day6_test.txt"), Charsets.UTF_8);
        
    }
}
