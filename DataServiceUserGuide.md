# Introduction #

The DataService API is a convenience API on top of the existing, regular [Database API](DatabaseApi.md). The goal is to simplify HTML5 Database programming without sacrificing important features and keeping good runtime performance.

The API involves an interface, a few annotations and GWT's Deferred Binding to glue the parts together.

# Quick start #
## Project setup ##
If you use a Maven setup, [add the repository](MavenUserGuide.md) and the following to your `pom.xml` file:

```
<dependencies>
  ...
  <dependency>
    <groupId>com.google.code.gwt-mobile-webkit</groupId>
    <artifactId>gwt-html5-database</artifactId>
  </dependency>
</dependencies>
```

If you don't use Maven, copy the `gwt-html5-database-x.x.x.jar` file to your project classpath. This jar contains everything you need to use the DataService API.

Next, in your GWT module `gwt.xml` file (which uses the DataService API), add the following entry:
```
<inherits name="com.google.code.gwt.database.Html5Database" />
```
This imports the Database API, which includes the DataService API into your module. Now you are ready to use the API!

## GWT client code ##
In your GWT module, create an interface with a Connection annotation (in a `.client` package, because eventually the code runs on the client side):

```
package my.gwt.package.client;

import com.google.code.gwt.database.client.service.*;

@Connection(name = "myDatabase", version = "1.0", description = "My Database", maxsize = 10000)
public interface MyDataService extends DataService {

  @Update("CREATE TABLE IF NOT EXISTS testtable ("
            + "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
            + "adate INTEGER")
  void initDatabase(VoidCallback callback);
}
```

As you probably can imagine, a call to the `initDatabase()` will eventually run the `CREATE TABLE` statement on the database.

Alright, let's review what is shown here.
  1. the interface declaration: Your interface **must** extend `DataService` in order to be recognized as an actual DataService. This is important for the GWT deferred binding process.
  1. the interface must be annotated with `@Connection`, and all four parameters must be provided. These are the exact same parameters as provided to the `Database.openDatabase()` method.
  1. the interface methods must have a `void` return type, a `@Update` (or `@Select`) annotation and a list of parameter(s) which must **always** end with a `[Void|Scalar|List|RowIdList]Callback` declaration - almost like a GWT RPC service method.

So how do we use this interface?

Try this:

```
MyDataService dbService = GWT.create(MyDataService.class);

dbService.initDatabase(new VoidCallback() {
  public void onFailure(DataServiceException e) {
    // Handle failure. We get here if no Database implementation is available,
    // or if the SQL statement could not be executed.
  }
  public void onSuccess() {
    // Transaction completed! Continue...
  }
}
```

If you're used to the GWT RPC mechanism, you'll see a familiar structure here: a Callback interface providing a Failure and a Success path. Either the `onFailure()` or the `onSuccess()` callback method is called. The callback design is due to the asynchronous nature of the HTML5 Database API.


# DataService capabilities #

## Transactions ##
### Multiple SQL statements in a single transaction ###
The DataService API supports transactions. Each method in the DataService starts (and ends) its own transaction. To make INSERT statements efficient, you can iterate over a collection and execute a SQL statement at each iteration in the `@Update` annotation, e.g.:

```
@Update(sql="INSERT INTO testtable (adate) VALUES ({_.getTime()})", foreach="clicks")
void insertDate(Collection<Date> clicks, RowIdListCallback callback);
```

