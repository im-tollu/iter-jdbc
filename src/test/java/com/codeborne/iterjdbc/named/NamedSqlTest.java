package com.codeborne.iterjdbc.named;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class NamedSqlTest {

  @Test
  void parse() {
    assertThat(NamedSql.parse("select F1 from TABLE t where t.F2=:abc and t.F3=:def"))
      .isEqualTo(new NamedSql(
        "select F1 from TABLE t where t.F2=:abc and t.F3=:def",
        "select F1 from TABLE t where t.F2=? and t.F3=?",
        List.of("abc", "def")
      ));

    assertThat(NamedSql.parse("select F1 from TABLE t where t.F2=:abc -- and t.F3=:def"))
      .isEqualTo(new NamedSql(
        "select F1 from TABLE t where t.F2=:abc -- and t.F3=:def",
        "select F1 from TABLE t where t.F2=? -- and t.F3=:def",
        List.of("abc")
      ));

    assertThat(NamedSql.parse("select F1 from TABLE t where t.F2=:abc -- and t.F3=:def\nand t.F4=:ghi"))
      .isEqualTo(new NamedSql(
        "select F1 from TABLE t where t.F2=:abc -- and t.F3=:def\nand t.F4=:ghi",
        "select F1 from TABLE t where t.F2=? -- and t.F3=:def\nand t.F4=?",
        List.of("abc", "ghi")
      ));
  }
}
