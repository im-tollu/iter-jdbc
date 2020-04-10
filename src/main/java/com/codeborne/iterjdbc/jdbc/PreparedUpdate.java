package com.codeborne.iterjdbc.jdbc;

import com.codeborne.iterjdbc.named.NamedSql;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;

public class PreparedUpdate implements AutoCloseable {
  private final PreparedStatement stmt;
  private final NamedSql namedSql;

  public PreparedUpdate(PreparedStatement stmt, NamedSql namedSql) {
    this.stmt = stmt;
    this.namedSql = namedSql;
  }

  public int execute(Map<String, Object> params) {
    try {
      var positionalParams = namedSql.toPositionalParams(params);
      for (int pos = 1; pos <= positionalParams.length; pos++) {
        stmt.setObject(pos, positionalParams[pos - 1]);
      }
      return stmt.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void close() {
    try {
      stmt.close();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PreparedUpdate that = (PreparedUpdate) o;
    return stmt.equals(that.stmt) && namedSql.equals(that.namedSql);
  }

  @Override
  public int hashCode() {
    return Objects.hash(stmt, namedSql);
  }

  @Override
  public String toString() {
    return "PreparedUpdate{" +
      "stmt=" + stmt +
      ", namedSql=" + namedSql +
      '}';
  }
}
