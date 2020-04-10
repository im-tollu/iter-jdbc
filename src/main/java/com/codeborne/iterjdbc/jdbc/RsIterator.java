package com.codeborne.iterjdbc.jdbc;

import com.codeborne.iterjdbc.CloseableIterator;
import com.codeborne.iterjdbc.WithCloseHandlers;
import com.codeborne.iterjdbc.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;

public class RsIterator<E> extends WithCloseHandlers implements CloseableIterator<E> {
  private final ResultSet rs;
  private final RowMapper<E> rowMapper;
  private Boolean hasNext;

  public RsIterator(ResultSet rs, RowMapper<E> rowMapper) {
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

  boolean hasSameResultSet(ResultSet thatRs) {
    return this.rs == thatRs;
  }
}
