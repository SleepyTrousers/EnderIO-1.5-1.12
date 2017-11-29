package crazypants.enderio.base.machine.fuel;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NullHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class SolidFuelCenter {

  private SolidFuelCenter() {
  }

  private static final ISolidFuelHandler DEFAULT = new ISolidFuelHandler() {

    @Override
    public boolean isInGUI() {
      return false;
    }

    @Override
    public int getPowerUsePerTick() {
      return 0;
    }

    @Override
    public long getBurnTime(@Nonnull ItemStack itemstack) {
      return -1;
    }

  };

  public static ISolidFuelHandler getActiveSolidFuelHandler() {
    EntityPlayer player = Minecraft.getMinecraft().player;

    if (NullHelper.untrust(player) == null) {
      // Log.warn("(in ItemTooltipEvent) net.minecraft.client.Minecraft.player is marked @Nonnull but it is null.");
      return DEFAULT;
    }

    if (player.openContainer instanceof ISolidFuelHandler) {
      return (ISolidFuelHandler) player.openContainer;
    } else if (player.openContainer instanceof ISolidFuelHandler.Provider) {
      ((ISolidFuelHandler.Provider) player.openContainer).getSolidFuelHandler();
    }
    return DEFAULT;
  }

}
