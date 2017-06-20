package com.github.migbee.utils;

import com.github.migbee.annotation.ChangeSet;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;

public class ChangeSetComparatorTest {

	ChangeSetComparator tested = new ChangeSetComparator();

	Method changeSet1;
	Method changeSet1_;
	Method changeSet2;

	@Before
	public void setup() throws NoSuchMethodException {
		changeSet1 = ChangeLogResourceV1.class.getDeclaredMethod("changeSet1");
		changeSet1_ = ChangeLogResourceV1.class.getDeclaredMethod("changeSet1_");
		changeSet2 = ChangeLogResourceV1.class.getDeclaredMethod("changeSet2");
	}

	@Test
	public void shouldReturnZeroWhenSameOrder() {
		int actual = tested.compare(changeSet1, changeSet1_);
		assertEquals(actual, 0);
	}

	@Test
	public void shouldReturnMinusOneWhenGreaterOrder() {
		int actual = tested.compare(changeSet1, changeSet2);
		assertEquals(actual, -1);
	}

	@Test
	public void shouldReturnOneWhenLessOrder() {
		int actual = tested.compare(changeSet2, changeSet1);
		assertEquals(actual, 1);
	}

	public class ChangeLogResourceV1 {

		@ChangeSet(order = "1", name="test", author="unit tests")
		public void changeSet1 () {}

		@ChangeSet(order = "1", name="test2", author="unit tests")
		public void changeSet1_ () {}

		@ChangeSet(order = "2", name="test3", author="unit tests")
		public void changeSet2 () {}
	}

}
