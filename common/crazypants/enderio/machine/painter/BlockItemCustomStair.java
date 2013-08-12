package crazypants.enderio.machine.painter;

import net.minecraft.item.ItemBlock;

public class BlockItemCustomStair extends ItemBlock {

  public BlockItemCustomStair(int id) {
    super(id);
    setHasSubtypes(true);
  }

  @Override
  public int getMetadata(int par1) {
    return par1;
  }
}
