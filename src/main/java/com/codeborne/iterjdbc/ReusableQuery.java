package com.codeborne.iterjdbc;

import com.codeborne.iterjdbc.named.NamedSql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;

public class ReusableQuery<E> implements AutoCloseable {
  private final PreparedStatement stmt;
  private final NamedSql namedSql;
  private final RowMapper<E> rowMapper;

  public ReusableQuery(PreparedStatement stmt, NamedSql namedSql, RowMapper<E> rowMapper) {
    this.stmt = stmt;
    this.namedSql = namedSql;
    this.rowMapper = rowMapper;
  }

  public CloseableIterator<E> run(Map<String, Object> params) {
    try {
      PreparedQueriesUtils.setParams(this.stmt, this.namedSql.toPositionalParams(params));
      ResultSet rs = this.stmt.executeQuery();
      return new RsIterator<>(rs, this.rowMapper);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public E runForSingleResult(Map<String, Object> params) {
    try (CloseableIterator<E> results = run(params)) {
      return results.hasNext() ? results.next() : null;
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
    ReusableQuery<?> that = (ReusableQuery<?>) o;
    return stmt.equals(that.stmt) &&
      namedSql.equals(that.namedSql) &&
      rowMapper.equals(that.rowMapper);
  }

  @Override
  public int hashCode() {
    return Objects.hash(stmt, namedSql, rowMapper);
  }

  @Override
  public String toString() {
    return "PreparedQuery{" +
      "stmt=" + stmt +
      ", namedSql=" + namedSql +
      ", rowMapper=" + rowMapper +
      '}';
  }
}
