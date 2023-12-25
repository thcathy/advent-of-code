package com.adventofcode.year2023;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.adventofcode.year2023.Day20Part1.Pulse.HIGH;
import static com.adventofcode.year2023.Day20Part1.Pulse.LOW;

public class Day20Part1 {
    Logger log = LoggerFactory.getLogger(Day20Part1.class);
    final static String inputFile = "2023/day20.txt";

    public static void main(String... args) throws IOException {
        Day20Part1 solution = new Day20Part1();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = calculatePulseValue(lines);
        log.warn("What do you get if you multiply the total number of low pulses sent by the total number of high pulses sent? {}", result);
    }
    
    long calculatePulseValue(List<String> inputs) {
        var modules = parseInput(inputs);
        var transmissions = new ArrayDeque<Transmission>();
        var pulseCount = Arrays.stream(Pulse.values())
                            .collect(Collectors.toMap(k -> k, v -> 0L));
        for (int i = 0; i < 1000; i++) {

            transmissions.add(new Transmission("button", "broadcaster", Pulse.LOW));

            while (!transmissions.isEmpty()) {
                var transmission = transmissions.poll();
                pulseCount.compute(transmission.pulse, (k, v) -> v + 1);

                modules.computeIfPresent(transmission.destination, (k, v) -> {
                    v.receive(transmission, transmissions);
                    return v;
                });
            }
        }
        return pulseCount.get(LOW) * pulseCount.get(HIGH);
    }


    //region Data Objects

    interface Module {
        List<String> destinations();
        String id();
        default void receive(Transmission transmission, Queue<Transmission> output) {
            destinations().forEach(dest -> output.add(new Transmission(id(), dest, transmission.pulse)));
        }
    }

    record FlipFlop(String id, FlipFlopValue value, List<String> destinations) implements Module {
        public void receive(Transmission transmission, Queue<Transmission> output) {
            if (transmission.pulse == HIGH) return;

            if (value.on) {
                destinations.forEach(dest -> output.add(new Transmission(id, dest, LOW)));
            } else {
                destinations.forEach(dest -> output.add(new Transmission(id, dest, HIGH)));
            }
            value.on = !value.on;
        }
    }

    record Conjunction(String id, Map<String, Pulse> inputPulse, List<String> destinations) implements Module {
        public void receive(Transmission transmission, Queue<Transmission> output) {
            inputPulse.put(transmission.source, transmission.pulse);
            var allHighPulses = inputPulse.values().stream().allMatch(p -> p == HIGH);
            var sendPulse = allHighPulses ? LOW : HIGH;
            destinations.forEach(dest -> output.add(new Transmission(id, dest, sendPulse)));
        }
    }

    record Broadcast(String id, List<String> destinations) implements Module {}

    record Transmission(String source, String destination, Pulse pulse) {}

    enum Pulse {HIGH, LOW}

    class FlipFlopValue { boolean on = false; }
    
    //endregion

    //region Input Parsing

    Map<String, Module> parseInput(List<String> inputs) {
        var modules = inputs.stream().map(this::parseModule)
                        .collect(Collectors.toMap(Module::id, v -> v));

        modules.values().stream()
                .filter(m -> m instanceof Conjunction)
                .forEach(m -> {
                    modules.values().stream()
                            .filter(other -> other.destinations().contains(m.id()))
                            .map(source -> source.id())
                            .forEach(source -> {
                                var conjunction = (Conjunction) m;
                                conjunction.inputPulse.put(source, Pulse.LOW);
                            });
                });
        return modules;
    }

    Module parseModule(String input) {
        var inputs = input.split(" -> ");
        var destinations = Arrays.stream(inputs[1].trim().split(", ")).toList();
        if ("broadcaster".equals(inputs[0]))
            return new Broadcast(inputs[0], destinations);
        else if (inputs[0].charAt(0) == '%')
            return new FlipFlop(inputs[0].substring(1), new FlipFlopValue(), destinations);
        else if (inputs[0].charAt(0) == '&') {
            return new Conjunction(inputs[0].substring(1), new HashMap<>(), destinations);
        }
        throw new RuntimeException("cannot parse module");
    }

    //endregion

    @Test
    public void unitTest() throws IOException {        
        var lines = Resources.readLines(ClassLoader.getSystemResource("2023/day20_test.txt"), Charsets.UTF_8);
        var result = calculatePulseValue(lines);
        Assert.assertEquals(32000000, result);

        var lines2 = Resources.readLines(ClassLoader.getSystemResource("2023/day20_test2.txt"), Charsets.UTF_8);
        var result2 = calculatePulseValue(lines2);
        Assert.assertEquals(11687500, result2);
    }
}
