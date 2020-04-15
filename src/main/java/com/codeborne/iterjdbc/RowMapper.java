package com.codeborne.iterjdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface RowMapper<E> {
  E mapRow(ResultSet rs) throws SQLException;
}
