package crazypants.enderio.base.integration.jei;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;
import com.enderio.core.common.util.stackable.Things;

import crazypants.enderio.base.fluid.Fluids;
import crazypants.enderio.util.Prep;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

public class JeiHidingRegistry {

  private interface IConfiguredIngredient<V> {

    @Nonnull
    NNList<? extends Object> get();

    /**
     * "hide" forcefully hides this item. At least one "hide" configuration is enough to do so. This is intended for pack makers and users.
     * <p>
     * "show" determines if the item is shown unless it's hidden. At least one "show" configuration is enough to not hide it unless it is hidden. This is
     * intended for the mod(s).
     */
    void set(@Nonnull V v, boolean show, boolean hide);

  }

  private static class ConfiguredItemstack implements IConfiguredIngredient<ItemStack> {

    private final @Nonnull Item item;
    private final @Nonnull BitSet isset = new BitSet();
    private final @Nonnull BitSet shown = new BitSet();
    private final @Nonnull BitSet hidden = new BitSet();

    ConfiguredItemstack(@Nonnull ItemStack v) {
      this.item = v.getItem();
    }

    @Override
    public @Nonnull NNList<ItemStack> get() {
      NNList<ItemStack> list = new NNList<>();
      if (isset.get(OreDictionary.WILDCARD_VALUE)) {
        if (!shown.get(OreDictionary.WILDCARD_VALUE) || hidden.get(OreDictionary.WILDCARD_VALUE)) {
          addStack(list, new ItemStack(item, 1, OreDictionary.WILDCARD_VALUE));
        }
      } else {
        for (int i = isset.nextSetBit(0); i >= 0; i = isset.nextSetBit(i + 1)) {
          if (i >= OreDictionary.WILDCARD_VALUE) {
            break;
          }
          if (!shown.get(i) || hidden.get(i)) {
            addStack(list, new ItemStack(item, 1, i));
          }
        }
      }
      return list;
    }

    // This is a bit of a hack. It works around the detail that JEI may still display some item variant, e.g. an item filled with a fluid.
    // So in addition to a "clean" item, we also add all items promoted for the creative menu that match it.
    // In theory, the getSubItems() items alone should be enough, but better be sure...
    private void addStack(@Nonnull NNList<ItemStack> list, @Nonnull ItemStack stack) {
      list.add(stack);
      NNList<ItemStack> temp = new NNList<>();
      for (CreativeTabs tab : item.getCreativeTabs()) {
        if (tab != null) {
          item.getSubItems(tab, temp);
        }
      }
      for (ItemStack tabStack : temp) {
        if (stack.getItemDamage() == OreDictionary.WILDCARD_VALUE || stack.getItemDamage() == tabStack.getItemDamage()) {
          list.add(tabStack);
        }
      }
    }

    @Override
    public int hashCode() {
      return item.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      ConfiguredItemstack other = (ConfiguredItemstack) obj;
      if (item != other.item) {
        return false;
      }
      return true;
    }

    @Override
    public void set(@Nonnull ItemStack v, boolean show, boolean hide) {
      if (v.getItem() != item) {
        throw new UnsupportedOperationException("Cannot configure item '" + v.getItem() + "' using configuration of item '" + item + "'");
      }
      if (show) {
        shown.set(v.getItemDamage());
      }
      if (hide) {
        hidden.set(v.getItemDamage());
      }
      isset.set(v.getItemDamage());
    }

  }

  private static class ConfiguredFluidstack implements IConfiguredIngredient<FluidStack> {

    private final @Nonnull Fluid fluid;
    private boolean shown = false, hidden = false;

    ConfiguredFluidstack(Fluid fluid) {
      this.fluid = NullHelper.notnull(fluid, "Encountered Forge fluid stack without fluid");
    }

    @Override
    @Nonnull
    public NNList<Object> get() {
      return !shown || hidden ? makeResult() : NNList.emptyList();
    }

    private @Nonnull NNList<Object> makeResult() {
      NNList<Object> result = new NNList<>();
      result.add(new FluidStack(fluid, 1));
      // also hide the bucket for that fluid. Won't hide other stuff like tanks...
      ItemStack bucket = Fluids.getBucket(fluid);
      if (Prep.isValid(bucket)) {
        result.add(bucket);
      }
      return result;
    }

    @Override
    public int hashCode() {
      return fluid.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      ConfiguredFluidstack other = (ConfiguredFluidstack) obj;
      if (fluid != other.fluid) {
        return false;
      }
      return true;
    }

    @Override
    public void set(@Nonnull FluidStack v, boolean show, boolean hide) {
      if (v.getFluid() != fluid) {
        throw new UnsupportedOperationException("Cannot configure fluid '" + v.getFluid() + "' using configuration of fluid '" + fluid + "'");
      }
      shown |= show;
      hidden |= hide;
    }

  }

  // not a Set because you cannot get() a Set's keys
  private static final @Nonnull Map<IConfiguredIngredient<?>, IConfiguredIngredient<?>> DATA = new HashMap<>();

  public static @Nonnull NNList<Object> getObjectsToHide() {
    NNList<Object> result = new NNList<>();
    for (IConfiguredIngredient<?> ci : DATA.keySet()) {
      result.addAll(ci.get());
    }
    return result;
  }

  public static void set(@Nonnull Things thing, boolean show, boolean hide) {
    for (ItemStack stack : thing.getItemStacksRaw()) {
      if (stack != null) {
        set(stack, show, hide);
      }
    }
  }

  public static void set(@Nonnull ItemStack stack, boolean show, boolean hide) {
    if (Prep.isValid(stack)) {
      ConfiguredItemstack cis = new ConfiguredItemstack(stack);
      if (DATA.containsKey(cis)) {
        cis = (ConfiguredItemstack) DATA.get(cis);
      } else {
        DATA.put(cis, cis);
      }
      cis.set(stack, show, hide);
    }
  }

  public static void set(@Nonnull Fluid fluid, boolean show, boolean hide) {
    ConfiguredFluidstack cis = new ConfiguredFluidstack(fluid);
    if (DATA.containsKey(cis)) {
      cis = (ConfiguredFluidstack) DATA.get(cis);
    } else {
      DATA.put(cis, cis);
    }
    cis.set(new FluidStack(fluid, 1), show, hide);
  }

  public static void set(@Nonnull FluidStack stack, boolean show, boolean hide) {
    ConfiguredFluidstack cis = new ConfiguredFluidstack(stack.getFluid());
    if (DATA.containsKey(cis)) {
      cis = (ConfiguredFluidstack) DATA.get(cis);
    } else {
      DATA.put(cis, cis);
    }
    cis.set(stack, show, hide);
  }

}
