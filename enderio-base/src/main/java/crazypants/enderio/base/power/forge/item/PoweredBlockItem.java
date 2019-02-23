package crazypants.enderio.base.power.forge.item;

import javax.annotation.Nonnull;

import com.enderio.core.common.transform.EnderCoreMethods.IOverlayRenderAware;

import crazypants.enderio.base.ItemEIO;
import crazypants.enderio.base.machine.base.block.AbstractCapabilityPoweredMachineBlock;
import crazypants.enderio.base.machine.baselegacy.AbstractPowerConsumerBlock;
import crazypants.enderio.base.machine.baselegacy.AbstractPoweredMachineBlock;
import crazypants.enderio.base.render.itemoverlay.PowerBarOverlayRenderHelper;
import crazypants.enderio.util.NbtValue;
import net.minecraft.item.ItemStack;

public class PoweredBlockItem extends ItemEIO implements IInternalPoweredItem, IOverlayRenderAware {

  public PoweredBlockItem(@Nonnull AbstractPoweredMachineBlock<?> block) {
    super(block);
  }

  public PoweredBlockItem(@Nonnull AbstractPowerConsumerBlock<?> block) {
    super(block);
  }

  public PoweredBlockItem(@Nonnull AbstractCapabilityPoweredMachineBlock<?> block) {
    super(block);
  }

  @Override
  public int getMaxEnergyStored(@Nonnull ItemStack container) {
    return NbtValue.ENERGY_BUFFER.getInt(container, 0);
  }

  @Override
  public int getMaxInput(@Nonnull ItemStack container) {
    return getMaxEnergyStored(container) / 100;
  }

  @Override
  public int getMaxOutput(@Nonnull ItemStack container) {
    return 0;
  }

  @Override
  public void renderItemOverlayIntoGUI(@Nonnull ItemStack stack, int xPosition, int yPosition) {
    if (stack.getCount() == 1) {
      PowerBarOverlayRenderHelper.instance_machine.render(stack, xPosition, yPosition);
    }
  }

}
