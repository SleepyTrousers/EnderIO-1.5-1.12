package crazypants.enderio.base.block.darksteel.door;

import javax.annotation.Nonnull;

import crazypants.enderio.base.EnderIOTab;
import net.minecraft.block.Block;
import net.minecraft.item.ItemDoor;

public class BlockItemDarkSteelDoor extends ItemDoor {

  public BlockItemDarkSteelDoor(@Nonnull Block block) {
    super(block);
    setCreativeTab(EnderIOTab.tabEnderIO);
  }
}

// TODO 1.12 add getBurnTime() {return 0; }
