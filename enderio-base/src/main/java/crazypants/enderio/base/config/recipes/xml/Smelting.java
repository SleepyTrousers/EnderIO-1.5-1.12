package crazypants.enderio.base.config.recipes.xml;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import com.enderio.core.common.util.NNList.Callback;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.StaxFactory;
import crazypants.enderio.base.integration.tic.TicProxy;
import crazypants.enderio.util.Prep;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class Smelting extends AbstractCrafting {

  private Float exp;
  private boolean tinkers = false;
  private boolean vanilla = true;

  private ItemFloatAmount input;

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    try {
      super.readResolve();
      if (exp == null) {
        if (valid) {
          exp = FurnaceRecipes.instance().getSmeltingExperience(getOutput().getItemStack());
        }
      } else {
        if (exp < 0) {
          throw new InvalidRecipeConfigException("Invalid negative value for 'exp'");
        }
        if (exp > 1) {
          throw new InvalidRecipeConfigException("Invalid value for 'exp', above 100%");
        }
      }
      if (input == null) {
        throw new InvalidRecipeConfigException("Missing <input>");
      }
      if (!vanilla && !tinkers) {
        throw new InvalidRecipeConfigException("One or more of 'vanilla' or 'tinkers' must be enabled");
      }
      if (vanilla && input.amount != 1f) {
        throw new InvalidRecipeConfigException("For 'vanilla' setting an input amount is not valid");
      }

      valid = valid && input.isValid() && (!vanilla || Prep.isValid(input.getItemStack()));

    } catch (InvalidRecipeConfigException e) {
      throw new InvalidRecipeConfigException(e, "in <smelting>");
    }
    return this;
  }

  @Override
  public void enforceValidity() throws InvalidRecipeConfigException {
    super.enforceValidity();
    input.enforceValidity();
  }

  @Override
  public void register(@Nonnull String recipeName) {
    if (isValid() && isActive()) {
      if (vanilla) {
        final ItemStack result = getOutput().getItemStack();
        input.getThing().getItemStacks().apply(new Callback<ItemStack>() {
          @SuppressWarnings("null")
          @Override
          public void apply(@Nonnull ItemStack stack) {
            if (!EnderIO.DOMAIN.equals(stack.getItem().getRegistryName().getResourceDomain())) {
              Log.debug("Adding smelting recipes for non-EnderIO items is not recommended, recipe: " + recipeName + " (" + stack + " => " + result + ")");
            }
            final ItemStack smeltingResult = FurnaceRecipes.instance().getSmeltingResult(stack);
            if (Prep.isValid(smeltingResult)) {
              if (result.getItem() != smeltingResult.getItem() || result.getCount() != smeltingResult.getCount()) {
                Log.error("Cannot add smelting recipe " + recipeName + " (" + stack + " => " + result + ") because another mod already has registered a recipe "
                    + stack + " => " + smeltingResult + ".");
              } else {
                Log.debug(
                    "Smelting recipe " + recipeName + " (" + stack + " => " + result + ") is a real duplicate and will be ignored (XP may be different).");
              }
            } else {
              GameRegistry.addSmelting(stack, result, exp);
            }
          }
        });
      }
      if (tinkers) {
        TicProxy.registerSmelterySmelting(input.getThing(), getOutput().getThing(), 1f / input.amount);
      }
    }
  }

  @Override
  public boolean setAttribute(StaxFactory factory, String name, String value) throws InvalidRecipeConfigException, XMLStreamException {
    if ("exp".equals(name)) {
      this.exp = Float.parseFloat(value);
      return true;
    }
    if ("tinkers".equals(name)) {
      this.tinkers = Boolean.parseBoolean(value);
      return true;
    }
    if ("vanilla".equals(name)) {
      this.vanilla = Boolean.parseBoolean(value);
      return true;
    }

    return super.setAttribute(factory, name, value);
  }

  @Override
  public boolean setElement(StaxFactory factory, String name, StartElement startElement) throws InvalidRecipeConfigException, XMLStreamException {
    if ("input".equals(name)) {
      if (input == null) {
        input = factory.read(new ItemFloatAmount().setAllowDelaying(false), startElement);
        return true;
      }
    }

    return super.setElement(factory, name, startElement);
  }

}
