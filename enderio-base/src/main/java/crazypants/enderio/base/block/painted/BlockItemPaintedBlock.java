package crazypants.enderio.base.block.painted;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NullHelper;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
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

  @Override
  public @Nonnull CreativeTabs[] getCreativeTabs() {
    // Hack for JEI
    if (NullHelper.untrust(getCreativeTab()) != null) {
      return super.getCreativeTabs();
    } else {
      return new CreativeTabs[] {};
    }
  }

}
