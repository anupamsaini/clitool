package com.anupam.clitool.gmail;

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

  private final Gmail gmailService;

  @Inject
  public GmailService(Gmail gmaiService) throws IOException {
    this.gmailService = gmaiService;
  }

  /**
   * Returns the {@code ListLabelsResponse} of the logged in user.
   *
   * @return user's gmail labels
   */
  public ListLabelsResponse getMetaData() throws IOException {
    return gmailService.users().labels().list("me").execute();
  }

  public ListMessagesResponse listEmailMessages() throws IOException {
    return gmailService.users().messages().list("me").execute();
  }

  public Message readMessage(String messageId) throws IOException {
    return gmailService.users().messages().get("me", messageId).execute();
  }
}
