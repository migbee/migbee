package com.github.migbee.service;

import com.github.migbee.annotation.ChangeLog;
import com.github.migbee.annotation.ChangeSet;
import com.github.migbee.exceptions.CriticalMigrationFailedException;
import com.github.migbee.exceptions.DBMigrationServiceException;
import com.github.migbee.utils.MigrationTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract migration service class, this class must be extended and call executeMigration
 * to do the migration.
 */
public abstract class AbstractMigrationService {

	private final static Logger logger = LoggerFactory.getLogger(AbstractMigrationService.class);

	/**
	 * Method executing the migration
	 * @return list of failed migrations
	 */
	public List<ChangeEntry> executeMigration() {
		List<ChangeEntry> failedMigrations = new ArrayList<>();
		for (Class<?> changelogClass : MigrationTools.fetchChangeLogs(this.getChangeLogBasePackageName())) {

			List<Method> changeSetMethods = MigrationTools.fetchChangeSets(changelogClass);

			for (Method changeSetMethod : changeSetMethods) {

				ChangeLog changeLogAnnotation = MigrationTools.getChangeLogAnnotation(changelogClass);
				ChangeSet changeSetAnnotation = MigrationTools.getChangeSetAnnotation(changeSetMethod);

				ChangeEntry changeEntry = MigrationTools.createChangeEntry(changeLogAnnotation,
						changelogClass,
						changeSetAnnotation,
						changeSetMethod);

				try {
					if (!this.isMigrationAlreadyDone(changeEntry)
							|| changeSetAnnotation.runAlways() ) {
						Object changelogInstance = this.getInstance(changelogClass);
						MigrationTools.executeChangeSetMethod(changeSetMethod, changelogInstance);
						logger.info("Migration " + changeSetAnnotation.name() + " done");
						this.putChangeEntry(changeEntry);
					}
				} catch (IllegalAccessException | InvocationTargetException | DBMigrationServiceException e) {
					logger.error("Migration " + changeSetAnnotation.name() + " failed", e);
					if (changeSetAnnotation.isCritical()) {
						throw new CriticalMigrationFailedException("Critical migration " + changeSetAnnotation.name() + " failed", e);
					}
					failedMigrations.add(changeEntry);
				}
			}
		}
		return failedMigrations;
	}

	/**
	 * Get the instance corresponding to an annotated class with changeLog
	 * @param changelogClass - which we want the instance
	 * @param <T> - class instance to return
	 * @return instance
	 */
	protected abstract <T> T getInstance(Class<T> changelogClass);

	/**
	 * Should return true if the migration has been already done (ie exist in database)
	 * @param changeEntry containing the migration method with it's annotation
	 * @return boolean true if already done
	 * @throws DBMigrationServiceException to throw if verifying migration already done fails
	 */
	protected abstract boolean isMigrationAlreadyDone(ChangeEntry changeEntry) throws DBMigrationServiceException;

	/**
	 * Create / update the changeEntry in database (in order to retrieve it with isMigrationAlreadyDone later)
	 * @param changeEntry containing the migration method with it's annotation
	 * @throws DBMigrationServiceException to throw if put fails
	 */
	protected abstract void putChangeEntry (ChangeEntry changeEntry) throws DBMigrationServiceException;

	/**
	 * Provide the package name where to lookup the migration classes
	 * @return package name
	 */
	protected abstract String getChangeLogBasePackageName ();

}
