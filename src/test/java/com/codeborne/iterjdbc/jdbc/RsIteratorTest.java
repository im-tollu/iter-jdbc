package com.codeborne.iterjdbc.jdbc;

import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class RsIteratorTest {
  ResultSet rs = mock(ResultSet.class);

  @Test
  void emptyResultSet() throws SQLException {
    when(rs.next()).thenReturn(false);
    var rsIterator = new RsIterator<>(rs, rs -> rs.getString("SHOULD_NOT_BE_CALLED"));

    verify(rs, never()).getString(any());
    assertThat(rsIterator.hasNext()).isFalse();
    assertThatThrownBy(rsIterator::next).isInstanceOf(NoSuchElementException.class);
  }

  @Test
  void iterate() throws SQLException {
    when(rs.next()).thenReturn(true, true, true, false);
    when(rs.getString(any())).thenReturn("A", "B", "C");

    var rsIterator = new RsIterator<>(rs, rs -> rs.getString("COLUMN_NAME"));

    assertThat(rsIterator).toIterable().containsExactly("A", "B", "C");
  }

  @Test
  void close_closesResultSet() throws SQLException {
    var rsIterator = new RsIterator<>(rs, rs -> 0);

    rsIterator.close();

    verify(rs).close();
  }

  @Test @SuppressWarnings("ConstantConditions")
  void stream() throws SQLException {
    when(rs.next()).thenReturn(true, true, true, false);
    when(rs.getString(any())).thenReturn("A", "B", "C");
    var rsIterator = new RsIterator<>(rs, rs -> rs.getString("COLUMN_NAME"));
    var isStreamClosed = new AtomicBoolean(false);
    rsIterator.onClose(() -> isStreamClosed.set(true));

    var stream = rsIterator.stream();

    assertThat(stream).containsExactly("A", "B", "C");
    assertThat(isStreamClosed).isTrue();
  }
}
