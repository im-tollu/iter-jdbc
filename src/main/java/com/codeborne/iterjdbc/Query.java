package com.codeborne.iterjdbc;

import com.codeborne.iterjdbc.named.NamedSql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

public class Query<E> {
  private final NamedSql namedSql;
  private final RowMapper<E> rowMapper;

  public Query(String sql, RowMapper<E> rowMapper) {
    this.namedSql = NamedSql.parse(sql);
    this.rowMapper = rowMapper;
  }

  public Query(NamedSql namedSql, RowMapper<E> rowMapper) {
    this.namedSql = namedSql;
    this.rowMapper = rowMapper;
  }

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
