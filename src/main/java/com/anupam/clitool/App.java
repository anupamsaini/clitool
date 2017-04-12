package com.anupam.clitool;

import com.google.inject.Guice;
import com.google.inject.Injector;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class App {
  private static final Logger logger = LogManager.getLogger(OauthProfileService.class.getName());

  public static void main(String[] args) {
    try {
      Injector injector = Guice.createInjector(new GmailAssitantModule());

      OauthProfileService tokenGenerator = injector.getInstance(OauthProfileService.class);
      logger.info(tokenGenerator.tokenInfo("user").toPrettyString());
      logger.info(tokenGenerator.userInfo("user").toPrettyString());
      return;
    } catch (IOException e) {
      System.err.println(e.getMessage());
    } catch (Throwable t) {
      t.printStackTrace();
    }
    System.exit(1);
  }
}
