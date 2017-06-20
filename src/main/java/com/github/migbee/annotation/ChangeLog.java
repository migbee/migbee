package com.github.migbee.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ChangeLog {

	/**
	 * Version of the change the application to apply the change.
	 * We sort in priority with the version
	 * @return version
	 */
	String version();  // must be set

	/**
	 * Sequence that provide an order for changelog classes.
	 * If not set, then canonical name of the class is taken and sorted alphabetically, ascending.
	 * @return order
	 */
	String order() default "";
}
