# Introduction #

Interacting with the local Database is complex: it involves several levels of callbacks, and the programmer needs to deal with at least five callback methods if he wants to execute a single SQL statement.

This document explores an additional, DataService API which should simplify the code without making it 'too simple'.

# Idea #

It would be great if we could approach a local database much like we would approach an RPC service, by means of an interface description. A DataService would look like this:

```
@Connection(name = "ClckCnt", version = "1.0", description = "Click Counter", maxsize = 10000)
public interface ClickCountDataService extends DataService {

  /**
   * Makes sure that the 'clickcount' table exists in the Database.
   * Uses the Void callback to ignore any resultSet.
   */
  @Update("CREATE TABLE IF NOT EXISTS clickcount ("
            + "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
            + "clicked INTEGER)")
  void initTable(VoidCallback callback);
  
  /**
   * Records a Click value.
   * Uses the RowId callback to get the primary key of the inserted record.
   */
  @Update("INSERT INTO clickcount (clicked) VALUES ({when.getTime()})")
  void insertClick(Date when, RowIdListCallback callback);
  
  /**
   * Records a set of Click values.
   * Uses the RowId callback to get the primary keys of inserted records.
   */
  @Update(sql="INSERT INTO clickcount (clicked) VALUES ({_.getTime()})", foreach="clicks")
  void insertClicks(Collection<Date> clicks, RowIdListCallback callback);
  
  /**
   * Obtains the list of all recorded clicks.
   * Uses the List callback to handle a 'full' resultSet.
   */
  @Select("SELECT clicked FROM clickcount")
  void getAllClicks(ListCallback<ClickRow> callback);

  /**
   * Obtains the number of clicks recorded in the database.
   * Uses the Scalar callback to get a single value as result.
   */
  @Select("SELECT count(*) FROM clickcount")
  void getClickCount(ScalarCallback<Integer> callback);
}
```

It would be used like this:

```
// Initialize the DataService instance:
ClickCountDataService service = GWT.create(ClickCountDataService.class);

// Make sure the clickcount table is in the database:
service.initTable(new VoidCallback() {
  public void onFailure(DataServiceException error) {
    // No success, stop the program!
  }

  public void onSuccess() {
    // The clickcount table is certainly in the database.
    // Obtain all records:
    service.getAllClicks(new ListCallback<ClickRow>() {
      public void onFailure(DataServiceException error) {
        // Something went wrong!
      }

      public void onSuccess(List<ClickRow> result) {
        // Done! Now do something with the clickcount records:
        for (ClickRow row : result) {
          // ...
        }
      }
    });
  }
});
```

# Improvements over regular API #

The DataService API makes several improvements over the regular Database API:
  1. Separation of Concerns: The API enforces separation of the service definition itself (the DataService interface) and the client code calling the services;
  1. Simplicity: The amount of callbacks to implement a transaction are reduced from 5 to 2, while making sure all default callback behaviour is sensible. This ensures no confusion between two different `onFailure()` and `onSuccess()` methods;
  1. Extensibility: If, for instance, Microsoft decides to implement the Database API in IE9, while using a completely different Database engine than SQLite, the service method might be annotated with additional compatibility annotations. GWT's Deferred Binding compiler would automatically pick the right query;


# Design issues #

Although the design looks clean and should be able to do the job, there are a few caveats:

## 1. Handling exceptions and failures ##
The service expects the Database API to just 'be there'. How should we cope with the fact no database is available - throw an Exception at each service method, checked or unchecked? Go straight to the `onFailure()` callback?

## 2. Moving resultSet data **out** of the transaction ##
The callback methods should be called after the transaction is finished. In the example above, this probably means (needs investigation) that the resultSet of the `insertClick()` call must be **copied** before the transaction ends, and then provided to the `onSuccess()` method - or, a third callback method should be provided.

## 3. Hidden transaction context ##
The transaction context is totally hidden from the programmer. Each service method creates its own transaction. This is done to remove a callback level, and greatly simplifies Database programming. However, this also means that multiple service calls cannot efficiently use a single transaction context.

