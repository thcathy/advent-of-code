package com.adventofcode.year2017;


import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Day20Part2 {
    final static String inputFile = "2017/day20_1.txt";
    
    public static void main(String... args) throws IOException {
        Day20Part2 solution = new Day20Part2();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = particlesLeft(parseParticles(lines));
        System.out.println("ow many particles are left after all collisions are resolved? " + result);
    }

    List<Particle> parseParticles(List<String> inputs) {
        return inputs.stream().map(Particle::new).toList();
    }

    int particlesLeft(List<Particle> particles) {        
        for (int i = 0; i < 10_000; i++) {
            Map<Coordinate, List<Particle>> occupiedPosition = new HashMap<>();
            for (Particle p : particles) {
                p.move();
                if (occupiedPosition.containsKey(p.position)) {
                    occupiedPosition.get(p.position).add(p);
                } else {
                    var list = new ArrayList<Particle>();
                    list.add(p);
                    occupiedPosition.put(p.position, list);
                }
            }
            particles = occupiedPosition.values().stream()
                            .filter(l -> l.size() == 1)
                            .map(l -> l.get(0)).toList();
        }
        return particles.size();
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

        void move() {
            velocity = velocity.add(acceleration);
            position = position.add(velocity);
        }
    }
    
    @Test
    public void unitTest() throws IOException {
        var particle = new Particle("p=< 3,0,0>, v=< 2,0,0>, a=<-1,0,0>");
        assertEquals(new Coordinate(3, 0, 0), particle.position);
        assertEquals(new Coordinate(2, 0, 0), particle.velocity);
        assertEquals(new Coordinate(-1, 0, 0), particle.acceleration);

        particle.move();
        assertEquals(new Coordinate(4, 0, 0), particle.position);
        assertEquals(new Coordinate(1, 0, 0), particle.velocity);
        assertEquals(new Coordinate(-1, 0, 0), particle.acceleration);        
    }
}
