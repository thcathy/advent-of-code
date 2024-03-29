package com.adventofcode.year2018;


import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Day6Part1 {
    final static String inputFile = "2018/day6.txt";
    final static int DISTANCE_THRESHOLD = 10000;
    
    public static void main(String... args) throws IOException {
        Day6Part1 solution = new Day6Part1();
        solution.run();
    }

    void run() throws IOException {
           var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
           var result = safeRegionSize(parseMap(lines), DISTANCE_THRESHOLD);
           System.out.println("What is the size of the region containing all locations which have a total distance to all given coordinates of less than 10000? " + result);
    }

    int largestArea(Map<Character, Integer> areaOfEachCoordinates) {
        return areaOfEachCoordinates.values().stream().mapToInt(v -> v).max().getAsInt();
    }
    
    int safeRegionSize(Map<Character, Point> map, int distanceThreshold) {
        int safeRegionSize = 0;
        Area boundary = boundary(map);
        for (int x = boundary.minX; x <= boundary.maxX; x++) {
            for (int y = boundary.minY; y <= boundary.maxY; y++) {
                if (totalDistance(new Point(x, y), map) < distanceThreshold)
                    safeRegionSize++;
            }
        }       
        return safeRegionSize;
    }

    int totalDistance(Point p, Map<Character, Point> map) {
        return map.values().stream()
                .mapToInt(v -> distance(p, v))
                .sum();
    }

    Area boundary(Map<Character, Point> map) {
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
        for (Point p : map.values()) {
            minX = Math.min(minX, p.x);
            minY = Math.min(minY, p.y);
            maxX = Math.max(maxX, p.x);
            maxY = Math.max(maxY, p.y);
        }
        return new Area(minX, maxX, minY, maxY);
    }

    Map<Character, Point> parseMap(List<String> inputs) {
        var map = new HashMap<Character, Point>();
        for (int i = 0; i < inputs.size(); i++) {
            var values = inputs.get(i).split(",");
            map.put((char)(i+65), new Point(Integer.parseInt(values[0].trim()), Integer.parseInt(values[1].trim())));
        }
        return map;
    }
    
    int distance(Point a, Point b) { return Math.abs(a.x - b.x) + Math.abs(a.y - b.y); }

    record Point(int x, int y) {}

    record Area(int minX, int maxX, int minY, int maxY) {}
        
    @Test
    public void unitTest() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource("2018/day6_test.txt"), Charsets.UTF_8);
        var map = parseMap(lines);
        assertEquals(6, map.size());
        var boundary = boundary(map);
        assertEquals(1, boundary.minX);
        assertEquals(1, boundary.minY);
        assertEquals(8, boundary.maxX);
        assertEquals(9, boundary.maxY);        
        assertEquals(16, safeRegionSize(map, 32));
    }
}
