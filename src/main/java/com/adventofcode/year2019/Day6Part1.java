package com.adventofcode.year2019;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import static org.junit.Assert.assertEquals;

public class Day6Part1 {
    Logger log = LoggerFactory.getLogger(Day6Part1.class);
    final static String inputFile = "2019/day6_1.txt";
    final static int PROGRAM_OUTPUT = -1;

    public static void main(String... args) throws IOException {
        Day6Part1 solution = new Day6Part1();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var objects = orbitMap(lines);
        var result = totalOrbits(objects.values());
        log.warn("What is the total number of direct and indirect orbits = {}", result);
    }

    Map<String, SpaceObject> orbitMap(List<String> inputs) {
        var objects = new HashMap<String, SpaceObject>();
        for (String input : inputs) {
            var inputArray = input.split("\\)");
            var innerObject = findOrAdd(objects, inputArray[0]);
            var outerObject = findOrAdd(objects, inputArray[1]);
            outerObject.inOrbitAround = innerObject;
        }
        return objects;
    }

    int totalOrbits(Collection<SpaceObject> objects) {
        return objects.stream().mapToInt(this::totalOrbits).sum();
    }

    int totalOrbits(SpaceObject object) {
        int count = 0;
        while (object.inOrbitAround != null) {
            count++;
            object = object.inOrbitAround;
        }
        return count;
    }

    SpaceObject findOrAdd(Map<String, SpaceObject> objects, String name) {
        if (objects.containsKey(name))
            return objects.get(name);
        SpaceObject object = new SpaceObject(name);
        objects.put(name, object);
        return object;
    }

    class SpaceObject {
        String name;
        SpaceObject inOrbitAround;

        public SpaceObject(String name) {
            this.name = name;
        }
    }

    @Test
    public void orbitMap_testcases() {
        var objects = orbitMap(testInput());
        assertEquals(12, objects.size());
    }

    @Test
    public void totalOrbits_testcases() {
        var objects = orbitMap(testInput());
        assertEquals(3, totalOrbits(objects.get("D")));
        assertEquals(7, totalOrbits(objects.get("L")));
        assertEquals(0, totalOrbits(objects.get("COM")));

        assertEquals(42, totalOrbits(objects.values()));
    }

    List<String> testInput() {
        var inputs = new ArrayList<String>();
        inputs.add("COM)B");
        inputs.add("B)C");
        inputs.add("C)D");
        inputs.add("D)E");
        inputs.add("E)F");
        inputs.add("B)G");
        inputs.add("G)H");
        inputs.add("D)I");
        inputs.add("E)J");
        inputs.add("J)K");
        inputs.add("K)L");
        return inputs;
    }
}
