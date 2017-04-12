package com.anupam.clitool;

import com.anupam.clitool.GmailAssitantModule.Scopes;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.io.IOException;
import java.util.List;

@Singleton
public class PersistantCredentialManager {

  private final FileDataStoreFactory dataStoreFactory;
  private final HttpTransport httpTransport;
  private final JsonFactory jsonFactory;
  private final GoogleClientSecrets clientSecrets;

  @Inject
  public PersistantCredentialManager(
      JsonFactory jsonFactory,
      HttpTransport httpTransport,
      FileDataStoreFactory fileDataStoreFactory,
      GoogleClientSecrets googleClientSecrets) {
    this.dataStoreFactory = fileDataStoreFactory;
    this.httpTransport = httpTransport;
    this.jsonFactory = jsonFactory;
    this.clientSecrets = googleClientSecrets;
  }

  public Credential getInstalledAppCredential(String credentialId, List<Scopes> scopes) throws IOException {
    Preconditions.checkArgument(!Strings.isNullOrEmpty(credentialId), "credentialId");
    Preconditions.checkArgument((scopes != null && !scopes.isEmpty()), "scopes");

    ImmutableList.Builder<String> passedScopes = ImmutableList.builder();
    for (Scopes scope : scopes) {
      passedScopes.add(scope.getScopePath());
    }

    GoogleAuthorizationCodeFlow flow =
        new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, jsonFactory, clientSecrets, passedScopes.build())
            .setDataStoreFactory(dataStoreFactory)
            .build();
    return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver())
        .authorize(credentialId);
  }
}
