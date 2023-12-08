package com.adventofcode.year2023;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;

public class Day5Part1 {
    Logger log = LoggerFactory.getLogger(Day5Part1.class);
    final static String inputFile = "2023/day5.txt";

    public static void main(String... args) throws IOException {
        Day5Part1 solution = new Day5Part1();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var almanac = parseAlmanac(lines);
        var result = lowestLocation(almanac);
        log.warn("What is the lowest location number that corresponds to any of the initial seed numbers? {}", result);
    }

    long lowestLocation(Almanac almanac) {
        return almanac.seeds.stream()
                .mapToLong(s -> findLocation(almanac, s))
                .min().getAsLong();
    }

    long findLocation(Almanac almanac, long seed) {
        long mappedValue = getValue(almanac.seedToSoil, seed);
        mappedValue = getValue(almanac.soilToFertilizer, mappedValue);
        mappedValue = getValue(almanac.fertilizerToWater, mappedValue);
        mappedValue = getValue(almanac.waterToLight, mappedValue);
        mappedValue = getValue(almanac.lightToTemperature, mappedValue);
        mappedValue = getValue(almanac.temperatureToHumidity, mappedValue);
        mappedValue = getValue(almanac.humidityToLocation, mappedValue);
        return mappedValue;
    }

    long getValue(Map map, long key) {
        var inRange = map.ranges.stream().filter(r -> r.source <= key && key < r.source + r.length).findFirst();
        return inRange.map(range -> key - range.source + range.destination).orElse(key);
    }

    Almanac parseAlmanac(List<String> inputs) {
        var seeds = Arrays.stream(inputs.get(0).split(": ")[1].split(" "))
                        .map(Long::parseLong).toList();

        int lineNumber = 3;
        var seedToSoil = parseMap(lineNumber, inputs);
        lineNumber += seedToSoil.ranges.size() + 2;

        var soilToFertilizer = parseMap(lineNumber, inputs);
        lineNumber += soilToFertilizer.ranges.size() + 2;

        var fertilizerToWater = parseMap(lineNumber, inputs);
        lineNumber += fertilizerToWater.ranges.size() + 2;

        var waterToLight = parseMap(lineNumber, inputs);
        lineNumber += waterToLight.ranges.size() + 2;

        var lightToTemperature = parseMap(lineNumber, inputs);
        lineNumber += lightToTemperature.ranges.size() + 2;

        var temperatureToHumidity = parseMap(lineNumber, inputs);
        lineNumber += temperatureToHumidity.ranges.size() + 2;

        var humidityToLocation = parseMap(lineNumber, inputs);

        return new Almanac(seeds, seedToSoil, soilToFertilizer ,fertilizerToWater, waterToLight, lightToTemperature, temperatureToHumidity, humidityToLocation);
    }

    Map parseMap(int lineNumber, List<String> inputs) {
        var ranges = new ArrayList<Range>();
        Function<Integer, Boolean> hasContent = (l) -> l < inputs.size() && StringUtils.isNotBlank(inputs.get(l));

        while (hasContent.apply(lineNumber)) {
            var line = inputs.get(lineNumber).split(" ");
            ranges.add(new Range(Long.parseLong(line[1]), Long.parseLong(line[0]), Long.parseLong(line[2])));
            lineNumber++;
        }
        return new Map(ranges);
    }
    record Almanac(List<Long> seeds,
                   Map seedToSoil,
                   Map soilToFertilizer,
                   Map fertilizerToWater,
                   Map waterToLight,
                   Map lightToTemperature,
                   Map temperatureToHumidity,
                   Map humidityToLocation) {}

    record Map(List<Range> ranges) {}

    record Range(long source, long destination, long length) {}

    
    @Test
    public void unitTest() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource("2023/day5_test.txt"), Charsets.UTF_8);
        var almanac = parseAlmanac(lines);
        assertEquals(35, lowestLocation(almanac));
    }
}
