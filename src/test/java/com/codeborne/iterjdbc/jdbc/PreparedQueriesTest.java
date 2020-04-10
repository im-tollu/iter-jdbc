package com.codeborne.iterjdbc.jdbc;

import com.codeborne.iterjdbc.RowMapper;
import com.codeborne.iterjdbc.named.NamedSql;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PreparedQueriesTest {
  Connection conn = mock(Connection.class);
  PreparedQueries preparedQueries = new PreparedQueries(conn);

  @Test
  void prepareQuery() throws SQLException {
    var stmt = mock(PreparedStatement.class);
    when(conn.prepareStatement(any())).thenReturn(stmt);
    var sql = "select 'abc' from DUAL";
    RowMapper<String> rowMapper = rs -> rs.getString(1);

    var preparedQuery = preparedQueries.prepareQuery(sql, rowMapper);

    verify(conn).prepareStatement(NamedSql.parse(sql).getSqlPositional());
    assertThat(preparedQuery).isEqualTo(new PreparedQuery<>(stmt, NamedSql.parse(sql), rowMapper));
  }
}
