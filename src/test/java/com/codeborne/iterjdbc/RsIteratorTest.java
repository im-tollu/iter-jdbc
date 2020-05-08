package com.codeborne.iterjdbc;

import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class RsIteratorTest {
  ResultSet rs = mock(ResultSet.class);

  @Test
  void emptyResultSet() throws SQLException {
    when(rs.next()).thenReturn(false);
    RsIterator<String> rsIterator = new RsIterator<>(rs, rs -> rs.getString("SHOULD_NOT_BE_CALLED"));

    verify(rs, never()).getString(any());
    assertThat(rsIterator.hasNext()).isFalse();
    assertThatThrownBy(rsIterator::next).isInstanceOf(NoSuchElementException.class);
  }

  @Test
  void iterate() throws SQLException {
    when(rs.next()).thenReturn(true, true, true, false);
    when(rs.getString(any())).thenReturn("A", "B", "C");

    RsIterator<String> rsIterator = new RsIterator<>(rs, this::rowMapper);

    assertThat(rsIterator).toIterable().containsExactly("A", "B", "C");
  }

  @Test
  void close_closesResultSet() throws SQLException {
    RsIterator<String> rsIterator = new RsIterator<>(rs, this::rowMapper);

    rsIterator.close();

    verify(rs).close();
  }

  @Test
  void stream() throws SQLException {
    when(rs.next()).thenReturn(true, true, true, false);
    when(rs.getString(any())).thenReturn("A", "B", "C");
    RsIterator<String> rsIterator = spy(new RsIterator<>(rs, this::rowMapper));

    Stream<String> stream = rsIterator.stream();

    assertThat(stream).containsExactly("A", "B", "C");
    verify(rsIterator, never()).close();
  }

  private String rowMapper(ResultSet rs) throws SQLException {
    return rs.getString("COLUMN_NAME");
  }
}
