package com.codeborne.iterjdbc;

import java.util.Map;

public class SingleUseQuery<E> extends WithCloseHandlers {
  private final ReusableQuery<E> reusableQuery;

  public SingleUseQuery(ReusableQuery<E> reusableQuery) {
    this.reusableQuery = reusableQuery;
  }

  public CloseableIterator<E> run(Map<String, Object> params) {
    CloseableIterator<E> results = reusableQuery.run(params);
    results.onClose(reusableQuery::close);
    return results;
  }

  public E runForSingleResult(Map<String, Object> params) {
    E result = reusableQuery.runForSingleResult(params);
    reusableQuery.close();
    return result;
  }
}
