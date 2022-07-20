package com.adventofcode.year2017;


import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

public class Day14Part2 {
    final static String puzzleInput = "amgozmfv";

    public static void main(String... args) throws IOException {
        Day14Part2 solution = new Day14Part2();
        solution.run();
    }

    void run() throws IOException {
        var result = totalRegionsOfUsedSpaces(usedSpacesFromInput(puzzleInput));
        System.out.printf("How many regions are present given your key string? %s %n", result);
    }
   
    int totalRegionsOfUsedSpaces(Set<Point> usedSpaces) {
        int regions = 0;
        while (usedSpaces.size() > 0) {
            Point p = usedSpaces.stream().findFirst().get();
            Set<Point> pointsInSameRegion = pointsInSameRegion(p, usedSpaces);
            regions++;
            usedSpaces.removeAll(pointsInSameRegion);
        }
        return regions;
    }

    Set<Point> pointsInSameRegion(Point p, Set<Point> usedSpaces) {
        Set<Point> pointsInRegions = new HashSet<>();
        List<Point> possiblePoints = new LinkedList<>();
        possiblePoints.addAll(p.neighbors());
        pointsInRegions.add(p);
        while (!possiblePoints.isEmpty()) {
            Point point = possiblePoints.remove(0);
            if (usedSpaces.contains(point) && !pointsInRegions.contains(point)) {                
                pointsInRegions.add(point);
                possiblePoints.addAll(point.neighbors());
            }
        }
        return pointsInRegions;
    }

    Set<Point> usedSpacesFromInput(String input) {
        Set<Point> usedSpaces = new HashSet<>();
        for (int i = 0; i < 128; i++) {            
            String hashedString = KnotHash.hash(input + "-" + i);
            String binaryString = StringUtils.leftPad(new BigInteger(hashedString, 16).toString(2), 128, "0");           
            for (int j = 0; j < binaryString.length(); j++) {
                if (binaryString.charAt(j) == '1') {
                    usedSpaces.add(new Point(i, j));
                }
            }
        }
        return usedSpaces;
    }
    
    record Point(int x, int y) {
        List<Point> neighbors() {
            return List.of(
                new Point(x-1, y),
                new Point(x+1, y),
                new Point(x, y-1),
                new Point(x, y+1)
            );
        }
    }

    static class KnotHash {
        static String hash(String input) {
            int[] inputArray = ArrayUtils.addAll(input.chars().toArray(), 17, 31, 73, 47, 23);
            int[] sparseHash = sparseHash(inputArray);
            return toHex(denseHash(sparseHash));
        }
    
        static int[] sparseHash(int[] inputs) {
            int[] intArray = IntStream.rangeClosed(0, 255).toArray();
            int skipSize = 0;
            int currentPosition = 0;
    
            for (int i=0; i<64; i++) {
                for (int input : inputs) {
                    int start = currentPosition;
                    int end = start + input - 1;
                    while (start < end) {
                        swap(intArray, start, end);
                        start++;
                        end--;
                    }
                    currentPosition = (currentPosition + input + skipSize) % intArray.length;
                    skipSize++;
                }
            }
    
            return intArray;
        }
    
        static void swap(int[] array, int pos1, int pos2) {
            pos1 = pos1 % array.length;
            pos2 = pos2 % array.length;
            int temp = array[pos1];
            array[pos1] = array[pos2];
            array[pos2] = temp;
        }
    
        static int[] denseHash(int[] input) {
            int[] result = new int[input.length / 16];
            for (int i=0; i < result.length; i++) {
                int start = i * 16;
                int value = input[start];
                for (int j=1; j < 16; j++) {
                    value = value ^ input[start + j];
                }
                result[i] = value;
            }
            return result;
        }
    
        static String toHex(int[] values) {
            StringBuilder sb = new StringBuilder();
            for (int v : values) {
                sb.append(toHex(v));
            }
            return sb.toString();
        }
    
        static String toHex(int value) {
            var string = Integer.toHexString(value);
            return string.length() == 1 ? "0" + string : string;
        }
    }    

    @Test
    public void unitTest() {
        assertEquals(1242, totalRegionsOfUsedSpaces(usedSpacesFromInput("flqrgnkx")));
    }
}
