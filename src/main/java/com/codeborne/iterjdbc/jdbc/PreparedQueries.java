package com.codeborne.iterjdbc.jdbc;

import com.codeborne.iterjdbc.RowMapper;
import com.codeborne.iterjdbc.named.NamedSql;

import java.sql.Connection;
import java.sql.SQLException;

public class PreparedQueries {
  private final Connection conn;

  public PreparedQueries(Connection conn) {
    this.conn = conn;
  }

  <E> PreparedQuery<E> prepareQuery(String sql, RowMapper<E> rowMapper) {
    try {
      var namedSql = NamedSql.parse(sql);
      var stmt = conn.prepareStatement(namedSql.getSqlPositional());
      return new PreparedQuery<>(stmt, namedSql, rowMapper);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
