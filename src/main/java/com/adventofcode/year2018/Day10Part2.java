package com.adventofcode.year2018;


import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;

public class Day10Part2 {
    final static String inputFile = "2018/day10.txt";
    final static int maxPrintHeight = 20;
    final static int maxSecond = 100000;
        
    public static void main(String... args) throws IOException {
        Day10Part2 solution = new Day10Part2();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        System.out.println("exactly how many seconds would they have needed to wait for that message to appear? " + "found the answer from below...");
        runForSecond(parseLights(lines), maxSecond);
    }

    void runForSecond(List<Light> lights, int second) {
        for (int i=1; i<=second; i++) {
            for (Light l : lights) l.move();
            printMap(lights, i);
        }
    }
    
    void printMap(List<Light> lights, int second) {
        int minX = Integer.MAX_VALUE; int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE; int maxY = Integer.MIN_VALUE;
        Set<Position> lightOn = new HashSet<>();
        for (Light light: lights) {
            lightOn.add(light.position);
            minX = Math.min(minX, light.position.x);
            minY = Math.min(minY, light.position.y);
            maxX = Math.max(maxX, light.position.x);
            maxY = Math.max(maxY, light.position.y);
        }
        printMap(lightOn, minX, maxX, minY, maxY, second);
    }

    void printMap(Set<Position> lights, int minX, int maxX, int minY, int maxY, int second) {
        if (maxY - minY > maxPrintHeight) {
            return;
        }

        System.out.printf(">>>>>>>>>>>>>>>>>> After %d second %n", second);
        for (int y=minY; y<=maxY; y++) {
            for (int x=minX; x<=maxX; x++) {
                System.out.print(lights.contains(new Position(x, y)) ? '#' : '.');
            }
            System.out.println();
        }
    }

    List<Light> parseLights(List<String> inputs) {
        return inputs.stream().map(this::parseLight).toList();
    }

    Light parseLight(String input) {
        Pattern p = Pattern.compile("-?\\d+");
        Matcher m = p.matcher(input);
        List<Integer> values = new ArrayList<>();
        while (m.find()) {
            values.add(Integer.parseInt(m.group()));
        }
        return new Light(
            new Position(values.get(0), values.get(1)), 
            values.get(2), values.get(3));
    }

    record Position(int x, int y) {}

    class Light {
        Position position;
        final int xVelocity, yVelocity;
        
        Light(Position position, int xVelocity, int yVelocity) {
            this.position = position;
            this.xVelocity = xVelocity;
            this.yVelocity = yVelocity;
        }

        void move() {
            int newX = position.x + xVelocity;
            int newY = position.y + yVelocity;
            position = new Position(newX, newY);
        }
    }
            
    @Test
    public void unitTest() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource("2018/day10_test.txt"), Charsets.UTF_8);
        var lights = parseLights(lines);
        assertEquals(9, lights.get(0).position.x);
        assertEquals(1, lights.get(0).position.y);
        assertEquals(-1, lights.get(1).xVelocity);
        assertEquals(0, lights.get(1).yVelocity);
        runForSecond(lights, 5);
    }
}
