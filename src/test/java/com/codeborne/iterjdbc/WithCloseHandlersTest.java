package com.codeborne.iterjdbc;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WithCloseHandlersTest {
  @Test
  void close() {
    StringBuilder closingSequence = new StringBuilder();

    try (WithCloseHandlers withCloseHandlers = new WithCloseHandlers() {}) {
      withCloseHandlers.onClose(() -> closingSequence.append("a"));
      withCloseHandlers.onClose(() -> closingSequence.append("b"));
      withCloseHandlers.onClose(() -> closingSequence.append("c"));
    }

    assertThat(closingSequence.toString()).isEqualTo("abc");
  }

  @Test
  void close_withExceptions() {
    WithCloseHandlers withCloseHandlers = new WithCloseHandlers() {};
    RuntimeException expectedException = new RuntimeException();
    withCloseHandlers.onClose(() -> { throw expectedException; });

    assertThatThrownBy(withCloseHandlers::close)
      .isInstanceOf(RuntimeException.class)
      .hasMessage("Cannot invoke all close handlers")
      .hasSuppressedException(expectedException);
  }
}
