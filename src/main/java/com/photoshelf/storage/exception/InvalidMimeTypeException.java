package com.photoshelf.storage.exception;

public class InvalidMimeTypeException extends RuntimeException {

	public InvalidMimeTypeException(String message) {
		super(message);
	}

	public InvalidMimeTypeException(String message, Throwable cause) {
		super(message, cause);
	}
}
