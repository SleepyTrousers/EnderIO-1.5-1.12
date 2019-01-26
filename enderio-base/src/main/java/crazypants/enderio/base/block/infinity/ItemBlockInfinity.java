package crazypants.enderio.base.block.infinity;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

public class ItemBlockInfinity extends ItemBlock {

  public ItemBlockInfinity(@Nonnull Block block) {
    super(block);
    setHasSubtypes(true);
  }

  @Override
  public int getMetadata(int damage) {
    return damage;
  }

}