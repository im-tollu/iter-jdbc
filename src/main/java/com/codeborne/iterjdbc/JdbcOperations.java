package com.codeborne.iterjdbc;

import com.codeborne.iterjdbc.jdbc.PreparedQueries;
import com.codeborne.iterjdbc.jdbc.PreparedQuery;
import com.codeborne.iterjdbc.jdbc.Queries;

import java.sql.Connection;
import java.util.Map;

public class JdbcOperations {
  private final PreparedQueries preparedQueries;
  private final Queries queries;

  public JdbcOperations(Connection conn) {
    this.preparedQueries = new PreparedQueries(conn);
    this.queries = new Queries(preparedQueries);
  }

  public <E> CloseableIterator<E> executeQuery(String sql, Map<String, Object> params, RowMapper<E> rowMapper) {
    return queries.executeQuery(sql, params, rowMapper);
  }

  public <E> PreparedQuery<E> preparedQuery(String sql, RowMapper<E> rowMapper) {
    return preparedQueries.prepareQuery(sql, rowMapper);
  }
}
