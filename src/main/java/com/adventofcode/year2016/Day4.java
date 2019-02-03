package com.adventofcode.year2016;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class Day4 {
    Logger log = LoggerFactory.getLogger(Day4.class);
    final static String inputFile = "2016/day4_1.txt";

    public static void main(String... args) throws IOException {
        Day4 solution = new Day4();
        solution.firstStar();
        solution.secondStar();
    }

    void firstStar() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var total = lines.stream()
                        .map(this::convertToRoom)
                        .filter(Room::isValid)
                        .mapToInt(r -> r.sectorId)
                        .sum();

        log.warn("First star - the sum of valid room sector IDs: {}", total);
    }

    void secondStar() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var sectorId = lines.stream()
                            .map(this::convertToRoom)
                            .filter(Room::isValid)
                            .filter(r -> decode(r.name, r.sectorId).equals("northpole object storage"))
                            .findFirst().get().sectorId;

        log.warn("Second star - the sector ID of the room where North Pole objects are stored: {}", sectorId);
    }

    String decode(String input, int code) {
        var decoded = input.chars()
                .mapToObj(c -> decode((char)c, code))
                .collect(Collectors.toList());

        var result = decoded.toString()
                . substring(1, 3*decoded. size()-1)
                . replaceAll(", ", "");
        log.debug("{},{} decoded to {}", input, code, result);
        return result;
    }

    char decode(char c, int code) {
        if (c == '-') return ' ';

        return (char)(((int)c + code - (int)'a') % 26 + (int)'a');
    }

    class Room {
        int sectorId;
        char[] checksum;
        HashMap<Character, Integer> letterFrequency;
        String name;

        boolean isValid() {
            List<Map.Entry<Character, Integer>> letterEntries = new ArrayList(letterFrequency.entrySet());
            Collections.sort(letterEntries, (o1, o2) -> {
                if (o1.getValue() != o2.getValue())
                    return o2.getValue() - o1.getValue();
                else
                    return o1.getKey() - o2.getKey();
            });

            log.debug("checksum {} - sorted letter frequency: {}", checksum, letterEntries);

            for (var i=0; i<checksum.length; i++) {
                if (checksum[i] != letterEntries.get(i).getKey())
                    return false;
            }
            return true;
        }
    }

    Room convertToRoom(String input) {
        var room = new Room();
        var allSegments = input.split("-");
        var lastSegment = allSegments[allSegments.length-1];
        var codeSegments = Arrays.copyOf(allSegments, allSegments.length-1);

        HashMap<Character, Integer> codes = new HashMap<>();
        Arrays.stream(codeSegments)
                .flatMapToInt(s -> s.chars())
                .forEach(i -> addToMap(codes, i));

        room.letterFrequency = codes;
        room.sectorId = Integer.parseInt(lastSegment.split("\\[")[0]);
        room.checksum = lastSegment.split("\\[")[1].replace("]", "").toCharArray();
        room.name = input.substring(0, input.lastIndexOf("-"));
        return room;
    }

    void addToMap(HashMap<Character, Integer> codes, int input) {
        char letter = (char) input;
        var count = codes.get(letter);
        if (count == null)
            codes.put(letter, 1);
        else
            codes.put(letter, ++count);
    }

    @Test
    public void test_isValidRoom() {
        assertTrue(convertToRoom("aaaaa-bbb-z-y-x-123[abxyz]").isValid());
        assertTrue(convertToRoom("a-b-c-d-e-f-g-h-987[abcde]").isValid());
        assertTrue(convertToRoom("not-a-real-room-404[oarel]").isValid());
        assertFalse(convertToRoom("totally-real-room-200[decoy]").isValid());
    }

    @Test
    public void convertToRoom_getSectorId() {
        assertEquals(123, convertToRoom("aaaaa-bbb-z-y-x-123[abxyz]").sectorId);
        assertEquals("aaaaa-bbb-z-y-x", convertToRoom("aaaaa-bbb-z-y-x-123[abxyz]").name);
    }

    @Test
    public void test_decode() {
        String input = "qzmt-zixmtkozy-ivhz";
        String expected = "very encrypted name";
        assertEquals(expected, decode(input, 343));
    }
}