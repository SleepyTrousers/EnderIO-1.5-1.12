package crazypants.enderio.machine.painter;

import net.minecraft.item.ItemBlock;

public class BlockItemCustomFence extends ItemBlock {

  public BlockItemCustomFence(int id) {
    super(id);
    setHasSubtypes(true);
  }

  @Override
  public int getMetadata(int par1) {
    return par1;
  }

}
