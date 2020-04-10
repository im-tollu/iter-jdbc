package com.codeborne.iterjdbc.jdbc;

import com.codeborne.iterjdbc.CloseableIterator;
import com.codeborne.iterjdbc.RowMapper;

import java.util.Map;

public class Queries {
  private final PreparedQueries preparedQueries;

  public Queries(PreparedQueries preparedQueries) {
    this.preparedQueries = preparedQueries;
  }

  public <E> CloseableIterator<E> executeQuery(
    String sql,
    Map<String, Object> params,
    RowMapper<E> rowMapper
  ) {
    var query = preparedQueries.prepareQuery(sql, rowMapper);
    var results = query.execute(params);
    results.onClose(query::close);
    return results;
  }

  public int executeUpdate(String sql, Map<String, Object> params) {
    try (var preparedUpdate = preparedQueries.prepareUpdate(sql)) {
      return preparedUpdate.execute(params);
    }
  }
}
