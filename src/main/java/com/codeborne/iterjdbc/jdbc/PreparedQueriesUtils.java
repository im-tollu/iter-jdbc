package com.codeborne.iterjdbc.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;

class PreparedQueriesUtils {
  static void close(PreparedStatement stmt) {
    try {
      stmt.close();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  static void setParams(PreparedStatement stmt, Object[] params) throws SQLException {
    for (int pos = 1; pos <= params.length; pos++) {
      stmt.setObject(pos, params[pos - 1]);
    }
  }
}
