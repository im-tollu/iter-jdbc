package com.codeborne.iterjdbc.named;

import com.codeborne.iterjdbc.RowMapper;
import com.codeborne.iterjdbc.jdbc.PreparedQuery;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MappedNamedSqlTest {
  @Test
  void prepareQuery() throws SQLException {
    NamedSql namedSql = NamedSql.parse("some sql query");
    RowMapper<String> rowMapper = rs -> "any";
    MappedNamedSql<String> mappedNamedSql = namedSql.withRowMapper(rowMapper);

    var conn = mock(Connection.class);
    var stmt = mock(PreparedStatement.class);
    when(conn.prepareStatement(any())).thenReturn(stmt);

    assertThat(mappedNamedSql.prepareQuery(conn))
      .isEqualTo(new PreparedQuery<>(stmt, namedSql, rowMapper));
  }
}
