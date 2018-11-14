package crazypants.enderio.base.config.recipes.xml;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import com.enderio.core.common.util.stackable.Things;

import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.StaxFactory;
import net.minecraft.potion.PotionHelper;
import net.minecraft.potion.PotionType;

public class Brewing extends AbstractConditional {

  private Item input;
  private Potion in, out;

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    try {
      super.readResolve();
      if (input == null) {
        throw new InvalidRecipeConfigException("Missing <reagent>");
      }
      if (in == null) {
        throw new InvalidRecipeConfigException("Missing <input>");
      }
      if (out == null) {
        throw new InvalidRecipeConfigException("Missing <output>");
      }

      valid = input.isValid() && in.isValid() && out.isValid();

    } catch (InvalidRecipeConfigException e) {
      throw new InvalidRecipeConfigException(e, "in <brewing>");
    }
    return this;
  }

  @Override
  public void enforceValidity() throws InvalidRecipeConfigException {
    input.enforceValidity();
    in.enforceValidity();
    out.enforceValidity();
    if (input.getThing().isEmpty()) {
      throw new InvalidRecipeConfigException("Valid child elements are invalid in <brewing>");
    }
  }

  @Override
  public void register(@Nonnull String recipeName) {
    if (isValid() && isActive()) {
      final Things thing = input.getThing();
      PotionType inPotion = in.getPotion();
      PotionType outPotion = out.getPotion();
      if (!thing.isEmpty() && inPotion != null && outPotion != null) {
        PotionHelper.addMix(inPotion, thing.asIngredient(), outPotion);
      }
    }
  }

  @Override
  public boolean setElement(StaxFactory factory, String name, StartElement startElement) throws InvalidRecipeConfigException, XMLStreamException {
    if ("reagent".equals(name)) {
      if (input == null) {
        input = factory.read(new Item().setAllowDelaying(false), startElement);
        return true;
      }
    }
    if ("input".equals(name)) {
      if (in == null) {
        in = factory.read(new Potion(), startElement);
        return true;
      }
    }
    if ("output".equals(name)) {
      if (out == null) {
        out = factory.read(new Potion(), startElement);
        return true;
      }
    }

    return super.setElement(factory, name, startElement);
  }

}
