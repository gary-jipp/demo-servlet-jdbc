package servlet;

import java.util.Properties;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.sql.DataSource;
import org.apache.log4j.Logger;
import common.LogHelper;
import common.WebConstants;

/**
 * This abstract class defines some convenience methods for Servlets
 * 
 * @author Gary
 */
public abstract class JDBCServlet extends HttpServlet {
  protected Logger logger;
  protected DataSource datasource;
  protected Properties properties;

  @Override
  public void init() throws ServletException {
    super.init();
    this.logger = LogHelper.getLogger(getClass());
    this.datasource = (DataSource) getServletContext().getAttribute(WebConstants.DATASOURCE);
    this.properties = (Properties) getServletContext().getAttribute(WebConstants.PROPERTIES);
  }

  /**
   * General purpose JDBC close method
   * 
   * @param obj
   */
  protected void close(AutoCloseable obj) {
    if (obj == null)
      return;
    try {
      obj.close();
    }
    catch (Exception e) {
      logger.error(e.getMessage());
    }
  }
}
