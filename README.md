# iter-jdbc
Iterator-focused JDBC wrapper library

This project was inspired by Spring-Jdbc. In particular, named parameter SQL
 queries and stream-like result processing, without storing the whole
  result set in memory. But unlike Spring-Jdbc, the goal of this project is
   to provide and accept iterators wherever possible instead of callbacks and
    collections.
    
## Usage

To use this library you need an instance of `java.sql.Connection`.

```
Connection conn = dataSource.getConnection();
```

### Queries that return result sets

**Single-usage query** - PreparedStatement is closed after execution.

```
var query = new Query(
    "select USERNAME from USERS where ROLE = :userRole",
    rs -> rs.getString("USERNAME")
);
try(var teachers = query.connect(conn).runOnce(Map.of("userRole", "teacher"))) {
    teachers.forEachRemaining(System.out::println);
}
```

**Single-usage single-result query** - PreparedStatement and results are
 closed after execution.

```
var query = new Query(
    "select count(1) from USERS where ROLE = :userRole",
    rs -> rs.getString("USERNAME")
);
var teachersCount = query.connect(conn).runOnceForSingleResult(Map.of("userRole", "teacher"));

System.out.println("Total teachers in the college: " + teachersCount);
```
 
**Reusable query** - PreparedStatement is left open after execution.
 
```
try(
   var query = new Query(
       "select USERNAME from USERS where ROLE = :userRole",
       rs -> rs.getString("USERNAME")
   )
) {
   System.out.println("Teachers:");
   try(var teachers = query.connect(conn).run(Map.of("userRole", "teacher"))) {
       teachers.forEachRemaining(System.out::println);
   }
   
   System.out.println("\nStudents:");
   try(var students = query.connect(conn).run(Map.of("userRole", "students"))) {
       students.forEachRemaining(System.out::println);
   }
}
```

**Reusable single-result query** - PreparedStatement is left open and
 result set is closed after execution.
 
```
try(
   var query = new Query(
       "select count(1) from USERS where ROLE = :userRole",
       rs -> rs.getString("USERNAME")
   )
) {
   var teachersCount = query.connect(conn).runForSingleResult(Map.of("userRole", "teacher"));
   System.out.println("Total teachers in the college: " + teachersCount);
   
   var studentsCount = query.connect(conn).runForSingleResult(Map.of("userRole", "student"));
   System.out.println("Total students in the college: " + studentsCount);
}
```