## 4. How to handle SQL failures - optional rollback? ##
The regular StatementCallback enables the user to rollback a transaction when something went wrong. Should the user somehow be able to specify how to handle failures, or should we just always mandate a rollback?

## 5. Transaction mode: readonly and read/write ##
The Database API offers two transaction modes: One is read/write, the other is readonly. Should the DataService API always, for simplicity sake, use the read/write transaction mode, or should we introspect the SQL statements to decide what mode to use?

## 6. Collection input parameters ##
How do we cope with Collections of data as **input**? For instance, adding any number of records to a table using a collection, or providing values for a SQL `IN()` expression from a Collection.

The usecase is to efficiently provide collection data to a SQL statement, without resorting to invoking additional transactions.

There are two kinds of collections: One kind mandates a SQL statement for each item in the collection (e.g. `INSERT` statements), and the other kind needs to repeat parts of the SQL statement _in situ_ (e.g. `IN()` expressions).

We can express 'iterations' in two ways. One way is to define some additional grammar within the SQL statement string:

```
@SQL("{repeat(clicks, ';') {INSERT INTO clickcount (clicked) VALUES ({#.getTime()})}")
void insertData(Collection<Date> clicks, VoidCallback callback);
```

The other is to add additional annotation grammar to get the same result:

```
@SQL(
  stmt="INSERT INTO clickcount (clicked) VALUES ({#.getTime()}"
  foreach="clicks"
)
void insertData(Collection<Date> clicks, VoidCallback callback);
```

For repeating SQL statements, the annotation version seems clearer and more expressive. It is more true to the Java language we're programming in.

However, for repeating expressions inside SQL I am not so sure. This is the String grammar version:

```
@SQL("SELECT id FROM clickcount WHERE clicked IN({repeat(clicks, ',') {#.getTime()}})")
void getClickIds(Collection<Date> clicks, ListCallback<GenericRow> callback);
```

This is how the heavily annotated version would look like:

```
@Concat({
  @SQL(stmt="SELECT id FROM clickcount WHERE clicked IN("),
  @SQL(stmt="{#.getTime()}", foreach="clicks", join=","),
  @SQL(stmt=")")
})
void getClickIds(Collection<Date> clicks, ListCallback<GenericRow> callback);
```

Ouch!

In summary, it seems that the annotated version is perfectly suited for `INSERT`-like statements which need to be repeated for each item in the Collection, and the String grammar version seems best suited for `IN()`-like expression repetitions.


# Experimental version #

The code repository is updated with an experimental version of the DataService API, and a new download is also available (version 1.5). Use as follows:
  1. [Download](http://code.google.com/p/gwt-mobile-webkit/downloads/list?q=label:API-Database) a 1.5 version of the Database API library
  1. Pick up the jar file `gwt-html5-database.jar`, and/or check out sample at `samples/HelloDatabase`.

The current version implements the design issues as follows:
  1. If calling `openDatabase()` fails, the callback's `onFailure()` method is invoked, rather than an Exception is thrown;
  1. If the Callback expects a List resultset, the resultset is retained after the transaction (rather than copied to an ArrayList or something). This seems to work just fine on Safari;
  1. The Transaction context is completely hidden from the service user;
  1. Whenever a SQL failure happens, the statements are always rolled back.
  1. The transactions are all in read/write mode, except if all SQL statements start with `SELECT` - in taht case, the transaction is executed in read-only mode.
  1. Input collections are dealt with in two different ways. 1st: The `@Update` annotation has a `foreach` attribute. This attribute can contain an expression resolving to a Java `Iterable` type or an Array. The specified SQL statement is executed for each item in the collection, and the value of the item is assigned to the identifier `_`. 2nd: If a collection value is used in a parameter to the SQL statement (e.g. `... WHERE x IN({listOfNrs})`), the query is expanded to all values of the provided collection at runtime.