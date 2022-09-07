package com.adventofcode.year2018;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
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

public class Day4Part1 {
    final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    Logger log = LoggerFactory.getLogger(Day4Part1.class);
    final static String inputFile = "2018/day4.txt";
    
    public static void main(String... args) throws IOException {
        Day4Part1 solution = new Day4Part1();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var schedules = parseGuardSleepSchedule(sortedRecords(lines));
        var guardId = guardAsleepMost(schedules);        
        var result =  guardId * mostAsleepMinute(schedules.get(guardId));
        log.warn("What is the ID of the guard you chose multiplied by the minute you chose? {}", result);
    }

    int mostAsleepMinute(int[] schedule) {
        int maxSleepCount = Integer.MIN_VALUE;
        int minute = 0;
        for (int i = 0; i < 60; i++) {
            if (schedule[i] > maxSleepCount) {
                minute = i;
                maxSleepCount = schedule[i];
            }
        }
        return minute;        
    }

    int guardAsleepMost(Map<Integer, int[]> schedules) {
        int maxSleepMinutes = Integer.MIN_VALUE;
        int guardId = -1;
        for (Entry<Integer, int[]> schedule : schedules.entrySet()) {
            int sleepMinutes = Arrays.stream(schedule.getValue()).sum();
            if (sleepMinutes > maxSleepMinutes) {
                maxSleepMinutes = sleepMinutes;
                guardId = schedule.getKey();
            }
        }
        return guardId;
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
        assertEquals(10, guardAsleepMost(schedules));
        assertEquals(24, mostAsleepMinute(schedules.get(10)));
    }
}
