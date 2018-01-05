package crazypants.enderio.base.machine.fuel;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class SolidFuelCenter {

  private SolidFuelCenter() {
  }

  private static final @Nonnull NNList<ISolidFuelHandler> HANDLERS = new NNList<>();
  
  static {
    HANDLERS.add(new ISolidFuelHandler() {

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

    });
  }

  private static final ISolidFuelHandler DEFAULT = new ISolidFuelHandler() {

    @Override
    public boolean isInGUI() {
      for (ISolidFuelHandler handler : HANDLERS) {
        if (handler.isInGUI()) {
          return true;
        }
      }
      return false;
    }

    @Override
    public int getPowerUsePerTick() {
      for (ISolidFuelHandler handler : HANDLERS) {
        int powerUsePerTick = handler.getPowerUsePerTick();
        if (powerUsePerTick >= 0) {
          return powerUsePerTick;
        }
      }
      return 0;
    }

    @Override
    public long getBurnTime(@Nonnull ItemStack itemstack) {
      for (ISolidFuelHandler handler : HANDLERS) {
        long burnTime = handler.getBurnTime(itemstack);
        if (burnTime >= 0) {
          return burnTime;
        }
      }
      return -1;
    }

  };

  public static void registerSolidFuelHandler(@Nonnull ISolidFuelHandler handler) {
    HANDLERS.add(handler);
  }

  public static ISolidFuelHandler getActiveSolidFuelHandler() {
    EntityPlayer player = Minecraft.getMinecraft().player;

    if (NullHelper.untrust(player) == null) {
      return DEFAULT;
    }

    if (player.openContainer instanceof ISolidFuelHandler) {
      return (ISolidFuelHandler) player.openContainer;
    } else if (player.openContainer instanceof ISolidFuelHandler.Provider) {
      return ((ISolidFuelHandler.Provider) player.openContainer).getSolidFuelHandler();
    }

    return DEFAULT;
  }

}
