
import java.io.*;
import java.nio.file.*;
import java.util.Locale;

public class FileBenchmark {

    // Count newline characters by reading one byte at a time.
    static long countUnbuffered(Path file) throws IOException {
        long lines = 0;

        try (InputStream in = Files.newInputStream(file)) {
            int data;
            while ((data = in.read()) != -1) {
                if (data == '\n') {
                    lines++;
                }
            }
        }

        return lines;
    }

    // Count lines using BufferedReader.
    static long countBuffered(Path file) throws IOException {
        long lines = 0;

        try (BufferedReader reader = Files.newBufferedReader(file)) {
            while (reader.readLine() != null) {
                lines++;
            }
        }

        return lines;
    }

    static void createAttendanceFile(Path file, int megabytes)
            throws IOException {
        long targetBytes = megabytes * 1024L * 1024L;
        String record = "Attendance record for student 192321019\n";

        try (BufferedWriter writer = Files.newBufferedWriter(file)) {
            long written = 0;

            while (written < targetBytes) {
                writer.write(record);
                written += record.getBytes().length;
            }
        }
    }

    static double timeCount(Path file, boolean buffered)
            throws IOException {
        long start = System.nanoTime();

        if (buffered) {
            countBuffered(file);
        } else {
            countUnbuffered(file);
        }

        return (System.nanoTime() - start) / 1_000_000.0;
    }

    static void createPassFile(Path file, int records)
            throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(file)) {
            for (int i = 1; i <= records; i++) {
                writer.write("P" + i + ",192321019,Student" + i
                        + ",12,4500.0");
                writer.newLine();
            }
        }
    }

    static double timeFind(Path file, String id)
            throws IOException {
        PassStore store = new PassStore(file);

        long start = System.nanoTime();
        store.find(id);
        return (System.nanoTime() - start) / 1_000_000.0;
    }

    public static void main(String[] args) throws IOException {
        System.out.println("BUFFERING MEASUREMENTS");
        System.out.println("Size | Unbuffered ms | Buffered ms");

        for (int size : new int[]{1, 10, 50}) {
            Path file = Files.createTempFile("attendance", ".txt");
            createAttendanceFile(file, size);

            // Warm-up buffered and unbuffered methods.
            countUnbuffered(file);
            countBuffered(file);

            double unbufferedTime = timeCount(file, false);
            double bufferedTime = timeCount(file, true);

            System.out.printf(Locale.US, "%d MB | %.3f | %.3f%n",
                    size, unbufferedTime, bufferedTime);

            Files.delete(file);
        }

        System.out.println("\nSEARCH GROWTH MEASUREMENTS");
        System.out.println("Records | Search time ms");

        for (int count : new int[]{1000, 10000, 50000, 100000}) {
            Path file = Files.createTempFile("passes", ".csv");
            createPassFile(file, count);

            String lastId = "P" + count;

            // Warm-up search.
            timeFind(file, lastId);

            double total = 0;
            int repetitions = 5;

            for (int i = 0; i < repetitions; i++) {
                total += timeFind(file, lastId);
            }

            System.out.printf(Locale.US, "%d | %.3f%n",
                    count, total / repetitions);

            Files.delete(file);
        }
    }
}
