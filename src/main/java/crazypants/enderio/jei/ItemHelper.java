package crazypants.enderio.jei;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import crazypants.enderio.Log;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameData;

public class ItemHelper {

  private ItemHelper() {
  }

  public static @Nonnull List<ItemStack> getValidItems() {
    List<ItemStack> list = new ArrayList<ItemStack>();
    List<ItemStack> sublist = new ArrayList<ItemStack>();
    for (Item item : GameData.getItemRegistry()) {
      for (CreativeTabs tab : item.getCreativeTabs()) {
        item.getSubItems(item, tab, sublist);
        for (ItemStack stack : sublist) {
          if (stack == null) {
            Log.error("The item " + item + " (" + item.getUnlocalizedName() + ") produces null itemstacks in getSubItems()");
          } else if (stack.getItem() == null) {
            Log.error("The item " + item + " (" + item.getUnlocalizedName() + ") produces itemstacks without item in getSubItems()");
          } else {
            list.add(stack);
          }
        }
        sublist.clear();
      }
    }
    return list;
  }

}
