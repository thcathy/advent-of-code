package com.adventofcode.year2023;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.LongStream;

import static org.junit.Assert.assertEquals;

public class Day5Part2 {
    Logger log = LoggerFactory.getLogger(Day5Part2.class);
    final static String inputFile = "2023/day5.txt";

    public static void main(String... args) throws IOException {
        Day5Part2 solution = new Day5Part2();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var almanac = parseAlmanac(lines);
        var result = lowestLocation(almanac);
        log.warn("What is the lowest location number that corresponds to any of the initial seed numbers? {}", result);
    }

    long lowestLocation(Almanac almanac) {
        return LongStream.rangeClosed(0, Long.MAX_VALUE)
                .filter(v -> isCorrespondToSeed(almanac, v))
                .findFirst().getAsLong();
    }

    boolean isCorrespondToSeed(Almanac almanac, long location) {
        long mappedKey = almanac.humidityToLocation.getKey(location);
        mappedKey = almanac.temperatureToHumidity.getKey(mappedKey);
        mappedKey = almanac.lightToTemperature.getKey(mappedKey);
        mappedKey = almanac.waterToLight.getKey(mappedKey);
        mappedKey = almanac.fertilizerToWater.getKey(mappedKey);
        mappedKey = almanac.soilToFertilizer.getKey(mappedKey);
        mappedKey = almanac.seedToSoil.getKey(mappedKey);

        long finalMappedKey = mappedKey;
        return almanac.seeds.stream().anyMatch(r -> r.isContainKey(finalMappedKey));
    }

    Almanac parseAlmanac(List<String> inputs) {
        var seedInputs = inputs.get(0).split(": ")[1].split(" ");
        var seedRanges = new ArrayList<Range>();
        for (int i = 0; i < seedInputs.length; i += 2) {
            var source = Long.parseLong(seedInputs[i]);
            var length = Long.parseLong(seedInputs[i+1]);
            seedRanges.add(new Range(source, 0, length));
        }

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

        return new Almanac(seedRanges, seedToSoil, soilToFertilizer ,fertilizerToWater, waterToLight, lightToTemperature, temperatureToHumidity, humidityToLocation);
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
    record Almanac(List<Range> seeds,
                   Map seedToSoil,
                   Map soilToFertilizer,
                   Map fertilizerToWater,
                   Map waterToLight,
                   Map lightToTemperature,
                   Map temperatureToHumidity,
                   Map humidityToLocation) {}

    record Map(List<Range> ranges) {
        long getKey(long value) {
            var inRange = ranges.stream().filter(r -> r.isContainValue(value)).findFirst();
            return inRange.map(r -> value - r.destination + r.source).orElse(value);
        }
    }

    record Range(long source, long destination, long length) {
        boolean isContainKey(long key) {
            return source <= key && key < source + length;
        }

        boolean isContainValue(long value) {
            return destination <= value && value < destination + length;
        }
    }

    
    @Test
    public void unitTest() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource("2023/day5_test.txt"), Charsets.UTF_8);
        var almanac = parseAlmanac(lines);
        assertEquals(46, lowestLocation(almanac));
    }
}
