package com.adventofcode.year2016;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class Day11 {
    Logger log = LoggerFactory.getLogger(Day11.class);

    public static void main(String... args) throws IOException {
        Day11 solution = new Day11();
        solution.firstStar();
        solution.secondStar();
    }

    void firstStar() throws IOException {
        var facility = new Facility(List.of("promethiumG", "promethiumM"), List.of("cobaltG", "curiumG", "rutheniumG", "plutoniumG"), List.of("cobaltM", "curiumM", "rutheniumM", "plutoniumM"), List.of());
        log.warn("First star - minimum number of steps required to bring all of the objects to the fourth floor? {}", minimumStepsToComplete(facility));
    }

    void secondStar() throws IOException {
        var facility = new Facility(List.of("promethiumG", "promethiumM", "eleriumG", "eleriumM", "dilithiumG", "dilithiumM"), List.of("cobaltG", "curiumG", "rutheniumG", "plutoniumG"), List.of("cobaltM", "curiumM", "rutheniumM", "plutoniumM"), List.of());
        log.warn("Second star - minimum number of steps required to bring all of the objects to the fourth floor? {}", minimumStepsToComplete(facility));
    }

    int minimumStepsToComplete(Facility facility) {
        Set<String> allStates = new HashSet<>();
        var facilities = List.of(facility);
        while (!facilities.isEmpty()) {
            var completed = facilities.stream().filter(Facility::isCompleted).findFirst();
            if (completed.isPresent())
                return completed.get().step;
            facilities = facilities.stream()
                    .flatMap(f ->generateNextSteps(f))
                    .filter(f -> f.isSafe())
                    .filter(f -> !allStates.contains(f.hash()))
                    .map(f -> {allStates.add(f.hash()); return f;})
                    .collect(Collectors.toList());
        }
        return -1;
    }

    Stream<Facility> generateNextSteps(Facility facility) {
        var itemCombinations = itemCombinations(facility.floors[facility.elevatorAt]);

        return Stream.concat(
                facility.canElevatorMoveUp() ? itemCombinations.stream().map(i -> facility.next(facility.elevatorAt + 1, i)) : Stream.of(),
                facility.canElevatorMoveDown() ? itemCombinations.stream().map(i -> facility.next(facility.elevatorAt - 1, i)) : Stream.of()
        );
    }

    List<List<String>> itemCombinations(List<String> items) {
        var stream = items.stream().map(i -> List.of(i));   // single items
        for (int i=0; i < items.size(); i++) {              // double items
            for (int j=i+1; j < items.size(); j++) {
                stream = Stream.concat(stream, Stream.of(List.of(items.get(i), items.get(j))));
            }
        }
        return stream.collect(Collectors.toList());
    }

    class Facility {
        int elevatorAt = 0;
        int step = 0;
        List<String>[] floors = (List<String>[]) new List[4];

        Facility(List<String> floor1, List<String> floor2, List<String> floor3, List<String> floor4) {
            floors[0] = floor1; floors[1] = floor2; floors[2] = floor3; floors[3] = floor4;
        }

        Facility next(int elevatorTo, List<String> itemsMoved) {
            var nextFacility = new Facility(new ArrayList<>(floors[0]), new ArrayList<>(floors[1]), new ArrayList<>(floors[2]), new ArrayList<>(floors[3]));
            nextFacility.step = step + 1;
            nextFacility.floors[elevatorAt].removeAll(itemsMoved);
            nextFacility.floors[elevatorTo].addAll(itemsMoved);
            nextFacility.elevatorAt = elevatorTo;
            return nextFacility;
        }

        public boolean isSafe() {
            return Arrays.stream(floors).allMatch(this::isFloorSafe);
        }

        boolean isFloorSafe(List<String> items) {
            return items.stream()
                    .filter(s -> s.endsWith("M"))
                    .allMatch(s -> isChipSafe(s, items));
        }

        boolean isChipSafe(String chip, List<String> items) {
            var correspondingGenerator = chip.substring(0, chip.length()-1 ) + "G";
            if (items.contains(correspondingGenerator))
                return true;

            return !items.stream().anyMatch(s -> s.endsWith("G") && !s.equals(correspondingGenerator));
        }

        boolean isCompleted() { return floors[0].isEmpty() && floors[1].isEmpty() && floors[2].isEmpty();}
        boolean canElevatorMoveUp() { return elevatorAt < 3; }
        boolean canElevatorMoveDown() { return elevatorAt > 0; }

        String hash() {
            List<String> keys = new ArrayList<>();
            keys.add("e" + elevatorAt);
            for (int i = 0; i < floors.length; i++) {
                final int floor = i;
                floors[i].forEach(s -> keys.add(s + floor));
            }
            Collections.sort(keys);
            return String.join("", keys);
        }
    }

    @Test
    public void test_facilityIsSafe() {
        assertTrue(new Facility(List.of("HM", "LM"), List.of("HG"), List.of("LG"), List.of()).isSafe());
        assertTrue(new Facility(List.of("LM"), List.of("HG", "HM"), List.of("LG"), List.of()).isSafe());
        assertTrue(new Facility(List.of("LM"), List.of(), List.of("LG","HG","HM"), List.of()).isSafe());
        assertTrue(new Facility(List.of("LM"), List.of("HM"), List.of("LG", "HG"), List.of()).isSafe());
        assertTrue(new Facility(List.of("LM", "HM"), List.of(), List.of("LG", "HG"), List.of()).isSafe());
        assertTrue(new Facility(List.of(), List.of("LM", "HM"), List.of("LG", "HG"), List.of()).isSafe());
        assertTrue(new Facility(List.of(), List.of(), List.of("LG", "HG", "LM", "HM"), List.of()).isSafe());
        assertTrue(new Facility(List.of(), List.of(), List.of("LG", "HG"), List.of( "LM", "HM")).isSafe());
        assertTrue(new Facility(List.of(), List.of(), List.of("LG", "HG", "HM"), List.of( "LM")).isSafe());
        assertTrue(new Facility(List.of(), List.of(), List.of("HM"), List.of("LM", "LG", "HG")).isSafe());
        assertFalse(new Facility(List.of(), List.of("LM"), List.of("HM", "LG"), List.of("HG")).isSafe());
    }

    @Test
    public void test_itemCombinations() {
        assertEquals(0, itemCombinations(List.of()).size());
        assertEquals(1, itemCombinations(List.of("HM")).size());
        assertEquals(3, itemCombinations(List.of("HM", "HG")).size());
        assertEquals( 10, itemCombinations(List.of("HM", "HG", "LG", "LM")).size());
    }

    @Test
    public void test_minimumStepsToComplete() {
        assertEquals(11, minimumStepsToComplete(new Facility(List.of("HM", "LM"), List.of("HG"), List.of("LG"), List.of())));
    }
}