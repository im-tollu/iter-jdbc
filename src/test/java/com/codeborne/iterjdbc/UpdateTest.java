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
  void forReuse() throws SQLException {
    Connection conn = mock(Connection.class);
    PreparedStatement stmt = mock(PreparedStatement.class);
    when(conn.prepareStatement(any())).thenReturn(stmt);
    NamedSql namedSql = NamedSql.parse("some sql query");

    ReusableUpdate reusableUpdate = new Update(namedSql).forReuse(conn);

    verify(conn).prepareStatement(namedSql.getSqlPositional());
    assertThat(reusableUpdate).isEqualTo(new ReusableUpdate(stmt, namedSql));
  }

  @Test
  void forSingleUse() throws SQLException {
    Connection conn = mock(Connection.class);
    PreparedStatement stmt = mock(PreparedStatement.class);
    when(conn.prepareStatement(any())).thenReturn(stmt);
    NamedSql namedSql = NamedSql.parse("some sql query");

    SingleUseUpdate singleUseUpdate = new Update(namedSql).forSingleUse(conn);

    assertThat(singleUseUpdate).isEqualTo(new SingleUseUpdate(new ReusableUpdate(stmt, namedSql)));
  }

  @Test
  void constructors_produceEqualInstances() {
    String sql = "some sql query";

    assertThat(new Update(sql)).isEqualTo(new Update(NamedSql.parse(sql)));
  }
}
