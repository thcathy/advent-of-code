package com.adventofcode.year2017;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Day21Part2 {
    final static String inputFile = "2017/day21_1.txt";
    final static int ITERATION = 18;
    final static Matrix BASE_MATRIX = Matrix.parseFromString(".#./..#/###");
    
    public static void main(String... args) throws IOException {
        Day21Part2 solution = new Day21Part2();
        solution.run();
    }

    void run() throws IOException {
        var lines = Resources.readLines(ClassLoader.getSystemResource(inputFile), Charsets.UTF_8);
        var result = countPixelsStayOn(BASE_MATRIX, parseRules(lines));
        System.out.println("How many pixels stay on after 5 iterations? " + result);
    }

    int countPixelsStayOn(Matrix matrix, Map<Matrix, Matrix> rules) {        
        for (int i = 0; i < ITERATION; i++) {
            matrix = enhance(matrix, rules);           
        }
        return matrix.count('#');
    }
    
    Matrix enhance(Matrix matrix, Map<Matrix, Matrix> rules) {
        int size = matrix.data.length;
        int blockSize = (size % 2 == 0) ? 2 : 3;
        int newBlockSize = blockSize + 1;
        int newSize = size / blockSize * (newBlockSize);
        char[][] newData = new char[newSize][newSize];
        for (int i = 0; i < size; i += blockSize) {
            for (int j = 0; j < size; j += blockSize) {
                Matrix m = matrix.subMatrix(i, j, blockSize);
                Matrix next = m.matchRule(rules);
                fill(newData, next, (i / blockSize) * newBlockSize, (j / blockSize) * newBlockSize);
            }
        }
        return new Matrix(newData);
    }

    void fill(char[][] data, Matrix matrix, int x, int y) {
        int size = matrix.data.length;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                data[i+x][j+y] = matrix.data[i][j];
            }
        }
    }
   
    static Map<Matrix, Matrix> parseRules(List<String> inputs) {
        return inputs.stream()
                        .map(s -> s.split(" => "))
                        .collect(
                            Collectors.toMap(
                                    arr -> Matrix.parseFromString(arr[0]),
                                    arr -> Matrix.parseFromString(arr[1])));
    }
    
    record Matrix(char[][] data) {
        int count(char c) {
            int count = 0;
            for (int i=0; i < data.length; i++) {
                for (int j=0; j < data.length; j++) {
                    if (data[i][j] == c)
                        count++;
                }
            }
            return count;
        }

        Matrix subMatrix(int i, int j, int size) {
            char[][] subData = new char[size][size];
            for (int i2=0; i2 < size; i2++) {
                for (int j2=0; j2 < size; j2++) {
                    subData[i2][j2] = data[i + i2][j + j2];
                }
            }
            return new Matrix(subData);
        }
        
        Matrix matchRule(Map<Matrix, Matrix> rules) {            
            for (Map.Entry<Matrix, Matrix> entry : rules.entrySet()) {
                Matrix key = entry.getKey();
                if (equals(key)
                        || equals(key.flipHorizontal()) || equals(key.flipVertical())
                        || equals(key.rotateClockwise()) || equals(key.rotateAntiClockwise())
                        || equals(key.rotateClockwise().rotateClockwise())
                        || equals(key.flipHorizontal().rotateClockwise())
                        || equals(key.flipVertical().rotateClockwise())) {
                    return entry.getValue();
                }
            }             
            throw new IllegalArgumentException("cannot match any enhancement rule");
        }

        static Matrix parseFromString(String input) {
            String[] rows = input.split("/");
            char[][] grid = new char[rows.length][rows.length];
            for (int i = 0; i < rows.length; i++) {
                String row = rows[i];
                for (int j = 0; j < row.length(); j++) {
                    grid[i][j] = row.charAt(j);
                }
            }
            return new Matrix(grid);
        }
        
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (char[] row : data) {
                for (char c : row) {
                    sb.append(c);
                }
                sb.append(System.lineSeparator());
            }
            return sb.toString();
        }
        
        public boolean equals(Matrix other) {
            if (data.length != other.data.length)
                return false;

            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data.length; j++) {
                    if (data[i][j] != other.data[i][j]) {
                        return false;
                    }
                }
            }
            return true;
        }
        
        public Matrix flipHorizontal() {
            char[][] newData = new char[data.length][data.length];
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data.length; j++) {
                    newData[i][data.length-j-1] = data[i][j];
                }
            }
            return new Matrix(newData);
        }

        public Matrix flipVertical() {
            char[][] newData = new char[data.length][data.length];
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data.length; j++) {
                    newData[data.length-i-1][j] = data[i][j];
                }
            }
            return new Matrix(newData);
        }

        public Matrix rotateClockwise() {
            char[][] newData = new char[data.length][data.length];
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data.length; j++) {
                    newData[j][data.length - i - 1] = data[i][j];
                }
            }
            return new Matrix(newData);
        }
        
        public Matrix rotateAntiClockwise() {
            char[][] newData = new char[data.length][data.length];
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data.length; j++) {
                    newData[data.length-j-1][i] = data[i][j];
                }
            }
            return new Matrix(newData);
        }
    }
    
    @Test
    public void unitTest() throws IOException {
        var m = BASE_MATRIX;        
        assertTrue(Matrix.parseFromString(".#./#../###").equals(m.flipHorizontal()));
        assertTrue(Matrix.parseFromString("###/..#/.#.").equals(m.flipVertical()));
        assertTrue(Matrix.parseFromString("#../#.#/##.").equals(m.rotateClockwise()));
        assertTrue(Matrix.parseFromString(".##/#.#/..#").equals(m.rotateAntiClockwise()));

        var lines = Resources.readLines(ClassLoader.getSystemResource("2017/day21_test.txt"), Charsets.UTF_8);
        var rules = parseRules(lines);
        var m2 = enhance(m, rules);
        assertTrue(Matrix.parseFromString("#..#/..../..../#..#").equals(m2));
        var m3 = enhance(m2, rules);
        assertTrue(Matrix.parseFromString("##.##./#..#../....../##.##./#..#../......").equals(m3));
        assertEquals(12, m3.count('#'));
        
    }
}
