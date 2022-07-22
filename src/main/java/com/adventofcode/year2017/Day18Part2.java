package com.adventofcode.year2017;


import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.apache.commons.lang3.math.NumberUtils;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Day18Part2 {
    final static String inputFile = "2017/day18_1.txt";
    final static int RECEIVE_TIMEOUT_SECOND = 5;
    
    public static void main(String... args) throws Exception {
        Day18Part2 solution = new Day18Part2();
        solution.run();
    }

    void run() throws Exception {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = program1SendTimes(lines);
        System.out.println("How many times did program 1 send a value? " + result);
    }

    int program1SendTimes(List<String> inputs) throws InterruptedException {
        List<Instruction> instructions = parseInstruction(inputs);
        LinkedBlockingQueue<Long> queue0 = new LinkedBlockingQueue<>();
        LinkedBlockingQueue<Long> queue1 = new LinkedBlockingQueue<>();
        Program program0 = new Program(instructions, 0, queue1, queue0);
        Program program1 = new Program(instructions, 1, queue0, queue1);

        ExecutorService pool = Executors.newFixedThreadPool(2);
        pool.submit(() -> program0.run());
        pool.submit(() -> program1.run());
        pool.awaitTermination(RECEIVE_TIMEOUT_SECOND * 2, TimeUnit.SECONDS);
        pool.shutdownNow();
        return program1.sendTimes;
    }

    List<Instruction> parseInstruction(List<String> inputs) {
        return inputs.stream().map(i -> {
            String[] input = i.split(" ");
            return new Instruction(stringToFunction(input[0]), Arrays.copyOfRange(input, 1, input.length));
        }).collect(Collectors.toList());
    }
    
    BiConsumer<Program, String[]> stringToFunction(String str) {
        return switch (str) {
            case "snd" -> this::send;
            case "set" -> this::set;
            case "add" -> this::increase;
            case "mul" -> this::multiply;
            case "mod" -> this::remainder;
            case "rcv" -> this::receive;
            case "jgz" -> this::jump;
            default -> throw new IllegalStateException("Unexpected value: " + str);
        };
    }

    class Program {
        final List<Instruction> instructions;
        Map<String, Long> registers = new HashMap<>();        
        BlockingQueue<Long> sendQueue;
        BlockingQueue<Long> receiveQueue;
        int currentInstruction = 0;
        int sendTimes = 0;

        public Program(List<Instruction> instructions, long programId, 
            BlockingQueue<Long> sendQueue, BlockingQueue<Long> receiveQueue) {
            this.instructions = instructions;
            this.sendQueue = sendQueue;
            this.receiveQueue = receiveQueue;
            registers.put("p", programId);
        }

        public void run() {
            try {
                while (currentInstruction < instructions.size()) {
                    runNextInstruction();
                }
            } catch (Exception e) {
                System.out.println("Program ended due to exception: " + e.getMessage());
                e.printStackTrace();
            }            
        }

        public void runNextInstruction() {
            Instruction instruction = instructions.get(currentInstruction);
            instruction.function.accept(this, instruction.params);
        }

        long getValue(String r) {
            if (NumberUtils.isCreatable(r)) {
                return Integer.valueOf(r);
            }
            return registers.getOrDefault(r, 0L);
        }
    }

    record Instruction(BiConsumer<Program, String[]> function, String[] params) {
    }

    void send(Program program, String[] params) {
        program.sendQueue.add(program.getValue(params[0]));
        program.sendTimes++;
        program.currentInstruction++;
    }
    
    void set(Program program, String[] params) {
        program.registers.put(params[0], program.getValue(params[1]));
        program.currentInstruction++;
    }

    void increase(Program program, String[] params) {
        program.registers.put(params[0], program.getValue(params[0]) + program.getValue(params[1]));
        program.currentInstruction++;
    }

    void multiply(Program program, String[] params) {
        program.registers.put(params[0], program.getValue(params[0]) * program.getValue(params[1]));
        program.currentInstruction++;
    }

    void remainder(Program program, String[] params) {
        program.registers.put(params[0], program.getValue(params[0]) % program.getValue(params[1]));
        program.currentInstruction++;
    }

    void receive(Program program, String[] params) {
        try {
            Long value = program.receiveQueue.poll(RECEIVE_TIMEOUT_SECOND, TimeUnit.SECONDS);
            if (value == null) {
                throw new RuntimeException("Timeout in polling value");
            }
            program.registers.put(params[0], value);
            program.currentInstruction++;
        } catch (InterruptedException e) {            
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    void jump(Program program, String[] params) {
        if (program.getValue(params[0]) > 0) {
            program.currentInstruction += program.getValue(params[1]);
        } else {
            program.currentInstruction++;
        }
    }

    @Test
    public void unitTest() throws Exception {
        var lines = Resources.readLines(ClassLoader.getSystemResource("2017/day18_test2.txt"), Charsets.UTF_8);
        assertEquals(3, program1SendTimes(lines));
    }
}
