package com.github.migbee.utils;

import com.github.migbee.annotation.ChangeLog;
import com.github.migbee.annotation.ChangeSet;
import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static java.util.Arrays.asList;

public class MigrationTools {

	public static List<Class<?>> fetchChangeLogs(String packageName) {
		Reflections reflections = new Reflections(packageName, new TypeAnnotationsScanner());
		List<Class<?>> changeLogs = new ArrayList<>(reflections.getTypesAnnotatedWith(ChangeLog.class, true));

		Collections.sort(changeLogs, new ChangeLogComparator());

		return changeLogs;
	}

	public static List<Method> fetchChangeSets(final Class<?> type) {
		final List<Method> changeSets = filterChangeSetAnnotation(asList(type.getDeclaredMethods()));

		Collections.sort(changeSets, new ChangeSetComparator());

		return changeSets;
	}

	private static List<Method> filterChangeSetAnnotation(List<Method> allMethods) {
		final Set<String> changeSetNames = new HashSet<>();
		final List<Method> changeSetMethods = new ArrayList<>();
		for (final Method method : allMethods) {
			if (isAnnotationPresent(method, ChangeSet.class)) {
				String name = getChangeSetAnnotation(method).name();
				if (changeSetNames.contains(name)) {
					throw new RuntimeException(String.format("Duplicate changeSet name found: '%s'", name));
				}
				changeSetNames.add(name);
				changeSetMethods.add(method);
			}
		}
		return changeSetMethods;
	}

	public static boolean isRunAlwaysChangeSet(Method changeSetMethod) {
		if (isAnnotationPresent(changeSetMethod, ChangeSet.class)){
			ChangeSet annotation = getChangeSetAnnotation(changeSetMethod);
			return annotation.runAlways();
		} else {
			return false;
		}
	}

	public static boolean isAnnotationPresent(Method method, Class<? extends Annotation> annotation) {
		return method.isAnnotationPresent(annotation);
	}

	public static ChangeLog getChangeLogAnnotation (Class<?> declaringClass) {
		return declaringClass.getAnnotation(ChangeLog.class);
	}

	public static ChangeSet getChangeSetAnnotation (Method method) {
		return method.getAnnotation(ChangeSet.class);
	}

	public static Object executeChangeSetMethod(Method changeSetMethod, Object instance)
			throws IllegalAccessException, InvocationTargetException {

		return changeSetMethod.invoke(instance);
	}

}
