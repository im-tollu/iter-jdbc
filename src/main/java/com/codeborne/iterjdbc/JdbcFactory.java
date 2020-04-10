package com.codeborne.iterjdbc;

import com.codeborne.iterjdbc.jdbc.PreparedQueries;
import com.codeborne.iterjdbc.jdbc.Queries;

public interface JdbcFactory {
  PreparedQueries getPreparedQueries();

  Queries getQueries();
}
