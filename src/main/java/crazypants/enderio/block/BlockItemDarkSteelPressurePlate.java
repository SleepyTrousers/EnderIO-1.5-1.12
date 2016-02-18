package crazypants.enderio.block;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

public class BlockItemDarkSteelPressurePlate extends ItemBlock {

  public BlockItemDarkSteelPressurePlate(Block block) {
    super(block);
    this.setMaxDamage(0);
    this.setHasSubtypes(true);
  }

  @Override
  public int getMetadata(int p_77647_1_) {
    return 0;
  }

}
