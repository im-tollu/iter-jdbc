# iter-jdbc
Iterator-focused JDBC wrapper library

This project was inspired by Spring-Jdbc. In particular, named parameter SQL
 queries and stream-like result processing, without storing the whole
  result set in memory. But unlike Spring-Jdbc, the goal of this project is
   to provide and accept iterators wherever possible instead of callbacks and
    collections.
    
## Usage

First, `JdbcFactory` should be implemented. Its responsibility is to current
 or new `java.sql.connection` and use it to create instances of `Queries` and
  `PreparedQueries`. It may be needed to use current connection if queries
   should be executed in some transaction.
   
Next, create an instance of `JdbcOperations` - it can be long-lived and used
 as a field of repository classes. Don't forget to close CloseableIterators
  in the end.
 
```java
public class Example {
    public void printUsers(JdbcFactory jdbcFactory, String role) {
        JdbcOperations jdbc = new JdbcOperations(jdbcFactory);
        
        var results = jdbc.executeQuery(
          "select USERNAME from USERS where ROLE = :userRole",
          Map.of("userRole", role),
          rs -> rs.getString("USERNAME")
        );
        
        results.forEachRemaining(System.out::println);  
        
        results.close();
    }
}
```
