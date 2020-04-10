package com.codeborne.iterjdbc;

import com.codeborne.iterjdbc.jdbc.PreparedQuery;
import com.codeborne.iterjdbc.jdbc.PreparedUpdate;

import java.util.Map;

public class JdbcOperations {
  private final JdbcFactory factory;

  public JdbcOperations(JdbcFactory factory) {
    this.factory = factory;
  }

  public <E> CloseableIterator<E> executeQuery(String sql, Map<String, Object> params, RowMapper<E> rowMapper) {
    return factory.getQueries().executeQuery(sql, params, rowMapper);
  }

  public <E> E executeQueryForSingleResult(String sql, Map<String, Object> params, RowMapper<E> rowMapper) {
    return factory.getQueries().executeQueryForSingleResult(sql, params, rowMapper);
  }

  public <E> PreparedQuery<E> prepareQuery(String sql, RowMapper<E> rowMapper) {
    return factory.getPreparedQueries().prepareQuery(sql, rowMapper);
  }

  public int executeUpdate(String sql, Map<String, Object> params) {
    return factory.getQueries().executeUpdate(sql, params);
  }

  public PreparedUpdate prepareUpdate(String sql) {
    return factory.getPreparedQueries().prepareUpdate(sql);
  }
}
