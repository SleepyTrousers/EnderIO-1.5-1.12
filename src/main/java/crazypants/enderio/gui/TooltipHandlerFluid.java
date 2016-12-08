package crazypants.enderio.gui;

import java.util.List;

import com.enderio.core.client.handlers.SpecialTooltipHandler.ITooltipCallback;

import crazypants.enderio.EnderIO;
import crazypants.enderio.fluid.FluidFuelRegister;
import crazypants.enderio.fluid.IFluidCoolant;
import crazypants.enderio.fluid.IFluidFuel;
import crazypants.enderio.power.PowerDisplayUtil;
import crazypants.util.TextUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;

public class TooltipHandlerFluid implements ITooltipCallback {
  
  @Override
  public void addCommonEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
    
  }

  @Override
  public void addBasicEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
   
  }

  @Override
  public void addDetailedEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
    FluidStack fluid = FluidContainerRegistry.getFluidForFilledItem(itemstack);
    if (fluid != null) {
      IFluidFuel fuel = FluidFuelRegister.instance.getFuel(fluid);
      if (fuel != null) {
        list.add(EnderIO.lang.localize("fuel.tooltip.heading"));
        list.add(TextFormatting.ITALIC + " " + PowerDisplayUtil.formatPowerPerTick(fuel.getPowerPerCycle()));
        list.add(TextFormatting.ITALIC + " " + TextUtil.format(EnderIO.lang.localize("fuel.tooltip.burnTime"), fuel.getTotalBurningTime()));
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
  public boolean shouldHandleItem(ItemStack item) {
    return FluidContainerRegistry.getFluidForFilledItem(item) != null;
  }

}
