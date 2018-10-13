package crazypants.enderio.base.config.recipes;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

public interface RecipeConfigElement {

  /**
   * Called after the object has been populated from XML. Needs to check if it is formally correct.
   * 
   * @return The object itself
   * @throws InvalidRecipeConfigException
   * @throws XMLStreamException
   */
  Object readResolve() throws InvalidRecipeConfigException, XMLStreamException;

  /**
   * Determine if an object is semantically valid and throw a nice user-presentable error if not.
   * 
   * @throws InvalidRecipeConfigException
   */
  void enforceValidity() throws InvalidRecipeConfigException;

  /**
   * Determine if an object is semantically valid.
   * 
   * @return
   */
  boolean isValid();

  boolean setAttribute(StaxFactory factory, String name, String value) throws InvalidRecipeConfigException, XMLStreamException;

  boolean setElement(StaxFactory factory, String name, StartElement startElement) throws InvalidRecipeConfigException, XMLStreamException;

  /**
   * Sets a human-readable designation of the source of the XML, e.g. the filename or the IMC sender.
   */
  default void setSource(String source) {
  }

  default String getSource() {
    return "unknown";
  }

}