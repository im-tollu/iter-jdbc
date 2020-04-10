package com.codeborne.iterjdbc.jdbc;

import com.codeborne.iterjdbc.RowMapper;
import com.codeborne.iterjdbc.named.NamedSql;
import org.junit.jupiter.api.Test;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PreparedQueryTest {
  PreparedStatement statement = mock(PreparedStatement.class);
  NamedSql namedSql = NamedSql.parse("select A from B where C = :c and D = :d");
  RowMapper<String> rowMapper = rs -> rs.getString(1);
  PreparedQuery<String> preparedQuery = new PreparedQuery<>(statement, namedSql, rowMapper);

  @Test
  void execute() throws SQLException {
    var rs = mock(ResultSet.class);
    when(statement.executeQuery()).thenReturn(rs);

    var results = preparedQuery.execute(Map.of("c", 123L, "d", "value of d"));

    verify(statement).setObject(1, 123L);
    verify(statement).setObject(2, "value of d");
    verify(statement, never()).close();
    assertThat(results).isEqualTo(new RsIterator<>(rs, rowMapper));
  }

  @Test
  void close() throws SQLException {
    preparedQuery.close();

    verify(statement).close();
  }
}
