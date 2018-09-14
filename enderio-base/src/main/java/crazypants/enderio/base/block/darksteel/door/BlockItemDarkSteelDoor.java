package crazypants.enderio.base.block.darksteel.door;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.EnderIOTab;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class BlockItemDarkSteelDoor extends ItemDoor {

  public BlockItemDarkSteelDoor(@Nonnull Block block) {
    super(block);
    setCreativeTab(EnderIOTab.tabEnderIO);
  }

  @SuppressWarnings("null")
  public BlockItemDarkSteelDoor(@Nonnull Block block, @Nullable CreativeTabs tab) {
    super(block);
    setCreativeTab(tab);
  }

  @Override
  public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> list) {
    if (this.isInCreativeTab(tab)) {
      list.add(new ItemStack(this));
    }
  }

  @Override
  public int getItemBurnTime(@Nonnull ItemStack itemStack) {
    return 0;
  }

}
