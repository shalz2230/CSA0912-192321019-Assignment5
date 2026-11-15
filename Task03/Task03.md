
# TASK A3 – DISCOVER THE DATA-ACCESS DESIGN PATTERN

## A3.1 – Identify the Common Pattern

The three classes StudentDB, CourseDB and MarksDB perform similar database operations.

Common steps:

1. Establish a database connection.
2. Create an SQL query.
3. Execute the SQL statement.
4. Read the ResultSet.
5. Convert database records into Java objects.
6. Close the database connection.

Parts that differ:

- StudentDB works with student records.
- CourseDB works with course records.
- MarksDB works with marks records.
- SQL queries and table columns are different.
- Java object fields are different.

The repeated code can be moved into a common Data Access Object (DAO) design.

## A3.2 – Design a Generic Data-Access Interface

A generic DAO interface provides common database operations for different entity types.

Java Code:

import java.util.List;
import java.util.Optional;

public interface Dao<T, K> {

    void insert(T entity);

    Optional<T> findById(K id);

    List<T> findAll();

    void update(T entity);

    void delete(K id);
}

Student Class:

public class Student {

    private int studentId;
    private String name;
    private String department;

    public Student(int studentId, String name, String department) {
        this.studentId = studentId;
        this.name = name;
        this.department = department;
    }

    public int getStudentId() {
        return studentId;
    }

    public String getName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }
}

StudentDao Implementation Design:

import java.sql.*;
import java.util.*;

public class StudentDao implements Dao<Student, Integer> {

    private Connection con;

    public StudentDao(Connection con) {
        this.con = con;
    }

    @Override
    public void insert(Student s) {
        String sql = "INSERT INTO student VALUES (?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, s.getStudentId());
            ps.setString(2, s.getName());
            ps.setString(3, s.getDepartment());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Student> findById(Integer id) {
        String sql = "SELECT * FROM student WHERE student_id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Student s = new Student(
                        rs.getInt("student_id"),
                        rs.getString("name"),
                        rs.getString("department")
                    );
                    return Optional.of(s);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public List<Student> findAll() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM student";

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                students.add(new Student(
                    rs.getInt("student_id"),
                    rs.getString("name"),
                    rs.getString("department")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return students;
    }

    @Override
    public void update(Student s) {
        String sql = "UPDATE student SET name=?, department=? WHERE student_id=?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, s.getName());
            ps.setString(2, s.getDepartment());
            ps.setInt(3, s.getStudentId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Integer id) {
        String sql = "DELETE FROM student WHERE student_id=?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

Explanation:

The Dao<T,K> interface defines common operations. StudentDao implements these operations for Student objects. The same interface can be reused for Course and Marks entities.

## A3.3 – Map Objects to Tables

Student Java class is mapped to the student database table.

| Java Field | Database Column | Data Type |
|---|---|---|
| studentId | student_id | INT |
| name | name | VARCHAR(100) |
| department | department | VARCHAR(100) |

Primary Key: student_id

SQL Table Creation:

CREATE TABLE student (
    student_id INT PRIMARY KEY,
    name VARCHAR(100),
    department VARCHAR(100)
);

The student_id uniquely identifies every student record.

## Alternative Concept – Direct SQL vs Generic DAO

Direct SQL inside service or GUI classes:

- SQL statements are written directly in multiple classes.
- Database changes require modifying several locations.
- Code duplication increases.
- Maintenance becomes difficult.

Generic DAO approach:

- SQL operations are centralized in DAO classes.
- Service and GUI classes call DAO methods.
- Database changes are easier to maintain.
- Code reuse improves.

### Complexity Analysis

1. Direct SQL approach:
   - Time complexity per database operation: Depends on the database query and indexes.
   - Space complexity: Depends on the records returned.
   - Maintenance: More places may require changes.

2. Generic DAO approach:
   - Time complexity per database operation: Same as direct SQL for equivalent queries.
   - Space complexity: Same for equivalent query results, with small additional object overhead.
   - Maintenance: Fewer places require changes.

### Conclusion

The DAO design pattern separates database operations from application logic. It improves code reusability, maintainability and organization. A generic DAO interface allows Student, Course and Marks entities to use common database operations.
