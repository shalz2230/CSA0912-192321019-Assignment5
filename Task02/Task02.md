
# TASK A2 – DON'T READ WHAT YOU DON'T NEED

## A2.1 – Predict the Cost

The attendance log has a size of 50 MB.

If the program reads one byte at a time using FileInputStream.read(), it makes approximately 52,428,800 read calls (assuming 1 MB = 1,048,576 bytes).

This approach is slow because every read operation involves method-call overhead. Reading one byte at a time is inefficient.

## A2.2 – Redesign Using Buffering

The program can use BufferedInputStream to read data efficiently.

Java code:

import java.io.*;

public class BufferedCounter {
    public static void main(String[] args) throws IOException {
        long lines = 0;

        try (BufferedInputStream bis =
                 new BufferedInputStream(new FileInputStream("attendance.txt"))) {

            int data;
            while ((data = bis.read()) != -1) {
                if (data == '\n') {
                    lines++;
                }
            }
        }

        System.out.println("Total lines: " + lines);
    }
}

Explanation:

BufferedInputStream reads a block of data into an internal buffer instead of accessing the file for every byte. This reduces the number of underlying file read operations and improves performance.

## A2.3 – When Buffering Does Not Help

Buffering provides little benefit when the entire data is already available in memory or when the application reads only a very small file once.

## Alternative Concept – Files.readAllLines()

Alternative Java code:

import java.nio.file.*;
import java.io.IOException;
import java.util.List;

public class ReadAllLines {
    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Path.of("attendance.txt"));
        System.out.println("Total lines: " + lines.size());
    }
}

Complexity Analysis:

1. Buffered reading:
   - Time Complexity: O(n), where n is the file size.
   - Space Complexity: O(b), where b is the buffer size.
   - Suitable for large files.

2. Files.readAllLines():
   - Time Complexity: O(n).
   - Space Complexity: O(n), because all lines are stored in memory.
   - Suitable for small files that fit comfortably in memory.

## Conclusion

Buffered reading is better for large files because it uses limited memory and reduces file access overhead. Files.readAllLines() is simpler but consumes more memory because it loads all lines at once.
