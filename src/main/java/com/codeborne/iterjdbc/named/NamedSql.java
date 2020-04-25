package com.codeborne.iterjdbc.named;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import static java.lang.String.format;

public class NamedSql {
  private final String sqlNamed;
  private final String sqlPositional;
  private final List<String> paramNames;

  NamedSql(String sqlNamed, String sqlPositional, List<String> paramNames) {
    this.sqlNamed = sqlNamed;
    this.sqlPositional = sqlPositional;
    this.paramNames = paramNames;
  }

  public String getSqlPositional() {
    return sqlPositional;
  }

  public Object[] toPositionalParams(Map<String, Object> params) {
    return paramNames.stream().map(extractParam(params)).toArray();
  }

  private Function<String, Object> extractParam(Map<String, Object> params) {
    return (String paramName) -> {
      Object paramVal = params.get(paramName);
      if (paramVal == null) {
        String msg = format("No value provided for [%s] in query [%s]", paramName, sqlNamed);
        throw new IllegalArgumentException(msg);
      }
      return paramVal;
    };
  }

  public static NamedSql parse(String sql) {
    return SqlParser.parse(sql);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    NamedSql that = (NamedSql) o;
    return sqlNamed.equals(that.sqlNamed) &&
      sqlPositional.equals(that.sqlPositional) &&
      paramNames.equals(that.paramNames);
  }

  @Override
  public int hashCode() {
    return Objects.hash(sqlNamed, sqlPositional, paramNames);
  }

  @Override
  public String toString() {
    return "NamedParametersSql{" +
      "sqlNamed='" + sqlNamed + '\'' +
      ", sqlPositional='" + sqlPositional + '\'' +
      ", paramNames=" + paramNames +
      '}';
  }
}
