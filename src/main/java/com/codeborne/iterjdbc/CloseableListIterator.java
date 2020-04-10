package com.codeborne.iterjdbc;

import java.util.Iterator;

import static java.util.Arrays.asList;

public class CloseableListIterator<E> extends WithCloseHandlers implements CloseableIterator<E>{
  private final Iterator<E> iterator;

  @SafeVarargs
  public CloseableListIterator(E... elements) {
    this.iterator = asList(elements).iterator();
  }

  @Override
  public boolean hasNext() {
    return iterator.hasNext();
  }

  @Override
  public E next() {
    return iterator.next();
  }
}
