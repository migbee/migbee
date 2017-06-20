package com.github.migbee.service;

import com.github.migbee.annotation.ChangeLog;
import com.github.migbee.annotation.ChangeSet;
import com.github.migbee.exceptions.DBMigrationServiceException;
import com.github.migbee.interfaces.IChangeEntry;
import com.github.migbee.service.AbstractMigrationServiceResource.ChangeLogResource1;
import com.github.migbee.service.AbstractMigrationServiceResource.ChangeLogResource2;
import com.github.migbee.utils.ChangeSetComparatorTest;
import com.github.migbee.utils.MigrationTools;
import com.github.migbee.utils.MigrationToolsTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(MigrationTools.class)
public class AbstractMigrationServiceTest {

	@Mock
	MigrationToolsTest.ChangeLogResourceWithOther changeLog;

	@Mock
	AbstractMigrationService tested;

	@Mock
	IChangeEntry changeEntry;

	ChangeLogResource1 resource1;
	ChangeLogResource2 resource2;

	@Mock
	ChangeSet changeSetAnnotation;
	@Mock
	ChangeLog changeLogAnnotation;

	Method fakeMethod;

	@Before
	public void setup() throws NoSuchMethodException {
		PowerMockito.mockStatic(MigrationTools.class);
		MockitoAnnotations.initMocks(this);
		Mockito.doCallRealMethod().when(tested).executeMigration();
		resource1 = new AbstractMigrationServiceResource().new ChangeLogResource1();
		resource2 = new AbstractMigrationServiceResource().new ChangeLogResource2();
		Mockito.when(tested.createChangeEntry(any(), any(), any(), any(), any(), any(), anyBoolean())).thenReturn(changeEntry);
		fakeMethod = ChangeSetComparatorTest.ChangeLogResourceV1.class.getDeclaredMethod("changeSet1");
	}

	@Test
	public void shouldFetchChangeLogs () {
		String expectedPackage = "A Package";
		Mockito.when(tested.getChangeLogBasePackageName()).thenReturn(expectedPackage);
		Mockito.when(MigrationTools.fetchChangeLogs(any())).thenReturn(new ArrayList<Class<?>>());

		tested.executeMigration();

		PowerMockito.verifyStatic(Mockito.times(1));
		MigrationTools.fetchChangeLogs(expectedPackage);
	}

	@Test
	public void shouldDoNothingIfNoChangeLog () {
		Mockito.when(MigrationTools.fetchChangeLogs(any())).thenReturn(Arrays.asList());

		tested.executeMigration();

		verify(tested, times(1)).executeMigration();
		verify(tested, times(1)).getChangeLogBasePackageName();
		Mockito.verifyNoMoreInteractions(tested);
	}

	@Test
	public void shouldGetInstance () {
		Mockito.when(tested.getInstance(any())).thenReturn(resource1);
		Mockito.when(MigrationTools.fetchChangeLogs(any())).thenReturn(
				Arrays.asList(ChangeLogResource1.class, ChangeLogResource2.class));

		tested.executeMigration();
		verify(tested, times(2)).getInstance(any());
		verify(tested, times(1)).getInstance(AbstractMigrationServiceResource.ChangeLogResource1.class);
		verify(tested, times(1)).getInstance(AbstractMigrationServiceResource.ChangeLogResource2.class);
	}

	@Test
	public void shouldNotCreateChangeEntryIfEmptySet () {
		Mockito.when(tested.getInstance(any())).thenReturn(resource1);
		Mockito.when(MigrationTools.fetchChangeLogs(any())).thenReturn(Arrays.asList(ChangeLogResource1.class));
		Mockito.when(MigrationTools.fetchChangeSets(any())).thenReturn(Arrays.asList());

		tested.executeMigration();
		PowerMockito.verifyStatic(Mockito.times(1));
		MigrationTools.fetchChangeSets(ChangeLogResource1.class);
		verify(tested, never()).createChangeEntry(any(), any(), any(), any(), any(), any(), anyBoolean());
	}

	private void mockCreateChangeEntry () throws NoSuchMethodException {
		Mockito.when(tested.getInstance(any())).thenReturn(resource1);
		Mockito.when(MigrationTools.fetchChangeLogs(any())).thenReturn(Arrays.asList(ChangeLogResource1.class));
		Mockito.when(MigrationTools.fetchChangeSets(any())).thenReturn(Arrays.asList(fakeMethod));
		Mockito.when(MigrationTools.isAnnotationPresent(any(), any())).thenReturn(true);
		Mockito.when(MigrationTools.getChangeSetAnnotation(any())).thenReturn(changeSetAnnotation);
		Mockito.when(MigrationTools.getChangeLogAnnotation(any())).thenReturn(changeLogAnnotation);
	}

	@Test
	public void shouldCreateChangeEntry () throws NoSuchMethodException {
		mockCreateChangeEntry();

		tested.executeMigration();
		verify(tested, times(1)).createChangeEntry(any(), any(), any(), any(), any(), any(), anyBoolean());
	}

	@Test
	public void shouldPutChangeEntry () throws NoSuchMethodException, DBMigrationServiceException {
		mockCreateChangeEntry();

		tested.executeMigration();
		verify(tested, times(1)).putChangeEntry(changeEntry);
	}

	@Test
	public void shouldNotExecuteWhenAlreadyDone () throws NoSuchMethodException, DBMigrationServiceException {
		mockCreateChangeEntry();
		Mockito.when(tested.isMigrationAlreadyDone(any())).thenReturn(true);

		tested.executeMigration();
		verify(tested, never()).putChangeEntry(changeEntry);
	}

	@Test
	public void shouldExecuteWhenNotAlreadyDone () throws NoSuchMethodException, DBMigrationServiceException {
		mockCreateChangeEntry();
		Mockito.when(tested.isMigrationAlreadyDone(any())).thenReturn(false);

		tested.executeMigration();
		verify(tested, times(1)).putChangeEntry(changeEntry);
	}

	@Test
	public void shouldExecuteWhenAlwaysExecute () throws NoSuchMethodException, DBMigrationServiceException {
		mockCreateChangeEntry();
		Mockito.when(tested.isMigrationAlreadyDone(any())).thenReturn(true);
		Mockito.when(MigrationTools.isRunAlwaysChangeSet(any())).thenReturn(true);

		tested.executeMigration();
		PowerMockito.verifyStatic(Mockito.times(1));
		MigrationTools.isRunAlwaysChangeSet(fakeMethod);
		verify(tested, times(1)).putChangeEntry(changeEntry);

	}


}
