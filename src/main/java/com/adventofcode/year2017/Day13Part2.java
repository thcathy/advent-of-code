package com.adventofcode.year2017;


import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertEquals;

public class Day13Part2 {
    final static String inputFile = "2017/day13_1.txt";

    public static void main(String... args) throws IOException {
        Day13Part2 solution = new Day13Part2();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = delayToPassFirewall(parseEachLayerCatchTime(lines));
        System.out.printf("What is the fewest number of picoseconds that you need to delay? %d %n", result);
    }

    int delayToPassFirewall(Map<Integer, Integer> eachLayerCatchTime) {
        int startPicosecond = 0;
        boolean passed;
        do {
            passed = true;
            for (Map.Entry<Integer, Integer> layer : eachLayerCatchTime.entrySet()) {
                if ((layer.getKey() + startPicosecond) % layer.getValue() == 0) {
                    passed = false;
                    break;
                }
            }
            startPicosecond++;
        } while (!passed);
        return startPicosecond - 1;
    }
    int catchTime(int range) {
        return 2 * range - 2;
    }

    Map<Integer, Integer> parseEachLayerCatchTime(List<String> inputs) {
        return inputs.stream()
                .map(s -> s.split(": "))
                .collect(Collectors.toMap(
                        arr -> Integer.parseInt(arr[0]),
                        arr -> catchTime(Integer.parseInt(arr[1])))
                );
    }

    @Test
    public void testcase() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        assertEquals(10, delayToPassFirewall(parseEachLayerCatchTime(lines)));
    }
}
