package com.github.migbee.utils;

import com.github.migbee.annotation.ChangeLog;
import com.github.migbee.annotation.ChangeSet;
import org.junit.Test;
import org.mockito.Mockito;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

public class MigrationToolsTest {

	@Test
	public void fetchChangeLogShouldReturnEmptyList () {
		assertTrue(MigrationTools.fetchChangeLogs("anyPackage").isEmpty());
	}

	@Test
	public void fetchChangeLogShouldReturnList () {
		List<Class<?>> actual = MigrationTools.fetchChangeLogs("com.github.migbee.utils");

		assertFalse(actual.isEmpty());
		assertTrue(actual.contains(ChangeLogComparatorTest.ChangeLogResourceV1.class));
	}

	@Test
	public void fetchChangeLogShouldReturnOrderedList () {
		List<Class<?>> actual = MigrationTools.fetchChangeLogs("com.github.migbee.utils");
		List<Class<?>> expected = new ArrayList<>(actual);
		Collections.sort(expected, new ChangeLogComparator());

		assertArrayEquals(expected.toArray(), actual.toArray());
	}

	@Test
	public void fetchChangeSetsShouldReturnEmptyList () {
		assertTrue(MigrationTools.fetchChangeSets(ChangeLogComparatorTest.class).isEmpty());
	}


	@Test
	public void fetchChangeSetsShouldReturnList () throws NoSuchMethodException {
		List<Method> actual = MigrationTools.fetchChangeSets(ChangeSetComparatorTest.ChangeLogResourceV1.class);

		assertFalse(actual.isEmpty());
		assertTrue(actual.contains(ChangeSetComparatorTest.ChangeLogResourceV1.class.getDeclaredMethod("changeSet1")));
	}

	@Test
	public void fetchChangeSetsShouldReturnOrderedList () {
		List<Method> actual = MigrationTools.fetchChangeSets(ChangeSetComparatorTest.ChangeLogResourceV1.class);
		List<Method> expected = new ArrayList<>(actual);
		Collections.sort(expected, new ChangeSetComparator());

		assertArrayEquals(expected.toArray(), actual.toArray());
	}

	@Test(expected = RuntimeException.class)
	public void fetchChangeSetsShouldThrowARuntimeExceptionIfSameName () {
		MigrationTools.fetchChangeSets(ChangeLogResourceWithSameName.class);
	}

	@Test
	public void fetchChangeSetsShouldReturnOnlyAnnotatedMethod () throws NoSuchMethodException {
		List<Method> actual = MigrationTools.fetchChangeSets(ChangeLogResourceWithOther.class);

		assertFalse(actual.isEmpty());
		assertEquals(actual.size(), 1);
		assertEquals(actual.get(0), ChangeLogResourceWithOther.class.getDeclaredMethod("changeSet1"));
	}

	@Test
	public void isAlwaysRunShouldBeFalse () throws NoSuchMethodException {
		boolean actual = MigrationTools.isRunAlwaysChangeSet(ChangeSetComparatorTest.ChangeLogResourceV1.class.getDeclaredMethod("changeSet1"));
		assertFalse(actual);
	}

	@Test
	public void isAlwaysRunShouldBeTrue () throws NoSuchMethodException {
		boolean actual = MigrationTools.isRunAlwaysChangeSet(ChangeLogResourceWithOther.class.getDeclaredMethod("changeSet1"));
		assertTrue(actual);
	}


	@Test
	public void isAnnotationPresentShouldBeFalse () throws NoSuchMethodException {
		boolean actual = MigrationTools.isAnnotationPresent(ChangeLogResourceWithOther.class.getDeclaredMethod("changeSet1"), ChangeLog.class);
		assertFalse(actual);
	}

	@Test
	public void isAnnotationPresentShouldBeTrue () throws NoSuchMethodException {
		boolean actual = MigrationTools.isAnnotationPresent(ChangeLogResourceWithOther.class.getDeclaredMethod("changeSet1"), ChangeSet.class);
		assertTrue(actual);
	}

	@Test
	public void getChangeLogAnnotationShouldReturnNull () {
		ChangeLog actual = MigrationTools.getChangeLogAnnotation(ChangeLogResourceWithSameName.class);
		assertNull(actual);
	}

	@Test
	public void getChangeLogAnnotationShouldReturnAnotation () {
		ChangeLog actual = MigrationTools.getChangeLogAnnotation(ChangeLogResourceWithOther.class);
		assertEquals("11", actual.version());
		assertEquals("5", actual.order());
	}

	@Test
	public void getChangeSetAnnotationShouldReturnNull () throws NoSuchMethodException {
		ChangeSet actual =  MigrationTools.getChangeSetAnnotation(ChangeLogResourceWithOther.class.getDeclaredMethod("changeSet1_"));
		assertNull(actual);
	}

	@Test
	public void getChangeSetAnnotationShouldReturnAnotation () throws NoSuchMethodException {
		ChangeSet actual = MigrationTools.getChangeSetAnnotation(ChangeLogResourceWithOther.class.getDeclaredMethod("changeSet1"));
		assertEquals("1", actual.order());
		assertEquals("test", actual.name());
		assertEquals("unit tests", actual.author());
		assertEquals(true, actual.runAlways());
		assertEquals(false, actual.isCritical());
	}

	@Test
	public void executeChangeSetMethodShouldRunIt () throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		ChangeLogResourceWithOther actual = Mockito.mock(ChangeLogResourceWithOther.class);
		MigrationTools.executeChangeSetMethod(ChangeLogResourceWithOther.class.getDeclaredMethod("changeSet1"), actual);
		Mockito.verify(actual, times(1)).changeSet1();
		Mockito.verify(actual, never()).changeSet1_();
	}

	public class ChangeLogResourceWithSameName {

		@ChangeSet(order = "1", name="test", author="unit tests")
		public void changeSet1 () {}

		@ChangeSet(order = "1", name="test", author="unit tests")
		public void changeSet1_ () {}

	}

	@ChangeLog(version = "11", order = "5")
	public class ChangeLogResourceWithOther {

		@ChangeSet(order = "1", name="test", author="unit tests", runAlways = true)
		public void changeSet1 () {}

		public void changeSet1_ () {}

	}

}
