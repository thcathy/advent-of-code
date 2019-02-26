package com.adventofcode.year2016;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;

public class Day19Part2 {
    Logger log = LoggerFactory.getLogger(Day19Part2.class);
    MessageDigest messageDigest = MessageDigest.getInstance("MD5");

    public Day19Part2() throws NoSuchAlgorithmException {}

    public static void main(String... args) throws Exception {
        Day19Part2 solution = new Day19Part2();
        solution.run();
    }

    void run() throws Exception {
        log.warn("WARNING: It takes couple hours to run !");
       log.warn("Second star - which Elf gets all the presents? {}", elfGetsAllPresents(3014603));
    }

    int elfGetsAllPresents(int length) {
        var thisElf = initElves(length);

        for (; length > 1; length--) {
            var elfBeforeStolen = elfBeforeStolen(thisElf, length);
            elfBeforeStolen.next = elfBeforeStolen.next.next;
            thisElf = thisElf.next;
            if (length % 10000 == 0) log.info("{}", length);
        }
        return thisElf.id;
    }

    Elf elfBeforeStolen(Elf elf, int length) {
        for (int i = length / 2; i > 1; i--) { elf = elf.next; }
        return elf;
    }

    Elf initElves(int length) {
        var firstElf = new Elf(1, null);
        var elf = firstElf;
        Elf newElf = null;
        for (int i = 2; i <= length; i++) {
            newElf = new Elf(i, null);
            elf.next = newElf;
            elf = newElf;
        }
        newElf.next = firstElf;
        return firstElf;
    }

    class Elf {
        int id;
        Elf next;

        public Elf(int id, Elf next) {
            this.id = id;
            this.next = next;
        }
    }

    @Test
    public void test_elfGetsAllPresents() {
        assertEquals(2, elfGetsAllPresents(5));
    }

}