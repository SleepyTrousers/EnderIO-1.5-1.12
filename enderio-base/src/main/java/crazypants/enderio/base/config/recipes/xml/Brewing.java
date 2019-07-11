package crazypants.enderio.base.config.recipes.xml;

import java.util.Optional;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import com.enderio.core.common.util.stackable.Things;

import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.StaxFactory;
import net.minecraft.potion.PotionHelper;
import net.minecraft.potion.PotionType;

public class Brewing extends AbstractConditional {

  private Optional<Item> input = empty();
  private Optional<Potion> in = empty(), out = empty();

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    try {
      super.readResolve();
      if (!input.isPresent()) {
        throw new InvalidRecipeConfigException("Missing <reagent>");
      }
      if (!in.isPresent()) {
        throw new InvalidRecipeConfigException("Missing <input>");
      }
      if (!out.isPresent()) {
        throw new InvalidRecipeConfigException("Missing <output>");
      }

      valid = input.get().isValid() && in.get().isValid() && out.get().isValid();

    } catch (InvalidRecipeConfigException e) {
      throw new InvalidRecipeConfigException(e, "in <brewing>");
    }
    return this;
  }

  @Override
  public void enforceValidity() throws InvalidRecipeConfigException {
    input.get().enforceValidity();
    in.get().enforceValidity();
    out.get().enforceValidity();
    if (input.get().getThing().isEmpty()) {
      throw new InvalidRecipeConfigException("Valid child elements are invalid in <brewing>");
    }
  }

  @Override
  public void register(@Nonnull String recipeName) {
    if (isValid() && isActive()) {
      final Things thing = input.get().getThing();
      PotionType inPotion = in.get().getPotion();
      PotionType outPotion = out.get().getPotion();
      if (!thing.isEmpty()) {
        PotionHelper.addMix(inPotion, thing.asIngredient(), outPotion);
      }
    }
  }

  @Override
  public boolean setElement(StaxFactory factory, String name, StartElement startElement) throws InvalidRecipeConfigException, XMLStreamException {
    if ("reagent".equals(name) && !input.isPresent()) {
      input = of(factory.read(new Item().setAllowDelaying(false), startElement));
      return true;
    }
    if ("input".equals(name) && !in.isPresent()) {
      in = of(factory.read(new Potion(), startElement));
      return true;
    }
    if ("output".equals(name) && !out.isPresent()) {
      out = of(factory.read(new Potion(), startElement));
      return true;
    }

    return super.setElement(factory, name, startElement);
  }

}
