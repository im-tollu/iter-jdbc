package com.codeborne.iterjdbc.named;

import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

class NamedSqlTest {

  @Test
  void parse() {
    assertThat(NamedSql.parse("select F1 from TABLE t where t.F2=:abc and t.F3=:def"))
      .isEqualTo(new NamedSql(
        "select F1 from TABLE t where t.F2=:abc and t.F3=:def",
        "select F1 from TABLE t where t.F2=? and t.F3=?",
        asList("abc", "def")
      ));
  }

  @Test
  void parse_withInlineComment() {
    assertThat(NamedSql.parse("select F1 from TABLE t where t.F2=:abc -- and t.F3=:def"))
      .isEqualTo(new NamedSql(
        "select F1 from TABLE t where t.F2=:abc -- and t.F3=:def",
        "select F1 from TABLE t where t.F2=? -- and t.F3=:def",
        singletonList("abc")
      ));

    assertThat(NamedSql.parse("select F1 from TABLE t where t.F2=:abc -- and t.F3=:def\nand t.F4=:ghi"))
      .isEqualTo(new NamedSql(
        "select F1 from TABLE t where t.F2=:abc -- and t.F3=:def\nand t.F4=:ghi",
        "select F1 from TABLE t where t.F2=? -- and t.F3=:def\nand t.F4=?",
        asList("abc", "ghi")
      ));
  }

  @Test
  void parse_withStringLiteral() {
    assertThat(NamedSql.parse("select F1 from TABLE t where t.F2='and t.F3=:def\n ' and t.F4=:ghi"))
      .isEqualTo(new NamedSql(
        "select F1 from TABLE t where t.F2='and t.F3=:def\n ' and t.F4=:ghi",
        "select F1 from TABLE t where t.F2='and t.F3=:def\n ' and t.F4=?",
        singletonList("ghi")
      ));
  }
}
