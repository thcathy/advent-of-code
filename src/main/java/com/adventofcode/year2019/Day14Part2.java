package com.adventofcode.year2019;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import static org.junit.Assert.assertEquals;

public class Day14Part2 {
    static Logger log = LoggerFactory.getLogger(Day14Part2.class);
    final static String inputFile = "2019/day14_1.txt";

    public static void main(String... args) throws IOException {
        Day14Part2 solution = new Day14Part2();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = fuelFrom1TrillionOre(lines);
        log.warn("what is the maximum amount of FUEL you can produce? {}", result);
    }

    public long fuelFrom1TrillionOre(List<String> inputs) {
        var reactionMap = buildReactionMap(inputs);
        var initMaterial = new HashMap<String, Long>();
        initMaterial.put("ORE", 1000000000000l);
        var oreToProduce1Fuel = oreToProduce("FUEL", 1, reactionMap, initMaterial);
        long numberOfFuel = 1000000000000l / oreToProduce1Fuel;
        try {
            while (true) {
                initMaterial = new HashMap<String, Long>();
                initMaterial.put("ORE", 1000000000000l);
                oreToProduce("FUEL", numberOfFuel, reactionMap, initMaterial);
                numberOfFuel++;
            }
        } catch (RuntimeException e) { numberOfFuel--; }
        return numberOfFuel;
    }

    public Map<String, Reaction> buildReactionMap(List<String> inputs) {
        return inputs.stream()
                .map(Reaction::build)
                .collect(Collectors.toMap(r -> r.outputType, r -> r));
    }

    public long oreToProduce1Fuel(Map<String, Reaction> outputProcedures) {
        return oreToProduce("FUEL", 1, outputProcedures, new HashMap<>());
    }

    private long oreToProduce(String material, long quantity, Map<String, Reaction> outputProcedures, Map<String, Long> remainingMaterials) {
        long remainingMaterial = remainingMaterials.getOrDefault(material,0l);
        if (remainingMaterial >= quantity) {
            remainingMaterials.put(material, remainingMaterial-quantity);
            return "ORE".equals(material) ? quantity : 0;
        } else if ("ORE".equals(material)) {
            throw new RuntimeException("No more ore");
        }

        var procedure = outputProcedures.get(material);
        var numOfReaction = (long) Math.ceil(((double)quantity-remainingMaterial) / procedure.outputQuantity);
        var ores = IntStream.range(0, procedure.inputType.size()).boxed()
                .mapToLong(i -> oreToProduce(procedure.inputType.get(i), procedure.inputQuantity.get(i) * numOfReaction, outputProcedures, remainingMaterials))
                .sum();
        remainingMaterials.put(material, remainingMaterial + (numOfReaction * procedure.outputQuantity) - quantity);
        return ores;
    }

    @Test
    public void oreToProduce1Fuel_testcases() {
        var input2 = List.of(
                "157 ORE => 5 NZVS",
                "165 ORE => 6 DCFZ",
                "44 XJWVT, 5 KHKGT, 1 QDVJ, 29 NZVS, 9 GPVTF, 48 HKGWZ => 1 FUEL",
                "12 HKGWZ, 1 GPVTF, 8 PSHF => 9 QDVJ",
                "179 ORE => 7 PSHF",
                "177 ORE => 5 HKGWZ",
                "7 DCFZ, 7 PSHF => 2 XJWVT",
                "165 ORE => 2 GPVTF",
                "3 DCFZ, 7 NZVS, 5 HKGWZ, 10 PSHF => 8 KHKGT"
        );
        assertEquals(82892753, fuelFrom1TrillionOre(input2));

        var input3 = List.of(
                "171 ORE => 8 CNZTR",
                "7 ZLQW, 3 BMBT, 9 XCVML, 26 XMNCP, 1 WPTQ, 2 MZWV, 1 RJRHP => 4 PLWSL",
                "114 ORE => 4 BHXH",
                "14 VRPVC => 6 BMBT",
                "6 BHXH, 18 KTJDG, 12 WPTQ, 7 PLWSL, 31 FHTLT, 37 ZDVW => 1 FUEL",
                "6 WPTQ, 2 BMBT, 8 ZLQW, 18 KTJDG, 1 XMNCP, 6 MZWV, 1 RJRHP => 6 FHTLT",
                "15 XDBXC, 2 LTCX, 1 VRPVC => 6 ZLQW",
                "13 WPTQ, 10 LTCX, 3 RJRHP, 14 XMNCP, 2 MZWV, 1 ZLQW => 1 ZDVW",
                "5 BMBT => 4 WPTQ",
                "189 ORE => 9 KTJDG",
                "1 MZWV, 17 XDBXC, 3 XCVML => 2 XMNCP",
                "12 VRPVC, 27 CNZTR => 2 XDBXC",
                "15 KTJDG, 12 BHXH => 5 XCVML",
                "3 BHXH, 2 VRPVC => 7 MZWV",
                "121 ORE => 7 VRPVC",
                "7 XCVML => 6 RJRHP",
                "5 BHXH, 4 VRPVC => 5 LTCX"
        );
        assertEquals(460664, fuelFrom1TrillionOre(input3));
    }

    static class Reaction {
        List<String> inputType = new ArrayList<>();
        List<Long> inputQuantity = new ArrayList<>();
        String outputType;
        long outputQuantity;

        static Reaction build(String string) {
            var reaction = new Reaction();
            var stringArray = string.split(" => ");
            var inputArray = stringArray[0].split(",");
            var outputArray = stringArray[1].split(" ");
            for (String input : inputArray) {
                var values = input.trim().split(" ");
                reaction.inputQuantity.add(Long.valueOf(values[0]));
                reaction.inputType.add(values[1]);
            }
            reaction.outputQuantity = Long.valueOf(outputArray[0]);
            reaction.outputType = outputArray[1];
            return reaction;
        }
    }
}
