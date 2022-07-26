package com.adventofcode.year2017;


import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Day25Part1 {
    final static String inputFile = "2017/day25_1.txt";
    
    public static void main(String... args) throws Exception {
        Day25Part1 solution = new Day25Part1();
        solution.run();
    }

    void run() throws Exception {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = diagnosticChecksum(createMachine(lines));
        System.out.println("What is the diagnostic checksum it produces once it's working again? " + result);
    }

    int diagnosticChecksum(Machine machine) {
        for (int i = 0; i < machine.stepToPerform; i++) {
            machine.nextState();
        }
        return machine.diagnosticChecksum();
    }

    Machine createMachine(List<String> inputs) {
        char currentState = inputs.get(0).charAt(inputs.get(0).length() - 2);
        int stepToPerform = Integer.valueOf(inputs.get(1).split(" ")[5]);
        Map<Character, State> states = new HashMap<>();
        for (int i = 3; i < inputs.size(); i += 10) {
            State s = createState(inputs.subList(i, i + 9));
            states.put(s.id, s);
        }
        return new Machine(states.get(currentState), stepToPerform, states);
    }

    State createState(List<String> inputs) {
        char stateId = inputs.get(0).charAt(inputs.get(0).length() - 2);                
        return new State(stateId, createAction(inputs.subList(2, 5)), createAction(inputs.subList(6, 9)));
    }

    Action createAction(List<String> inputs) {
        int newValue = Integer.parseInt(inputs.get(0).substring(inputs.get(0).length() - 2, inputs.get(0).length() - 1));
        int move = inputs.get(1).contains("right") ? 1 : -1;
        char nextState = inputs.get(2).charAt(inputs.get(2).length() - 2);
        return new Action(newValue, move, nextState); 
    }

    class Machine {
        State currentState;
        int stepToPerform;
        Map<Character, State> states;
        int cursor = 0;
        Set<Integer> tape = new HashSet<>();

        public Machine(State currentState, int stepToPerform, Map<Character, State> states) {
            this.currentState = currentState;
            this.stepToPerform = stepToPerform;
            this.states = states;
        }

        public void nextState() {
            if (tape.contains(cursor)) {
                takeAction(currentState.onOne);
            } else {
                takeAction(currentState.onZero);
            }
        }

        void takeAction(Action action) {
            if (action.newValue == 1) {
                tape.add(cursor);
            } else {
                tape.remove(cursor);
            }

            cursor += action.move;
            currentState = states.get(action.nextStateId());                
        }

        int diagnosticChecksum() {
            return tape.size();
        }
    }

    record State(char id, Action onZero, Action onOne) {}

    record Action(int newValue, int move, char nextStateId) {}

    @Test
    public void unitTest() throws Exception {        
        var lines = Resources.readLines(ClassLoader.getSystemResource("2017/day25_test.txt"), Charsets.UTF_8);        
        Machine machine = createMachine(lines);
        assertEquals('A', machine.currentState.id);
        assertEquals(6, machine.stepToPerform);
        assertEquals(2, machine.states.size());
        State stateA = machine.states.get('A');
        assertEquals('A', stateA.id);
        assertEquals(1, stateA.onZero.newValue);
        assertEquals(1, stateA.onZero.move);
        assertEquals('B', stateA.onZero.nextStateId);
        assertEquals(0, stateA.onOne.newValue);
        assertEquals(-1, stateA.onOne.move);
        assertEquals('B', stateA.onOne.nextStateId);

        assertEquals(3, diagnosticChecksum(machine));
    }

}
