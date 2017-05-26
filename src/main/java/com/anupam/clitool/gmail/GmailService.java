package com.anupam.clitool.gmail;

import com.anupam.clitool.Constants;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListLabelsResponse;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.inject.Inject;
import java.io.IOException;

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
      Credential credential) throws IOException {
    this.httpTransport = httpTransport;
    this.jsonFactory = jsonFactory;
    this.credential = credential;
  }

  /**
   * Returns the {@code ListLabelsResponse} of the logged in user.
   *
   * @return user's gmail labels
   */
  public ListLabelsResponse getMetaData() throws IOException {
    Gmail gmailService = new Gmail.Builder(httpTransport, jsonFactory, credential)
        .setApplicationName(Constants.APPLICATION_NAME).build();
    ListLabelsResponse listResponse = gmailService.users().labels().list("me").execute();
    return listResponse;
  }

  public ListMessagesResponse listEmailMessages() throws IOException {
    Gmail gmailService = new Gmail.Builder(httpTransport, jsonFactory, credential)
        .setApplicationName(Constants.APPLICATION_NAME).build();
    ListMessagesResponse listResponse = gmailService.users().messages().list("me").execute();
    return listResponse;
  }

  public Message readMessage(String messageId) throws IOException {
    Gmail gmailService = new Gmail.Builder(httpTransport, jsonFactory, credential)
        .setApplicationName(Constants.APPLICATION_NAME).build();
    Message listResponse = gmailService.users().messages().get("me", messageId).execute();
    return listResponse;
  }
}
