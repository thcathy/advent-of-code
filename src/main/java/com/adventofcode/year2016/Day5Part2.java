package com.adventofcode.year2016;

import java.security.MessageDigest;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;

public class Day5Part2 {
    Logger log = LoggerFactory.getLogger(Day5Part2.class);

    public static void main(String... args) throws Exception {
        Day5Part2 solution = new Day5Part2();
        solution.secondStar();
    }

    void secondStar() throws Exception {
        log.warn("Second star - the password is = {}", decode("ugkcyxxp"));
    }

    String decode(String input) throws Exception {
        char[] password = new char[8];
        int index = 0, count = 0;
        var messageDigest = MessageDigest.getInstance("MD5");
        while (count < 8) {
            var md5 = md5Hex(messageDigest, input + String.valueOf(index));
            if (md5.startsWith("00000")) {
                int position = parsePosition(md5.substring(5, 6));
                if (position >= 0 && password[position] == '\u0000') {
                    password[position] = md5.charAt(6);
                    log.debug("password = {}. Found from {} - {}", password, index, md5);
                    count++;
                }
            }
            index++;
        }
        return new String(password);
    }

    private int parsePosition(String input) {
        try {
            int position = Integer.valueOf(input);
            if (position < 8)
                return position;
            else
                return -1;
        } catch (Exception e) {
            return -1;
        }
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
    public void test_decode_abc() throws Exception {
        assertEquals("05ace8e3", decode("abc"));
    }
}