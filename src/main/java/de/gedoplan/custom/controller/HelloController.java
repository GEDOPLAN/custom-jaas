package de.gedoplan.custom.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Dominik Mathmann
 */
@Named
@Dependent
@Stateless
public class HelloController {

  @Inject
  private HttpServletRequest request;

  @RolesAllowed({"super-user"})
  public String getHello()
  {
    return "Hello Super User";
  }

  @PermitAll
  public String getRoles()
  {
    List<String> roles = new ArrayList<>();
    roles.add("user");
    roles.add("super-user");

    return "User: "
        + this.request.getUserPrincipal().getName()
        + " with roles: "
        + roles.stream().map(r -> r + " : " + this.request.isUserInRole(r)).collect(Collectors.joining(", "));
  }
}
