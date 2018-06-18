package crazypants.enderio.base.integration.jei;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.Callback;

import crazypants.enderio.base.Log;
import crazypants.enderio.util.Prep;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemHelper {

  protected static final class Walker implements Callback<ItemStack> {
    private final @Nonnull NNList<ItemStack> list;
    private Item item;

    protected void set(Item item) {
      this.item = item;
    }

    protected Walker(@Nonnull NNList<ItemStack> list) {
      this.list = list;
    }

    @Override
    public void apply(@Nonnull ItemStack stack) {
      if (Prep.isInvalid(stack)) {
        Log.error("The item " + item + " (" + item.getUnlocalizedName() + ") produces empty itemstacks in getSubItems()");
      } else if (stack.getItem() == Items.AIR) {
        Log.error("The item " + item + " (" + item.getUnlocalizedName() + ") produces itemstacks without item in getSubItems()");
      } else {
        list.add(stack);
      }
    }
  }

  private ItemHelper() {
  }

  public static @Nonnull NNList<ItemStack> getValidItems() {
    final NNList<ItemStack> list = new NNList<ItemStack>();
    final NNList<ItemStack> sublist = new NNList<ItemStack>();
    final Walker callback = new Walker(list);
    for (final Item item : Item.REGISTRY) {
      callback.set(item);
      item.getSubItems(CreativeTabs.SEARCH, sublist);
      sublist.apply(callback);
      sublist.clear();
    }
    return list;
  }

  /*
  public static @Nonnull NNList<ItemStack> getValidItems() {
    final NNList<ItemStack> list = new NNList<ItemStack>();
    final NNList<ItemStack> sublist = new NNList<ItemStack>();
    for (final Item item : Item.REGISTRY) {
      item.getSubItems(CreativeTabs.SEARCH, sublist);
      list.addAll(sublist.stream().filter(new Predicate<ItemStack>() {
        @Override
        public boolean test(ItemStack t) {
          return t != null && Prep.isValid(t) && t.getItem() != Items.AIR;
        }
      }).toArray(ItemStack[]::new));
      sublist.clear();
    }
    return list;
  }
   */
  
}
