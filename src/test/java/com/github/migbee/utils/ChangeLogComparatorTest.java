package com.github.migbee.utils;

import com.github.migbee.annotation.ChangeLog;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ChangeLogComparatorTest {

	ChangeLogComparator tested = new ChangeLogComparator();

	@Test
	public void shouldReturnZeroWhenSameVersionAndName() {
		int actual = tested.compare(ChangeLogResourceV1.class, ChangeLogResourceV1.class);
		assertEquals(actual, 0);
	}

	@Test
	public void shouldReturnOneWhenVersionLess() {
		int actual = tested.compare(ChangeLogResourceV2.class, ChangeLogResourceV1.class);
		assertEquals(actual, 1);
	}

	@Test
	public void shouldReturnMinusOneWhenVersionLess() {
		int actual = tested.compare(ChangeLogResourceV1.class, ChangeLogResourceV2.class);
		assertEquals(actual, -1);
	}

	@Test
	public void shouldReturnNegativeWhenSameVersionButLessOrder() {
		int actual = tested.compare(ChangeLogResourceV2Order1.class, ChangeLogResourceV2Order2.class);
		assertTrue(actual < 0);
	}

	@Test
	public void shouldReturnPositiveWhenSameVersionButNoOrderForOne() {
		int actual = tested.compare(ChangeLogResourceV2Order1.class, ChangeLogResourceV2.class);
		assertTrue(actual > 0);
	}

	@ChangeLog(version = "1")
	public class ChangeLogResourceV1 {
	}

	@ChangeLog(version = "1")
	public class ChangeLogResourceV1_Other {
	}

	@ChangeLog(version = "2")
	public class ChangeLogResourceV2 {
	}

	@ChangeLog(version = "2", order="1")
	public class ChangeLogResourceV2Order1 {
	}

	@ChangeLog(version = "2", order="2")
	public class ChangeLogResourceV2Order2 {
	}

}
