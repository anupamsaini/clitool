package com.anupam.clitool;

import com.google.inject.Guice;
import com.google.inject.Injector;
import java.io.IOException;

public class App {

  public static void main(String[] args) {
    try {
      Injector injector = Guice.createInjector(new GmailAssitantModule());

      OauthProfileService tokenGenerator = injector.getInstance(OauthProfileService.class);
      tokenGenerator.tokenInfo("user");
      tokenGenerator.userInfo("user");
      return;
    } catch (IOException e) {
      System.err.println(e.getMessage());
    } catch (Throwable t) {
      t.printStackTrace();
    }
    System.exit(1);
  }
}
