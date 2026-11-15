
# TASK A5 – DESIGN FOR CORRECTNESS AND SAFETY

## A5.1 – Transaction Design

A student transfers ₹500 from a hostel wallet to a canteen wallet. Both wallet updates must be completed successfully. If either update fails, the transaction must be rolled back so that money is not lost or created.

### JDBC Transaction Pseudocode

```java
Connection con = DriverManager.getConnection(url, user, password);

try {
    con.setAutoCommit(false);

    // Deduct ₹500 from hostel wallet
    PreparedStatement debit = con.prepareStatement(
        "UPDATE wallet SET balance = balance - ? WHERE wallet_id = ? AND balance >= ?"
    );
    debit.setDouble(1, 500);
    debit.setInt(2, hostelWalletId);
    debit.setDouble(3, 500);

    int debitRows = debit.executeUpdate();

    if (debitRows != 1) {
        throw new SQLException("Insufficient balance or hostel wallet not found");
    }

    // Add ₹500 to canteen wallet
    PreparedStatement credit = con.prepareStatement(
        "UPDATE wallet SET balance = balance + ? WHERE wallet_id = ?"
    );
    credit.setDouble(1, 500);
    credit.setInt(2, canteenWalletId);

    int creditRows = credit.executeUpdate();

    if (creditRows != 1) {
        throw new SQLException("Canteen wallet not found");
    }

    // Save both updates together
    con.commit();

} catch (SQLException e) {
    // Undo changes if any operation fails
    con.rollback();
    System.out.println("Transfer failed. Transaction rolled back.");

} finally {
    con.setAutoCommit(true);
    con.close();
}
```

### Explanation

1. `setAutoCommit(false)` starts a transaction.
2. The first SQL statement deducts ₹500 from the hostel wallet.
3. The second SQL statement adds ₹500 to the canteen wallet.
4. `commit()` permanently saves both changes if both updates succeed.
5. If an error occurs, `rollback()` cancels the changes made in the transaction.

Therefore, the two wallet updates behave as one unit. The transaction prevents a partial transfer.

### Alternative: Two Independent Auto-Commit Updates

If both updates use separate auto-commit statements, the first deduction may be permanently saved before the second update executes. If the program fails at that point, ₹500 is deducted from the hostel wallet but never reaches the canteen wallet.

This causes money loss and incorrect wallet balances.

A transaction has some additional overhead for transaction management, logging and locking. However, both approaches perform two database updates, so their basic update work is constant, O(1), for a fixed number of wallet records. A transaction is safer because it maintains data correctness.

---

## A5.2 – Injection Analysis

### Unsafe Login Query

```java
String sql = "SELECT * FROM users WHERE id='" + id +
             "' AND pwd='" + pwd + "'";
```

This query is unsafe because it directly joins user input with SQL commands.

An attacker may enter the following as the password:

```text
' OR '1'='1' --
```

The resulting SQL condition can become:

```sql
SELECT * FROM users
WHERE id='student' AND pwd='' OR '1'='1' -- '
```

The condition `'1'='1'` is always true. The `--` comments out the remaining part of the SQL statement in common SQL dialects. This may allow the login check to succeed without the correct password, depending on the query and application logic.

This attack is called SQL injection.

### Safe Login Using PreparedStatement

```java
String sql =
    "SELECT * FROM users WHERE id = ? AND pwd = ?";

PreparedStatement ps = con.prepareStatement(sql);

ps.setString(1, id);
ps.setString(2, pwd);

ResultSet rs = ps.executeQuery();

if (rs.next()) {
    System.out.println("Login successful");
} else {
    System.out.println("Invalid username or password");
}
```

### Explanation

- `?` placeholders represent user-supplied values.
- `setString()` passes the values separately from the SQL command.
- User input is treated as data rather than executable SQL.
- Therefore, SQL injection through these input values is prevented.

In a real application, passwords should be stored as secure password hashes and verified using an appropriate password-hashing method rather than stored or compared as plain text.

### Conclusion

JDBC transactions protect wallet transfers from partial updates, while `PreparedStatement` protects login queries from SQL injection. Both techniques improve the correctness and security of database applications.
