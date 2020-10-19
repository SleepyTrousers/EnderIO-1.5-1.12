package crazypants.enderio.base.config.recipes.xml;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.NNIterator;

import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.StaxFactory;
import crazypants.enderio.base.recipe.IRecipeInput;
import crazypants.enderio.base.recipe.RecipeLevel;
import crazypants.enderio.base.recipe.ThingsRecipeInput;
import crazypants.enderio.base.recipe.alloysmelter.AlloyRecipeManager;
import net.minecraft.item.ItemStack;

public class Alloying extends AbstractCrafting {

  private float exp = 0f;
  private int energy;
  private final NNList<ItemIntegerAmount> input = new NNList<>();
  private boolean needsDeduping = false;

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    try {
      super.readResolve();
      if (input.isEmpty()) {
        throw new InvalidRecipeConfigException("Missing <input>");
      }
      if (input.size() > 3) {
        throw new InvalidRecipeConfigException("Too many <input>s");
      }
      if (exp < 0) {
        throw new InvalidRecipeConfigException("Invalid negative value for 'exp'");
      }
      if (exp > 1) {
        throw new InvalidRecipeConfigException("Invalid value for 'exp', above 100%");
      }
      if (energy <= 0) {
        throw new InvalidRecipeConfigException("Invalid low value for 'energy'");
      }

      for (NNIterator<ItemIntegerAmount> itr = input.fastIterator(); valid && itr.hasNext();) {
        valid = valid && itr.next().isValid();
      }

      // make sure duplicate inputs can be resolved to different items
      if (valid && input.size() >= 2) {
        final NNList<ItemStack> stacks0 = input.get(0).getThing().getItemStacks();
        final NNList<ItemStack> stacks1 = input.get(1).getThing().getItemStacks();
        if (input.get(0).isSame(input.get(1))) {
          needsDeduping = true;
          if (stacks0.size() == 1) {
            valid = false;
          }
        }
        if (input.size() == 3) {
          if (input.get(0).isSame(input.get(2))) {
            needsDeduping = true;
            if (stacks0.size() == 1) {
              valid = false;
            }
            if (input.get(1).isSame(input.get(2))) {
              if (stacks1.size() <= 2) {
                valid = false;
              }
            }
          } else if (input.get(1).isSame(input.get(2))) {
            needsDeduping = true;
            if (stacks1.size() == 1) {
              valid = false;
            }
          }
        }
      }

    } catch (InvalidRecipeConfigException e) {
      throw new InvalidRecipeConfigException(e, "in <alloying>");
    }
    return this;
  }

  @Override
  public void enforceValidity() throws InvalidRecipeConfigException {
    super.enforceValidity();
    for (NNIterator<ItemIntegerAmount> itr = input.fastIterator(); itr.hasNext();) {
      itr.next().enforceValidity();
    }
  }

  @Override
  public void register(@Nonnull String recipeName, @Nonnull RecipeLevel recipeLevel) {
    if (isValid() && isActive()) {
      NNList<IRecipeInput> inputStacks = new NNList<>();
      for (NNIterator<ItemIntegerAmount> itr = input.fastIterator(); itr.hasNext();) {
        final ItemIntegerAmount item = itr.next();
        inputStacks.add(new ThingsRecipeInput(item.getThing()).setCount(item.getAmount()));
      }
      AlloyRecipeManager.getInstance().addRecipe(needsDeduping, inputStacks, getOutput().getItemStack(), energy, exp, recipeLevel);
    }
  }

  @Override
  public boolean setAttribute(StaxFactory factory, String name, String value) throws InvalidRecipeConfigException, XMLStreamException {
    if ("exp".equals(name)) {
      this.exp = Float.parseFloat(value);
      return true;
    }
    if ("energy".equals(name)) {
      this.energy = Integer.parseInt(value);
      return true;
    }

    return super.setAttribute(factory, name, value);
  }

  @Override
  public boolean setElement(StaxFactory factory, String name, StartElement startElement) throws InvalidRecipeConfigException, XMLStreamException {
    if ("input".equals(name)) {
      input.add(factory.read(new ItemIntegerAmount().setAllowDelaying(false), startElement));
      return true;
    }

    return super.setElement(factory, name, startElement);
  }

}
