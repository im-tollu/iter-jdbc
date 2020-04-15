package com.codeborne.iterjdbc;

import java.util.ArrayList;
import java.util.List;

public abstract class WithCloseHandlers implements AutoCloseable {
  private final List<Runnable> closeHandlers = new ArrayList<>();

  @Override
  public void close() {
    ArrayList<Throwable> errs = new ArrayList<>();
    closeHandlers.forEach(handler -> {
      try {
        handler.run();
      } catch (Throwable e) {
        errs.add(e);
      }
    });
    if (errs.size() > 0) {
      RuntimeException exception = new RuntimeException("Cannot invoke all close handlers");
      errs.forEach(exception::addSuppressed);
      throw exception;
    }
  }

  public void onClose(Runnable closeHandler) {
    this.closeHandlers.add(closeHandler);
  }
}
