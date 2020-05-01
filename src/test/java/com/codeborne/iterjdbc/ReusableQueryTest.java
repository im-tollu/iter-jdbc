package com.codeborne.iterjdbc;

import com.codeborne.iterjdbc.named.NamedSql;
import org.junit.jupiter.api.Test;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ReusableQueryTest {
  PreparedStatement stmt = mock(PreparedStatement.class);
  NamedSql namedSql = NamedSql.parse("select A from B where C = :c and D = :d");
  RowMapper<String> rowMapper = rs -> "any";

  ReusableQuery<String> reusableQuery = new ReusableQuery<>(stmt, namedSql, rowMapper);

  @Test
  void run() throws SQLException {
    ResultSet rs = mock(ResultSet.class);
    when(stmt.executeQuery()).thenReturn(rs);
    Map<String, Object> params = new HashMap<>();
    params.put("c", 123L);
    params.put("d", "value of d");

    CloseableIterator<String> results = reusableQuery.run(params);
    results.close();

    verify(stmt).setObject(1, 123L);
    verify(stmt).setObject(2, "value of d");
    verify(stmt, never()).close();
    assertThat(results).isEqualTo(new RsIterator<>(rs, rowMapper));
  }

  @Test
  void runForSingleResult() throws SQLException {
    ResultSet rs = mock(ResultSet.class);
    when(rs.next()).thenReturn(true, true, false);
    when(stmt.executeQuery()).thenReturn(rs);
    Map<String, Object> params = new HashMap<>();
    params.put("c", 123L);
    params.put("d", "value of d");

    String result = reusableQuery.runForSingleResult(params);

    verify(stmt).setObject(1, 123L);
    verify(stmt).setObject(2, "value of d");
    verify(stmt, never()).close();
    assertThat(result).isEqualTo("any");
  }

  @Test
  void close() throws SQLException {
    reusableQuery.close();

    verify(stmt).close();
  }
}
