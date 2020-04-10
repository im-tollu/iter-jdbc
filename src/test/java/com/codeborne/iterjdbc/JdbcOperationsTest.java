package com.codeborne.iterjdbc;

import com.codeborne.iterjdbc.jdbc.PreparedQueries;
import com.codeborne.iterjdbc.jdbc.PreparedQuery;
import com.codeborne.iterjdbc.jdbc.PreparedUpdate;
import com.codeborne.iterjdbc.jdbc.Queries;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SuppressWarnings({"unchecked", "rawtypes"})
class JdbcOperationsTest {
  Queries queries = mock(Queries.class);
  PreparedQueries preparedQueries = mock(PreparedQueries.class);
  JdbcFactory factory = mock(JdbcFactory.class);
  PreparedQuery<String> preparedQuery = mock(PreparedQuery.class);
  PreparedUpdate preparedUpdate = mock(PreparedUpdate.class);

  JdbcOperations jdbc = new JdbcOperations(factory);

  String sql = "some sql query";
  Map<String, Object> params = Map.of("param1", "value1");
  RowMapper rowMapper = rs -> "any String";
  CloseableIterator results = new CloseableListIterator("result");

  @BeforeEach
  void setUp() {
    when(factory.getQueries()).thenReturn(queries);
    when(factory.getPreparedQueries()).thenReturn(preparedQueries);
    when(queries.executeQuery(any(), any(), any())).thenReturn(results);
    when(queries.executeUpdate(any(), any())).thenReturn(12);
    when(queries.executeQueryForSingleResult(any(), any(), any())).thenReturn("result");
    when(preparedQueries.prepareQuery(any(), ArgumentMatchers.<RowMapper<String>>any()))
      .thenReturn(preparedQuery);
    when(preparedQueries.prepareUpdate(any())).thenReturn(preparedUpdate);
    when(preparedQuery.execute(any())).thenReturn(results);
    when(preparedUpdate.execute(any())).thenReturn(12);
  }

  @Test
  void executeQuery() {
    var actualResults = jdbc.executeQuery(sql, params, rowMapper);

    verify(queries).executeQuery(sql, params, rowMapper);
    assertThat(actualResults).isSameAs(results);
  }

  @Test
  void executeQueryForSingleResult() {
    var actualResult = jdbc.executeQueryForSingleResult(sql, params, rowMapper);

    verify(queries).executeQueryForSingleResult(sql, params, rowMapper);
    assertThat(actualResult).isEqualTo("result");
  }

  @Test
  void prepareQuery() {
    var actualPreparedQuery = jdbc.prepareQuery(sql, rowMapper);

    verify(preparedQueries).prepareQuery(sql, rowMapper);
    assertThat(actualPreparedQuery).isSameAs(preparedQuery);
  }

  @Test
  void executeUpdate() {
    var affectedRows = jdbc.executeUpdate(sql, params);

    verify(queries).executeUpdate(sql, params);
    assertThat(affectedRows).isEqualTo(12);
  }

  @Test
  void prepareUpdate() {
    var actualPreparedUpdate = jdbc.prepareUpdate(sql);

    verify(preparedQueries).prepareUpdate(sql);
    assertThat(actualPreparedUpdate).isSameAs(preparedUpdate);
  }
}
