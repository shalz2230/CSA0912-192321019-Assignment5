
# TASK B1 – IMPLEMENT AND INSTRUMENT THE FILE STORE

## B1.1 – Validate the Implementation

The PassStore program uses a text file to store bus-pass records. Each valid record contains five comma-separated fields: pass ID, register number, student name, route number and amount.

| Test | Input | Expected result | What it verifies |
|---|---|---|---|
| Add | P1001, 192311001, Arun, 12, 4500 | Record added | Checks file creation and appending |
| Find existing | P1003 | Returns P1003 record | Checks successful searching |
| Find damaged | P1002 | Returns null | Checks damaged-line handling |
| Find missing | P9999 | Returns null | Checks missing ID handling |
| List | All records | Displays valid records | Checks listing and damaged-line skipping |

The damaged P1002 line has only two fields instead of five. The find method checks the number of fields and skips invalid lines using `continue`. Therefore, the damaged line does not stop the search.

## B1.2 – Compare with the Design

The implementation follows the Part A1 design by storing records in a text file and using buffered character-based file reading and writing.

The find method reads the file line by line, separates the fields and compares the pass ID. A validation condition was added to skip damaged records that do not contain exactly five fields.

A list method was also included to display valid records.

The implementation uses a simple comma-separated format. Student names containing commas would require an escaping or quoting rule in a more complete file format.

## B1.3 – Measure the Effect of Buffering

The FileBenchmark program measures line-counting time using an unbuffered byte-by-byte approach and a BufferedReader approach.

Actual measurements from my execution:

| File size | Unbuffered time (ms) | Buffered time (ms) |
|---|---:|---:|
| 1 MB | [enter result] | [enter result] |
| 10 MB | [enter result] | [enter result] |
| 50 MB | [enter result] | [enter result] |

### Interpretation

The unbuffered method reads one byte at a time, which can cause many input operations. The buffered method reads larger blocks into memory and processes them efficiently.

I expect buffering to reduce the time needed for larger files. Actual performance depends on the computer, storage device, JVM and operating system.

## B1.4 – Measure Growth

The program measures the time needed to find the last pass record as the number of records increases.

Actual measurements from my execution:

| Number of records | Average search time (ms) |
|---:|---:|
| 1,000 | [enter result] |
| 10,000 | [enter result] |
| 50,000 | [enter result] |
| 100,000 | [enter result] |

### Interpretation

The find method scans records sequentially until it finds the required pass ID. When the target is near the end, more records must be checked as the file grows.

Therefore, the search time generally increases with the number of records. The time complexity of one search is O(n), where n is the number of records.

### Alternative: HashMap

If the file is read once at program start and stored in a HashMap using pass ID as the key, searches can be performed in average O(1) time.

For q searches on n records:

- Sequential file search: O(q × n)
- Load once into HashMap and search: O(n + q), on average

The sequential approach uses less additional memory because it reads one line at a time. The HashMap approach requires O(n) additional memory to store the records.

### Conclusion

The file store successfully adds, finds and lists valid bus-pass records. Damaged records are skipped without stopping the search. Buffering improves file-reading efficiency, while a HashMap can improve repeated search performance at the cost of additional memory.
