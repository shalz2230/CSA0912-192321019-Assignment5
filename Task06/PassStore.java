
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

public class PassStore {
    private final Path file;

    PassStore(Path file) {
        this.file = file;
    }

    public void add(String passId, String regNo, String name,
                    int route, double amount) throws IOException {
        try (BufferedWriter w = Files.newBufferedWriter(
                file, StandardOpenOption.CREATE,
                StandardOpenOption.APPEND)) {

            w.write(String.join(",", passId, regNo, name,
                    String.valueOf(route), String.valueOf(amount)));
            w.newLine();
        }
    }

    // Finds a pass by ID. Returns null if it is not found.
    public String[] find(String passId) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",", -1);

                // Skip damaged records
                if (fields.length != 5) {
                    continue;
                }

                if (fields[0].equals(passId)) {
                    return fields;
                }
            }
        }

        return null;
    }

    // Lists all valid pass records.
    public void list() throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",", -1);

                if (fields.length == 5) {
                    System.out.println(Arrays.toString(fields));
                } else {
                    System.out.println("Damaged line skipped: " + line);
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Path f = Files.createTempFile("passes", ".csv");
        PassStore store = new PassStore(f);

        store.add("P1001", "192311001", "Arun", 12, 4500);
        Files.writeString(f, "P1002,DAMAGED LINE\n",
                StandardOpenOption.APPEND);
        store.add("P1003", "192311003", "Divya", 7, 3800);

        System.out.println("Find existing pass:");
        System.out.println(Arrays.toString(store.find("P1003")));

        System.out.println("Find damaged pass:");
        System.out.println(Arrays.toString(store.find("P1002")));

        System.out.println("Find missing pass:");
        System.out.println(Arrays.toString(store.find("P9999")));

        System.out.println("List all valid passes:");
        store.list();

        Files.delete(f);
    }
}
