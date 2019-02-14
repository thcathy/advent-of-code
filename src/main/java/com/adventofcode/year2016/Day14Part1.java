package com.adventofcode.year2016;

import java.security.MessageDigest;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class Day14Part1 {
    Logger log = LoggerFactory.getLogger(Day14Part1.class);

    public static void main(String... args) throws Exception {
        Day14Part1 solution = new Day14Part1();
        solution.firstStar();
    }

    void firstStar() throws Exception {
        var keys = generateKeys("ihaygndm");
        log.warn("First star - what index produces your 64th one-time pad key? {}", keys.get(63).index);
    }

    List<Key> generateKeys(String input) throws Exception {
        var messageDigest = MessageDigest.getInstance("MD5");
        var realKeys = new LinkedList<Key>();
        var hashCache = new HashMap<Integer, String>();
        int index = 0;

        while (realKeys.size() < 64) {
            var md5 = hashCache.computeIfAbsent(index, (i) -> md5Hex(messageDigest,input + i));
            var anyRealKeys = firstTriple(md5, index).filter(k -> isRealKey(k, input, hashCache, messageDigest));
            anyRealKeys.ifPresent(realKeys::add);
            index++;
        }
        return realKeys;
    }

    boolean isRealKey(Key key, String input, Map<Integer, String> hashCache, MessageDigest messageDigest) {
        return IntStream.rangeClosed(key.index + 1, key.index + 1000)
                .mapToObj(i -> hashCache.computeIfAbsent(i, (v) -> md5Hex(messageDigest,input + v)))
                .anyMatch(s -> isRealKey(s, key));
    }

    boolean isRealKey(String md5, Key key) {
        return md5.contains(MessageFormat.format("{0}{0}{0}{0}{0}", key.character));
    }

    Optional<Key> firstTriple(String input, int index) {
        return IntStream.rangeClosed(0, input.length() - 3)
                .mapToObj(i -> input.substring(i, i+3))
                .filter(this::isTriple).findFirst()
                .flatMap(s -> Optional.of(new Key(index, s.charAt(0))));
    }

    boolean isTriple(String input) { return input.charAt(0) == input.charAt(1) && input.charAt(0) == input.charAt(2); }

    class Key {
        int index;
        char character;

        public Key(int index, char character) {
            this.index = index;
            this.character = character;
        }

        @Override
        public String toString() { return index + "," + character; }
    }

    String md5Hex(MessageDigest messageDigest, String in) {
        byte[] digest = messageDigest.digest(in.getBytes());
        StringBuffer sb = new StringBuffer();
        for (byte b : digest) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }

    @Test
    public void test_indexOfPadKey() throws Exception {
        var keys = generateKeys("abc");
        assertEquals(22728, keys.get(63).index);
    }

    @Test
    public void test_isRealKey() throws Exception {
        var messageDigest = MessageDigest.getInstance("MD5");
        assertFalse(isRealKey(new Key(18, '8'), "abc", new HashMap<>(), messageDigest));
        assertTrue(isRealKey(new Key(39, 'e'), "abc", new HashMap<>(), messageDigest));
        assertTrue(isRealKey(new Key(92, '9'), "abc", new HashMap<>(), messageDigest));
    }

    @Test
    public void test_allTripleCharacters() throws Exception {
        var messageDigest = MessageDigest.getInstance("MD5");
        var result = firstTriple(md5Hex(messageDigest, "abc18"), 18);
        assertEquals('8', result.get().character);
    }
}