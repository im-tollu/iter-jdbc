package com.codeborne.iterjdbc;

import java.util.Iterator;

public interface CloseableIterator<E> extends Iterator<E>, AutoCloseable {
  @Override
  void close();

  default void onClose(Runnable closeHandler) {
    throw new UnsupportedOperationException();
  }
}
