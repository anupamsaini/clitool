package com.anupam.clitool.profile;

import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.anupam.clitool.Constants;
import com.anupam.clitool.GmailAssitantModule.Scope;
import com.anupam.clitool.PersistantCredentialManager;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Tokeninfo;
import com.google.api.services.oauth2.model.Userinfoplus;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;

/** OAuth profile information service for installed application. */
public class ProfileService {
  private static final Logger logger = LogManager.getLogger(ProfileService.class.getName());

  private final HttpTransport httpTransport;
  private final JsonFactory jsonFactory;
  private final GoogleClientSecrets clientSecrets;
  private final Credential credential;

  public interface ProfileServiceFactory {
    ProfileService create(String userId);
  }

  @Inject
  public ProfileService(JsonFactory jsonFactory, HttpTransport httpTransport,
      GoogleClientSecrets googleClientSecrets,
      PersistantCredentialManager persistantCredentialManager,
      @Named(Constants.OAUTH_SERVICE_SCOPES) List<Scope> oauthServiceScopes,
      @Assisted String userId) throws IOException {
    this.httpTransport = httpTransport;
    this.jsonFactory = jsonFactory;
    this.clientSecrets = googleClientSecrets;
    this.credential =
        persistantCredentialManager.getInstalledAppCredential(userId, oauthServiceScopes);
  }

  /**
   * Returns the {@code Tokeninfo} of the logged in user.
   *
   * @return token info
   * @throws IOException
   */
  public Tokeninfo tokenInfo() throws IOException {
    Oauth2 oauth2 = new Oauth2.Builder(httpTransport, jsonFactory, credential)
        .setApplicationName(Constants.APPLICATION_NAME).build();
    Tokeninfo tokeninfo = oauth2.tokeninfo().setAccessToken(credential.getAccessToken()).execute();

    if (!tokeninfo.getAudience().equals(clientSecrets.getDetails().getClientId())) {
      logger.error("ERROR: audience does not match our client ID!");
    }
    return tokeninfo;
  }

  /**
   * Returns the {@code Userinfoplus} of the logged in user.
   *
   * @return user info
   * @throws IOException
   */
  public Userinfoplus userInfo() throws IOException {
    Oauth2 oauth2 = new Oauth2.Builder(httpTransport, jsonFactory, credential)
        .setApplicationName(Constants.APPLICATION_NAME).build();
    Userinfoplus userinfo = oauth2.userinfo().get().execute();
    return userinfo;
  }
}
