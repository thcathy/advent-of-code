package com.adventofcode.year2017;


import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Day20Part1 {
    final static String inputFile = "2017/day20_1.txt";
    
    public static void main(String... args) throws IOException {
        Day20Part1 solution = new Day20Part1();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = particleClosestToOrigin(parseParticles(lines));
        System.out.println("Which particle will stay closest to position <0,0,0> in the long term? " + result);
    }

    List<Particle> parseParticles(List<String> inputs) {
        return inputs.stream().map(Particle::new).toList();
    }

    int particleClosestToOrigin(List<Particle> particles) {
        int closestParticleIndex = -1;
        int smallestAcceleration = Integer.MAX_VALUE;
        int closestDistance = Integer.MAX_VALUE;        
        for (int i = 0; i < particles.size(); i++) {
            var p = particles.get(i);
            if (p.acceleration.sum() < smallestAcceleration) {
                if (smallestAcceleration == 0 && p.distanceFromOrigin() >= closestDistance) {
                    continue;
                }
                closestParticleIndex = i;
                closestDistance = p.distanceFromOrigin();
                smallestAcceleration = p.acceleration.sum();
            }
        }
        return closestParticleIndex;
    }

    record Coordinate(int x, int y, int z) {
        static Coordinate parse(String input) {
            String[] stringValues = input.substring(input.indexOf("<") + 1, input.indexOf(">")).split(",");
            return new Coordinate(
                    Integer.valueOf(stringValues[0].trim()),
                    Integer.valueOf(stringValues[1].trim()),
                    Integer.valueOf(stringValues[2].trim()));
        }
        
        Coordinate add(Coordinate other) {
            return new Coordinate(x + other.x, y + other.y, z + other.z);
        }

        int sum() {
            return Math.abs(x) + Math.abs(y) + Math.abs(z);
        }
    } 

    class Particle {
        Coordinate position;
        Coordinate velocity;
        Coordinate acceleration;

        Particle(String input) {
            String[] strings = input.split(", ");
            position = Coordinate.parse(strings[0]);
            velocity = Coordinate.parse(strings[1]);
            acceleration = Coordinate.parse(strings[2]);
        }

        int distanceFromOrigin() {
            return Math.abs(position.x) + Math.abs(position.y) + Math.abs(position.z);
        }
    }
    
    @Test
    public void unitTest() throws IOException {
        var particle = new Particle("p=< 3,0,0>, v=< 2,0,0>, a=<-1,0,0>");
        assertEquals(new Coordinate(3, 0, 0), particle.position);
        assertEquals(new Coordinate(2, 0, 0), particle.velocity);
        assertEquals(new Coordinate(-1, 0, 0), particle.acceleration);
    }
}
