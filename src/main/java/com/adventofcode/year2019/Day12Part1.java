package com.adventofcode.year2019;


import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;

public class Day12Part1 {
    Logger log = LoggerFactory.getLogger(Day12Part1.class);
    final static String inputFile = "2019/day10_1.txt";

    public static void main(String... args) throws IOException {
        Day12Part1 solution = new Day12Part1();
        solution.run();
    }

    void run() throws IOException {
        var moons = List.of(
                new Moon(new Dimensions(5, 4, 4)),
                new Moon(new Dimensions(-11, -11, -3)),
                new Moon(new Dimensions(0, 7, 0)),
                new Moon(new Dimensions(-13, 2, 10))
        );
        var result = totalEnergyAfterStep(moons, 1000);
        log.warn("What is the total energy in the system = {}", result);
    }

    int totalEnergyAfterStep(List<Moon> moons, int step) {
        IntStream.range(0, step).forEach(i -> moveMoons(moons));
        return moons.stream().mapToInt(Moon::totalEnergy).sum();
    }

    void moveMoons(List<Moon> moons) {
        for (int i = 0; i < moons.size() - 1; i++) {
            for (int j = i+1; j < moons.size(); j++) {
                applyGravity(moons.get(i), moons.get(j));
            }
        }
        moons.forEach(Moon::applyVelocity);
    }


    @Test
    public void moveMoons_testcases() {
        var moons = List.of(
                new Moon(new Dimensions(-1, 0, 2)),
                new Moon(new Dimensions(2, -10, -7)),
                new Moon(new Dimensions(4, -8, 8)),
                new Moon(new Dimensions(3, 5, -1))
        );
        moveMoons(moons);
        assertEquals(2, moons.get(0).position.x);
        assertEquals(-1, moons.get(0).position.y);
        assertEquals(1, moons.get(0).position.z);
        assertEquals(3, moons.get(0).velocity.x);
        assertEquals(-1, moons.get(0).velocity.y);
        assertEquals(-1, moons.get(0).velocity.z);

        for (int i = 2; i <= 10; i++)
            moveMoons(moons);

        var totalEnergy = moons.stream().mapToInt(Moon::totalEnergy).sum();
        assertEquals(179, totalEnergy);
    }

    @Test
    public void totalEnergyAfterStep_testcases() {
        var moons1 = List.of(
                new Moon(new Dimensions(-1, 0, 2)),
                new Moon(new Dimensions(2, -10, -7)),
                new Moon(new Dimensions(4, -8, 8)),
                new Moon(new Dimensions(3, 5, -1))
        );
        assertEquals(179, totalEnergyAfterStep(moons1, 10));


        var moons2 = List.of(
                new Moon(new Dimensions(-8, -10, 0)),
                new Moon(new Dimensions(5, 5, 10)),
                new Moon(new Dimensions(2, -7, 3)),
                new Moon(new Dimensions(9, -8, -3))
        );
        var result = totalEnergyAfterStep(moons2, 100);
        assertEquals(1940, result);
    }

    void applyGravity(Moon moon1, Moon moon2) {
        try {
            applyGravity(moon1, moon2, "x");
            applyGravity(moon1, moon2, "y");
            applyGravity(moon1, moon2, "z");
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    void applyGravity(Moon moon1, Moon moon2, String fieldName) throws Exception {
        Field field = Dimensions.class.getDeclaredField(fieldName);
        var position1 = (int) field.get(moon1.position);
        var position2 = (int) field.get(moon2.position);
        var velocity1 = (int) field.get(moon1.velocity);
        var velocity2 = (int) field.get(moon2.velocity);
        if (position1 > position2) {
            field.set(moon1.velocity, velocity1 - 1);
            field.set(moon2.velocity, velocity2 + 1);
        } else if (position2 > position1) {
            field.set(moon1.velocity, velocity1 + 1);
            field.set(moon2.velocity, velocity2 - 1);
        }
    }

    class Dimensions {
        int x = 0, y = 0, z = 0;

        public Dimensions() {}

        public Dimensions(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        int absoluteSum() { return Math.abs(x) + Math.abs(y) + Math.abs(z); }

        @Override
        public String toString() {
            return new StringJoiner(", ", "[", "]")
                    .add("x=" + x).add("y=" + y).add("z=" + z).toString();
        }
    }

    class Moon {
        Dimensions position;
        Dimensions velocity = new Dimensions();

        public Moon(Dimensions position) {
            this.position = position;
        }

        void applyVelocity() {
            position.x += velocity.x;
            position.y += velocity.y;
            position.z += velocity.z;
        }

        int potentialEnergy() { return position.absoluteSum(); }

        int kineticEnergy() { return velocity.absoluteSum(); }

        int totalEnergy() { return potentialEnergy() * kineticEnergy(); }

        @Override
        public String toString() {
            return new StringJoiner(", ", "[", "]")
                    .add("pos=" + position).add("velocity=" + velocity)
                    .toString();
        }
    }
}
