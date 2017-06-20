package com.github.migbee.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ChangeSet {

	/**
	 * Author of the changeSet.
	 * Mandatory
	 * @return author
	 */
	String author();  // must be set

	/**
	 * Unique NAME of the changeSet.
	 * Mandatory
	 * @return unique name
	 */
	String name();      // must be set

	/**
	 * Sequence that provide correct order for changeSet. Sorted alphabetically, ascending.
	 * Mandatory.
	 * @return ordering
	 */
	String order();   // must be set

	/**
	 * Executes the change set on every migbee's execution, even if it has been run before.
	 * Optional (default is false)
	 * @return should run always?
	 */
	boolean runAlways() default false;

	/**
	 * When true, if the migration fails, an exceptionRuntime is thrown
	 * Optional (default is false)
	 * @return isCritical ?
	 */
	boolean isCritical() default false;

}
