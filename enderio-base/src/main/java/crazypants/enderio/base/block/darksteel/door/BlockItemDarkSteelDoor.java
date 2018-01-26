package crazypants.enderio.base.block.darksteel.door;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.EnderIOTab;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemDoor;

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
}

// TODO 1.12 add getBurnTime() {return 0; }
