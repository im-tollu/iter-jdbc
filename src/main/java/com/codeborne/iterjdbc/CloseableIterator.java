package com.codeborne.iterjdbc;

import java.util.Iterator;

public interface CloseableIterator<E> extends Iterator<E>, AutoCloseable {
  default void onClose(Runnable closeHandler) {
    throw new UnsupportedOperationException();
  }
}
