package com.codeborne.iterjdbc;

import java.util.Iterator;
import java.util.Map;

public class SingleUseUpdate {
  private final ReusableUpdate reusableUpdate;

  public SingleUseUpdate(ReusableUpdate reusableUpdate) {
    this.reusableUpdate = reusableUpdate;
  }

  public int run(Map<String, Object> params) {
    int affectedRows = reusableUpdate.run(params);
    reusableUpdate.close();
    return affectedRows;
  }

  public int runBatch(Iterator<Map<String, Object>> paramsIterator) {
    int affectedRows = reusableUpdate.runBatch(paramsIterator);
    reusableUpdate.close();
    return affectedRows;
  }
}
