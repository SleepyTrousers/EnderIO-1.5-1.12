package crazypants.enderio.config.recipes;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

public interface RecipeConfigElement {

  Object readResolve() throws InvalidRecipeConfigException, XMLStreamException;

  boolean isValid();

  boolean setAttribute(StaxFactory factory, String name, String value) throws InvalidRecipeConfigException, XMLStreamException;

  boolean setElement(StaxFactory factory, String name, StartElement startElement) throws InvalidRecipeConfigException, XMLStreamException;
}