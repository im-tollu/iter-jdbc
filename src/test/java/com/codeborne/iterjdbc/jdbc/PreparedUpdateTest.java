package com.codeborne.iterjdbc.jdbc;

import com.codeborne.iterjdbc.named.NamedSql;
import org.junit.jupiter.api.Test;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static java.sql.Statement.EXECUTE_FAILED;
import static java.sql.Statement.SUCCESS_NO_INFO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PreparedUpdateTest {
  PreparedStatement statement = mock(PreparedStatement.class);
  NamedSql namedSql = NamedSql.parse("insert into A (B, C) values (:b, :c)");
  Map<String, Object> params = Map.of("b", 123L, "c", "value of c");

  PreparedUpdate preparedUpdate = new PreparedUpdate(statement, namedSql);

  @Test
  void execute() throws SQLException {
    when(statement.executeUpdate()).thenReturn(12);

    var affectedRows = preparedUpdate.execute(params);

    verify(statement).setObject(1, 123L);
    verify(statement).setObject(2, "value of c");
    verify(statement, never()).close();
    assertThat(affectedRows).isEqualTo(12);
  }

  @Test
  void executeBatch() throws SQLException {
    when(statement.executeBatch()).thenReturn(new int[]{5, 7, SUCCESS_NO_INFO, EXECUTE_FAILED});
    var paramsIterator = List.of(params, params).iterator();

    var affectedRows = preparedUpdate.executeBatch(paramsIterator);

    verify(statement, times(2)).setObject(1, 123L);
    verify(statement, times(2)).setObject(2, "value of c");
    verify(statement).executeBatch();
    verify(statement, never()).close();
    assertThat(affectedRows).isEqualTo(12);
  }

  @Test
  void close() throws SQLException {
    preparedUpdate.close();

    verify(statement).close();
  }
}
