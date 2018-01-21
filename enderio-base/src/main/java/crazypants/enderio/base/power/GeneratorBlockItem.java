package crazypants.enderio.base.power;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class GeneratorBlockItem extends PoweredBlockItem {

  public GeneratorBlockItem(@Nonnull Block block) {
    super(block);
  }

  @Override
  public int getMaxInput(@Nonnull ItemStack container) {
    return super.getMaxOutput(container);
  }

  @Override
  public int getMaxOutput(@Nonnull ItemStack container) {
    return super.getMaxInput(container);
  }

}
