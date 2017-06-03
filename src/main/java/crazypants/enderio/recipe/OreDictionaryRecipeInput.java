package crazypants.enderio.recipe;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NullHelper;

import crazypants.util.Prep;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class OreDictionaryRecipeInput extends RecipeInput {

  private static final @Nonnull Map<String, Set<StackWrapper>> oreCache = new HashMap<String, Set<StackWrapper>>();

  private static @Nonnull Set<StackWrapper> getCached(@Nonnull String oreDict) {
    Set<StackWrapper> set = oreCache.get(oreDict);
    if (set == null) {
      set = new HashSet<StackWrapper>();
      List<ItemStack> ores = OreDictionary.getOres(oreDict);
      for (ItemStack ore : ores) {
        if (Prep.isValid(NullHelper.notnullF(ore, "OreDictionary has null stacks"))) {
          set.add(new StackWrapper(NullHelper.notnullF(ore, "OreDictionary has null stacks")));
        }
      }
      synchronized (oreCache) {
        oreCache.put(oreDict, set);
      }
    }
    return set;
  }

  static class StackWrapper {
    private final @Nonnull ItemStack stack;

    StackWrapper(@Nonnull ItemStack stack) {
      this.stack = stack;
    }

    public ItemStack getStackCopy(int size) {
      final ItemStack copy = stack.copy();
      copy.setCount(size);
      return copy;
    }

    @Override
    public int hashCode() {
      return stack.getItem().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      StackWrapper other = (StackWrapper) obj;
      if (stack.getItem() != other.stack.getItem())
        return false;
      else if (!stack.getHasSubtypes())
        return true;
      else if (stack.getMetadata() == OreDictionary.WILDCARD_VALUE)
        return true;
      else if (other.stack.getMetadata() == OreDictionary.WILDCARD_VALUE)
        return true;
      else
        return stack.getMetadata() == other.stack.getMetadata();
    }

  }

  private final @Nonnull String oreDict;

  public OreDictionaryRecipeInput(@Nonnull ItemStack stack, @Nonnull String oreDict, float multiplier, int slot) {
    super(stack, true, multiplier, slot);
    this.oreDict = oreDict;
  }

  public OreDictionaryRecipeInput(@Nonnull OreDictionaryRecipeInput copy) {
    super(copy.getInput(), true, copy.getMulitplier(), copy.getSlotNumber());
    oreDict = copy.oreDict;
  }

  @Override
  public @Nonnull RecipeInput copy() {
    return new OreDictionaryRecipeInput(this);
  }

  @Override
  public boolean isInput(@Nonnull ItemStack test) {
    if (Prep.isInvalid(test)) {
      return false;
    }
    return getCached(oreDict).contains(new StackWrapper(test));
  }

  @Override
  public ItemStack[] getEquivelentInputs() {
    Set<StackWrapper> cached = getCached(oreDict);
    if (cached.isEmpty()) {
      return null;
    }
    ItemStack[] result = new ItemStack[cached.size()];
    int i = 0;
    for (StackWrapper stackWrapper : cached) {
      result[i++] = stackWrapper.getStackCopy(getInput().getCount());
    }
    return result;
  }

  @Override
  public String toString() {
    return "OreDictionaryRecipeInput [oreDict=" + oreDict + " amount=" + getInput().getCount() + "]";
  }

}
