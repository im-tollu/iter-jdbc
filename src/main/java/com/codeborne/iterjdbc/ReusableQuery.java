package com.codeborne.iterjdbc;

import com.codeborne.iterjdbc.named.NamedSql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;

public class ReusableQuery<E> extends WithCloseHandlers {
  private final Connection conn;
  private final NamedSql namedSql;
  private final RowMapper<E> rowMapper;
  private PreparedStatement stmt;

  public ReusableQuery(Connection conn, NamedSql namedSql, RowMapper<E> rowMapper) {
    this.conn = conn;
    this.namedSql = namedSql;
    this.rowMapper = rowMapper;
  }

  public CloseableIterator<E> run(Map<String, Object> params) {
    prepareStatementIfNotYet();
    try {
      PreparedQueriesUtils.setParams(this.stmt, this.namedSql.toPositionalParams(params));
      ResultSet rs = this.stmt.executeQuery();
      return new RsIterator<>(rs, this.rowMapper);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public E runForSingleResult(Map<String, Object> params) {
    prepareStatementIfNotYet();
    try (CloseableIterator<E> results = run(params)) {
      return results.hasNext() ? results.next() : null;
    }
  }

  private void prepareStatementIfNotYet() {
    try {
      if (this.stmt == null) {
        stmt = conn.prepareStatement(namedSql.getSqlPositional());
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
    ReusableQuery<?> that = (ReusableQuery<?>) o;
    return conn.equals(that.conn) &&
      namedSql.equals(that.namedSql) &&
      rowMapper.equals(that.rowMapper);
  }

  @Override
  public int hashCode() {
    return Objects.hash(conn, namedSql, rowMapper);
  }

  @Override
  public String toString() {
    return "PreparedQuery{" +
      "conn=" + conn +
      ", namedSql=" + namedSql +
      ", rowMapper=" + rowMapper +
      '}';
  }
}
