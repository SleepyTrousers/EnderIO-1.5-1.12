package crazypants.enderio.base.config.recipes.xml;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.NNIterator;

import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.StaxFactory;
import crazypants.enderio.base.recipe.IRecipeInput;
import crazypants.enderio.base.recipe.ThingsRecipeInput;
import crazypants.enderio.base.recipe.alloysmelter.AlloyRecipeManager;

public class Alloying extends AbstractCrafting {

  private Float exp;
  private int energy;
  private final @Nonnull NNList<IntItem> input = new NNList<>();

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
      if (exp == null) {
        exp = 0f;
      } else {
        if (exp < 0) {
          throw new InvalidRecipeConfigException("Invalid negative value for 'exp'");
        }
        if (exp > 1) {
          throw new InvalidRecipeConfigException("Invalid value for 'exp', above 100%");
        }
      }
      if (energy < 0) {
        throw new InvalidRecipeConfigException("Invalid negative value for 'energy'");
      }

      for (NNIterator<IntItem> itr = input.fastIterator(); valid && itr.hasNext();) {
        valid = valid && itr.next().isValid();
      }

    } catch (InvalidRecipeConfigException e) {
      throw new InvalidRecipeConfigException(e, "in <alloying>");
    }
    return this;
  }

  @Override
  public void enforceValidity() throws InvalidRecipeConfigException {
    super.enforceValidity();
    for (NNIterator<IntItem> itr = input.fastIterator(); itr.hasNext();) {
      itr.next().enforceValidity();
    }
  }

  @Override
  public void register() {
    if (isValid() && isActive()) {
      NNList<IRecipeInput> inputStacks = new NNList<>();
      for (NNIterator<IntItem> itr = input.fastIterator(); itr.hasNext();) {
        final IntItem item = itr.next();
        inputStacks.add(new ThingsRecipeInput(item.getThing()).setCount(item.getAmount()));
      }
      AlloyRecipeManager.getInstance().addRecipe(inputStacks, getOutput().getItemStack(), energy, exp);
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
      input.add(factory.read(new IntItem(), startElement));
      return true;
    }

    return super.setElement(factory, name, startElement);
  }

}