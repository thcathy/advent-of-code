package com.adventofcode.year2018;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Day4Part2 {
    final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    Logger log = LoggerFactory.getLogger(Day4Part2.class);
    final static String inputFile = "2018/day4.txt";
    
    public static void main(String... args) throws IOException {
        Day4Part2 solution = new Day4Part2();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var schedules = parseGuardSleepSchedule(sortedRecords(lines));        
        var result =  guardIdTimesMinutes(schedules);
        log.warn("What is the ID of the guard you chose multiplied by the minute you chose? {}", result);
    }

    int guardIdTimesMinutes(Map<Integer, int[]> schedules) {
        int maxSleepCount = Integer.MIN_VALUE;
        int guardId = -1, mostAsleepMinute = -1;
        for (Entry<Integer, int[]> schedule : schedules.entrySet()) {
            var minutes = schedule.getValue();
            for (int i = 0; i < 60; i++) {
                if (minutes[i] > maxSleepCount) {
                    mostAsleepMinute = i;
                    guardId = schedule.getKey();
                    maxSleepCount = minutes[i];
                }
            }
        }
        return guardId * mostAsleepMinute;
    }

    Map<Integer, int[]> parseGuardSleepSchedule(List<Record> records) {
        Map<Integer, int[]> guardSleepSchedule = new HashMap<>();
        int startSleepMinute = 0;
        int activeGuardId = -1;
        for (Record record : records) {
            if (record.text.startsWith("Guard #")) {                
                activeGuardId = Integer.parseInt(record.text.split(" ")[1].substring(1));
            } else if (record.text.startsWith("falls asleep")) {
                startSleepMinute = record.time.getMinute();
            } else if (record.text.startsWith("wakes up")) {
                int[] sleepMinutes = guardSleepSchedule.computeIfAbsent(activeGuardId, k -> new int[60]);
                for (int i = startSleepMinute; i < record.time.getMinute(); i++) {
                    sleepMinutes[i] += 1;
                }
            }
        }
        return guardSleepSchedule;
    }

    List<Record> sortedRecords(List<String> inputs) {
        return inputs.stream()
                .map(this::parseRecord)
                .sorted(Comparator.comparing(Record::time))
                .toList();
    }
    
    Record parseRecord(String input) {
        String[] inputs = input.split("] ");
        return new Record(LocalDateTime.parse(inputs[0].substring(1), formatter), inputs[1]);
    }

    record Record(LocalDateTime time, String text) {}

    @Test
    public void unitTest() throws IOException {
        Record r = parseRecord("[1518-11-02 00:40] falls asleep");
        assertEquals(LocalDateTime.of(1518, 11, 2, 0, 40), r.time);
        assertEquals("falls asleep", r.text);

        var lines = Resources.readLines(ClassLoader.getSystemResource("2018/day4_test.txt"), Charsets.UTF_8);
        var records = sortedRecords(lines);
        assertEquals("Guard #10 begins shift", records.get(0).text);
        assertEquals("wakes up", records.get(records.size() - 1).text);

        var schedules = parseGuardSleepSchedule(records);
        assertEquals(4455, guardIdTimesMinutes(schedules));
    }
}
