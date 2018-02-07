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

  private ItemHelper() {
  }

  public static @Nonnull NNList<ItemStack> getValidItems() {
    final NNList<ItemStack> list = new NNList<ItemStack>();
    final NNList<ItemStack> sublist = new NNList<ItemStack>();
    for (final Item item : Item.REGISTRY) {
      item.getSubItems(CreativeTabs.SEARCH, sublist);
      sublist.apply(new Callback<ItemStack>() {
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
      });
      sublist.clear();
    }
    return list;
  }

}
