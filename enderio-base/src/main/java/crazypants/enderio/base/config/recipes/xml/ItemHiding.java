package crazypants.enderio.base.config.recipes.xml;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLStreamException;

import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.StaxFactory;
import crazypants.enderio.base.integration.jei.JeiHidingRegistry;

public class ItemHiding extends Item implements Hiding.IHidingElement {

  private boolean show = false, hide = false;

  @Override
  public boolean setAttribute(StaxFactory factory, String name, String value) throws InvalidRecipeConfigException, XMLStreamException {
    if ("show".equals(name)) {
      this.show = Boolean.parseBoolean(value);
      return true;
    }
    if ("hide".equals(name)) {
      this.hide = Boolean.parseBoolean(value);
      return true;
    }

    return super.setAttribute(factory, name, value);
  }

  @Override
  public void register(@Nonnull String recipeName) {
    JeiHidingRegistry.set(getThing(), show, hide);
  }

}
