package crazypants.enderio.base.config.recipes.xml;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.StaxFactory;

public class Capacitors extends Recipes {

  @Override
  public boolean setElement(StaxFactory factory, String name, StartElement startElement) throws InvalidRecipeConfigException, XMLStreamException {
    if ("capacitor".equals(name)) {
      addRecipe(new Capacitor(), factory, startElement);
      return true;
    } else {
      factory.skip(startElement);
      return true;
    }
  }

}
