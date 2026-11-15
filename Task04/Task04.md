
# TASK A4 – WHAT SHOULD BE SERIALIZED?

## A4.1 – Decide What to Persist

The Hostel object contains a list of Room objects. Each Room object contains a list of Resident objects.

Each Resident contains:
- Name
- Register Number
- Password
- Database Connection reference

### Fields to be Serialized

1. Hostel details must be serialized because they represent the main hostel data.
2. Room details must be serialized because they contain room information.
3. Resident name must be serialized because it identifies the resident.
4. Register number must be serialized because it identifies the student.
5. Password should not be serialized in plain text because it is sensitive information. It should be stored securely using a password hash in an appropriate authentication system.
6. Database Connection must be marked transient because it is a live resource and cannot be meaningfully serialized.

Example:

private transient Connection connection;

### Object Graph Serialization

When the Hostel object is serialized, Java automatically serializes the reachable objects in its object graph.

The process includes:
- Hostel object
- Room objects
- Resident objects

All reachable serializable objects are saved automatically.

The transient database Connection field is skipped during serialization.

When the object is deserialized, the transient field receives its default value, which is null for an object reference.

## A4.2 – Plan for Change

Suppose a new phone number field is added to the Resident class after old serialization files have already been created.

Example:

private String phoneNumber;

### Reading Old Files

When an old serialized file is read:

1. Java checks the serialVersionUID of the saved class and the current class.
2. If both serialVersionUID values match, deserialization can continue.
3. The newly added phoneNumber field receives its default value, which is null.
4. Existing fields are restored from the old serialized file.

### Role of serialVersionUID

serialVersionUID identifies the version of a serializable class.

Example:

private static final long serialVersionUID = 1L;

If the serialVersionUID in the saved object does not match the current class version, Java throws InvalidClassException.

Maintaining the same serialVersionUID allows certain compatible class changes, such as adding a new field, to be handled during deserialization.

## Alternative Concept – CSV or JSON

Instead of Java serialization, hostel data can be stored using CSV or JSON formats.

### CSV Approach

Each resident can be stored as one row.

Example:

name,registerNumber,phoneNumber
Arun,192311001,9876543210
Divya,192311003,9876501234

### JSON Approach

Resident information can be represented as structured objects.

Example:

{
  "name": "Arun",
  "registerNumber": "192311001",
  "phoneNumber": "9876543210"
}

### Comparison

1. Java Serialization:
   - Stores Java objects in binary format.
   - Can efficiently preserve object relationships.
   - Requires compatible Java class definitions.
   - File format is less human-readable.

2. CSV:
   - Stores data as plain text.
   - Easy to open and edit.
   - Requires manual handling of object relationships.
   - New columns may require updates to reading and writing logic.

3. JSON:
   - Stores structured data in readable text format.
   - Supports nested objects and lists.
   - New optional fields can be handled using suitable parsing logic.
   - Usually requires additional parsing and object mapping.

### Complexity Analysis

Let n represent the number of residents.

Java Serialization:
- Saving time: O(n) for a resident object graph.
- Loading time: O(n).
- Space complexity: O(n) for the serialized data and object graph.

CSV:
- Saving time: O(n).
- Loading time: O(n).
- Space complexity: O(n).

JSON:
- Saving time: O(n).
- Loading time: O(n).
- Space complexity: O(n).

Actual file sizes and execution times depend on the data and implementation.

### Conclusion

Java serialization is useful when saving and restoring Java object graphs. However, sensitive information and live database connections should not be serialized. CSV and JSON are more portable and human-readable alternatives. JSON is especially useful for structured data containing nested objects.
