# iter-jdbc
Iterator-focused JDBC wrapper library

This project was inspired by Spring-Jdbc. In particular, named parameter SQL
 queries and stream-like result processing, without storing the whole
  result set in memory. But unlike Spring-Jdbc, the goal of this project is
   to provide and accept iterators wherever possible instead of callbacks and
    collections.
    
## Add

Using the following Maven coordinates:

```
<dependency>
  <groupId>com.codeborne</groupId>
  <artifactId>iter-jdbc</artifactId>
  <version>0.2</version>
</dependency>
```
    
## Use

Create a repository:

```
public class BooksRepo {
  public BooksRepo(DataSource ds) {
    this.ds = ds;
  }

  private final DataSource ds;

  private static final Query<Book> allBooksQuery = new Query<>(
    "select b.TITLE from BOOKS",
    BooksRepo::mapRowToBook
  );

  private static Book mapRowToBook(ResultSet rs) throws SQLException {
    return new Book(rs.getString("TITLE"));
  }

  public CloseableIterator<Book> allBooks() {
    try (Connection conn = ds.getConnection()) {
      return allBooksQuery.connect(conn)
        .runOnce(emptyMap());
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
```

Create a service:

```
public class BooksReportService {
  public BooksReportService(BooksRepo repo) {
    this.repo = repo;
  }

  private final BooksRepo repo;

  public void writeReport(Writer w) {
    try (CloseableIterator<Book> books = repo.allBooks()) {
      int bookNum = 0;
      while (books.hasNext()) {
        bookNum++;
        Book book = books.next();
        w.write(bookNum + ".\t" + book.getTitle() + "\n");
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
```

## Test

Test repositories with integration tests and a real database.

Test service classes using `CloseableListIterator`. Test that code under test
closes resources (prepared queries and iterators):

```
class BooksReportServiceTest {
  BooksRepo repo = mock(BooksRepo.class);
  BooksReportService reportService = new BooksReportService(repo);

  @Test
  void writeReport() {
    CloseableListIterator<Book> books = spy(new CloseableListIterator<>(
      new Book("A book"),
      new Book("Another book"),
      new Book("One more book")
    ));
    when(repo.allBooks()).thenReturn(books);
    StringWriter w = new StringWriter();

    reportService.writeReport(w);

    assertThat(w.toString()).isEqualTo(
      "1.\tA book\n" +
      "2.\tAnother book\n" +
      "3.\tOne more book\n"
    );
    verify(books).close();
  }
}
```
