package com.codeborne.iterjdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.Objects;

class RsIterator<E> extends WithCloseHandlers implements CloseableIterator<E> {
  private final ResultSet rs;
  private final RowMapper<E> rowMapper;
  private Boolean hasNext;

  RsIterator(ResultSet rs, RowMapper<E> rowMapper) {
    this.rs = rs;
    this.rowMapper = rowMapper;
    this.onClose(this::closeRs);
  }

  @Override
  public boolean hasNext() {
    if (hasNext == null) {
      advance();
    }
    return hasNext;
  }

  @Override
  public E next() {
    if (!hasNext) {
      throw new NoSuchElementException();
    }
    try {
      E next = rowMapper.mapRow(rs);
      advance();
      return next;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private void advance() {
    try {
      hasNext = rs.next();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private void closeRs() {
    try {
      rs.close();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RsIterator<?> that = (RsIterator<?>) o;
    return rs.equals(that.rs) &&
      rowMapper.equals(that.rowMapper);
  }

  @Override
  public int hashCode() {
    return Objects.hash(rs, rowMapper);
  }

  @Override
  public String toString() {
    return "RsIterator{" +
      "rs=" + rs +
      ", rowMapper=" + rowMapper +
      '}';
  }
}
