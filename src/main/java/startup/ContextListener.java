package startup;

import java.io.InputStream;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import common.LogHelper;
import common.WebConstants;

/**
 * Called on server startup. Initializes context<br>
 */
public class ContextListener implements ServletContextListener {
  private Logger logger;

  @Override
  public void contextInitialized(ServletContextEvent event) {
    ServletContext servletContext = event.getServletContext();
    
    // Set the root name for log4j logging
    LogHelper.setLogRoot(WebConstants.LOGROOT);

    this.logger = LogHelper.getLogger(getClass());
    String instance = servletContext.getRealPath("/");
    logger.info("ContextListener starting: instance=" + instance);
    // System.setProperty("instance", instance);

    // Obtain & save environment naming context
    Context context = getContext();
    servletContext.setAttribute(WebConstants.CONTEXT, context);

    // Load application properties
    Properties properties = getProperties(servletContext, instance);
    servletContext.setAttribute(WebConstants.PROPERTIES, properties);
    logger.info(properties.size() + " sql properties read, instance=" + instance);

    // Initialize DataSource - JNDI item name must be in web.xml
    String name = servletContext.getInitParameter(WebConstants.DATASOURCE);
    if (name != null) {
      DataSource ds = initDataSource(name, event, context);
      servletContext.setAttribute(WebConstants.DATASOURCE, ds);
    }
    else
      logger.info("No Datasource specified. Nothing to do");

    logger.info("ContextListener complete");
  }

  /**
   * Initialize the SQL properties for this application.<br>
   * Properties are loaded from the properties file specified in web.xml
   */
  private Properties getProperties(ServletContext servletContext, String instance) {
    Properties properties = new Properties();

    // The properties Lookup name is in web-xml
    String filename = servletContext.getInitParameter(WebConstants.PROPERTIES);

    logger.info("Loading Properties : " + filename);
    InputStream in = this.getClass().getResourceAsStream(filename);
    try {
      properties.load(in);
    }
    catch (Exception e) {
      logger.error("unable to load properties: " + e);
    }
    logger.info("SQL Properties loaded: " + properties.size());

    return properties;
  }

  /**
   * Initialize Database
   * 
   * @param event
   * @param context
   */
  private DataSource initDataSource(String name, ServletContextEvent event, Context context) {
    DataSource ds = null;
    logger.info("Initializing DataSource: " + name);

    // Fetch datasource from JNDI tree
    try {
      ds = (DataSource) context.lookup(name);
    }
    catch (Exception e) {
      logger.error("Unable to find datasource " + name + ": " + e.getMessage());
    }

    // Test DataSource to see if we can get a real connection
    try {
      Connection connection = ds.getConnection();
      logger.info(connection.getMetaData().getURL());
      connection.close();
    }
    catch (Exception e) {
      logger.error("Unable to initialize database Connection" + ": " + e.getMessage());
    }

    logger.info("DataSource initialized");
    return ds;
  }

  /**
   * Get Host name of server
   * 
   * @param logger
   * @return
   */
  public static String getHostname(Logger logger) {
    try {
      String hostname = java.net.InetAddress.getLocalHost().getCanonicalHostName();
      return hostname;
    }
    catch (UnknownHostException e) {
      logger.error(e.getMessage());
      return null;
    }
  }

  /**
   * Get naming context
   * 
   * @return
   */
  private Context getContext() {
    try {
      return (Context) new InitialContext().lookup("java:comp/env");
    }
    catch (NamingException e) {
      throw new RuntimeException("Unable to read JNDI environment: " + e.toString());
    }
  }

  @Override
  public void contextDestroyed(ServletContextEvent event) {
    logger.info("Stopping ContextListener");
    Enumeration<Driver> drivers = DriverManager.getDrivers();
    while (drivers.hasMoreElements()) {
      Driver driver = drivers.nextElement();
      try {
        DriverManager.deregisterDriver(driver);
        logger.info(String.format("deregistering jdbc driver: %s", driver));
      }
      catch (SQLException e) {
        logger.error(String.format("Error deregistering jdbc driver: %s", e) + e.getMessage());
      }
    }
    logger.info("ContextListener Stopped");
    // LogManager.shutdown();
  }
}