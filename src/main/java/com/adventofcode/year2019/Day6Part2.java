package com.adventofcode.year2019;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import static org.junit.Assert.assertEquals;

public class Day6Part2 {
    Logger log = LoggerFactory.getLogger(Day6Part2.class);
    final static String inputFile = "2019/day6_1.txt";
    final static int PROGRAM_OUTPUT = -1;

    public static void main(String... args) throws IOException {
        Day6Part2 solution = new Day6Part2();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = minTransfers(lines);
        log.warn("What is the minimum number of orbital transfers required = {}", result);
    }

    int minTransfers(List<String> inputs) {
        var objects = orbitMap(inputs);
        var myPath = pathToCOM(objects.get("YOU"));
        var santaPath = pathToCOM(objects.get("SAN"));
        var remaining = new HashSet<String>(myPath);
        remaining.removeAll(santaPath);
        santaPath.removeAll(myPath);
        return remaining.size() + santaPath.size();
    }

    Set<String> pathToCOM(SpaceObject object) {
        var path = new HashSet<String>();
        while (!object.name.equals("COM")) {
            path.add(object.inOrbitAround.name);
            object = object.inOrbitAround;
        }
        return path;
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
    public void minTransfers_testcases() {
        assertEquals(4, minTransfers(testInput()));
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
        inputs.add("K)YOU");
        inputs.add("I)SAN");
        return inputs;
    }
}
