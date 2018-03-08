package crazypants.enderio.base.item.darksteel.upgrade.flippers;

import javax.annotation.Nonnull;

import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.base.config.Config;
import crazypants.enderio.base.handler.darksteel.AbstractUpgrade;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public class SwimUpgrade extends AbstractUpgrade {

  private static final @Nonnull String UPGRADE_NAME = "swim";

  public static final @Nonnull SwimUpgrade INSTANCE = new SwimUpgrade();

  public SwimUpgrade() {
    super(UPGRADE_NAME, "enderio.darksteel.upgrade.swim", new ItemStack(Blocks.WATERLILY), Config.darkSteelSwimCost);
  }

  @Override
  public boolean canAddToItem(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item) {
    return item.isForSlot(EntityEquipmentSlot.FEET) && !hasUpgrade(stack, item);
  }

  @Override
  public void onPlayerTick(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item, @Nonnull EntityPlayer player) {
    if (player.isInWater() && !player.capabilities.isFlying) {
      player.motionX *= 1.1;
      player.motionZ *= 1.1;
    }
  }

}
