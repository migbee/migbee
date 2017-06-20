![migbee](https://raw.githubusercontent.com/migbee/migbee/master/misc/migbee_min.png)
=======

### What for?

migbee is a Java library which can help you to *manage changes* in your DB and *synchronize* them with your application.
The concept and the code is strongly inspired from [mongobee](http://github.com/mongobee) without the dependencies such as Spring and MongoDb.

The goal is to keep this library simple and comfortable to use.

### What's special?

migbee provides the same advantage as mongobee such as a new approach for adding changes (change sets) based on Java classes and methods with appropriate annotations.

### Adding maven dependency

* Maven


** Add the bintray url library

<repositories>
    <repository>
        <id>bintray</id>
        <name>dependency for migbee</name>
        <url>http://jcenter.bintray.com</url>
    </repository>
</repositories>

** Add the dependency
    <dependency>
      <groupId>com.github.migbee</groupId>
      <artifactId>migbee</artifactId>
      <version>0.1.0</version>
      <type>pom</type>
    </dependency>

* Gradle

** Add the bintray url library
    repositories {
        maven {
            url  "http://jcenter.bintray.com"
        }
    }

** Add the dependency
    compile 'com.github.migbee:migbee:0.1.0'

* Ivy

<dependency org='com.github.migbee' name='migbee' rev='0.1.0'>
  <artifact name='migbee' ext='pom' ></artifact>
</dependency>

### How to use?

You need to extends the abstract class AbstractMigrationService and override the methods :

* getChangeLogBasePackageName : return the package where to find the migration (changeLog) classes
```
return "com.github.migbee.example.changes"
```

* putChangeEntry : create / update in your database the changeEntry
```
    // below we use MigrationCRUDService injected in the constructor with com.google.inject.Injector
    try {
        this.migrationCRUDService.create(changeLog, changeSet, changeLogClassName, changeSetMethodName);
    } catch (ServiceException exception) {
        throw new DBMigrationServiceException(exception);
    }
```

* isMigrationAlreadyDone : verify if the migration is already done
```
    // below we use MigrationCRUDService injected in the constructor with com.google.inject.Injector
    try {
        return this.migrationCRUDService.exist(changeLog, changeSet, changeLogClassName, changeSetMethodName);
    } catch (ServiceException exception) {
        throw new DBMigrationServiceException(exception);
    }
```

* getInstance : create a new instance corresponding on the migration class (changeLog).
```
    // below we use com.google.inject.Injector initialized in the constructor
    return this.injector.getInstance(changelogClass);
```
