package com.codeborne.iterjdbc;

import com.codeborne.iterjdbc.named.NamedSql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

/**
 * This class is for making SQL queries that don't return a result set.
 *
 * Use it for such queries as INSERT, UPDATE, DELETE and some others that return the count of
 * the affected rows.
 *
 * A value object that can be stored, shared and reuse as many times as needed. It doesn't store any
 * resources by itself, but serves as an entry point to the API.
 *
 * @see Query for SQL queries that return a result set.
 */
public class Update {
  private final NamedSql namedSql;

  /**
   * This constructor will be used most of the time
   * @param sql - SQL query with named parameters like this: `delete from BOOKS where ID = :bookId`
   */
  public Update(String sql) {
    this.namedSql = NamedSql.parse(sql);
  }

  /**
   * @param namedSql - an SQL query that's been already parsed into {@link NamedSql}
   */
  public Update(NamedSql namedSql) {
    this.namedSql = namedSql;
  }

  /**
   * @param conn - JDBC connection
   * @return prepared update that is bound the the connection
   */
  public ReusableUpdate forReuse(Connection conn) {
    try {
      PreparedStatement stmt = conn.prepareStatement(namedSql.getSqlPositional());
      return new ReusableUpdate(stmt, namedSql);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * @param conn - JDBC connection
   * @return prepared update that is bound the the connection
   */
  public SingleUseUpdate forSingleUse(Connection conn) {
      return new SingleUseUpdate(this.forReuse(conn));
  }

  @Override
  public String toString() {
    return "Update{" +
      "namedSql=" + namedSql +
      '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Update update = (Update) o;
    return namedSql.equals(update.namedSql);
  }

  @Override
  public int hashCode() {
    return Objects.hash(namedSql);
  }
}
