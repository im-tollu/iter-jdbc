package com.codeborne.iterjdbc;

import com.codeborne.iterjdbc.named.NamedSql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

/**
 * This class is for making SQL queries that return a result set. It combines SQL with a row mapper.
 *
 * Use it for SELECT queries, event the ones like `select count(*)` that return a single result.
 *
 * A value object that can be stored, shared and reuse as many times as needed. It doesn't store any
 * resources by itself, but serves as an entry point to the API.
 *
 * @see Update for SQL queries that don't return a result set.
 *
 * @param <E> - element; each row from the result set will be mapped to an instance of this type.
 */
public class Query<E> {
  private final NamedSql namedSql;
  private final RowMapper<E> rowMapper;

  /**
   * This constructor will be used most of the time
   * @param sql - SQL query with named parameters like this: `select * from BOOKS where ID = :bookId`
   * @param rowMapper - maps {@link java.sql.ResultSet} to an instance of E
   */
  public Query(String sql, RowMapper<E> rowMapper) {
    this.namedSql = NamedSql.parse(sql);
    this.rowMapper = rowMapper;
  }

  /**
   * @param namedSql - an SQL query that's been already parsed into {@link NamedSql}
   * @param rowMapper - maps {@link java.sql.ResultSet} to an instance of E
   */
  public Query(NamedSql namedSql, RowMapper<E> rowMapper) {
    this.namedSql = namedSql;
    this.rowMapper = rowMapper;
  }

  /**
   * @param conn - JDBC connection
   * @return prepared query that is bound the the connection
   */
  public PreparedQuery<E> connect(Connection conn) {
    try {
      PreparedStatement stmt = conn.prepareStatement(namedSql.getSqlPositional());
      return new PreparedQuery<>(stmt, namedSql, rowMapper);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Query<?> that = (Query<?>) o;
    return namedSql.equals(that.namedSql) &&
      rowMapper.equals(that.rowMapper);
  }

  @Override
  public int hashCode() {
    return Objects.hash(namedSql, rowMapper);
  }

  @Override
  public String toString() {
    return "Query{" +
      "namedSql=" + namedSql +
      ", rowMapper=" + rowMapper +
      '}';
  }
}
