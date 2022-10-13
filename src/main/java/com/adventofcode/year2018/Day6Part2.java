package com.adventofcode.year2018;


import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Day6Part2 {
    final static String inputFile = "2018/day6.txt";
    final static char MULTI_CLOSEST = '.';
    
    public static void main(String... args) throws IOException {
        Day6Part1 solution = new Day6Part1();
        solution.run();
    }

    void run() throws IOException {
           var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
           var result = largestArea(areaOfEachCoordinates(parseMap(lines)));
           System.out.println("What is the size of the largest area that isn't infinite? " + result);
    }

    int largestArea(Map<Character, Integer> areaOfEachCoordinates) {
        return areaOfEachCoordinates.values().stream().mapToInt(v -> v).max().getAsInt();
    }
    
    Map<Character, Integer> areaOfEachCoordinates(Map<Character, Point> map) {
        var areaOfEachCoordinates = new HashMap<Character, Integer>();

        Area boundary = boundary(map);
        for (int x = boundary.minX; x <= boundary.maxX; x++) {
            for (int y = boundary.minY; y <= boundary.maxY; y++) {
                var closest = closest(new Point(x, y), map);
                if (closest != MULTI_CLOSEST) {
                    areaOfEachCoordinates.put(
                            closest,
                            areaOfEachCoordinates.getOrDefault(closest, 0) + 1);
                }
            }
        }
        
        // remove infinite
        for (int y = boundary.minY - 1; y <= boundary.maxY + 1; y++) {
            areaOfEachCoordinates.remove(closest(new Point(boundary.minX - 1, y), map));
        }
        for (int y = boundary.minY - 1; y <= boundary.maxY + 1; y++) {
            areaOfEachCoordinates.remove(closest(new Point(boundary.maxX + 1, y), map));
        }
        for (int x = boundary.minX - 1; x <= boundary.maxX + 1; x++) {
            areaOfEachCoordinates.remove(closest(new Point(x, boundary.minY - 1), map));
        }
        for (int x = boundary.minX - 1; x <= boundary.maxX + 1; x++) {
            areaOfEachCoordinates.remove(closest(new Point(x, boundary.maxY + 1), map));
        }

        return areaOfEachCoordinates;
    }

    Character closest(Point p, Map<Character, Point> map) {
        Character closest = null;
        int minDistance = Integer.MAX_VALUE;
        for (Map.Entry<Character, Point> entry : map.entrySet()) {
            int distance = distance(p, entry.getValue());
            if (distance == minDistance)
                closest = MULTI_CLOSEST;
            else if (distance < minDistance) {
                closest = entry.getKey();
                minDistance = distance;
            }
                
        }
        return closest;
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
        assertEquals('A', closest(new Point(2, 1), map).charValue());
        assertEquals(MULTI_CLOSEST, closest(new Point(2, 5), map).charValue());        
        assertEquals(17, largestArea(areaOfEachCoordinates(map)));
    }
}
