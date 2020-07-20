package common;

import java.util.Properties;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class LogHelper {
  private static String logRoot;

  /**
   * This must be done first before retriving a logger
   */
  public static void setLogRoot(String text) {
    LogHelper.logRoot = text;
  }

  public static Logger getLogger(Class clazz) {
    return getLogger(clazz, null);
  }

  public static Logger getLogger(Class clazz, Properties properties) {
    // logRoot must match servletContext or this will not work
    Logger logger = Logger.getLogger(logRoot + '.' + clazz.getName());
    if (properties != null) {
      // Override any previous configuration
      properties.setProperty("log4j.reset", "true");
      PropertyConfigurator.configure(properties);
    }
    return logger;
  }
}
