package crazypants.enderio.base.config.recipes.xml;

import java.util.Optional;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import com.enderio.core.common.util.EntityUtil;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.Callback;

import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.StaxFactory;
import crazypants.enderio.base.filter.FilterRegistry;
import crazypants.enderio.base.filter.IFilter;
import crazypants.enderio.base.filter.item.SoulFilter;
import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import crazypants.enderio.base.recipe.soul.BasicSoulBinderRecipe;
import crazypants.enderio.base.recipe.soul.DynamicSoulBinderRecipe;
import crazypants.enderio.base.recipe.spawner.EntityDataRegistry;
import crazypants.enderio.util.CapturedMob;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class Soulbinding extends AbstractCrafting {

  private enum Logic implements BasicSoulBinderRecipe.OutputFilter {
    NONE,
    APPLY {
      @Override
      @Nonnull
      public ItemStack apply(ItemStack output, CapturedMob mobType) {
        return mobType.toStack(output.getItem(), output.getItemDamage(), output.getCount());
      }
    },
    FILTER {
      @Override
      @Nonnull
      public ItemStack apply(ItemStack output, CapturedMob mobType) {
        final IFilter filter = FilterRegistry.getFilterForUpgrade(output);
        if (filter instanceof SoulFilter) {
          ((SoulFilter) filter).getSouls().add(mobType);
          FilterRegistry.writeFilterToStack(filter, output);
        }
        return output;
      }
    };
  }

  private enum SoulHandling {
    ALL,
    LISTED,
    SPAWNABLE;
  }

  private int energy, levels;
  private final NNList<Soul> souls = new NNList<>();
  private Optional<Item> input = empty();
  private Logic logic = Logic.NONE;
  private SoulHandling soulHandling = SoulHandling.LISTED;

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    try {
      super.readResolve();
      if (!input.isPresent()) {
        throw new InvalidRecipeConfigException("No <input>");
      }
      if (souls.isEmpty() && soulHandling == SoulHandling.LISTED) {
        throw new InvalidRecipeConfigException("No <soul>");
      }
      if (!souls.isEmpty() && soulHandling != SoulHandling.LISTED) {
        throw new InvalidRecipeConfigException("Cannot give <soul> when using ALL or SPAWNABLE");
      }
      if (energy <= 0) {
        throw new InvalidRecipeConfigException("Invalid low value for 'energy'");
      }
      if (levels <= 0) {
        throw new InvalidRecipeConfigException("Invalid negative or zero value for 'levels'");
      }

      boolean hasValidSoul = soulHandling != SoulHandling.LISTED;
      for (Soul soul : souls) {
        hasValidSoul = hasValidSoul || soul.isValid();
      }

      valid = valid && input.get().isValid() && hasValidSoul;

    } catch (InvalidRecipeConfigException e) {
      throw new InvalidRecipeConfigException(e, "in <soulbinding>");
    }
    return this;
  }

  @Override
  public void enforceValidity() throws InvalidRecipeConfigException {
    super.enforceValidity();
    input.get().enforceValidity();
    for (Soul soul : souls) {
      if (soul.isValid()) {
        soul.enforceValidity();
      }
    }
  }

  @Override
  public void register(@Nonnull String recipeName) {
    if (isValid() && isActive()) {

      NNList<ResourceLocation> soulnames = new NNList<>();
      Predicate<ResourceLocation> entityFilter = null;

      switch (soulHandling) {
      case ALL:
        soulnames.addAll(EntityUtil.getAllRegisteredMobNames());
        break;
      case SPAWNABLE:
        entityFilter = name -> name != null && !EntityDataRegistry.getInstance().isBlackListedForSpawning(name);
        break;
      default:
      case LISTED:
        for (Soul soul : souls) {
          if (soul.isValid()) {
            soulnames.add(soul.getMob().getEntityName());
          }
        }
        break;
      }

      final Predicate<ResourceLocation> entityFilter2 = entityFilter;

      input.get().getThing().getItemStacks().apply(new Callback<ItemStack>() {
        int i = 0;

        @Override
        public void apply(@Nonnull ItemStack anInput) {
          if (entityFilter2 != null) {
            MachineRecipeRegistry.instance.registerRecipe(MachineRecipeRegistry.SOULBINDER, //
                new DynamicSoulBinderRecipe(anInput, getOutput().getItemStack(), energy, levels, recipeName + i++, entityFilter2, logic));
          } else {
            MachineRecipeRegistry.instance.registerRecipe(MachineRecipeRegistry.SOULBINDER, //
                new BasicSoulBinderRecipe(anInput, getOutput().getItemStack(), energy, levels, recipeName + i++, soulnames, logic));
          }
        }
      });
    }
  }

  @Override
  public boolean setAttribute(StaxFactory factory, String name, String value) throws InvalidRecipeConfigException, XMLStreamException {
    if ("energy".equals(name)) {
      this.energy = Integer.parseInt(value);
      return true;
    }
    if ("levels".equals(name)) {
      this.levels = Integer.parseInt(value);
      return true;
    }
    if ("logic".equals(name)) {
      try {
        this.logic = Logic.valueOf(value);
      } catch (IllegalArgumentException e) {
        throw new InvalidRecipeConfigException("Invalid value for 'logic'");
      }
      return true;
    }
    if ("souls".equals(name)) {
      try {
        this.soulHandling = SoulHandling.valueOf(value);
      } catch (IllegalArgumentException e) {
        throw new InvalidRecipeConfigException("Invalid value for 'souls'");
      }
      return true;
    }

    return super.setAttribute(factory, name, value);
  }

  @Override
  public boolean setElement(StaxFactory factory, String name, StartElement startElement) throws InvalidRecipeConfigException, XMLStreamException {
    if ("input".equals(name)) {
      input = of(factory.read(new Item().setAllowDelaying(false), startElement));
      return true;
    }
    if ("soul".equals(name)) {
      souls.add(factory.read(new Soul(), startElement));
      return true;
    }

    return super.setElement(factory, name, startElement);
  }

}
