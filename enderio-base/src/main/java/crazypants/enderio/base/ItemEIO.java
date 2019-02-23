package crazypants.enderio.base;

import javax.annotation.Nonnull;

import crazypants.enderio.base.lang.Lang;
import crazypants.enderio.util.NbtValue;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemEIO extends ItemBlock {

  public ItemEIO(@Nonnull BlockEio<?> block) {
    super(block);
  }

  /**
   * Constructor for use with blocks that cannot extend {@link BlockEio}.
   */
  protected ItemEIO(@Nonnull Block block, boolean unused) {
    super(block);
  }

  @Override
  public @Nonnull String getItemStackDisplayName(@Nonnull ItemStack stack) {
    final String name = super.getItemStackDisplayName(stack);
    if (NbtValue.DATAROOT.hasTag(stack)) {
      return Lang.MACHINE_CONFIGURED.get(name);
    }
    return name;
  }

}
