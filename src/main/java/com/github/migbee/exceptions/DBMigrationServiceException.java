package com.github.migbee.exceptions;

public class DBMigrationServiceException extends Exception {
	public DBMigrationServiceException(Throwable cause) {
		super(cause);
	}

	public DBMigrationServiceException(String message) {
		super(message);
	}
}
