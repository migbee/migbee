package com.github.migbee.service;

import com.github.migbee.annotation.ChangeLog;
import com.github.migbee.annotation.ChangeSet;
import com.github.migbee.exceptions.DBMigrationServiceException;
import com.github.migbee.utils.MigrationTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Abstract migration service class, this class must be extended and call executeMigration
 * to do the migration.
 */
public abstract class AbstractMigrationService {

	private final static Logger logger = LoggerFactory.getLogger(AbstractMigrationService.class);

	/**
	 * Method executing the migration
	 */
	public void executeMigration() {
		for (Class<?> changelogClass : MigrationTools.fetchChangeLogs(this.getChangeLogBasePackageName())) {

			Object changelogInstance = this.getInstance(changelogClass);
			List<Method> changeSetMethods = MigrationTools.fetchChangeSets(changelogInstance.getClass());

			for (Method changeSetMethod : changeSetMethods) {

				ChangeLog changeLogAnnotation = MigrationTools.getChangeLogAnnotation(changelogClass);
				ChangeSet changeSetAnnotation = MigrationTools.getChangeSetAnnotation(changeSetMethod);
				String changeLogClassName = changelogClass.getName();
				String changeSetMethodName = changeSetMethod.getName();

				try {
					if (!this.isMigrationAlreadyDone(changeLogAnnotation, changeSetAnnotation, changeLogClassName, changeSetMethodName)
							|| changeSetAnnotation.runAlways() ) {
						MigrationTools.executeChangeSetMethod(changeSetMethod, changelogInstance);
						logger.info("Migration " + changeSetAnnotation.name() + " done");
						this.putChangeEntry(changeLogAnnotation, changeSetAnnotation, changeLogClassName, changeSetMethodName);
					}
				} catch (IllegalAccessException | InvocationTargetException | DBMigrationServiceException e) {
					logger.error("Migration " + changeSetAnnotation.name() + " failed", e);
					if (changeSetAnnotation.isCritical()) {
						throw new RuntimeException("Critical migration " + changeSetAnnotation.name() + " failed", e);
					}
				}
			}

		}
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
	 * @param changeLog applying the migration
	 * @param changeSet applying the migration
	 * @param changeLogClassName class name
	 * @param changeSetMethodName method name
	 * @return boolean true if already done
	 * @throws DBMigrationServiceException to throw if verifying migration already done fails
	 */
	protected abstract boolean isMigrationAlreadyDone(ChangeLog changeLog, ChangeSet changeSet, String changeLogClassName, String changeSetMethodName) throws DBMigrationServiceException;

	/**
	 * Create / update the changeEntry in database (in order to retrieve it with isMigrationAlreadyDone later)
	 * @param changeLog applying the migration
	 * @param changeSet applying the migration
	 * @param changeLogClassName class name
	 * @param changeSetMethodName method name
	 * @throws DBMigrationServiceException to throw if put fails
	 */
	protected abstract void putChangeEntry (ChangeLog changeLog, ChangeSet changeSet, String changeLogClassName, String changeSetMethodName) throws DBMigrationServiceException;

	/**
	 * Provide the package name where to lookup the migration classes
	 * @return package name
	 */
	protected abstract String getChangeLogBasePackageName ();

}
