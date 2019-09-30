package crazypants.enderio.base.config.recipes.xml;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.config.recipes.IRecipeRoot;
import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.StaxFactory;

public class Aliases implements IRecipeRoot {

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    return this;
  }

  @Override
  public boolean isValid() {
    return true;
  }

  @Override
  public boolean isActive() {
    return true;
  }

  @Override
  public void register(@Nonnull String recipeName) {
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T extends IRecipeRoot> T addRecipes(IRecipeRoot other, Overrides overrides) throws InvalidRecipeConfigException {
    return (T) this;
  }

  @Override
  public boolean setAttribute(StaxFactory factory, String name, String value) throws InvalidRecipeConfigException, XMLStreamException {
    if ("enderio".equals(name)) {
      return true;
    }
    if ("xsi".equals(name)) {
      return true;
    }
    if ("schemaLocation".equals(name)) {
      return true;
    }

    return false;
  }

  @Override
  public boolean setElement(StaxFactory factory, String name, StartElement startElement) throws InvalidRecipeConfigException, XMLStreamException {
    if ("alias".equals(name)) {
      aliases.add(factory.read(new Alias(), startElement));
    } else {
      factory.skip(startElement);
    }
    return true;
  }

  @Override
  public void enforceValidity() throws InvalidRecipeConfigException {
  }

  @Override
  public List<AbstractConditional> getRecipes() {
    return Collections.emptyList();
  }

  // json

  private final NNList<Alias> aliases = new NNList<>();

  protected NNList<Alias> getAliases() {
    return aliases;
  }

}
