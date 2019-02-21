package com.adventofcode.year2016;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.adventofcode.year2016.Day17.Direction.D;
import static com.adventofcode.year2016.Day17.Direction.L;
import static com.adventofcode.year2016.Day17.Direction.R;
import static com.adventofcode.year2016.Day17.Direction.U;
import static org.junit.Assert.assertEquals;

public class Day17 {
    Logger log = LoggerFactory.getLogger(Day17.class);
    MessageDigest messageDigest = MessageDigest.getInstance("MD5");

    public Day17() throws NoSuchAlgorithmException {}

    public static void main(String... args) throws Exception {
        Day17 solution = new Day17();
        solution.run();
    }

    void run() throws Exception {
        log.warn("First star - what is the shortest path? {}", shortestPathToEnding(4, "bwnlcvfs"));
        log.warn("Second star - what is the length of the longest path? {}", longestPathToEnding(4, "bwnlcvfs"));
    }

    int longestPathToEnding(int size, String input) {
        State longestState = new State(size, input, "", 0, 0);
        var states = nextValidState(longestState);
        while (!states.isEmpty()) {
            var endState = states.stream().filter(this::isEndState).findFirst();
            if (endState.isPresent()) longestState = endState.get();

            states = states.stream().filter(s -> !isEndState(s)).flatMap(s -> nextValidState(s).stream()).collect(Collectors.toList());
        }
        return longestState.path.length();
    }

    String shortestPathToEnding(int size, String input) {
        var states = nextValidState(new State(size, input, "", 0, 0));
        while (!states.isEmpty()) {
            var path = states.stream().filter(this::isEndState).map(s -> s.path).findFirst();
            if (path.isPresent()) return path.get();

            states = states.stream().flatMap(s -> nextValidState(s).stream()).collect(Collectors.toList());
        }
        return null;
    }

    class State {
        String input, path;
        int size, roomX, roomY;

        public State(int size, String input, String path, int roomX, int roomY) {
            this.size = size;
            this.input = input;
            this.path = path;
            this.roomX = roomX;
            this.roomY = roomY;
        }
    }

    enum Direction { U, D, L, R }

    boolean isEndState(State state) { return state.roomX == state.size-1 && state.roomY == state.size-1; }

    boolean isDoorOpen(char input) { return input == 'b' || input == 'c' || input == 'd' || input == 'e' || input == 'f'; }

    boolean isValid(State state) { return state.roomX >=0 && state.roomY >= 0 && state.roomX < state.size && state.roomY < state.size; }

    List<State> nextValidState(State state) {
        var newStates = allNextStates(state);
        var hash = md5Hex(state.input + state.path);

        return IntStream.range(0, newStates.size()).boxed()
                .filter(i -> (isDoorOpen(hash.charAt(i)) && isValid(newStates.get(i))))
                .map(i -> newStates.get(i))
                .collect(Collectors.toList());
    }

    State move(Direction direction, State state) {
        var roomX = state.roomX;
        var roomY = state.roomY;

        if (direction == U) roomX--;
        else if (direction == D) roomX++;
        else if (direction == L) roomY--;
        else if (direction == R) roomY++;

        return new State(state.size, state.input, state.path + direction.toString(), roomX, roomY);
    }

    List<State> allNextStates(State state) {
        return List.of(move(U, state), move(D, state), move(L, state), move(R, state));
    }

    String md5Hex(String in) {
        byte[] digest = messageDigest.digest(in.getBytes());
        StringBuffer sb = new StringBuffer();
        for (byte b : digest) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.substring(0, 4);
    }

    @Test
    public void test_nextValidState() {
        var nextStates = nextValidState(new State(4, "hijkl", "", 0, 0));
        assertEquals(1, nextStates.size());
        assertEquals("D", nextStates.get(0).path);
        assertEquals(1, nextStates.get(0).roomX);
        assertEquals(0, nextStates.get(0).roomY);

        nextStates = nextValidState(nextStates.get(0));
        assertEquals(2, nextStates.size());
    }

    @Test
    public void test_shortestPathToEnding() {
        assertEquals("DDRRRD", shortestPathToEnding(4, "ihgpwlah"));
        assertEquals("DDUDRLRRUDRD", shortestPathToEnding(4, "kglvqrro"));
        assertEquals("DRURDRUDDLLDLUURRDULRLDUUDDDRR", shortestPathToEnding(4, "ulqzkmiv"));
    }

    @Test
    public void test_longestPathToEnding() {
        assertEquals(370, longestPathToEnding(4, "ihgpwlah"));
        assertEquals(492, longestPathToEnding(4, "kglvqrro"));
        assertEquals(830, longestPathToEnding(4, "ulqzkmiv"));
    }

}