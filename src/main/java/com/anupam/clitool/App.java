package com.anupam.clitool;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.anupam.clitool.gmail.GmailService;
import com.anupam.clitool.gmail.GmailService.GmailServiceFactory;
import com.anupam.clitool.profile.ProfileService;
import com.anupam.clitool.profile.ProfileService.ProfileServiceFactory;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class App {
  private static final Logger logger = LogManager.getLogger(App.class.getName());

  public static void main(String[] args) throws Exception {
    Injector injector = getInjector();

    ProfileService profileService = getProfileService("user-profile", injector);
    logger.info(profileService.tokenInfo().toPrettyString());
    logger.info(profileService.userInfo().toPrettyString());

   GmailService gmailService = getGmailService("user-gmail", injector);
   logger.info(gmailService.getMetaData());
  }

  private static ProfileService getProfileService(String user, Injector injector) {
    ProfileServiceFactory profieServiceFactory = injector.getInstance(ProfileServiceFactory.class);
    ProfileService profileService = profieServiceFactory.create(user);
    return profileService;
  }

  private static GmailService getGmailService(String user, Injector injector) {
    GmailServiceFactory gmailServiceFactory = injector.getInstance(GmailServiceFactory.class);
    GmailService gmailService = gmailServiceFactory.create(user);
    return gmailService;
  }

  private static Injector getInjector() {
    return Guice.createInjector(new GmailAssitantModule());
  }
}
