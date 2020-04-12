package com.codeborne.iterjdbc.named;

class Fragment {
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
