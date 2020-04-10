package com.codeborne.iterjdbc.jdbc;

import com.codeborne.iterjdbc.CloseableListIterator;
import com.codeborne.iterjdbc.RowMapper;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class QueriesTest {
  PreparedQueries preparedQueries = mock(PreparedQueries.class);
  Queries queries = new Queries(preparedQueries);

  @Test @SuppressWarnings("unchecked")
  void executeQuery() {
    var sql = "select A from B where C = :paramC";
    RowMapper<String> rowMapper = rs -> "any string";
    Map<String, Object> params = Map.of("paramC", "value of c");

    var preparedQuery = mock(PreparedQuery.class);
    var resultsStub = new CloseableListIterator<String>();
    when(preparedQuery.execute(any())).thenReturn(resultsStub);
    when(preparedQueries.prepareQuery(any(), any())).thenReturn(preparedQuery);

    var results = queries.executeQuery(sql, params, rowMapper);
    results.close();

    verify(preparedQueries).prepareQuery(sql, rowMapper);
    verify(preparedQuery).execute(params);
    verify(preparedQuery).close();
    assertThat(results).isSameAs(resultsStub);
  }

  @Test
  void executeUpdate() {
    var sql = "insert into A (B) values (:c)";
    Map<String, Object> params = Map.of("c", "value of c");

    var preparedUpdate = mock(PreparedUpdate.class);
    when(preparedUpdate.execute(any())).thenReturn(12);
    when(preparedQueries.prepareUpdate(any())).thenReturn(preparedUpdate);

    int affectedRows = queries.executeUpdate(sql, params);

    verify(preparedQueries).prepareUpdate(sql);
    verify(preparedUpdate).execute(params);
    verify(preparedUpdate).close();
    assertThat(affectedRows).isEqualTo(12);
  }
}
