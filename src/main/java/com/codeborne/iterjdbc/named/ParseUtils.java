package com.codeborne.iterjdbc.named;

import java.util.ArrayList;
import java.util.function.Predicate;

class ParseUtils {
  static final char PARAM_TOKEN = ':';
  static final String INLINE_COMMENT_TOKEN = "--";
  static final char STRING_LITERAL_TOKEN = '\'';

  static NamedSql parse(String sql) {
    var params = new ArrayList<String>();
    var sqlPositional = new StringBuilder();
    int pos = 0;
    while (pos < sql.length()) {
      if (isStringLiteralToken(sql, pos)) {
        var stringLiteral = seekStringLiteral(sql, stringLiteralStart(pos));
        if (stringLiteral.isNotEmpty()) {
          sqlPositional
            .append(STRING_LITERAL_TOKEN)
            .append(stringLiteral.getValue())
            .append(STRING_LITERAL_TOKEN);
          pos = stringLiteralAfterEnd(stringLiteral.afterEnd);
          continue;
        }
      }
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

  private static Fragment seekStringLiteral(String s, int start) {
    return seekFragment(s, start, ch -> ch != STRING_LITERAL_TOKEN);
  }

  private static boolean isStringLiteralToken(String s, int pos) {
    return isWithinStringBounds(s, stringLiteralStart(pos))
      && s.charAt(pos) == STRING_LITERAL_TOKEN;
  }

  private static int stringLiteralStart(int tokenStart) {
    return tokenStart + 1;
  }

  private static int stringLiteralAfterEnd(int literalAfterEnd) {
    return literalAfterEnd + 1;
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

}
