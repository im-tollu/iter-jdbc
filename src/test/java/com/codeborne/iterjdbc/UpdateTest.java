package com.codeborne.iterjdbc;

import com.codeborne.iterjdbc.named.NamedSql;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UpdateTest {
  @Test
  void connect() throws SQLException {
    var conn = mock(Connection.class);
    var stmt = mock(PreparedStatement.class);
    when(conn.prepareStatement(any())).thenReturn(stmt);
    var namedSql = NamedSql.parse("some sql query");

    var preparedUpdate = new Update(namedSql).connect(conn);

    verify(conn).prepareStatement(namedSql.getSqlPositional());
    assertThat(preparedUpdate).isEqualTo(new PreparedUpdate(stmt, namedSql));
  }

  @Test
  void constructors_produceEqualInstances() {
    var sql = "some sql query";

    assertThat(new Update(sql)).isEqualTo(new Update(NamedSql.parse(sql)));
  }
}
