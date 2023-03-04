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

public class Day15Part1 {
    final static String inputFile = "2022/day15.txt";

    public static void main(String... args) throws IOException {
        Day15Part1 solution = new Day15Part1();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = positionsCannotContainBeacon(parse(lines), 2000000);
        System.out.println("In the row where y=2000000, how many positions cannot contain a beacon? " + result);
    }

    int positionsCannotContainBeacon(List<SensorBeacon> pairs, int y) {
        int count = 0;
        Set<Position> beacons = pairs.stream().map(p -> p.beacon).collect(Collectors.toSet());

        for (int x = -5000000; x < 5000000; x++) {
            for (SensorBeacon pair : pairs) {
                var pos = new Position(x, y);
                var distance = distance(pos, pair.sensor);
                if (distance <= pair.distance && !beacons.contains(pos)) {
                    count++;
                    break;
                }
            }
        }
        return count;
    }

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
        assertEquals(14, pairs.size());
        assertEquals(new Position(2, 18), pairs.get(0).sensor);
        assertEquals(new Position(15, 3), pairs.get(2).beacon);
        assertEquals(26, positionsCannotContainBeacon(pairs, 10));
    }
}
