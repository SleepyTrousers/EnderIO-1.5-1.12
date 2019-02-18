package crazypants.enderio.base.power.forge.item;

import javax.annotation.Nonnull;

import crazypants.enderio.base.machine.base.block.AbstractCapabilityGeneratorBlock;
import crazypants.enderio.base.machine.baselegacy.AbstractGeneratorBlock;
import net.minecraft.item.ItemStack;

public class GeneratorBlockItem extends PoweredBlockItem {

  public GeneratorBlockItem(@Nonnull AbstractGeneratorBlock<?> block) {
    super(block);
  }

  public GeneratorBlockItem(@Nonnull AbstractCapabilityGeneratorBlock<?> block) {
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
