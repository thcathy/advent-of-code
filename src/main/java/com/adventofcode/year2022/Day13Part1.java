package com.adventofcode.year2022;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Day13Part1 {
    final static String inputFile = "2022/day13.txt";

    public static void main(String... args) throws IOException {
        Day13Part1 solution = new Day13Part1();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = sumPairsInRightOrder(lines);
        System.out.println("What is the sum of the indices of those pairs? " + result);
    }

    int sumPairsInRightOrder(List<String> inputs) {
        int sum = 0;
        int pairs = 0;
        for (int i = 0; i < inputs.size(); i+=3) {
            pairs++;
            if (compare(parse(inputs.get(i)), parse(inputs.get(i+1))) <= 0)
                sum += pairs;
        }
        return sum;
    }

    int compare(Item left, Item right) {
        if (left instanceof ValueItem leftValue && right instanceof ValueItem rightValue) return leftValue.value - rightValue.value;

        if (left instanceof ValueItem && right instanceof ListItem) return compare(new ListItem(List.of(left)), right);

        if (left instanceof ListItem && right instanceof ValueItem) return compare(left, new ListItem(List.of(right)));

        var list1 = ((ListItem)left).list;
        var list2 = ((ListItem)right).list;
        if (list1.isEmpty()) return list2.isEmpty() ? 0 : -1;

        var minSize = Math.min(list1.size(), list2.size());

        for (int i=0; i<minSize; i++) {
            var value = compare(list1.get(i), list2.get(i));
            if (value != 0) return value;
        }
        return list1.size() - list2.size();
    }

    Item parse(String input) {
        if (input.length() == 0) return new ListItem(Collections.EMPTY_LIST);

        if (input.startsWith("[")) {
            input = input.substring(1, input.length()-1);
            return new ListItem(divideTheList(input).stream().map(this::parse).toList());
        }

        if (!input.contains(",")) return new ValueItem(Integer.parseInt(input));

        throw new RuntimeException();
    }

    List<String> divideTheList(String input) {
        List<String> strings = new ArrayList<>();
        int i = 0, level = 0, start = 0;
        while (i < input.length()) {
            if (input.charAt(i) == '[') {
                level++;
            } else if (input.charAt(i) == ']') {
                level--;
            } else if (input.charAt(i) == ',' && level == 0) {
                strings.add(input.substring(start, i));
                start = i+1;
            }

            i++;
        }
        if (start < input.length())
            strings.add(input.substring(start));
        return strings;
    }

    interface Item {}

    record ValueItem(int value) implements Item {}

    record ListItem(List<Item> list) implements Item {}

    @Test
    public void unitTest() throws Exception {
        var lines = Files.readAllLines(Paths.get(ClassLoader.getSystemResource("2022/day13_test.txt").toURI()), Charset.defaultCharset());

        var items = (ListItem) parse("[1,1,3,1,1]");
        assertEquals(5, items.list.size());
        var items2 = (ListItem) parse("[[1],[2,3,4]]");
        assertEquals(2, items2.list.size());
        assertEquals(1, ((ListItem)items2.list.get(0)).list.size());
        assertEquals(3, ((ListItem)items2.list.get(1)).list.size());
        assertEquals(9, ((ValueItem)parse("9")).value);
        var items3 = (ListItem) parse("[[]]");
        assertEquals(1, items3.list.size());
        assertTrue(items3.list.get(0) instanceof ListItem);
        var items4 = (ListItem) parse("[1,[2,[3,[4,[5,6,7]]]],8,9]");
        assertEquals(4, items4.list.size());

        var itemsB = parse("[1,1,5,1,1]");
        var items2B = parse("[[1],4]");

        assertTrue(compare(items, itemsB) <= 0);
        assertTrue(compare(items2, items2B) <= 0);
        assertTrue(compare(parse("9"), parse("[[8,7,6]]")) > 0);

        assertEquals(13, sumPairsInRightOrder(lines));
    }
}
