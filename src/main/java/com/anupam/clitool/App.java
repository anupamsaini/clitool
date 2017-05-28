package com.anupam.clitool;

import com.anupam.clitool.gmail.GmailService;
import com.anupam.clitool.profile.ProfileService;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class App {

  private static final Logger logger = LogManager.getLogger(App.class.getName());

  public static void main(String[] args) throws Exception {
    Injector injector = getInjector();

    ProfileService profileService = injector.getInstance(ProfileService.class);
    logger.info(profileService.tokenInfo().toPrettyString());
    logger.info(profileService.userInfo().toPrettyString());

    GmailService gmailService = injector.getInstance(GmailService.class);
    logger.info(gmailService.getMetaData());
    ListMessagesResponse response = gmailService.listEmailMessages();
    logger.info(gmailService.readMessage(response.getMessages().get(0).getId()));
  }

  private static Injector getInjector() {
    return Guice.createInjector(new GmailAssitantModule());
  }
}
