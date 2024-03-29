package com.adventofcode.year2018;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Day3Part1 {
    final static int FABRIC_SIZE = 1000;

    Logger log = LoggerFactory.getLogger(Day3Part1.class);
    final static String inputFile = "2018/day3.txt";
    

    public static void main(String... args) throws IOException {
        Day3Part1 solution = new Day3Part1();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = countOverlap(lines);
        log.warn("How many square inches of fabric are within two or more claims? {}", result);
    }

    int countOverlap(List<String> inputs) {
        List<Claim> claims = inputs.stream().map(this::parseClaim).toList();
        int[][] fabric = new int[FABRIC_SIZE][FABRIC_SIZE];
        for (Claim claim : claims) {
            markClaim(fabric, claim);
        }
        return overlapSquares(fabric);
    }

    int overlapSquares(int[][] fabric) {
        int count = 0;
        for (int x=0; x<fabric.length; x++) {
            for (int y=0; y<fabric.length; y++) {
                if (fabric[x][y] > 1)
                    count++;
            }
        }
        return count;
    }

    void markClaim(int[][] fabric, Claim claim) {
        int rightEdge = claim.leftEdge + claim.wide;
        int bottomEdge = claim.topEdge + claim.tall;
        for (int x=claim.leftEdge; x<rightEdge; x++) {
            for (int y=claim.topEdge; y<bottomEdge; y++) {
                fabric[x][y] = fabric[x][y]+1;
            }
        }
    }

    Claim parseClaim(String input) {
        String[] inputs = input.split(" ");
        String id = inputs[0].substring(1);
        String[] edges = inputs[2].split(",");
        int leftEdge = Integer.parseInt(edges[0]);
        int topEdge = Integer.parseInt(edges[1].replace(":", ""));
        String[] area = inputs[3].split("x");
        return new Claim(id, leftEdge, topEdge, Integer.parseInt(area[0]), Integer.parseInt(area[1]));
    }

    record Claim(String id, int leftEdge, int topEdge, int wide, int tall) {}

    @Test
    public void unitTest() throws IOException {
        assertEquals(new Claim("3", 1, 2, 3, 4), parseClaim("#3 @ 1,2: 3x4"));

        var lines = Resources.readLines(ClassLoader.getSystemResource("2018/day3_test.txt"), Charsets.UTF_8);
        assertEquals(4, countOverlap(lines));
    }
}
