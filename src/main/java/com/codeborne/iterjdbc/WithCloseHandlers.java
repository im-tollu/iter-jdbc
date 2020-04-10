package com.codeborne.iterjdbc;

import java.util.ArrayList;
import java.util.List;

public abstract class WithCloseHandlers implements AutoCloseable {
  private final List<Runnable> closeHandlers = new ArrayList<>();

  @Override
  public void close() {
    var errs = new ArrayList<Throwable>();
    closeHandlers.forEach(handler -> {
      try {
        handler.run();
      } catch (Throwable e) {
        errs.add(e);
      }
    });
    if (errs.size() > 0) {
      var exception = new RuntimeException("Cannot invoke all close handlers");
      errs.forEach(exception::addSuppressed);
      throw exception;
    }
  }

  public void onClose(Runnable closeHandler) {
    this.closeHandlers.add(closeHandler);
  }
}
