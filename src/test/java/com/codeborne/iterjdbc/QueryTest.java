package com.codeborne.iterjdbc;

import com.codeborne.iterjdbc.named.NamedSql;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class QueryTest {
  @Test
  void connect() throws SQLException {
    NamedSql namedSql = NamedSql.parse("some sql query");
    RowMapper<String> rowMapper = rs -> "any";
    Connection conn = mock(Connection.class);
    PreparedStatement stmt = mock(PreparedStatement.class);
    when(conn.prepareStatement(any())).thenReturn(stmt);
    Query<String> query = new Query<>(namedSql, rowMapper);

    PreparedQuery<String> preparedQuery = query.connect(conn);

    verify(conn).prepareStatement(namedSql.getSqlPositional());
    assertThat(preparedQuery).isEqualTo(new PreparedQuery<>(stmt, namedSql, rowMapper));
  }

  @Test
  void constructors_produceEqualInstances() {
    String sql = "some sql query";
    RowMapper<String> rowMapper = rs -> "any";

    assertThat(new Query<>(sql, rowMapper)).isEqualTo(new Query<>(NamedSql.parse(sql), rowMapper));
  }
}
