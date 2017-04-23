package com.anupam.clitool.gmail;

import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.anupam.clitool.Constants;
import com.anupam.clitool.GmailAssitantModule.Scope;
import com.anupam.clitool.PersistantCredentialManager;
import com.anupam.clitool.profile.ProfileService;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListLabelsResponse;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;

/**
 * Service to perform operations on user's account.
 */
public class GmailService {
  private final HttpTransport httpTransport;
  private final JsonFactory jsonFactory;
  private final Credential credential;

  public interface GmailServiceFactory {
    GmailService create(String userId);
  }

  @Inject
  public GmailService(JsonFactory jsonFactory, HttpTransport httpTransport,
      GoogleClientSecrets googleClientSecrets,
      PersistantCredentialManager persistantCredentialManager,
      @Named(Constants.GMAIL_SERVICE_SCOPES) List<Scope> oauthServiceScopes,
      @Assisted String userId) throws IOException {
    this.httpTransport = httpTransport;
    this.jsonFactory = jsonFactory;
    this.credential =
        persistantCredentialManager.getInstalledAppCredential(userId, oauthServiceScopes);
  }

  /**
   * Returns the {@code ListLabelsResponse} of the logged in user.
   *
   * @return user's gmail labels
   * @throws IOException
   */
  public ListLabelsResponse getMetaData() throws IOException {
    Gmail gmailService = new Gmail.Builder(httpTransport, jsonFactory, credential)
        .setApplicationName(Constants.APPLICATION_NAME).build();
    ListLabelsResponse listResponse = gmailService.users().labels().list("me").execute();
    return listResponse;
  }
}
