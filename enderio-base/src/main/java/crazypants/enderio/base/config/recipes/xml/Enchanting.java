package crazypants.enderio.base.config.recipes.xml;

import java.util.Optional;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import com.enderio.core.common.util.stackable.Things;

import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.StaxFactory;
import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import crazypants.enderio.base.recipe.enchanter.EnchanterRecipe;

public class Enchanting extends AbstractConditional {

  private Optional<ItemIntegerAmount> input = empty();
  private Optional<Enchantment> enchantment = empty();

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    try {
      super.readResolve();
      if (!input.isPresent()) {
        throw new InvalidRecipeConfigException("Missing <input>");
      }
      if (!enchantment.isPresent()) {
        throw new InvalidRecipeConfigException("Missing <enchantment>");
      }

      valid = input.get().isValid() && enchantment.get().isValid();

    } catch (InvalidRecipeConfigException e) {
      throw new InvalidRecipeConfigException(e, "in <enchanting>");
    }
    return this;
  }

  @Override
  public void enforceValidity() throws InvalidRecipeConfigException {
    input.get().enforceValidity();
    enchantment.get().enforceValidity();
    if (input.get().getThing().isEmpty()) {
      throw new InvalidRecipeConfigException("Valid child elements are invalid in <enchanting>");
    }
  }

  @Override
  public void register(@Nonnull String recipeName) {
    if (isValid() && isActive()) {
      final Things thing = input.get().getThing();
      if (!thing.isEmpty()) {
        EnchanterRecipe recipe = new EnchanterRecipe(thing, input.get().getAmount(), enchantment.get().getEnchantment(), enchantment.get().getCostMultiplier());
        MachineRecipeRegistry.instance.registerRecipe(MachineRecipeRegistry.ENCHANTER, recipe);
      }
    }
  }

  @Override
  public boolean setElement(StaxFactory factory, String name, StartElement startElement) throws InvalidRecipeConfigException, XMLStreamException {
    if ("input".equals(name) && !input.isPresent()) {
      input = of(factory.read(new ItemIntegerAmount().setAllowDelaying(false), startElement));
      return true;
    }
    if ("enchantment".equals(name) && !enchantment.isPresent()) {
      enchantment = of(factory.read(new Enchantment(), startElement));
      return true;
    }

    return super.setElement(factory, name, startElement);
  }

}