package com.codeborne.iterjdbc.jdbc;

import com.codeborne.iterjdbc.CloseableIterator;
import com.codeborne.iterjdbc.RowMapper;
import com.codeborne.iterjdbc.named.NamedSql;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;

public class PreparedQuery<E> implements AutoCloseable {
  private final PreparedStatement stmt;
  private final NamedSql namedSql;
  private final RowMapper<E> rowMapper;

  public PreparedQuery(PreparedStatement stmt, NamedSql namedSql, RowMapper<E> rowMapper) {
    this.stmt = stmt;
    this.namedSql = namedSql;
    this.rowMapper = rowMapper;
  }

  public CloseableIterator<E> execute(Map<String, Object> params) {
    try {
      PreparedQueriesUtils.setParams(stmt, namedSql.toPositionalParams(params));
      var rs = stmt.executeQuery();
      return new RsIterator<>(rs, rowMapper);
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
    PreparedQuery<?> that = (PreparedQuery<?>) o;
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
