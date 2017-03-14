package de.gedoplan.custom.jaas;

import java.security.Principal;

/**
 * Einfache Implementierung von java.security.Principal.
 */
public class SimplePrincipal implements Principal {

  private String name;

  /**
   * SimplePrincipal erzeugen.
   * 
   * @param name Name
   */
  public SimplePrincipal(String name)
  {
    if (name == null) {
      throw new NullPointerException("principal name must not be null");
    }

    this.name = name;
  }

  /**
   * Namen des SimplePrincipal liefern.
   * 
   * @return Name
   */
  public String getName()
  {
    return this.name;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode()
  {
    return name.hashCode();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object obj)
  {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    SimplePrincipal other = (SimplePrincipal) obj;
    return name.equals(other.name);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString()
  {
    return this.getClass().getSimpleName() + "{" + this.name + "}";
  }
}