package com.codeborne.iterjdbc.named;

import com.codeborne.iterjdbc.RowMapper;

import java.util.Objects;

public class MappedNamedSql<E> {
  private final NamedSql namedSql;
  private final RowMapper<E> rowMapper;

  public MappedNamedSql(NamedSql namedSql, RowMapper<E> rowMapper) {
    this.namedSql = namedSql;
    this.rowMapper = rowMapper;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MappedNamedSql<?> that = (MappedNamedSql<?>) o;
    return namedSql.equals(that.namedSql) &&
      rowMapper.equals(that.rowMapper);
  }

  @Override
  public int hashCode() {
    return Objects.hash(namedSql, rowMapper);
  }

  @Override
  public String toString() {
    return "MappedNamedSql{" +
      "namedSql=" + namedSql +
      ", rowMapper=" + rowMapper +
      '}';
  }
}
