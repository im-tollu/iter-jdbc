package com.codeborne.iterjdbc.jdbc;

import com.codeborne.iterjdbc.named.NamedSql;
import org.junit.jupiter.api.Test;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PreparedUpdateTest {
  PreparedStatement statement = mock(PreparedStatement.class);
  NamedSql namedSql = NamedSql.parse("insert into A (B, C) values (:b, :c)");
  PreparedUpdate preparedUpdate = new PreparedUpdate(statement, namedSql);

  @Test
  void execute() throws SQLException {
    when(statement.executeUpdate()).thenReturn(12);

    var affectedRows = preparedUpdate.execute(Map.of("b", 123L, "c", "value of c"));

    verify(statement).setObject(1, 123L);
    verify(statement).setObject(2, "value of c");
    verify(statement, never()).close();
    assertThat(affectedRows).isEqualTo(12);
  }

  @Test
  void close() throws SQLException {
    preparedUpdate.close();

    verify(statement).close();
  }
}
