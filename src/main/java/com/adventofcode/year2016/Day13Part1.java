package com.adventofcode.year2016;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Day13Part1 {
    Logger log = LoggerFactory.getLogger(Day13Part1.class);

    public static void main(String... args) throws IOException {
        Day13Part1 solution = new Day13Part1();
        solution.firstStar();
    }

    void firstStar() throws IOException {
        int constant = 1350;
        int steps = fewestStepsToReach(31, 39, constant);
        log.warn("First star - fewest number of steps required? {}", steps);
    }

    int fewestStepsToReach(int DestinationX, int DestinationY, int constant) {
        Path start = new Path(0, 1, 1);
        Set<String> visited = new HashSet<>();
        var paths = List.of(start);
        while (!paths.isEmpty()) {
            var isReach = paths.stream().filter(p -> p.x == DestinationX && p.y == DestinationY).findFirst();
            if (isReach.isPresent())
                return isReach.get().step;
            paths = getAllValidNextSteps(constant, paths, visited);
            log.debug("step {}, paths {}", paths.get(0).step, paths.size());
        }
        return -1;
    }

    private List<Path> getAllValidNextSteps(int constant, List<Path> paths, Set<String> visited) {
        return paths.stream()
                .flatMap(p -> generateNextSteps(p))
                .filter(p -> p.isValid())
                .filter(p -> isOpenSpace(p.x, p.y, constant))
                .filter(p -> !visited.contains(p.hash()))
                .map(p -> {visited.add(p.hash()); return p;})
                .collect(Collectors.toList());
    }

    class Path {
        int step, x, y;

        public Path(int step, int x, int y) {
            this.step = step;
            this.x = x;
            this.y = y;
        }

        public String hash() { return x + "," + y; }

        public boolean isValid() { return x >= 0 && y >= 0; }
    }

    Stream<Path> generateNextSteps(Path input) {
        int step = input.step + 1;
        return Stream.of(
                new Path(step, input.x + 1, input.y),
                new Path(step, input.x - 1, input.y),
                new Path(step, input.x, input.y + 1),
                new Path(step, input.x, input.y - 1)
        );
    }

    boolean isOpenSpace(int x, int y, int constant) {
        int sum = x * x + 3 * x + 2 * x * y + y + y * y + constant;
        long numberOfOne = Integer.toBinaryString(sum).chars().filter(i -> i == '1').count();
        return numberOfOne % 2 == 0;
    }

    @Test
    public void test_fewestStepsToReach() {
        assertEquals(11, fewestStepsToReach(7, 4, 10));
    }

    @Test
    public void test_isOpenSpace() {
        assertTrue(isOpenSpace(0, 0, 10));
        assertFalse(isOpenSpace(1, 0, 10));
        assertFalse(isOpenSpace(6, 3, 10));
    }
}