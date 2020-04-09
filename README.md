# iter-jdbc
Iterator-focused JDBC wrapper library

This project was inspired by Spring-Jdbc. In particular, named parameter SQL
 queries and stream-like result processing, without storing the whole
  result set in memory. But unlike Spring-Jdbc, the goal of this project is
   to provide and accept iterators wherever possible instead of callbacks and
    collections.
