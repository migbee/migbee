![migbee](https://raw.githubusercontent.com/migbee/migbee/master/misc/migbee_min.png)
=======

### What for?

migbee is a Java library which can help you to *manage changes* in your DB and *synchronize* them with your application.
The concept and the code is strongly inspired from [mongobee](http://github.com/mongobee) without the dependencies such as Spring and MongoDb.

The goal is to keep this library simple and comfortable to use.

### What's special?

migbee provides the same advantage as mongobee such as a new approach for adding changes (change sets) based on Java classes and methods with appropriate annotations.

### Adding maven dependency

Library available on https://bintray.com/fpozzobon/migbee/migbee

* Maven


** Add the bintray url library

```
    <repositories>
        <repository>
            <id>bintray</id>
            <name>dependency for migbee</name>
            <url>http://jcenter.bintray.com</url>
        </repository>
    </repositories>
```

** Add the dependency

```
    <dependency>
      <groupId>com.github.migbee</groupId>
      <artifactId>migbee</artifactId>
      <version>0.1.0</version>
      <type>pom</type>
    </dependency>
```

* Gradle

** Add the bintray url library

```
    repositories {
        maven {
            url  "http://jcenter.bintray.com"
        }
    }
```

** Add the dependency

```
    compile 'com.github.migbee:migbee:0.1.0'
```

* Ivy

```
    <dependency org='com.github.migbee' name='migbee' rev='0.1.0'>
      <artifact name='migbee' ext='pom' ></artifact>
    </dependency>
```

### How to implement?

You need to extends the abstract class AbstractMigrationService and override the methods :

* getChangeLogBasePackageName : return the package where to find the migration (changeLog) classes

```
return "com.github.migbee.example.changes"
```

* putChangeEntry : create / update in your database the changeEntry

```
    // below we use MigrationCRUDService injected in the constructor with com.google.inject.Injector
    try {
        this.migrationCRUDService.create(changeEntry);
    } catch (ServiceException exception) {
        throw new DBMigrationServiceException(exception);
    }
```

* isMigrationAlreadyDone : verify if the migration is already done

```
    // below we use MigrationCRUDService injected in the constructor with com.google.inject.Injector
    try {
        return this.migrationCRUDService.exist(changeEntry);
    } catch (ServiceException exception) {
        throw new DBMigrationServiceException(exception);
    }
```

* getInstance : create a new instance corresponding on the migration class (changeLog)

```
    // below we use com.google.inject.Injector initialized in the constructor
    return this.injector.getInstance(changelogClass);
```

### And then ?

Then you need to create your migration classes in the package defined with getChangeLogBasePackageName.
These classes have to be annotated with @ChangeLog and the methods to run with @ChangeSet.

Example (using GUICE ):

```
    @ChangeLog( version="0.1.5", order = "001" )
    public class AMigrationExampleClass {

    	private final ACRUDService aCRUDService;

    	@Inject
    	public AMigrationExampleClass(ACRUDService aCRUDService) {
    		this.aCRUDService = aCRUDService;
    	}

    	@ChangeSet(order = "001", name = "initialise something", author="example")
    	public void initialiseSomething () {
    		try{
    			aCRUDService.initialiseData();
    		} catch(ServiceException e) {
    			e.printStackTrace();
    		}
    	}

    }
```

In the example above, GUICE will build the 'AMigrationExampleClass' instance with injecting the'aCRUDService' parameter needed to the migration.
It's of course possible to use other dependency injection like Spring for example or no injection at all, as long as your migration instance can use what it needs to apply the migration.
