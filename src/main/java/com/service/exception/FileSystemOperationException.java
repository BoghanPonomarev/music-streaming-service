package com.service.exception;

public class FileSystemOperationException extends RuntimeException {

    public FileSystemOperationException(String message, Throwable cause) {
        super(message, cause);
    }

}
