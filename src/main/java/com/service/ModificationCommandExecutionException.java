package com.service;

public class ModificationCommandExecutionException extends RuntimeException {

  public ModificationCommandExecutionException() {
  }

  public ModificationCommandExecutionException(String message) {
    super(message);
  }

  public ModificationCommandExecutionException(String message, Throwable cause) {
    super(message, cause);
  }

  public ModificationCommandExecutionException(Throwable cause) {
    super(cause);
  }

  public ModificationCommandExecutionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
