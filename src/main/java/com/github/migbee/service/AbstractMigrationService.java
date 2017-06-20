package com.github.migbee.service;

import com.github.migbee.annotation.ChangeLog;
import com.github.migbee.annotation.ChangeSet;
import com.github.migbee.exceptions.DBMigrationServiceException;
import com.github.migbee.interfaces.IChangeEntry;
import com.github.migbee.utils.MigrationTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

/**
 * Utilities to deal with reflections and annotations
 *
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
				IChangeEntry changeEntry = this.createChangeEntry(changeSetMethod);
				if (changeEntry == null) {
					throw new RuntimeException("CreateChangeEntry returned null for the method " + changeSetMethod);
				}
				try {
					if (!this.isMigrationAlreadyDone(changeEntry) || MigrationTools.isRunAlwaysChangeSet(changeSetMethod)) {
						MigrationTools.executeChangeSetMethod(changeSetMethod, changelogInstance);
						logger.info("Migration " + changeEntry.getName() + " done");
						this.putChangeEntry(changeEntry);
					}
				} catch (IllegalAccessException | InvocationTargetException | DBMigrationServiceException e) {
					logger.error("Migration " + changeEntry.getName() + " failed", e);
					if (changeEntry.getCritical()) {
						throw new RuntimeException("Critical migration " + changeEntry.getName() + " failed", e);
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
	 * @param changeEntry to verify
	 * @return boolean true if already done
	 * @throws DBMigrationServiceException
	 */
	protected abstract boolean isMigrationAlreadyDone(IChangeEntry changeEntry) throws DBMigrationServiceException;

	/**
	 * Create / update the changeEntry in database (in order to retrieve it with isMigrationAlreadyDone later)
	 * @param changeEntry which we want to insert in database
	 * @return the created / updated changeEntry
	 * @throws DBMigrationServiceException
	 */
	protected abstract IChangeEntry putChangeEntry (IChangeEntry changeEntry) throws DBMigrationServiceException;

	/**
	 * Provide the package name where to lookup the migration classes
	 * @return package name
	 */
	protected abstract String getChangeLogBasePackageName ();

	/**
	 * Create the change Entry object to store / check from the database
	 * @param version annotated
	 * @param name annotated
	 * @param author annotated
	 * @param timestamp date now
	 * @param changeLogClass where the method is
	 * @param changeSetMethodName name of the method
	 * @param isCritical annotation
	 * @return the changeEntry IChangeEntry
	 */
	protected abstract IChangeEntry createChangeEntry (String version,
													   String name,
													   String author,
													   Date timestamp,
													   String changeLogClass,
													   String changeSetMethodName,
													   boolean isCritical);

	private IChangeEntry createChangeEntry(Method changeSetMethod) {
		ChangeLog changeLogAnnotation = MigrationTools.getChangeLogAnnotation(changeSetMethod.getDeclaringClass());
		if (MigrationTools.isAnnotationPresent(changeSetMethod, ChangeSet.class)
				&& changeLogAnnotation != null){
			ChangeSet annotation = changeSetMethod.getAnnotation(ChangeSet.class);

			return this.createChangeEntry(
					changeLogAnnotation.version(),
					annotation.name(),
					annotation.author(),
					new Date(),
					changeSetMethod.getDeclaringClass().getName(),
					changeSetMethod.getName(),
					annotation.isCritical());
		} else {
			return null;
		}
	}

}
