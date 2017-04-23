package com.anupam.clitool;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.List;

import com.anupam.clitool.profile.ProfileService;
import com.anupam.clitool.profile.ProfileService.ProfileServiceFactory;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.common.collect.ImmutableList;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Named;

public class GmailAssitantModule extends AbstractModule {
  @Override
  protected void configure() {
    install(new FactoryModuleBuilder().build(ProfileServiceFactory.class));
  }

  @Provides
  @Named("fileDataStorePath")
  public String fileDataStorePath() {
    return ".store/oauth2_sample";
  }

  @Provides
  @Singleton
  public FileDataStoreFactory getFileDataStoreFactory(
      @Named("fileDataStorePath") String fileDataStorePath) throws IOException {
    return new FileDataStoreFactory(new File(System.getProperty("user.home"), fileDataStorePath));
  }

  @Provides
  @Singleton
  public HttpTransport getHttpTransport() throws GeneralSecurityException, IOException {
    return GoogleNetHttpTransport.newTrustedTransport();
  }

  @Provides
  @Singleton
  public JsonFactory getJsonFactory() {
    return JacksonFactory.getDefaultInstance();
  }

  @Provides
  @Singleton
  public GoogleClientSecrets getGoogleClientSecrets(JsonFactory jsonFactory) throws IOException {
    return GoogleClientSecrets.load(
        jsonFactory,
        new InputStreamReader(ProfileService.class
            .getResourceAsStream("/META-INF/client_secrets.json")));
  }

  @Provides
  @Singleton
  @Named(Constants.OAUTH_SERVICE_SCOPES)
  public List<Scope> getOauthServiceScopes() {
    return ImmutableList.of(Scope.PROFILE_READ, Scope.PROFILE_EMAIL);
  }

  public static enum Scope {
    PROFILE_READ("https://www.googleapis.com/auth/userinfo.profile"),
    PROFILE_EMAIL("https://www.googleapis.com/auth/userinfo.profile");

    private final String scopePath;

    Scope(String path) {
      this.scopePath = path;
    }

    public String getScopePath() {
      return this.scopePath;
    }
  }

}
