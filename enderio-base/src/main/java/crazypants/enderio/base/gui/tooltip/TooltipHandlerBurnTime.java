package crazypants.enderio.base.gui.tooltip;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.client.handlers.SpecialTooltipHandler.ITooltipCallback;

import crazypants.enderio.base.config.config.PersonalConfig;
import crazypants.enderio.base.lang.Lang;
import crazypants.enderio.base.lang.LangPower;
import crazypants.enderio.base.machine.fuel.ISolidFuelHandler;
import crazypants.enderio.base.machine.fuel.SolidFuelCenter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

public class TooltipHandlerBurnTime implements ITooltipCallback {

  @Override
  public void addCommonEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    ISolidFuelHandler handler = SolidFuelCenter.getActiveSolidFuelHandler();
    final long burnTime = handler.getBurnTime(itemstack);
    if (burnTime > 0) {
      if (handler.isInGUI()) {
        int rate = handler.getPowerUsePerTick();
        list.add(Lang.FUEL_HEADING.get());
        list.add(TextFormatting.ITALIC + " " + Lang.FUEL_GENERATES.get(LangPower.RF(burnTime * rate), LangPower.RFt(rate)));
        list.add(TextFormatting.ITALIC + " " + Lang.FUEL_BURNTIME.get(burnTime));
      } else if (PersonalConfig.tooltipsAddFurnaceFuel.get()) {
        list.add(Lang.FUEL_HEADING.get());
        list.add(TextFormatting.ITALIC + " " + Lang.FUEL_BURNTIME.get(burnTime));
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
