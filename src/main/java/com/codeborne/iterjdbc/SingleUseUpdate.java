package com.codeborne.iterjdbc;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

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

  public int runBatch(Iterator<Map<String, Object>> paramsIterator, int batchSize) {
    int affectedRows = reusableUpdate.runBatch(paramsIterator, batchSize);
    reusableUpdate.close();
    return affectedRows;
  }

  @Override
  public String toString() {
    return "SingleUseUpdate{" +
      "reusableUpdate=" + reusableUpdate +
      '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    SingleUseUpdate that = (SingleUseUpdate) o;
    return reusableUpdate.equals(that.reusableUpdate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(reusableUpdate);
  }
}
