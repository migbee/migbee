package com.github.migbee.service;

import com.github.migbee.annotation.ChangeLog;
import com.github.migbee.annotation.ChangeSet;

import java.lang.reflect.Method;

/**
 * Change entry representing one method with :
 * ChangeLog annotation and class on it's class
 * ChangeSet annotation and method it's method
 */
public class ChangeEntry {

	private final ChangeLog changeLog;
	private final Class<?> changelogClass;
	private final ChangeSet changeSet;
	private final Method changeSetMethod;

	public ChangeEntry(ChangeLog changeLog, Class<?> changelogClass, ChangeSet changeSet, Method changeSetMethod) {
		this.changeLog = changeLog;
		this.changelogClass = changelogClass;
		this.changeSet = changeSet;
		this.changeSetMethod = changeSetMethod;
	}

	public ChangeLog getChangeLog() {
		return changeLog;
	}

	public Class<?> getChangelogClass() {
		return changelogClass;
	}

	public ChangeSet getChangeSet() {
		return changeSet;
	}

	public Method getChangeSetMethod() {
		return changeSetMethod;
	}
}
