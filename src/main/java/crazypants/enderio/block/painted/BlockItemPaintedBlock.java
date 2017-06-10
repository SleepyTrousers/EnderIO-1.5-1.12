package crazypants.enderio.block.painted;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class BlockItemPaintedBlock extends ItemBlock {

  public BlockItemPaintedBlock(@Nonnull Block block) {
    super(block);
    setHasSubtypes(true);
  }

  @Override
  public int getMetadata(int damage) {
    return damage;
  }

  @Override
  public @Nonnull String getUnlocalizedName(@Nonnull ItemStack stack) {
    if (block instanceof INamedSubBlocks) {
      return ((INamedSubBlocks) block).getUnlocalizedName(stack.getMetadata());
    } else {
      return super.getUnlocalizedName(stack);
    }
  }

  public static interface INamedSubBlocks {
    @Nonnull
    String getUnlocalizedName(int meta);
  }

}
