package crazypants.enderio.base.config.recipes.xml;

import java.util.List;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.StaxFactory;
import crazypants.enderio.base.recipe.spawner.PoweredSpawnerRecipeRegistry;

public class Spawning extends AbstractConditional {

  private final List<Entity> entities = new NNList<>();

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    try {
      super.readResolve();
      if (entities.isEmpty()) {
        throw new InvalidRecipeConfigException("Missing <entity>");
      }

      valid = true;
      for (Entity entity : entities) {
        valid = valid && entity.isValid();
      }

    } catch (InvalidRecipeConfigException e) {
      throw new InvalidRecipeConfigException(e, "in <spawning>");
    }
    return this;
  }

  @Override
  public void enforceValidity() throws InvalidRecipeConfigException {
    for (Entity entity : entities) {
      entity.enforceValidity();
    }
  }

  @Override
  public void register(@Nonnull String recipeName) {
    if (isValid() && isActive()) {
      for (Entity entity : entities) {
        if (entity.isDefault()) {
          PoweredSpawnerRecipeRegistry.getInstance().setDefaultCostMultiplier(entity.getCostMultiplier());
          PoweredSpawnerRecipeRegistry.getInstance().setAllowUnconfiguredMobs(!entity.isDisabled());
        } else if (entity.isDisabled()) {
          PoweredSpawnerRecipeRegistry.getInstance().addToBlacklist(entity.getMob().getEntityName());
        } else {
          PoweredSpawnerRecipeRegistry.getInstance().addEntityCost(entity.getMob().getEntityName(), entity.getCostMultiplier());
        }
      }
    }
  }

  @Override
  public boolean setElement(StaxFactory factory, String name, StartElement startElement) throws InvalidRecipeConfigException, XMLStreamException {
    if ("entity".equals(name)) {
      entities.add(factory.read(new Entity(), startElement));
      return true;
    }

    return super.setElement(factory, name, startElement);
  }

}