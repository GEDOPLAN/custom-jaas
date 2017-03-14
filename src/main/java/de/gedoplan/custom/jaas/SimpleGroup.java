package de.gedoplan.custom.jaas;

import java.security.Principal;
import java.security.acl.Group;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;

/**
 * Einfache Implementierung von java.security.acl.Group.
 */
public class SimpleGroup extends SimplePrincipal implements Group {

  private Collection<Principal> members = new HashSet<Principal>();

  /**
   * SimpleGroup erzeugen.
   * 
   * @param name Name
   */
  public SimpleGroup(String name)
  {
    super(name);
  }

  /**
   * Mitglied zur Gruppe hinzufuegen.
   * 
   * @param member Neues Mitglied
   * @return true, falls Mitglied echt hinzugefügt wurde.
   */
  public boolean addMember(Principal member)
  {
    return this.members.add(member);
  }

  /**
   * Mitglied aus der Gruppe entfernen.
   * 
   * @param member Mitglied
   * @return true, falls Mitglied entfern wurde; false, falls es nicht in der Gruppe war
   */
  public boolean removeMember(Principal member)
  {
    return this.members.remove(member);
  }

  /**
   * Pruefen auf Mitgliedschaft.
   * 
   * @param member Potenzielles Mitglied
   * @return true, falls Mitglied
   */
  public boolean isMember(Principal member)
  {
    return this.members.contains(member);
  }

  /**
   * Alle Mitglieder der Gruppe liefern.
   * 
   * @return Mitglieder
   */
  public Enumeration<? extends Principal> members()
  {
    return Collections.enumeration(this.members);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString()
  {
    return this.getClass().getSimpleName() + "{" + getName() + "," + this.members + "}";
  }
}