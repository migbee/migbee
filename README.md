# migbee
Database migration library


### What for?

migbee is a Java library which can help you to *manage changes* in your DB and *synchronize* them with your application.
The concept and the code is strongly inspired from [mongobee](http://github.com/mongobee) without the dependencies such as Spring and MongoDb.

The goal is to keep this tool simple and comfortable to use.

### What's special?

migbee provides the same advantage as mongobee such as a new approach for adding changes (change sets) based on Java classes and methods with appropriate annotations.

### How to use?

You need to implement the interface IChangeEntry which will be used to read / write in your database (example using hibernate).

You also need to extends the abstract class AbstractMigrationService and override the methods :

* getChangeLogBasePackageName : return the package where to find the migration (changeLog) classes
```
return "com.github.migbee.example.changes"
```

* createChangeEntry : return your implementation of IChangeEntry
```
return new ChangeEntryImpl(version,
                            name,
                            author,
                            timestamp,
                            changeLogClass,
                            changeSetMethodName,
                            isCritical);
```

* putChangeEntry : create / update in your database the changeEntry
```
    // below we use MigrationCRUDService injected in the constructor with com.google.inject.Injector
    try {
        return this.migrationCRUDService.create((ChangeEntryImpl)changeEntry);
    } catch (ServiceException exception) {
        throw new DBMigrationServiceException(exception);
    }
```

* isMigrationAlreadyDone : verify if the migration is already done
```
    // below we use MigrationCRUDService injected in the constructor with com.google.inject.Injector
    try {
        return this.migrationCRUDService.exist((ChangeEntryImpl)changeEntry);
    } catch (ServiceException exception) {
        throw new DBMigrationServiceException(exception);
    }
```

* getInstance : create a new instance corresponding on the migration class (changeLog).
```
    // below we use com.google.inject.Injector initialized in the constructor
    return this.injector.getInstance(changelogClass);
```
