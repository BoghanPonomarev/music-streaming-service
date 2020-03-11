package com.service.exception;

public class CommandExecutionException extends RuntimeException {

  public CommandExecutionException(String message, Throwable cause) {
    super(message, cause);
  }

}
