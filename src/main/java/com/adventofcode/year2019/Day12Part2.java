package com.adventofcode.year2019;


import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;

public class Day12Part2 {
    Logger log = LoggerFactory.getLogger(Day12Part2.class);
    final static String inputFile = "2019/day10_1.txt";

    public static void main(String... args) throws IOException {
        Day12Part2 solution = new Day12Part2();
        solution.run();
    }

    void run() throws IOException {
        var moons = List.of(
                new Moon(new Dimensions(5, 4, 4)),
                new Moon(new Dimensions(-11, -11, -3)),
                new Moon(new Dimensions(0, 7, 0)),
                new Moon(new Dimensions(-13, 2, 10))
        );
        var result = stepTillAllDimensionsEqualsToBeginning(moons);
        log.warn("What is the total energy in the system = {}", result);
    }

    long stepTillAllDimensionsEqualsToBeginning(List<Moon> moons) {
        long stepOnX = stepTillSingleDimensionEqualsToBeginning(moons, (moon) -> new Moon(new Dimensions(moon.position.x, 0, 0)), (d) -> d.x);
        long stepOnY = stepTillSingleDimensionEqualsToBeginning(moons, (moon) -> new Moon(new Dimensions(0, moon.position.y, 0)), (d) -> d.y);
        long stepOnZ = stepTillSingleDimensionEqualsToBeginning(moons, (moon) -> new Moon(new Dimensions(0, 0, moon.position.z)), (d) -> d.z);

        return lcm(lcm(stepOnX, stepOnY), stepOnZ);
    }

    long lcm(long number1, long number2) {
        if (number1 == 0 || number2 == 0) {
            return 0;
        }
        long absNumber1 = Math.abs(number1);
        long absNumber2 = Math.abs(number2);
        long absHigherNumber = Math.max(absNumber1, absNumber2);
        long absLowerNumber = Math.min(absNumber1, absNumber2);
        long lcm = absHigherNumber;
        while (lcm % absLowerNumber != 0) {
            lcm += absHigherNumber;
        }
        return lcm;
    }

    long stepTillSingleDimensionEqualsToBeginning(List<Moon> originMoons, Function<Moon, Moon> moonWithSingleDimension, Function<Dimensions, Integer> extractDimension) {
        var moons = originMoons.stream().map(m -> moonWithSingleDimension.apply(m)).collect(Collectors.toList());
        long step = 0;
        do {
            step++;
            moveMoons(moons);
        }
        while (!isSamePosition(originMoons, moons, extractDimension));
        return step + 1;
    }

    private boolean isSamePosition(List<Moon> originMoons, List<Moon> moons, Function<Dimensions, Integer> extractDimension) {
        return IntStream.range(0, originMoons.size()).boxed()
                .allMatch(i ->
                        extractDimension.apply(originMoons.get(i).position) == extractDimension.apply(moons.get(i).position)
                );
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
        IntStream.rangeClosed(1, 13).forEach(i -> moveMoons(moons));
        assertEquals(2, moons.get(0).position.x);
    }

    @Test
    public void stepTillMoonsStopMoving_testcases() {
        var moons1 = List.of(
                new Moon(new Dimensions(-1, 0, 2)),
                new Moon(new Dimensions(2, -10, -7)),
                new Moon(new Dimensions(4, -8, 8)),
                new Moon(new Dimensions(3, 5, -1))
        );
        assertEquals(2772, stepTillAllDimensionsEqualsToBeginning(moons1));


        var moons2 = List.of(
                new Moon(new Dimensions(-8, -10, 0)),
                new Moon(new Dimensions(5, 5, 10)),
                new Moon(new Dimensions(2, -7, 3)),
                new Moon(new Dimensions(9, -8, -3))
        );
        assertEquals(4686774924l, stepTillAllDimensionsEqualsToBeginning(moons2));
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Dimensions that = (Dimensions) o;
            return x == that.x &&
                    y == that.y &&
                    z == that.z;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, z);
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
