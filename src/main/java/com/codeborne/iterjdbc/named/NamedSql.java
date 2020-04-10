package com.codeborne.iterjdbc.named;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import static java.lang.Character.isLetterOrDigit;
import static java.lang.String.format;

public class NamedSql {
  private static final char START_PARAM = ':';

  private final String sqlNamed;
  private final String sqlPositional;
  private final List<String> paramNames;

  public NamedSql(String sqlNamed, String sqlPositional, List<String> paramNames) {
    this.sqlNamed = sqlNamed;
    this.sqlPositional = sqlPositional;
    this.paramNames = paramNames;
  }

  public String getSqlPositional() {
    return sqlPositional;
  }

  public static NamedSql parse(String sql) {
    var params = new ArrayList<String>();
    var sqlPositional = new StringBuilder();
    int pos = 0;
    while (pos < sql.length()) {
      if (sql.charAt(pos) == START_PARAM) {
        int start = pos + 1;
        int wordLen = seekWord(sql, start);
        if (wordLen > 0) {
          params.add(sql.substring(start, start + wordLen));
          sqlPositional.append('?');
        }
        pos = start + wordLen;
      } else {
        sqlPositional.append(sql.charAt(pos));
        pos++;
      }
    }
    return new NamedSql(sql, sqlPositional.toString(), params);
  }

  private static int seekWord(String s, int start) {
    int afterEnd = start;
    while (afterEnd < s.length() && isLetterOrDigit(s.charAt(afterEnd))) {
      afterEnd++;
    }
    return afterEnd - start;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    var that = (NamedSql) o;
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

  public Object[] toPositionalParams(Map<String, Object> params) {
    return paramNames.stream().map(extractParam(params)).toArray();
  }

  private Function<String, Object> extractParam(Map<String, Object> params) {
    return (String paramName) -> {
      var paramVal = params.get(paramName);
      if (paramVal == null) {
        var msg = format("No value provided for [%s] in query [%s]", paramName, sqlNamed);
        throw new IllegalArgumentException(msg);
      }
      return paramVal;
    };
  }
}
