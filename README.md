# demo-servlet-jdbc

This is a simple Java Servlet that accesses a database using a JDBC Datasource (connection pool) defined by the container.
This project is an excellent starting point for any Servlet that needs to use JDBC


- You will need a mySQL instance running somewhere with a "test" database.  The DataSource is defined in the Tomcat Plugin project settings and can be edited there. This demo app simply retrieves records from a table called "test".


- The DataSource name is jdbc/demo and is configured on the server and accessed by the app by that name. The DataSource pool is loaded on startup and saved in the servlet context where all servlets can access it.


- This demo servlet subclasses the abstract class JDBCServlet which just contains come useful helper functions such as close() and also provides uniform access to properties, the DataSource and logging. It doesn't really do anything else.