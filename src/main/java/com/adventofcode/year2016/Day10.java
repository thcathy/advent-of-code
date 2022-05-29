package com.adventofcode.year2016;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import static junit.framework.TestCase.assertEquals;

public class Day10 {
    Logger log = LoggerFactory.getLogger(Day10.class);
    final static String inputFile = "2016/day10_1.txt";

    public static void main(String... args) throws IOException {
        Day10 solution = new Day10();
        solution.run();
    }

    void run() throws IOException {
        var factory = new Factory();
        factory.isResponsible = (b) -> b.isContain(61, 17);

        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        lines.stream().filter(l -> l.startsWith("bot")).forEach(i -> executeInstruction(factory, i));
        lines.stream().filter(l -> l.startsWith("actionValue")).forEach(i -> executeInstruction(factory, i));

        log.warn("First star - the bot that is responsible for comparing actionValue-61 microchips with actionValue-17 microchips? {}", factory.responsibleBotNumber);

        log.warn("Second star - multiply together the values of one chip in each of outputs 0, 1, and 2? {}",
                factory.outputBins.get(0) * factory.outputBins.get(1) * factory.outputBins.get(2));
    }


    class Bot {
        Integer chip1;
        Integer chip2;

        void put(int chip) {
            if (chip1 == null)
                chip1 = chip;
            else
                chip2 = chip;
        }

        boolean isFull() {
            return chip1 != null && chip2 != null;
        }

        boolean isContain(int value1, int value2) {
            return (chip1 == value1 && chip2 == value2) || (chip1 == value2 && chip2 == value1);
        }
    }

    class Factory {
        Map<Integer, Bot> bots = new HashMap<>();
        Map<Integer, Integer> outputBins = new HashMap<>();
        Map<Integer, Instruction> instructions = new HashMap<>();
        Integer responsibleBotNumber;
        Function<Bot, Boolean> isResponsible;

        BiConsumer<Integer, Integer> giveChipToBot = (Integer chip, Integer botNumber) -> {
            var bot = bots.getOrDefault(botNumber, new Bot());
            bot.put(chip);
            bots.put(botNumber, bot);

            if (bot.isFull()) {
                if (isResponsible.apply(bot)) responsibleBotNumber = botNumber;
                Integer lowValue, highValue;
                if (bot.chip1 < bot.chip2) {
                    lowValue = bot.chip1;
                    highValue = bot.chip2;
                } else {
                    lowValue = bot.chip2;
                    highValue = bot.chip1;
                }
                bot.chip1 = null;
                bot.chip2 = null;
                runInstruction(instructions.get(botNumber), lowValue, highValue);
            }
        };

        BiConsumer<Integer, Integer> putOutputBin = (Integer chip, Integer binNumber) -> outputBins.put(binNumber, chip);

        private void runInstruction(Instruction instruction, int lowValue, int highValue) {
            instruction.lowValueCommand.action.accept(lowValue, instruction.lowValueCommand.target);
            instruction.highValueCommand.action.accept(highValue, instruction.highValueCommand.target);
        }

        Instruction buildInstruction(String[] inputs) {
            return new Instruction(
                    buildCommand(inputs[5], inputs[6]),
                    buildCommand(inputs[10], inputs[11]));
        }

        Command buildCommand(String command, String target) {
            if (command.startsWith("bot"))
                return new Command(this.giveChipToBot, Integer.valueOf(target));
            else
                return new Command(this.putOutputBin, Integer.valueOf(target));
        }

        public void setInstruction(String[] inputs) {
            instructions.put(Integer.valueOf(inputs[1]), buildInstruction(inputs));
        }
    }

    class Instruction {
        Command lowValueCommand;
        Command highValueCommand;

        public Instruction(Command lowValueCommand, Command highValueCommand) {
            this.lowValueCommand = lowValueCommand;
            this.highValueCommand = highValueCommand;
        }
    }

    class Command {
        BiConsumer<Integer, Integer> action;
        int target;

        public Command(BiConsumer<Integer, Integer> action, int target) {
            this.action = action;
            this.target = target;
        }
    }

    void executeInstruction(Factory factory, String input) {
        var params = input.split(" ");
        if (params[0].equals("actionValue"))
            factory.giveChipToBot.accept(Integer.valueOf(params[1]), Integer.valueOf(params[5]));
        else
            factory.setInstruction(params);
    }

    @Test
    public void test_executeInstructions() {
        var factory = new Factory();
        factory.isResponsible = (b) -> b.isContain(5, 2);

        executeInstruction(factory, "actionValue 5 goes to bot 2");
        assertEquals(5, factory.bots.get(2).chip1.intValue());

        executeInstruction(factory, "bot 2 gives low to bot 1 and high to bot 0");
        assertEquals(1, factory.instructions.get(2).lowValueCommand.target);
        assertEquals(factory.giveChipToBot, factory.instructions.get(2).lowValueCommand.action);
        assertEquals(0, factory.instructions.get(2).highValueCommand.target);
        assertEquals(factory.giveChipToBot, factory.instructions.get(2).highValueCommand.action);

        executeInstruction(factory, "actionValue 3 goes to bot 1");
        executeInstruction(factory, "bot 1 gives low to output 1 and high to bot 0");
        executeInstruction(factory, "bot 0 gives low to output 2 and high to output 0");
        assertEquals(0, factory.instructions.get(0).highValueCommand.target);
        assertEquals(factory.putOutputBin, factory.instructions.get(0).highValueCommand.action);

        executeInstruction(factory, "actionValue 2 goes to bot 2");
        assertEquals(5, factory.outputBins.get(0).intValue());
        assertEquals(2, factory.outputBins.get(1).intValue());
        assertEquals(3, factory.outputBins.get(2).intValue());
        assertEquals(2, factory.responsibleBotNumber.intValue());
    }
}
