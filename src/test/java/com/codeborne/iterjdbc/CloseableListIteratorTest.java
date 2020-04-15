package com.codeborne.iterjdbc;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CloseableListIteratorTest {
  @Test
  void emptyCloseableIterator() {
    assertThat(CloseableListIterator.emptyCloseableIterator()).toIterable().isEmpty();
  }

  @Test
  void listIterator() {
    assertThat(new CloseableListIterator<>())
      .toIterable().isEmpty();
    assertThat(new CloseableListIterator<>("a"))
      .toIterable().containsExactly("a");
    assertThat(new CloseableListIterator<>("a", "b", "c"))
      .toIterable().containsExactly("a", "b", "c");
  }
}
