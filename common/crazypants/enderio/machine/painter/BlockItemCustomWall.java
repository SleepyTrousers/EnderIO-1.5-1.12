package crazypants.enderio.machine.painter;

import net.minecraft.item.ItemBlock;

public class BlockItemCustomWall extends ItemBlock {

  public BlockItemCustomWall(int id) {
    super(id);
    setHasSubtypes(true);
  }

  @Override
  public int getMetadata(int par1) {
    return par1;
  }
}
