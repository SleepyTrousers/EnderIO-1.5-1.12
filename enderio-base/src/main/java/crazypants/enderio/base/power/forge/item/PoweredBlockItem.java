package crazypants.enderio.base.power.forge.item;

import javax.annotation.Nonnull;

import com.enderio.core.common.transform.EnderCoreMethods.IOverlayRenderAware;

import crazypants.enderio.base.render.itemoverlay.PowerBarOverlayRenderHelper;
import crazypants.enderio.util.NbtValue;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class PoweredBlockItem extends ItemBlock implements IInternalPoweredItem, IOverlayRenderAware {

  public PoweredBlockItem(@Nonnull Block block) {
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
