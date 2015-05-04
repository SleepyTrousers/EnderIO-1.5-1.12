package crazypants.enderio.gui;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.enderio.core.client.handlers.SpecialTooltipHandler;
import com.enderio.core.client.handlers.SpecialTooltipHandler.ITooltipCallback;
import com.enderio.core.common.util.Lang;

import crazypants.enderio.fluid.FluidFuelRegister;
import crazypants.enderio.fluid.IFluidCoolant;
import crazypants.enderio.fluid.IFluidFuel;
import crazypants.enderio.machine.power.PowerDisplayUtil;

public class TooltipHandlerFluid implements ITooltipCallback {

  private FluidStack fluid;
  
  @Override
  public void addCommonEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
    
  }

  @Override
  public void addBasicEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
   
  }

  @Override
  public void addDetailedEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
    if (fluid != null) {
      IFluidFuel fuel = FluidFuelRegister.instance.getFuel(fluid);
      if (fuel != null) {
        list.add(Lang.localize("fuel.tooltip.heading"));
        list.add(EnumChatFormatting.ITALIC + " " + PowerDisplayUtil.formatPowerPerTick(fuel.getPowerPerCycle()));
        list.add(EnumChatFormatting.ITALIC + " " + fuel.getTotalBurningTime() + " " + Lang.localize("fuel.tooltip.burnTime"));
      } else {
        IFluidCoolant coolant = FluidFuelRegister.instance.getCoolant(fluid);
        if (coolant != null) {
          list.add(Lang.localize("coolant.tooltip.heading"));
          list.add(EnumChatFormatting.ITALIC + " " + PowerDisplayUtil.formatPowerFloat(coolant.getDegreesCoolingPerMB(100) * 1000) + " "
              + Lang.localize("coolant.tooltip.degreesPerBucket"));
        }
      }
    }    
  }

  @Override
  public boolean shouldHandleItem(ItemStack item) {
    fluid = FluidContainerRegistry.getFluidForFilledItem(item);
    return fluid != null;
  }

}
