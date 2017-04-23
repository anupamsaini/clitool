package com.anupam.clitool;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.anupam.clitool.profile.ProfileService;
import com.anupam.clitool.profile.ProfileService.ProfileServiceFactory;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class App {
  private static final Logger logger = LogManager.getLogger(App.class.getName());

  public static void main(String[] args) {
    try {
      Injector injector = Guice.createInjector(new GmailAssitantModule());

      ProfileServiceFactory profieServiceFactory =
          injector.getInstance(ProfileServiceFactory.class);
      ProfileService profileService = profieServiceFactory.create("user");
      logger.info(profileService.tokenInfo().toPrettyString());
      logger.info(profileService.userInfo().toPrettyString());
      return;
    } catch (IOException e) {
      System.err.println(e.getMessage());
    } catch (Throwable t) {
      t.printStackTrace();
    }
    System.exit(1);
  }
}
