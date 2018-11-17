package com.bobocode.exception;

public class FileReaderException extends RuntimeException {
    public FileReaderException(String message, Exception e) {
        super(message, e);
    }
}
