package com.adventofcode.year2016;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;

public class Day15 {
    Logger log = LoggerFactory.getLogger(Day15.class);

    public static void main(String... args) throws Exception {
        Day15 solution = new Day15();
        solution.run();
    }

    void run() throws Exception {
        var discs = List.of(
                new Disc(13, 11),
                new Disc(5, 0),
                new Disc(17, 11),
                new Disc(3, 0),
                new Disc(7, 2),
                new Disc(19, 17)
        );
        log.warn("First star - What is the first time you can press the button to get a capsule? {}", findTimeCapsuleCanPass(discs));

        discs = new ArrayList<>(discs);
        discs.add(new Disc(11, 0));
        log.warn("Second star - What is the first time you can press the button to get a capsule? {}", findTimeCapsuleCanPass(discs));
    }

    int findTimeCapsuleCanPass(List<Disc> discs) {
        for (int i=0; i < Integer.MAX_VALUE; i++) {
            if (canCapsulePass(discs, i)) return i;
        }
        return -1;
    }

    boolean canCapsulePass(List<Disc> discs, int startTime) {
        for (int i=0; i < discs.size(); i++) {
            if (discs.get(i).positionAtTime(startTime + i + 1) != 0)
                return false;
        }
        return true;
    }

    class Disc {
        int hasPositions;
        int startPosition;

        public Disc(int hasPositions, int startPosition) {
            this.hasPositions = hasPositions;
            this.startPosition = startPosition;
        }

        public int positionAtTime(int time) { return (time + startPosition) % hasPositions; }
    }

    @Test
    public void test_discPositionAt() {
        assertEquals(0, new Disc(5, 4).positionAtTime(1));
        assertEquals(0, new Disc(5, 4).positionAtTime(6));
        assertEquals(1, new Disc(2, 1).positionAtTime(2));
        assertEquals(0, new Disc(2, 1).positionAtTime(7));
    }

    @Test
    public void test_findTimeCapsuleCanPass() {
        var discs = List.of(new Disc(5, 4), new Disc(2, 1));
        assertEquals(5, findTimeCapsuleCanPass(discs));
    }
}