package crazypants.enderio.gui;

import java.text.MessageFormat;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.client.handlers.SpecialTooltipHandler.ITooltipCallback;

import crazypants.enderio.EnderIO;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.fuel.ISolidFuelHandler;
import crazypants.enderio.machine.fuel.SolidFuelCenter;
import crazypants.enderio.power.PowerDisplayUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class TooltipHandlerBurnTime implements ITooltipCallback {

  @Override
  public void addCommonEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    ISolidFuelHandler handler = SolidFuelCenter.getActiveSolidFuelHandler();
    final long burnTime = handler.getBurnTime(itemstack);
    if (burnTime > 0) {
      if (handler.isInGUI()) {
        int rate = handler.getPowerUsePerTick();
        list.add(MessageFormat.format(EnderIO.lang.localize("power.generates"), PowerDisplayUtil.formatPower(burnTime * rate), PowerDisplayUtil.abrevation(),
            PowerDisplayUtil.formatPower(rate), PowerDisplayUtil.abrevation(), PowerDisplayUtil.perTickStr()));
      } else if (Config.addFurnaceFuelTootip) {
        list.add(MessageFormat.format(EnderIO.lang.localize("tooltip.burntime"), burnTime));
      }
    }
  }

  @Override
  public void addBasicEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {

  }

  @Override
  public void addDetailedEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {

  }

  @Override
  public boolean shouldHandleItem(@Nonnull ItemStack item) {
    return true; // Nothing saved by doing the check above here, too.
  }

}
