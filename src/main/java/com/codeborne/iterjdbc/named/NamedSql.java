package com.codeborne.iterjdbc.named;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.lang.String.format;

public class NamedSql {
  private static final char PARAM_TOKEN = ':';
  private static final String INLINE_COMMENT_TOKEN = "--";

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
      if (isInlineCommentToken(sql, pos)) {
        var inlineComment = seekInlineComment(sql, inlineCommentStart(pos));
        if (inlineComment.isNotEmpty()) {
          sqlPositional.append(INLINE_COMMENT_TOKEN).append(inlineComment.getValue());
          pos = inlineComment.afterEnd;
          continue;
        }
      }
      if (isParamNameToken(sql, pos)) {
        var paramName = seekParamName(sql, paramNameStart(pos));
        if (paramName.isNotEmpty()) {
          params.add(paramName.getValue());
          sqlPositional.append('?');
          pos = paramName.afterEnd;
          continue;
        }
      }
      sqlPositional.append(sql.charAt(pos));
      pos++;
    }
    return new NamedSql(sql, sqlPositional.toString(), params);
  }

  private static boolean isInlineCommentToken(String s, int pos) {
    int commentStart = inlineCommentStart(pos);
    return isWithinStringBounds(s, commentStart)
      && s.subSequence(pos, commentStart).equals(INLINE_COMMENT_TOKEN);
  }

  private static int inlineCommentStart(int tokenStart) {
    return tokenStart + 2;
  }

  private static Fragment seekInlineComment(String s, int start) {
    return seekFragment(s, start, ch -> ch != '\n');
  }

  private static boolean isParamNameToken(String s, int pos) {
    return isWithinStringBounds(s, paramNameStart(pos))
      && s.charAt(pos) == PARAM_TOKEN;
  }

  private static int paramNameStart(int tokenStart) {
    return tokenStart  + 1;
  }

  private static Fragment seekParamName(String s, int start) {
    return seekFragment(s, start, Character::isLetterOrDigit);
  }

  private static boolean isWithinStringBounds(String s, int pos) {
    return pos < s.length();
  }

  private static Fragment seekFragment(String s, int start, Predicate<Character> whileMatches) {
    int afterEnd = start;
    while (afterEnd < s.length() && whileMatches.test(s.charAt(afterEnd))) {
      afterEnd++;
    }
    return new Fragment(s, start, afterEnd);
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

  static class Fragment{
    final String s;
    final int start;
    final int afterEnd;

    Fragment(String s, int start, int afterEnd) {
      this.s = s;
      this.start = start;
      this.afterEnd = afterEnd;
    }

    boolean isNotEmpty() {
      return s.length() > 0;
    }

    String getValue() {
      return s.substring(start, afterEnd);
    }
  }
}
