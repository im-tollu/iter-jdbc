package com.codeborne.iterjdbc.named;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import static java.lang.String.format;

/**
 * This class handles SQL with named parameters.
 *
 * Use static factory {@link #parse(String)} to get an instance of this class.
 *
 * It takes SQL with named parameters like this: `select * from BOOKS where ID = :bookId`. It then
 * parses it and holds both SQL with positional parameters and a mapping to convert parameters map
 * to an array: `select * from BOOKS where ID = ?`.
 */
public class NamedSql {
  private final String sqlNamed;
  private final String sqlPositional;
  private final List<String> paramNames;

  NamedSql(String sqlNamed, String sqlPositional, List<String> paramNames) {
    this.sqlNamed = sqlNamed;
    this.sqlPositional = sqlPositional;
    this.paramNames = paramNames;
  }

  /**
   * @return - SQL query with positional placeholders (`?`) as required by standard JDBC syntax.
   */
  public String getSqlPositional() {
    return sqlPositional;
  }

  /**
   * @param params - mapping of the named parameters to their values.
   * @return - values of positional parameters in the correct order.
   */
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

  /**
   * Factory for this class that handles parsing of an SQL query.
   * @param sql - SQL query with named parameters like `select * from BOOKS where ID = :bookId`
   * @return - parsed SQL query that can be used as an input to {@link com.codeborne.iterjdbc.Query}
   * and {@link com.codeborne.iterjdbc.Update}
   */
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
