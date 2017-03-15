package de.gedoplan.custom.jaas;

import com.sun.security.auth.UserPrincipal;
import java.io.IOException;
import java.security.Principal;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.RequestScoped;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

/**
 * JAAS Login Module
 * 
 * @author Dominik Mathmann
 */
public class CustomJAASLoginModule implements LoginModule {

  protected Subject subject;

  protected Principal identity;

  protected boolean loggedIn;

  private CallbackHandler callbackHandler;

  private Map sharedState;

  private Map options;

  /**
   * Initialisierungs-Methode, übernimmt für die weiteren Aufrufe die Parameter in Instanzvariablen.
   * 
   * @param subject Aufrufer
   * @param callbackHandler Handler um Benutzereingaben zu erhalten
   * @param sharedState zusätzliche Informationen von anderen Auth-Providern
   * @param options Optionen aus SecurityDomain Konfiguration
   */
  public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState,
      Map<String, ?> options)
  {
    this.subject = subject;
    this.callbackHandler = callbackHandler;
    this.sharedState = sharedState;
    this.options = options;
  }

  /**
   * Login.
   * 
   * Ruft Benutzer und Passwor vom Bentzer ab und führt dann ein Dummy-Login über einen Webservice durch.
   * 
   * @return true/false Login-Status
   * @throws LoginException
   */
  public boolean login() throws LoginException
  {
    NameCallback nameCallback = new NameCallback("Benutzer");
    PasswordCallback passwordCallback = new PasswordCallback("Password", false);

    try {
      callbackHandler.handle(new Callback[]{nameCallback, passwordCallback});
      String username = nameCallback.getName();
      String password = new String(passwordCallback.getPassword());

      if (logInWS(username, password)) {
        loggedIn = true;
        identity = new UserPrincipal(username);
        subject.getPrincipals().add(identity);
        subject.getPublicCredentials().add(identity);
        return true;
      }
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    catch (UnsupportedCallbackException e) {
      e.printStackTrace();
    }

    return false;
  }

  /**
   * Methode wird durchlaufen wenn beim "login" kein Fehler aufgetreten ist.
   * 
   * @return
   * @throws LoginException
   */
  public boolean commit() throws LoginException
  {
    if (loggedIn) {
      Group group = new SimpleGroup("Roles");
      SimplePrincipal role = new SimplePrincipal("super-user");
      group.addMember(role);
      subject.getPrincipals().add(group);
      return true;
    }
    return false;
  }

  /**
   * Login nicht erfolgreich
   * 
   * @return
   * @throws LoginException
   */
  public boolean abort() throws LoginException
  {
    subject = null;
    return true;
  }

  /**
   * logout.
   * 
   * @return
   * @throws LoginException
   */
  public boolean logout() throws LoginException
  {
    if (subject != null && identity != null) {
      subject.getPrincipals().remove(identity);
      return true;
    }
    return false;
  }

  /**
   * Dummy Login über einen Webservice.
   * 
   * Login erfolgt über Userame und seine Mail Adresse auf Basis von jsonplaceholder-API
   * 
   * @param username Benutzername,
   * @param password Passwort
   * @return true wenn Benutzername und Passwort übereinstimmen.
   */
  private boolean logInWS(String username, String password)
  {
    List<String> allStickers = new ArrayList<>();
    Client client = ClientBuilder.newClient();
    WebTarget target = client.target("http://jsonplaceholder.typicode.com/users");
    JsonArray resp = target.request(MediaType.APPLICATION_JSON).get(JsonArray.class);

    return resp.stream().anyMatch(r -> {
      JsonObject jo = (JsonObject) r;
      return jo.getString("username").equals(username) && jo.getString("email").equals(password);
    });
  }

}
