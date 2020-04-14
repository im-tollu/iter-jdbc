package com.codeborne.iterjdbc.named;

import com.codeborne.iterjdbc.RowMapper;
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
  }

  @Test
  void parse_withInlineComment() {
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

  @Test
  void parse_withStringLiteral() {
    assertThat(NamedSql.parse("select F1 from TABLE t where t.F2='and t.F3=:def\n ' and t.F4=:ghi"))
      .isEqualTo(new NamedSql(
        "select F1 from TABLE t where t.F2='and t.F3=:def\n ' and t.F4=:ghi",
        "select F1 from TABLE t where t.F2='and t.F3=:def\n ' and t.F4=?",
        List.of("ghi")
      ));
  }

  @Test
  void withRowMapper() {
    NamedSql namedSql = NamedSql.parse("some sql query");
    RowMapper<String> rowMapper = rs -> "any";

    assertThat(namedSql.withRowMapper(rowMapper)).isEqualTo(new MappedNamedSql<>(namedSql, rowMapper));
  }
}
