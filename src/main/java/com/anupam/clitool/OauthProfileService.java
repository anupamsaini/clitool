package com.anupam.clitool;

import com.anupam.clitool.GmailAssitantModule.Scopes;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Tokeninfo;
import com.google.api.services.oauth2.model.Userinfoplus;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import java.io.IOException;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** OAuth profile information service for installed application. */
public class OauthProfileService {
  private static final Logger logger = LogManager.getLogger(OauthProfileService.class.getName());

  private static final List<Scopes> OAUTH_SERVICE_SCOPES =
      ImmutableList.of(Scopes.PROFILE_READ, Scopes.PROFILE_EMAIL);

  private final HttpTransport httpTransport;
  private final JsonFactory jsonFactory;
  private final GoogleClientSecrets clientSecrets;
  private final PersistantCredentialManager persistantCredentialManager;

  @Inject
  public OauthProfileService(
      JsonFactory jsonFactory,
      HttpTransport httpTransport,
      GoogleClientSecrets googleClientSecrets,
      PersistantCredentialManager persistantCredentialManager) {
    this.httpTransport = httpTransport;
    this.jsonFactory = jsonFactory;
    this.clientSecrets = googleClientSecrets;
    this.persistantCredentialManager = persistantCredentialManager;
  }

  /**
   * Returns the {@code Tokeninfo} of the logged in user.
   *
   * @param userId the logged in user's id.
   * @return token info
   * @throws IOException
   */
  public Tokeninfo tokenInfo(String userId) throws IOException {
    Credential credential =
        this.persistantCredentialManager.getInstalledAppCredential(userId, OAUTH_SERVICE_SCOPES);

    Oauth2 oauth2 = new Oauth2.Builder(httpTransport, jsonFactory, credential).build();
    Tokeninfo tokeninfo = oauth2.tokeninfo().setAccessToken(credential.getAccessToken()).execute();

    if (!tokeninfo.getAudience().equals(clientSecrets.getDetails().getClientId())) {
      logger.error("ERROR: audience does not match our client ID!");
    }
    return tokeninfo;
  }

  /**
   * Returns the {@code Userinfoplus} of the logged in user.
   *
   * @param userId the logged in user's id.
   * @return user info
   * @throws IOException
   */
  public Userinfoplus userInfo(String userId) throws IOException {
    Credential credential =
        this.persistantCredentialManager.getInstalledAppCredential(userId, OAUTH_SERVICE_SCOPES);

    Oauth2 oauth2 = new Oauth2.Builder(httpTransport, jsonFactory, credential).build();
    Userinfoplus userinfo = oauth2.userinfo().get().execute();
    return userinfo;
  }
}
