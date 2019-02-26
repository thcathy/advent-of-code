package com.adventofcode.year2016;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;

public class Day19Part1 {
    Logger log = LoggerFactory.getLogger(Day19Part1.class);
    MessageDigest messageDigest = MessageDigest.getInstance("MD5");

    public Day19Part1() throws NoSuchAlgorithmException {}

    public static void main(String... args) throws Exception {
        Day19Part1 solution = new Day19Part1();
        solution.run();
    }

    void run() throws Exception {
       log.warn("First star - which Elf gets all the presents? {}", elfGetsAllPresents(3014603));
    }

    int elfGetsAllPresents(int length) {
        var thisElf = initElves(length);

        while (thisElf != thisElf.next) {
            thisElf.present += thisElf.next.present;
            thisElf.next = thisElf.next.next;
            thisElf = thisElf.next;
        }
        return thisElf.id;
    }

    Elf initElves(int length) {
        var firstElf = new Elf(1, 1, null);
        var elf = firstElf;
        Elf newElf = null;
        for (int i = 2; i <= length; i++) {
            newElf = new Elf(i, 1, null);
            elf.next = newElf;
            elf = newElf;
        }
        newElf.next = firstElf;
        return firstElf;
    }

    class Elf {
        int id, present;
        Elf next;

        public Elf(int id, int present, Elf next) {
            this.id = id;
            this.present = present;
            this.next = next;
        }
    }

    @Test
    public void test_elfGetsAllPresents() {
        assertEquals(3, elfGetsAllPresents(5));
    }

}