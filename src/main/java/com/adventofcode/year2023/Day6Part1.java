package com.adventofcode.year2023;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;

public class Day6Part1 {
    Logger log = LoggerFactory.getLogger(Day6Part1.class);
    final static String inputFile = "2023/day6.txt";

    public static void main(String... args) throws IOException {
        Day6Part1 solution = new Day6Part1();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var races = parseRace(lines);
        var result = multiplyWaysCouldWin(races);
        log.warn("What do you get if you multiply these numbers together? {}", result);
    }

    long multiplyWaysCouldWin(List<Race> races) {
        return races.stream()
                .mapToLong(this::waysCouldWin)
                .reduce(1L, (a,b) -> a * b);
    }

    long waysCouldWin(Race race) {
        return IntStream.range(1, race.time)
                .filter(i -> couldWin(race, i))
                .count();
    }

    boolean couldWin(Race race, int holdButtonTime) {
        return distanceMoved(holdButtonTime, race.time) > race.bestDistance;
    }

    int distanceMoved(int holdButtonTime, int raceTime) {
        if (holdButtonTime == 0) return 0;

        var travelTime = raceTime - holdButtonTime;
        if (travelTime == 0 ) return 0;

        return travelTime * holdButtonTime;
    }

    List<Race> parseRace(List<String> inputs) {
        var times = inputs.get(0).split(": ")[1].trim().split("\\s+");
        var distance = inputs.get(1).split(": ")[1].trim().split("\\s+");

        var races = new ArrayList<Race>();
        for (int i = 0; i < times.length; i++) {
            races.add(new Race(Integer.parseInt(times[i]), Integer.parseInt(distance[i])));
        }
        return races;
    }

    record Race(int time, int bestDistance) {}
    
    @Test
    public void unitTest() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource("2023/day6_test.txt"), Charsets.UTF_8);
        var races = parseRace(lines);
        assertEquals(288, multiplyWaysCouldWin(races));
    }
}
