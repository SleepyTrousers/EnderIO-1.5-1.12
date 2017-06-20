package crazypants.enderio.machine.painter.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class BlockItemPaintedBlock extends ItemBlock {

  public BlockItemPaintedBlock(@Nonnull Block block, @Nonnull String name) {
    super(block);
    setHasSubtypes(true);
    setRegistryName(name);
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
