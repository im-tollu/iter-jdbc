package com.codeborne.iterjdbc;

import com.codeborne.iterjdbc.named.NamedSql;
import org.junit.jupiter.api.Test;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static java.sql.Statement.EXECUTE_FAILED;
import static java.sql.Statement.SUCCESS_NO_INFO;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ReusableUpdateTest {
  PreparedStatement statement = mock(PreparedStatement.class);
  NamedSql namedSql = NamedSql.parse("insert into A (B, C) values (:b, :c)");
  Map<String, Object> params = new HashMap<>();
  {
    params.put("c", "value of c");
    params.put("b", 123L);
  }

  ReusableUpdate reusableUpdate = new ReusableUpdate(statement, namedSql);

  @Test
  void run() throws SQLException {
    when(statement.executeUpdate()).thenReturn(12);

    int affectedRows = reusableUpdate.run(params);

    verify(statement).setObject(1, 123L);
    verify(statement).setObject(2, "value of c");
    verify(statement, never()).close();
    assertThat(affectedRows).isEqualTo(12);
  }

  @Test
  void runBatch() throws SQLException {
    when(statement.executeBatch())
      .thenReturn(new int[]{5, 7, SUCCESS_NO_INFO, EXECUTE_FAILED})
      .thenReturn(new int[]{4});
    Iterator<Map<String, Object>> paramsIterator = asList(params, params, params).iterator();
    int batchSize = 2;

    int affectedRows = reusableUpdate.runBatch(paramsIterator, batchSize);

    verify(statement, times(3)).setObject(1, 123L);
    verify(statement, times(3)).setObject(2, "value of c");
    verify(statement, times(2)).executeBatch();
    verify(statement, never()).close();
    assertThat(affectedRows).isEqualTo(16);
  }

  @Test
  void close() throws SQLException {
    reusableUpdate.close();

    verify(statement).close();
  }
}
