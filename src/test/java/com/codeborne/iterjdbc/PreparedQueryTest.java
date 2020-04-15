package com.codeborne.iterjdbc;

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
  RowMapper<String> rowMapper = rs -> "any";
  PreparedQuery<String> preparedQuery = new PreparedQuery<>(statement, namedSql, rowMapper);

  @Test
  void run() throws SQLException {
    var rs = mock(ResultSet.class);
    when(statement.executeQuery()).thenReturn(rs);

    var results = preparedQuery.run(Map.of("c", 123L, "d", "value of d"));
    results.close();

    verify(statement).setObject(1, 123L);
    verify(statement).setObject(2, "value of d");
    verify(statement, never()).close();
    assertThat(results).isEqualTo(new RsIterator<>(rs, rowMapper));
  }

  @Test
  void runOnce() throws SQLException {
    var rs = mock(ResultSet.class);
    when(statement.executeQuery()).thenReturn(rs);

    var results = preparedQuery.runOnce(Map.of("c", 123L, "d", "value of d"));
    results.close();

    verify(statement).setObject(1, 123L);
    verify(statement).setObject(2, "value of d");
    verify(statement).close();
    assertThat(results).isEqualTo(new RsIterator<>(rs, rowMapper));
  }

  @Test
  void runForSingleResult() throws SQLException {
    var rs = mock(ResultSet.class);
    when(rs.next()).thenReturn(true, true, false);
    when(statement.executeQuery()).thenReturn(rs);

    var result = preparedQuery.runForSingleResult(Map.of("c", 123L, "d", "value of d"));

    verify(statement).setObject(1, 123L);
    verify(statement).setObject(2, "value of d");
    verify(statement, never()).close();
    assertThat(result).isEqualTo("any");
  }

  @Test
  void runOnceForSingleResult() throws SQLException {
    var rs = mock(ResultSet.class);
    when(rs.next()).thenReturn(true, true, false);
    when(statement.executeQuery()).thenReturn(rs);

    var result = preparedQuery.runOnceForSingleResult(Map.of("c", 123L, "d", "value of d"));

    verify(statement).setObject(1, 123L);
    verify(statement).setObject(2, "value of d");
    verify(statement).close();
    assertThat(result).isEqualTo("any");
  }

  @Test
  void close() throws SQLException {
    preparedQuery.close();

    verify(statement).close();
  }
}
