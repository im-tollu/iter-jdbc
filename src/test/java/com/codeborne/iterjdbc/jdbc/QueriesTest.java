package com.codeborne.iterjdbc.jdbc;

import com.codeborne.iterjdbc.CloseableListIterator;
import com.codeborne.iterjdbc.RowMapper;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

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

  @Test @SuppressWarnings({"unchecked", "ConstantConditions"})
  void executeQueryForSingleResult() {
    var sql = "select A from B where C = :paramC";
    Map<String, Object> params = Map.of("paramC", "value of c");
    var isResultsClosed = new AtomicBoolean(false);

    var resultsStub = new CloseableListIterator<>("result");
    resultsStub.onClose(() -> isResultsClosed.set(true));
    var preparedQuery = mock(PreparedQuery.class);
    when(preparedQuery.execute(any())).thenReturn(resultsStub);
    when(preparedQueries.prepareQuery(any(), any())).thenReturn(preparedQuery);

    var result = queries.executeQueryForSingleResult(sql, params, rs -> "any String");

    verify(preparedQuery).execute(params);
    verify(preparedQuery).close();
    assertThat(isResultsClosed).isTrue();
    assertThat(result).isEqualTo("result");
  }

  @Test @SuppressWarnings({"unchecked", "ConstantConditions"})
  void executeQueryForSingleResult_whenNoResults() {
    var sql = "select A from B where C = :paramC";
    Map<String, Object> params = Map.of("paramC", "value of c");
    var isResultsClosed = new AtomicBoolean(false);

    var resultsStub = new CloseableListIterator<String>();
    resultsStub.onClose(() -> isResultsClosed.set(true));
    var preparedQuery = mock(PreparedQuery.class);
    when(preparedQuery.execute(any())).thenReturn(resultsStub);
    when(preparedQueries.prepareQuery(any(), any())).thenReturn(preparedQuery);

    var result = queries.executeQueryForSingleResult(sql, params, rs -> "any String");

    verify(preparedQuery).close();
    assertThat(isResultsClosed).isTrue();
    assertThat(result).isNull();
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

  @Test
  void executeBatchUpdate() {
    var sql = "insert into A (B) values (:c)";
    Map<String, Object> params = Map.of("c", "value of c");
    var paramsIterator = List.of(params).iterator();

    var preparedUpdate = mock(PreparedUpdate.class);
    when(preparedUpdate.executeBatch(any())).thenReturn(12);
    when(preparedQueries.prepareUpdate(any())).thenReturn(preparedUpdate);

    int affectedRows = queries.executeBatchUpdate(sql, paramsIterator);

    verify(preparedQueries).prepareUpdate(sql);
    verify(preparedUpdate).executeBatch(paramsIterator);
    verify(preparedUpdate).close();
    assertThat(affectedRows).isEqualTo(12);
  }
}
