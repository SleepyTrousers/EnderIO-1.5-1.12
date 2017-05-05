package crazypants.enderio.machine.painter.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class BlockItemPaintedBlock extends ItemBlock {

  public BlockItemPaintedBlock(Block block, String name) {
    super(block);
    setHasSubtypes(true);
    setRegistryName(name);
  }

  @Override
  public int getMetadata(int damage) {
    return damage;
  }

  @Override
  public String getUnlocalizedName(ItemStack stack) {
    if (block instanceof INamedSubBlocks) {
      return ((INamedSubBlocks) block).getUnlocalizedName(stack.getMetadata());
    } else {
      super.getUnlocalizedName(stack);
    }
    return this.block.getUnlocalizedName();
  }

  public static interface INamedSubBlocks {
    String getUnlocalizedName(int meta);
  }

}
