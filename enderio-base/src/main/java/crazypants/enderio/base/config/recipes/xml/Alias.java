package crazypants.enderio.base.config.recipes.xml;

import java.util.Optional;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLStreamException;

import com.enderio.core.common.util.stackable.Things;

import crazypants.enderio.base.Log;
import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.StaxFactory;

public class Alias extends AbstractConditional {

  private Optional<String> name = empty();

  private Optional<String> item = empty();

  @Override
  public String getName() {
    return get(name);
  }

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    try {
      super.readResolve();
      if (!name.isPresent()) {
        throw new InvalidRecipeConfigException("Missing name");
      }
      if (!item.isPresent()) {
        throw new InvalidRecipeConfigException("Missing item");
      }
    } catch (InvalidRecipeConfigException e) {
      throw new InvalidRecipeConfigException(e, "in alias '" + item.orElse("(missing item)") + "'");
    }
    if (isActive()) {
      Things.addAlias(get(name), get(item));
      Log.debug("Added alias '" + name.get() + "' => '" + item.get() + "'");
    }
    return this;
  }

  @Override
  public void register(@Nonnull String recipeName) {
  }

  @Override
  public boolean setAttribute(StaxFactory factory, String name, String value) throws InvalidRecipeConfigException, XMLStreamException {
    if ("name".equals(name)) {
      this.name = ofString(value);
      return true;
    }
    if ("item".equals(name)) {
      this.item = ofString(value);
      return true;
    }
    return super.setAttribute(factory, name, value);
  }

  @Override
  public void enforceValidity() throws InvalidRecipeConfigException {
  }

  // json

  protected String getItem() {
    return get(item);
  }

}
