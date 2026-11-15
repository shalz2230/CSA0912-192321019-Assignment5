
# TASK A1 – DESIGN A FILE FORMAT

## A1.1 – Represent the Record

The transport office stores bus-pass records containing seven fields.

### (a) Delimited Text File

File name: buspasses.txt

Field order:

1. Pass ID
2. Student Register Number
3. Student Name
4. Route Number
5. Boarding Stop
6. Validity Date
7. Amount Paid

Example record:

P1001|192311001|Arun Kumar|12|Central Stop|2027-06-30|4500.00

Delimiter: Pipe symbol (|).

If a field contains the pipe symbol, it will be escaped using a backslash. Backslashes inside fields will also be escaped. This prevents confusion while reading records.

### (b) Binary File using DataOutputStream

File name: buspasses.dat

Field order:

1. Pass ID – writeUTF()
2. Register Number – writeUTF()
3. Student Name – writeUTF()
4. Route Number – writeInt()
5. Boarding Stop – writeUTF()
6. Validity Date – writeUTF()
7. Amount Paid – writeDouble()

The DataOutputStream writes primitive values in binary format. Strings written using writeUTF() contain a length prefix, so they do not require a fixed field size.

### A1.2 – Design the Operations

#### 1. Add a New Pass

Pseudocode:

BEGIN
    OPEN file using BufferedWriter in append mode
    RECEIVE pass details
    VALIDATE all input fields
    CONVERT each field into delimited format
    WRITE the record into the file
    WRITE newline
    CLOSE file
END

Stream classes:
- FileWriter: Writes character data.
- BufferedWriter: Improves writing efficiency by buffering data.

#### 2. Find a Pass by Pass ID

Pseudocode:

BEGIN
    OPEN file using BufferedReader
    INPUT required Pass ID

    WHILE another line exists
        READ one line
        SPLIT line into fields

        IF number of fields is not seven
            SKIP damaged line
        ELSE IF Pass ID matches required Pass ID
            DISPLAY matching record
            CLOSE file
            STOP
        END IF
    END WHILE

    DISPLAY "Pass not found"
    CLOSE file
END

Stream classes:
- FileReader: Reads character data.
- BufferedReader: Reads complete lines efficiently.

#### 3. List All Passes on a Given Route

Pseudocode:

BEGIN
    OPEN file using BufferedReader
    INPUT required route number
    SET found = FALSE

    WHILE another line exists
        READ one line
        SPLIT line into fields

        IF number of fields is not seven
            SKIP damaged line
        ELSE IF route number matches required route
            DISPLAY record
            SET found = TRUE
        END IF
    END WHILE

    IF found = FALSE
        DISPLAY "No passes found for this route"
    END IF

    CLOSE file
END

Stream classes:
- FileReader: Reads characters from the file.
- BufferedReader: Reads records line by line.

### A1.3 – Design for Damaged Data

1. Damaged Line:
   Check whether each line contains exactly seven fields. Invalid records are skipped and recorded in a separate error log.

2. Missing File:
   Check whether the file exists before reading. If it does not exist, display an appropriate error message. When adding a new pass, create the file if necessary.

3. Incomplete Write:
   Write new data to a temporary file and use an atomic file replacement where supported. Flush and close streams properly. Keep a backup to recover data after a failure.

### Alternative Concept – RandomAccessFile

If RandomAccessFile with fixed-length records is used, each record occupies the same number of bytes.

The program can calculate the record position using:

Position = Record Index × Record Size

It can then use seek() to access the record directly.

Time Complexity:

- Text file search by ID: O(n), because records are scanned sequentially.
- RandomAccessFile access by known record index: O(1), because seek() jumps directly to the record.
- Searching by ID without an index still requires O(n) scanning.
- With an ID-to-record-index lookup structure, searching can be approximately O(1) on average using a hash map.

Space Complexity:

- Text file: Stores variable-length records and generally uses less space.
- Fixed-length binary file: May waste space because shorter values require padding.
- Hash map index: Requires additional O(n) memory.

### Conclusion

Delimited text files are easy to read, maintain and debug. Binary files are more compact and efficient for structured data. RandomAccessFile is useful when records need direct access by their known record positions.
