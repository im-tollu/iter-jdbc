package com.codeborne.iterjdbc;

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
  void run() throws SQLException {
    when(statement.executeUpdate()).thenReturn(12);

    var affectedRows = preparedUpdate.run(params);

    verify(statement).setObject(1, 123L);
    verify(statement).setObject(2, "value of c");
    verify(statement, never()).close();
    assertThat(affectedRows).isEqualTo(12);
  }

  @Test
  void runOnce() throws SQLException {
    when(statement.executeUpdate()).thenReturn(12);

    var affectedRows = preparedUpdate.runOnce(params);

    verify(statement).setObject(1, 123L);
    verify(statement).setObject(2, "value of c");
    verify(statement).close();
    assertThat(affectedRows).isEqualTo(12);
  }

  @Test
  void runBatch() throws SQLException {
    when(statement.executeBatch()).thenReturn(new int[]{5, 7, SUCCESS_NO_INFO, EXECUTE_FAILED});
    var paramsIterator = List.of(params, params).iterator();

    var affectedRows = preparedUpdate.runBatch(paramsIterator);

    verify(statement, times(2)).setObject(1, 123L);
    verify(statement, times(2)).setObject(2, "value of c");
    verify(statement).executeBatch();
    verify(statement, never()).close();
    assertThat(affectedRows).isEqualTo(12);
  }

  @Test
  void runBatchOnce() throws SQLException {
    when(statement.executeBatch()).thenReturn(new int[]{5, 7, SUCCESS_NO_INFO, EXECUTE_FAILED});
    var paramsIterator = List.of(params, params).iterator();

    var affectedRows = preparedUpdate.runBatchOnce(paramsIterator);

    verify(statement, times(2)).setObject(1, 123L);
    verify(statement, times(2)).setObject(2, "value of c");
    verify(statement).executeBatch();
    verify(statement).close();
    assertThat(affectedRows).isEqualTo(12);
  }

  @Test
  void close() throws SQLException {
    preparedUpdate.close();

    verify(statement).close();
  }
}