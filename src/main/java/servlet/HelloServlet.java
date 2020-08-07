package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Displays the status of the common properties.
 */
@WebServlet("/hello")
public class HelloServlet extends JDBCServlet {
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    doGet(request, response);
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    response.setHeader("Cache-Control", "no-cache");
    response.setContentType("text/plain");
    PrintWriter out = response.getWriter();

    String parameter = request.getParameter("id");

    ResultSet rs = null;
    Connection connection = null;
    PreparedStatement statement = null;

    // Use one of our SQL queries - all in one properties file so easy to manage
    String sql = properties.getProperty("sql.test1");
    try {
      connection = datasource.getConnection();
      statement = connection.prepareStatement(sql);

      int count = statement.getParameterMetaData().getParameterCount();
      if (count > 0)
        statement.setString(1, parameter);
      logger.debug(statement.toString());

      // Execute Query
      rs = statement.executeQuery();

      // Read MetaData
      ResultSetMetaData metaData = rs.getMetaData();
      count = metaData.getColumnCount();

      // Read Data from Query
      while (rs.next()) {

        // Read columns by MetaData (we almost never do this)
        for (int i = 1; i <= count; i++) {
          String name = metaData.getColumnName(i);
          String type = metaData.getColumnTypeName(i);
          String value = rs.getString(i);
          out.println(name + "(" + type + ")" + ": " + value);
        }

        // Read columns Explicitly (this is the normal way to do it)
        String name = rs.getString("name");
        String value = rs.getString("value");
        String created = rs.getString("created");
        out.println("name:" + name + ", value:" + value + ", created:" + created + "\n");
      }
    }
    catch (Exception e) {
      logger.error(e);
      out.println(e.getMessage());
    }
    finally {
      close(rs);
      close(statement);
      close(connection);
    }
  }

}