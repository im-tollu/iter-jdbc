package com.codeborne.iterjdbc;

import com.codeborne.iterjdbc.named.NamedSql;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static com.codeborne.iterjdbc.PreparedQueriesUtils.setParams;

public class ReusableUpdate implements AutoCloseable {
  private final PreparedStatement stmt;
  private final NamedSql namedSql;

  public ReusableUpdate(PreparedStatement stmt, NamedSql namedSql) {
    this.stmt = stmt;
    this.namedSql = namedSql;
  }

  public int run(Map<String, Object> params) {
    try {
      setParams(stmt, namedSql.toPositionalParams(params));
      return stmt.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public int runBatch(Iterator<Map<String, Object>> paramsIterator, int batchSize) {
    AtomicInteger affectedRows = new AtomicInteger(0);
    int remainingInBatch = batchSize;
    try {
      while (true) {
        if (remainingInBatch == 0 || !paramsIterator.hasNext()) {
          Arrays.stream(stmt.executeBatch())
            .filter(result -> result > 0)
            .forEach(affectedRows::addAndGet);
          remainingInBatch = batchSize;
        }
        if (!paramsIterator.hasNext()) {
          return affectedRows.get();
        }
        Map<String, Object> params = paramsIterator.next();
        setParams(stmt, namedSql.toPositionalParams(params));
        stmt.addBatch();
        remainingInBatch--;
      }
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
