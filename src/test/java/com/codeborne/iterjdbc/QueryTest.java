package com.codeborne.iterjdbc;

import com.codeborne.iterjdbc.named.NamedSql;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class QueryTest {
  @Test
  void forReuse() throws SQLException {
    NamedSql namedSql = NamedSql.parse("some sql query");
    RowMapper<String> rowMapper = rs -> "any";
    Connection conn = mock(Connection.class);
    PreparedStatement stmt = mock(PreparedStatement.class);
    when(conn.prepareStatement(any())).thenReturn(stmt);
    Query<String> query = new Query<>(namedSql, rowMapper);

    ReusableQuery<String> reusableQuery = query.forReuse(conn);

    verify(conn).prepareStatement(namedSql.getSqlPositional());
    assertThat(reusableQuery).isEqualTo(new ReusableQuery<>(conn, namedSql, rowMapper));
  }

  @Test
  void constructors_produceEqualInstances() {
    String sql = "some sql query";
    RowMapper<String> rowMapper = rs -> "any";

    assertThat(new Query<>(sql, rowMapper)).isEqualTo(new Query<>(NamedSql.parse(sql), rowMapper));
  }
}
