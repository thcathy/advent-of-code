package com.adventofcode.year2022;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertEquals;

public class Day15Part2 {
    final static String inputFile = "2022/day15.txt";

    public static void main(String... args) throws IOException {
        Day15Part2 solution = new Day15Part2();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = findTuningFrequency(parse(lines), 4000000);
        System.out.println("What is its tuning frequency? " + result);
    }

    long findTuningFrequency(List<SensorBeacon> pairs, int maxCoordinate) {
        Set<Position> beacons = pairs.stream().map(p -> p.beacon).collect(Collectors.toSet());

        for (SensorBeacon pair : pairs) {
            var sensor = pair.sensor;
            var minY = sensor.y - pair.distance - 1;
            if (minY < 0) minY = 0;
            var maxY = sensor.y + pair.distance + 1;
            if (maxY > maxCoordinate) maxY = maxCoordinate;

            for (int y = minY; y <= maxY; y++) {
                var distanceForX = pair.distance - Math.abs(pair.sensor.y - y) + 1;
                var leftX = sensor.x - distanceForX;
                var rightX = sensor.x + distanceForX;
                if (leftX >= 0 && isDistressBeacon(pairs, beacons, new Position(leftX, y)))
                    return findTuningFrequency(leftX, y);

                if (rightX<= maxCoordinate && isDistressBeacon(pairs, beacons, new Position(rightX, y)))
                    return findTuningFrequency(rightX, y);
            }
        }

        throw new RuntimeException();
    }

    private boolean isDistressBeacon(List<SensorBeacon> pairs, Set<Position> beacons, Position pos) {
        boolean valid = true;
        for (SensorBeacon pair : pairs) {
            var distance = distance(pos, pair.sensor);
            if (distance <= pair.distance || beacons.contains(pos)) {
                return false;
            }
        }
        return valid;
    }

    long findTuningFrequency(int x, int y) { return x * 4000000L + y; }

    List<SensorBeacon> parse(List<String> inputs) {
        return inputs.stream().map(this::parse).toList();
    }

    SensorBeacon parse(String input) {
        var inputs = input.split("=");
        int sensorX = Integer.parseInt(inputs[1].substring(0, inputs[1].indexOf(",")));
        int sensorY = Integer.parseInt(inputs[2].substring(0, inputs[2].indexOf(":")));
        int beaconX = Integer.parseInt(inputs[3].substring(0, inputs[3].indexOf(",")));
        int beaconY = Integer.parseInt(inputs[4]);
        return new SensorBeacon(new Position(sensorX, sensorY), new Position(beaconX, beaconY));
    }

    class SensorBeacon {
        Position sensor, beacon;
        int distance;

        SensorBeacon(Position sensor, Position beacon) {
            this.sensor = sensor;
            this.beacon = beacon;
            distance = distance(sensor, beacon);
        }
    }

    record Position(int x, int y) {}

    int distance(Position a, Position b) { return Math.abs(a.x - b.x) + Math.abs(a.y - b.y); }

    @Test
    public void unitTest() throws Exception {
        var lines = Files.readAllLines(Paths.get(ClassLoader.getSystemResource("2022/day15_test.txt").toURI()));
        var pairs = parse(lines);
        assertEquals(56000011, findTuningFrequency(pairs, 20));
    }
}
