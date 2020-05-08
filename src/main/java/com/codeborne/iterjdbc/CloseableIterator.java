package com.codeborne.iterjdbc;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface CloseableIterator<E> extends Iterator<E>, AutoCloseable {
  @Override
  void close();

  default void onClose(Runnable closeHandler) {
    throw new UnsupportedOperationException();
  }

  default Stream<E> stream() {
    Spliterator<E> spliterator = Spliterators.spliteratorUnknownSize(this, 0);
    return StreamSupport.stream(spliterator, false);
  }
}
