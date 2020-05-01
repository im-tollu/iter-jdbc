package com.codeborne.iterjdbc;

import com.codeborne.iterjdbc.named.NamedSql;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class ReusableUpdate implements AutoCloseable {
  private final PreparedStatement stmt;
  private final NamedSql namedSql;

  public ReusableUpdate(PreparedStatement stmt, NamedSql namedSql) {
    this.stmt = stmt;
    this.namedSql = namedSql;
  }

  public int run(Map<String, Object> params) {
    try {
      PreparedQueriesUtils.setParams(stmt, namedSql.toPositionalParams(params));
      return stmt.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public int runBatch(Iterator<Map<String, Object>> paramsIterator) {
    try {
      while (paramsIterator.hasNext()) {
        Map<String, Object> params = paramsIterator.next();
        PreparedQueriesUtils.setParams(stmt, namedSql.toPositionalParams(params));
        stmt.addBatch();
      }
      return Arrays.stream(stmt.executeBatch()).filter(i -> i >= 0).sum();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void close() {
    PreparedQueriesUtils.close(stmt);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ReusableUpdate that = (ReusableUpdate) o;
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