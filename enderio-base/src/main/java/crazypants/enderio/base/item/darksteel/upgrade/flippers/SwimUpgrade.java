package crazypants.enderio.base.item.darksteel.upgrade.flippers;

import javax.annotation.Nonnull;

import crazypants.enderio.base.config.Config;
import crazypants.enderio.base.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.base.init.ModObject;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class SwimUpgrade extends AbstractUpgrade {

  private static final @Nonnull String UPGRADE_NAME = "swim";

  public static final @Nonnull SwimUpgrade INSTANCE = new SwimUpgrade();

  public SwimUpgrade() {
    super(UPGRADE_NAME, "enderio.darksteel.upgrade.swim", new ItemStack(Blocks.WATERLILY), Config.darkSteelSwimCost);
  }

  @Override
  public boolean canAddToItem(@Nonnull ItemStack stack) {
    return stack.getItem() == ModObject.itemDarkSteelBoots.getItem() && !hasUpgrade(stack);
  }

  @Override
  public void onPlayerTick(@Nonnull ItemStack stack, @Nonnull EntityPlayer player) {
    if (player.isInWater() && !player.capabilities.isFlying) {
      player.motionX *= 1.1;
      player.motionZ *= 1.1;
    }
  }

}
