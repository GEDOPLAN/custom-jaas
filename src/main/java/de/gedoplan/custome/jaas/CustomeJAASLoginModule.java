package de.gedoplan.custome.jaas;

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
import org.jboss.security.SimpleGroup;
import org.jboss.security.SimplePrincipal;

@RequestScoped
public class CustomeJAASLoginModule implements LoginModule {

  protected Subject subject;

  protected Principal identity;

  protected boolean loggedIn;

  private CallbackHandler callbackHandler;

  private Map sharedState;

  private Map options;

  public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState,
      Map<String, ?> options)
  {
    this.subject = subject;
    this.callbackHandler = callbackHandler;
    this.sharedState = sharedState;
    this.options = options;
  }

  public boolean login() throws LoginException
  {
    NameCallback nameCallback = new NameCallback("username:");
    PasswordCallback passwordCallback = new PasswordCallback("password:", false);

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

  public boolean abort() throws LoginException
  {
    subject = null;
    return true;
  }

  public boolean logout() throws LoginException
  {
    if (subject != null && identity != null) {
      subject.getPrincipals().remove(identity);
      return true;
    }
    return false;
  }

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
