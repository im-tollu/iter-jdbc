package com.codeborne.iterjdbc;

import com.codeborne.iterjdbc.named.NamedSql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

public class Update {
  private final NamedSql namedSql;

  public Update(String sql) {
    this.namedSql = NamedSql.parse(sql);
  }

  public Update(NamedSql namedSql) {
    this.namedSql = namedSql;
  }

  public PreparedUpdate connect(Connection conn) {
    try {
      PreparedStatement stmt = conn.prepareStatement(namedSql.getSqlPositional());
      return new PreparedUpdate(stmt, namedSql);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String toString() {
    return "Update{" +
      "namedSql=" + namedSql +
      '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Update update = (Update) o;
    return namedSql.equals(update.namedSql);
  }

  @Override
  public int hashCode() {
    return Objects.hash(namedSql);
  }
}
