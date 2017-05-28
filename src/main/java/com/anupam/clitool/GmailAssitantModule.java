package com.anupam.clitool;

import com.anupam.clitool.profile.ProfileService;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.common.collect.ImmutableList;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.List;

public class GmailAssitantModule extends AbstractModule {

  @Override
  protected void configure() {
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
    return GoogleClientSecrets.load(jsonFactory, new InputStreamReader(
        ProfileService.class.getResourceAsStream(Constants.CLIENT_SECRETS_PATH)));
  }

  @Provides
  @Singleton
  @Named(Constants.ALL_SERVICE_SCOPES)
  public List<Scope> getAllServiceScopes() {
    return ImmutableList.of(Scope.GMAIL_LABELS, Scope.GMAIL_META_DATA, Scope.GMAIL_READ_ONY,
        Scope.USERINFO_PROFILE_EMAIL,
        Scope.USERINFO_PROFILE_READ);
  }

  @Inject
  @Provides
  @Singleton
  public Credential getCredential(JsonFactory jsonFactory, HttpTransport httpTransport,
      GoogleClientSecrets clientSecrets,
      @Named(Constants.ALL_SERVICE_SCOPES) List<Scope> scopes) throws IOException {
    FileDataStoreFactory fileDataStoreFactory = new FileDataStoreFactory(
        new File(System.getProperty("user.home"), Constants.FILE_DATASTORE_PATH));
    ImmutableList.Builder<String> passedScopes = ImmutableList.builder();
    for (Scope scope : scopes) {
      passedScopes.add(scope.getScopePath());
    }

    GoogleAuthorizationCodeFlow flow =
        new GoogleAuthorizationCodeFlow.Builder(httpTransport, jsonFactory, clientSecrets,
            passedScopes.build()).setDataStoreFactory(fileDataStoreFactory).build();
    return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver())
        .authorize(Constants.CREDETNAIL_STORE_IDENTIFIER);
  }

  @Inject
  @Provides
  public Gmail getGmailService(JsonFactory jsonFactory, HttpTransport httpTransport,
      Credential credential) {
    return new Gmail.Builder(httpTransport, jsonFactory, credential)
        .setApplicationName(Constants.APPLICATION_NAME).build();
  }


  public enum Scope {
    USERINFO_PROFILE_READ("https://www.googleapis.com/auth/userinfo.profile"),
    USERINFO_PROFILE_EMAIL("https://www.googleapis.com/auth/userinfo.profile"),

    GMAIL_LABELS(GmailScopes.GMAIL_LABELS),
    GMAIL_META_DATA(GmailScopes.GMAIL_METADATA),
    GMAIL_READ_ONY(GmailScopes.GMAIL_READONLY);

    private final String scopePath;

    Scope(String path) {
      this.scopePath = path;
    }

    public String getScopePath() {
      return this.scopePath;
    }
  }
}
