package crazypants.enderio.gui;

import java.text.MessageFormat;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.client.handlers.SpecialTooltipHandler.ITooltipCallback;
import com.enderio.core.common.util.FluidUtil;

import crazypants.enderio.EnderIO;
import crazypants.enderio.fluid.FluidFuelRegister;
import crazypants.enderio.fluid.IFluidCoolant;
import crazypants.enderio.fluid.IFluidFuel;
import crazypants.enderio.power.PowerDisplayUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;

public class TooltipHandlerFluid implements ITooltipCallback {

  @Override
  public void addCommonEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {

  }

  @Override
  public void addBasicEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {

  }

  @Override
  public void addDetailedEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    FluidStack fluid = FluidUtil.getFluidTypeFromItem(itemstack);
    if (fluid != null) {
      IFluidFuel fuel = FluidFuelRegister.instance.getFuel(fluid);
      if (fuel != null) {
        list.add(EnderIO.lang.localize("fuel.tooltip.heading"));
        list.add(TextFormatting.ITALIC + " " + PowerDisplayUtil.formatPowerPerTick(fuel.getPowerPerCycle()));
        Object[] objects = { fuel.getTotalBurningTime() };
        list.add(TextFormatting.ITALIC + " " + MessageFormat.format(EnderIO.lang.localize("fuel.tooltip.burnTime"), objects));
      } else {
        IFluidCoolant coolant = FluidFuelRegister.instance.getCoolant(fluid);
        if (coolant != null) {
          list.add(EnderIO.lang.localize("coolant.tooltip.heading"));
          list.add(TextFormatting.ITALIC + " " + PowerDisplayUtil.formatPowerFloat(coolant.getDegreesCoolingPerMB(100) * 1000) + " "
              + EnderIO.lang.localize("coolant.tooltip.degreesPerBucket"));
        }
      }
    }
  }

  @Override
  public boolean shouldHandleItem(@Nonnull ItemStack item) {
    return FluidUtil.getFluidTypeFromItem(item) != null;
  }

}
