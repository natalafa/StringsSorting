package com.company;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Main {
    public static final int RAM_LIMIT = 16 * 1024 * 1024;

    public static void main(String[] args) throws IOException {
        //String fileName = "/Users/nafanaseva/Projects/StringSorting/src/data2.txt";
        //String fileName = "/Users/nafanaseva/Projects/StringSorting/src/com/company/book_small.txt";
        String fileName = "/Users/nafanaseva/Projects/StringSorting/src/com/company/book.txt";
        //String fileName = "/Users/nafanaseva/Projects/StringSorting/src/data.txt";
        double startMemory = (double)Runtime.getRuntime().freeMemory() / 1024 / 1024;
        System.out.println("Memory at the start :" + startMemory);
        try (RandomAccessFile raf = new RandomAccessFile(fileName, "r")) {
            int bufferSize = RAM_LIMIT / 4;
            int length;
            int readFrom = 0;
            int iterationIndex = 0;
            int lastNewLineSymbolIndexInFile = 0;
            // todo:
            // 1) figure out the way of reading the data with offset
            // 2) dump sorted data into the chunks
            // 3) n-way merge into the resulting file
            while(true) {
                byte[] buff = new byte[bufferSize];
                System.out.println("Iteration start. File cursor at: " + raf.getFilePointer());
                raf.seek(lastNewLineSymbolIndexInFile);
                length = raf.read(buff, readFrom, bufferSize);
                if (length < 0) {
                    break;
                }
                StringBuilder sb = new StringBuilder();
                String s1 = new String(buff);
                sb.append(s1);
                int lastNewLineSymbolIndexInChunk = sb.lastIndexOf("\n");
                if (lastNewLineSymbolIndexInChunk == -1) {
                    lastNewLineSymbolIndexInChunk = sb.length();
                }
                String readChunk = sb.substring(0, lastNewLineSymbolIndexInChunk);
                if (lastNewLineSymbolIndexInChunk == sb.length()) {
                    lastNewLineSymbolIndexInFile += lastNewLineSymbolIndexInChunk;
                } else {
                    lastNewLineSymbolIndexInFile += (lastNewLineSymbolIndexInChunk + 1);
                }
                List<String> lines = Arrays.asList(readChunk.split("\n"));
                Collections.sort(lines);
                saveToFile(lines, "data_chunk_" + iterationIndex);
                iterationIndex++;
                System.out.println("Iteration end. File cursor at: " + raf.getFilePointer());
                System.out.println("Read chars: " + length);
                System.out.println("Last new line symbol index: " + lastNewLineSymbolIndexInFile);
                System.out.println("================");
            }
        }

        double endMemory = (double)Runtime.getRuntime().freeMemory() / 1024 / 1024;
        System.out.println("Memory at the end :" + endMemory);
        double totalConsumedMemory = (startMemory - endMemory);
        System.out.println("Consumed memory (MB): " + totalConsumedMemory);
    }

    public static void saveToFile(List<String> lines, String fileName) throws IOException {
        try (BufferedWriter br = new BufferedWriter(new FileWriter(fileName))) {
            for (String line : lines) {
                if ("".equals(line.trim())) {
                    continue;
                }
                br.write(line);
                br.newLine();
            }
        }
    }
}

