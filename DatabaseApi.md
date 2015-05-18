# Introduction #

The database API ([download](http://code.google.com/p/gwt-mobile-webkit/downloads/list?q=label:API-Database)) leverages the [W3C Web Database API](http://www.w3.org/TR/webdatabase/).

Using this API you are able to store data in a [SQLite](http://sqlite.org/) database which is embedded in the browser. This is great for offline features of your application.

Please also take a look at the [DataService API](DataServiceUserGuide.md): that API simplifies client-side database programming somewhat, and is built on top of the database API described here.

# Browser support #
Not every browser supports this feature, and currently there are no fallback scenario's to rely on Flash, Java or Gears plugins. We built support for the following browsers:

  1. iPhone Safari (OS2.0 and up - maybe earlier versions are supported, but are untested)
  1. Google Android (2.0 and up)
  1. Desktop Safari (3.1 and up)
  1. Google Chrome (4.0 and up)
  1. Opera (10.50 and up)

NOTE: The Database API as specified by W3C describes two API's: an Asynchronous and a Synchronous API. Currently we only built support for the Asynchronous API, the Synchronous API isn't supported by any browser yet.

# Usage #
## Get the Database API using Maven ##
[Add the repository](MavenUserGuide.md) and the following to your `pom.xml` file:

```
<dependencies>
  ...
  <dependency>
    <groupId>com.google.code.gwt-mobile-webkit</groupId>
    <artifactId>gwt-html5-database</artifactId>
  </dependency>
</dependencies>
```

## Add the Database API to your project ##
If you don't use Maven, copy the `gwt-html5-database-x.x.x.jar` file to your project classpath. This jar contains everything you need to use the Database API.

Next, in your GWT module `gwt.xml` file (which uses the Database API), add the following entry:
```
<inherits name="com.google.code.gwt.database.Html5Database" />
```
This imports the Database API into your module. Now you are ready to use the API!

## is API supported? ##
The following client code will test whether the API is supported in your browser:
```
if (Database.isSupported()) {
    // Interact with your database...
}
```

## Opening a Database ##
This line opens a database and returns a handle to that database:
```
Database db = Database.openDatabase("ClckCnt", "1.0", "Click Counter", 10000);
```

## Performing a Transaction ##
This code creates a new table if doesn't exist already:
```
db.transaction(new TransactionCallback() {
    public void onTransactionStart(SQLTransaction tx) {
        tx.executeSql("CREATE TABLE IF NOT EXISTS clickcount ("
                + "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
                + "clicked INTEGER)", null);
    }
    public void onTransactionFailure(SQLError error) {
        // handle error...
    }
    public void onTransactionSuccess() {
        // Proceed when successfully committed...
    }
});
```

As you can see, the Database API depends heavily on callback calls. In this case, you open a database transaction by calling the `transaction()` method on the Database instance, which must be provided with a callback instance to actually do something with the transaction.

There are basically two methods to perform transactions (each has some overloaded alternatives):
  1. `transaction()` to perform a read and/or write transaction;
  1. `readTransaction()` to perform just a read transaction.
The iPhone Safari database API does not implement the `readTransaction()` method. In this GWT library you can, because under the 'hood' [we just call the `transaction()` method](http://is.gd/1DpT2) instead.

The transaction callback needs to implement three methods:
  1. `onTransactionStart()` which executes the actual database transaction;
  1. `onTransactionFailure()` which is executed when the transaction is rolled back;
  1. `onTransacionSuccess()` which is executed after the transaction is successfully committed.

Within the `onTransactionStart()` method you have the `tx` parameter to execute your SQL on.

The `tx` instance provides two methods to execute SQL:
  1. `executeSql(sqlStatement, arguments)`
  1. `executeSql(sqlStatement, arguments, callback)`

Both methods execute the same SQL, but the second gives you a callback to handle resultsets and failures. The provided SQL must comply to the [SQLite language](http://sqlite.org/lang.html). Up to now all browser-embedded databases are of the [SQLite](http://sqlite.org/) type.

## Executing SQL ##
In order to execute SQL we need to do something like the following:
```
db.transaction(new TransactionCallback() {
    public void onTransactionStart(SQLTransaction tx) {
        tx.executeSql("INSERT INTO clickcount (clicked) VALUES (?)", new Object[] {new Date().getTime()});
        tx.executeSql("SELECT clicked FROM clickcount", null, new StatementCallback<ClickRow>() {
            public boolean onFailure(SQLTransaction transaction, SQLError error) {
                return false;  // don't roll back
            }
            public void onSuccess(SQLTransaction transaction, SQLResultSet<ClickRow> resultSet) {
                clickedData.clear();
                for (ClickRow row : resultSet.getRows()) {
                    clickedData.add(new Label("Clicked on " + row.getClicked()));
                }
            }
        });
    }
    public void onTransactionFailure(SQLError error) {
        // ...
    }
    public void onTransactionSuccess() {
        // ...
    }
});
```

The first call to `executeSql()` inserts data using a parameter list, similar to the way JDBC accepts parameters in a Statement. The parameters must be provided in an Object array, which has the same number of elements as parameters declared in the SQL statement using the `?` symbol.

The second call to `executeSql()` uses the additional callback parameter to handle the resultset. The callback mandates two methods to implement:
  1. `onfailure()` is invoked if the SQL statement somehow fails. If you want to rollback the transaction, return `true` here, otherwise just return `false`. The `error` parameter provides you the failure details;
  1. `onSuccess()` is invoked if the SQL was executed successfully. The `resultSet` parameter provides you the results of the query.

Both methods get the `transaction` parameter which allows you to execute additional SQL statements depending on the outcome of the executed SQL.

## Getting the ResultSet ##
Notice in the code snippet above that the second `executeSql()` method adds a callback instance with a Type parameter:
```
tx.executeSql("SELECT clicked FROM clickcount", null, new StatementCallback<ClickRow>() { ... });
```
In this case, the specified Type is `[http://is.gd/1DqDR ClickRow]`. ClickRow is a simple Java class representing a record in the ResultSet. Each property name in the bean represents a column in the ResultSet. The bean only needs getter methods in order to get the record values.
Instead of a specific Java bean representing the ResultSet records, you could also use the `[http://is.gd/1DqL9 GenericRow]` class which provides a generic way of getting the values from your ResultSet records.

# Beware #
## One Instance ##
During testing this API it became apparent that the instance returned by by `openDatabase()` must be retained throughout the application. If you open the database multiple times during the same session, it seemed that its behavior becomes unpredictable. (perhaps I need to solve this problem in the API, but for now, be careful!)

## Performance ##
The database (especially on a mobile device) is _slow_. Please test carefully using a **real** device rather than an emulator to experience potential slow response times in your webapp!

## WebKit Database transaction bug ##
Quote from http://blog.nextstop.com/2009/12/3-tips-for-developing-mobile-html5-apps.html:
"Stay away from the HTML5/SQLite db in iPhone OS 3.0. There is a nasty bug that locks the database if the user refreshes the web page or hits the back button while the db is in the middle of a transaction. Subsequent attempts to access the database fail completely (they either return an error or crash the browser). This is especially bad because there seems to be no way to recover from this without deleting the entire database in Settings > Safari > Databases. This is fixed in the latest Webkit builds, but hasn't been pushed to Mobile Safari yet (though I'm sure an update is coming soon). Here's the bug if you're interested: https://bugs.webkit.org/show_bug.cgi?id=25711"