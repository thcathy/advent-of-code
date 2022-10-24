package com.adventofcode.year2018;


import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class Day11Part2 {
    final static int gridSerialNumber = 9445;

    public static void main(String... args) throws IOException {
        Day11Part2 solution = new Day11Part2();
        solution.run();
    }

    void run() throws IOException {        
        var result = largestPowerSquare(gridSerialNumber);
        System.out.println("What is the X,Y,size identifier of the square with the largest total power? " + result);
    }

    String largestPowerSquare(int gridSerialNumber) {
        int gridSize = 300;
        int[][] grid = gridPower(gridSerialNumber, gridSize);
        int maxPower = Integer.MIN_VALUE;
        String maxPowerCoordinate = "";
        for (int shellSize=1; shellSize<=gridSize; shellSize++) {
            for (int x=0; x<gridSize-shellSize; x++) {
                for (int y=0; y<gridSize-shellSize; y++) {
                    int power = powerOfFuelShell(x, y, grid, shellSize);
                    if (power > maxPower) {
                        maxPower = power;
                        maxPowerCoordinate = (x+1) + "," + (y+1)  + "," + shellSize;
                    }
                }
            }
        }
        return maxPowerCoordinate;
    }

    int powerOfFuelShell(int x, int y, int[][] grid, int shellSize) {
        int power = 0;
        for (int i=x; i<x+shellSize; i++) {
            for (int j=y; j<y+shellSize; j++) {
                power += grid[i][j];
            }
        }
        return power;
    }

    int[][] gridPower(int gridSerialNumber, int gridSize) {
        int[][] grid = new int[gridSize][gridSize];
        for (int x=1; x<=gridSize; x++) {
            for (int y=1; y<gridSize; y++) {
                grid[x-1][y-1] = powerLevel(x, y, gridSerialNumber);
            }
        }
        return grid;
    }

    int powerLevel(int x, int y, int gridSerialNumber) {
        int rackId = x + 10;
        int powerLevel = (rackId * y + gridSerialNumber) * rackId;
        powerLevel = (powerLevel / 100) % 10;
        return powerLevel - 5;
    }
        
    @Test
    public void unitTest() throws IOException {        
        assertEquals(4, powerLevel(3, 5, 8));
        assertEquals(-5, powerLevel(122, 79, 57));
        assertEquals(0, powerLevel(217, 196, 39));
        assertEquals(4, powerLevel(101, 153, 71));
        assertEquals("90,269,16", largestPowerSquare(18));
        assertEquals("232,251,12", largestPowerSquare(42));
    }
}