As you can see, the `foreach` attribute is added to the `@Update` annotation. This attribute contains a Java expression resolving to either an `Iterable` or an `Array` of anything. Normally this would just be the name of a parameter (in the example, it is the parameter named `clicks`). In the SQL statement, you use the value of the iteration expressed as an underscore (`_`), like in the example: `_.getTime()` returns the millisecs from Jan 1s, 1970 as the value for the date (SQLite doesn't support date types).

All these statements are executed in a single transaction.

The `RowIdListCallback` in the example returns the generated primary key ID's in a List.

### What happens when the Web Database API is not supported in the browser ###
When the Web Database API itself is not supported in the browser, the `onFailure()` callback method is called immediately if you invoke a DataService method. The error code will be `0` and the message will be `Unable to open Web Database - API is NOT supported`.

### What happens when a statement fails ###
Whenever a SQL statement fails, the transaction is always rolled back, and the Callback's `onFailure()` method is called with an error code/message indicating the source of the error.

### Read-only and read-write mode ###
The Database API supports both read-only transactions and read-write transactions. The DataService checks whether all specified SQL statements of a service method start with `SELECT`, and if that is true, the transaction will be executed in read-only mode - otherwise, it will be executed in read-write mode.

## Parameters ##
Obviously, a Java method can carry some parameters. Luckily that matches perfectly with what we can do with the SQL statements. For instance:

```
@Update("INSERT INTO testtable (adate, name) VALUES ({when.getTime()}, {name})")
void insertDate(String name, Date when, RowIdListCallback callback);
```

The method declares two parameters before the callback: a String called `name` and a Date called `when`. These parameters also found their way in the SQL statements - between the curly braces you can see the Java expressions (that's what they are eventually) `when.getTime()` and `name`. The method parameters can be (re)used as much as you like. You could also use expressions not depending on any parameter, e.g. `{System.currentTimeMillis()}`.

Under the hood, the expressions with their curly braces are replaced with a question mark (`?`) and the expressions are moved to an `Object[]` array initializer (in this example, `new Object[] {when.getTime(), name}`).

### Collection parameters ###
There are two usecases for Collection (or Array, or Iterable) parameters:
  1. Create an SQL statement for each item in the collection (see [Multiple SQL statements in a single transaction](#Multiple_SQL_statements_in_a_single_transaction.md));
  1. Create an SQL `IN(...)` expression with a value for each item in the collection.

The collection is expanded during runtime with its values in the SQL statement (regardless of its placement in an `IN()` statement BTW). For instance:

```
@Select("SELECT * FROM atable WHERE value IN({filterValues})")
void getFilteredData(List<Integer> filterValues, ListCallback<GenericRow> callback);
```

The values in the provided collection are inserted in the SQL statement, like this:

```
filterValues = Arrays.asList(new Integer[] {1, 4, 10});
```

becomes something like this:

```
Object[] params = new Object[filterValues.size()];
for (int i=0; i<filterValues.size(); i++) {
  params[i] = filterValues.get(i);
}
tx.executeSql("SELECT * FROM atable WHERE value IN(?,?,?)", params);
```


## Getting results ##

### Anticipating no results: VoidCallback ###
If you execute SQL and you don't want to process any results, simply use the `VoidCallback` type:

```
@Update("INSERT INTO testtable (adate) VALUES ({when.getTime()})")
void insertDate(Date when, VoidCallback callback);
```

The `VoidCallback` still declares `onFailure()` and `onSuccess()` callbacks which must be provided by the caller. This is the simplest callback type.

### Anticipating a single value result: ScalarCallback ###
Some SQL queries return just a single value in a resultSet of 1 row and 1 column. The `ScalarCallback` is designed for these cases. E.g.:

```
@Select("SELECT COUNT(*) FROM testtable")
void getCount(ScalarCallback<Integer> callback);
```

A `SELECT COUNT(*)` query is a very typical usecase for the `ScalarCallback`. The returned value is provided in the `onSuccess()` callback method:

```
dbService.getCount(new ScalarCallback<Integer>() {
  public void onFailure(DataServiceException e) {
     // Handle failure
  }
  public void onSuccess(Integer result) {
     // Do something with the result count
  }
}
```

The `result` value is obtained as the first column from the first record in the resultSet. If no data is available, you probably executed the wrong SQL.

The types you can use for the `ScalarCallback` are `Byte`, `Short`, `Integer`, `Float`, `Double`, `Boolean` and `String`.

### Anticipating multiple rows: ListCallback ###
Most `SELECT` queries return a regular, tabular kind of resultSet. The `ListCallback` is designed for this usecase:

```
@Select("SELECT * FROM testtable")
void getData(ListCallback<GenericRow> callback);
```

The callback returns a `java.util.List` with the same type parameter as the `ListCallback` (in this case, `GenericRow`):

```
dbService.getData(new ListCallback<GenericRow>() {
  public void onFailure(DataServiceException e) {
     // Handle failure
  }
  public void onSuccess(List<GenericRow> resultSet) {
     // Do something with the resultSet
  }
}
```

The type which is used to define the 'row' (e.g. `GenericRow`) must be a [JavaScriptObject Overlay](http://code.google.com/p/google-web-toolkit/wiki/OverlayTypes) type. The type must define the same attributes as defined by the actual resultSet. E.g., the query:

```
SELECT name, adate FROM testtable
```

creates a resultSet which matches with the following row type:

```
public final class TestTableRow extends JavaScriptObject {
  protected TestTableRow() {}

  public native String getName() /*-{
    return this.name;
  }-*/;

  public native java.util.Date getADate() /*-{
    return new Date(this.adate)  // date is stored in millisecs!
  }-*/;
}
```

The references in the [JSNI](http://code.google.com/webtoolkit/doc/1.6/FAQ_Client.html#JavaScript_Native_Interface) code `this.name` and `this.adate` refer to the properties of a resultSet record. By means of a JavaScript overlay type, we can access these properties.

If you think creating your own row type is too complex, you can always turn to the generic row type `GenericRow`, just like in the example above. That type is a bit like the `ResultSet` type in JDBC: you access the data with methods like `getInteger(String columnName)` and `getString(String columnName)`. For most data types there is an accessor method.

### Anticipating primary key ID's (ROWID's): RowIdListCallback ###
The `@Update` annotation enables the use of a fourth Callback type: the `RowIdListCallback`. This callback type **only** works with SQL INSERT statements.

Example:

```
@Update("INSERT INTO testtable (adate) VALUES ({when.getTime()})")
void insertDate(Date when, RowIdListCallback callback);
```

Note the use of a `List`-kind Callback, when in fact you want a single value. Maybe later we have a `RowIdScalarCallback` for this purpose, but for now, this will suffice just fine:

```
dbService.insertDate(new Date(), new RowIdCallback() {
  public void onFailure(DataServiceException e) {
     // Handle failure
  }
  public void onSuccess(List<Integer> rowIds) {
    int id = rowIds.get(0);
    // Do something with 'id'
  }
}
```

If you use the `RowIdListCallback` on a service which iterates over a collection to execute zero or more SQL INSERT statements, the returned `List<Integer>` contains primary keys for the items in the same order as the executed statements. It should be easy to 'weave' them to the objects in the source collection.

## Using a DataService inheritance tree ##
Normally you would declare a DataService with an interface extending the `DataService` interface.

However, you can also create a DataService by **extending** another DataService:

```
@Connection(name="testdb", ...)
public interface GeneralDataService extends DataService {
  // general service methods here
}
```
```
// No need to annotate with @Connection!
public interface SpecificDataService extends GeneralDataService {
  // some additional services here
}
```

This is especially useful if another GWT module already declares a DataService to the same Database you'd like to use, only with additional service definitions.

In the end, the GWT Generator will create two DataService classes, one extending the other. No service method is generated twice, and the Generator maintains the DRY principle.