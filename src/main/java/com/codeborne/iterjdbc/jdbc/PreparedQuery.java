package com.codeborne.iterjdbc.jdbc;

import com.codeborne.iterjdbc.CloseableIterator;
import com.codeborne.iterjdbc.RowMapper;
import com.codeborne.iterjdbc.named.NamedSql;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

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
      var positionalParams = namedSql.toPositionalParams(params);
      for (int pos = 1; pos <= positionalParams.length; pos++) {
        stmt.setObject(pos, positionalParams[pos - 1]);
      }
      var rs = stmt.executeQuery();
      return new RsIterator<>(rs, rowMapper);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void close() {
    try {
      stmt.close();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
