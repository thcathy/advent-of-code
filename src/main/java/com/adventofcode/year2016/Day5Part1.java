package com.adventofcode.year2016;

import java.security.MessageDigest;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;

public class Day5Part1 {
    Logger log = LoggerFactory.getLogger(Day5Part1.class);

    public static void main(String... args) throws Exception {
        Day5Part1 solution = new Day5Part1();
        solution.firstStar();
    }

    void firstStar() throws Exception {
        log.warn("First star - the password is = {}", decode("ugkcyxxp"));
    }

    String decode(String input) throws Exception {
        char[] password = new char[8];
        int index = 0, count = 0;
        var messageDigest = MessageDigest.getInstance("MD5");
        while (count < 8) {
            var md5 = md5Hex(messageDigest, input + String.valueOf(index));
            if (md5.startsWith("00000")) {
                log.debug("{} character of password = {}. Found from {} - {}", count+1, md5.charAt(5), index, md5);
                password[count] = md5.charAt(5);
                count++;
            }
            index++;
        }
        return new String(password);
    }

    private static String md5Hex(MessageDigest messageDigest, String in) {
        byte[] digest = messageDigest.digest(in.getBytes());
        StringBuffer sb = new StringBuffer();
        for (byte b : digest) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }

    @Test
    public void test_decode_abc() throws Exception {
        assertEquals("18f47a30", decode("abc"));
    }
}