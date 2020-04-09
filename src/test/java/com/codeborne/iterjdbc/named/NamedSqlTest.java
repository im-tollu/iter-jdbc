package com.codeborne.iterjdbc.named;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class NamedSqlTest {

  @Test
  void parse() {
    String sqlNamed = "select F1 from TABLE t where t.F2=:abc and t.F3=:def";
    String sqlPositional = "select F1 from TABLE t where t.F2=? and t.F3=?";
    NamedSql expected = new NamedSql(sqlNamed, sqlPositional, List.of("abc", "def"));

    assertThat(expected).isEqualTo(NamedSql.parse(sqlNamed));
  }
}
