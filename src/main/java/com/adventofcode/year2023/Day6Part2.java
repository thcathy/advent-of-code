package com.adventofcode.year2023;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.stream.LongStream;

import static org.junit.Assert.assertEquals;

public class Day6Part2 {
    Logger log = LoggerFactory.getLogger(Day6Part2.class);
    final static String inputFile = "2023/day6.txt";

    public static void main(String... args) throws IOException {
        Day6Part2 solution = new Day6Part2();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var races = parseRace(lines);
        var result = waysCouldWin(races);
        log.warn("How many ways can you beat the record in this one much longer race? {}", result);
    }

    long waysCouldWin(Race race) {
        var minHoldSecond =  LongStream.range(1, race.time)
                .filter(i -> couldWin(race, i))
                .findFirst().getAsLong();

        for (long i = race.time; i > 0; i--) {
            if (couldWin(race, i)) {
                return i - minHoldSecond + 1;
            }
        }
        throw new RuntimeException();
    }

    boolean couldWin(Race race, long holdButtonTime) {
        return distanceMoved(holdButtonTime, race.time) > race.bestDistance;
    }

    long distanceMoved(long holdButtonTime, long raceTime) {
        if (holdButtonTime == 0) return 0;

        var travelTime = raceTime - holdButtonTime;
        if (travelTime == 0 ) return 0;

        return travelTime * holdButtonTime;
    }

    Race parseRace(List<String> inputs) {
        var time = inputs.get(0).split(": ")[1].trim().replaceAll("\\s+", "");
        var distance = inputs.get(1).split(": ")[1].trim().replaceAll("\\s+", "");
        return new Race(Long.parseLong(time), Long.parseLong(distance));
    }

    record Race(long time, long bestDistance) {}
    
    @Test
    public void unitTest() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource("2023/day6_test.txt"), Charsets.UTF_8);
        var races = parseRace(lines);
        assertEquals(71503, waysCouldWin(races));
    }
}
