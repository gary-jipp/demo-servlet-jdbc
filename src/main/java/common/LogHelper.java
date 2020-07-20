package common;

import org.apache.log4j.Logger;

public class LogHelper {
  private static String logRoot;

  /**
   * This must be done first before retriving a logger
   */
  public static void setLogRoot(String text) {
    LogHelper.logRoot = text;
  }

  public static Logger getLogger(Class clazz) {
    return Logger.getLogger(logRoot + '.' + clazz.getName());
  }
}
