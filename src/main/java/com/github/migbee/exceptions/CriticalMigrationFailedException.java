package com.github.migbee.exceptions;

/**
 * Exception thrown if a migration fails and was critical.
 */
public class CriticalMigrationFailedException extends RuntimeException {
	public CriticalMigrationFailedException(String message, Throwable cause) {
		super(message, cause);
	}
}
