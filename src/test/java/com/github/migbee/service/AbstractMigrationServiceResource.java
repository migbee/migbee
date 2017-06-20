package com.github.migbee.service;

import com.github.migbee.annotation.ChangeLog;
import com.github.migbee.annotation.ChangeSet;

public class AbstractMigrationServiceResource {

	@ChangeLog(version = "1", order = "1")
	public class ChangeLogResource1 {

		@ChangeSet(order = "1", name="test 1", author="unit tests")
		public void changeSet1 () {}

		@ChangeSet(order = "2", name="test 2", author="unit tests")
		public void changeSet2 () {}

	}

	@ChangeLog(version = "1", order = "2")
	public class ChangeLogResource2 {
		@ChangeSet(order = "1", name="test 3", author="unit tests", runAlways = true, isCritical = true)
		public void changeSet3Always () {}

	}

}
