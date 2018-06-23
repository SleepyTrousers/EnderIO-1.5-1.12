package crazypants.enderio.base.gui.tooltip;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.client.handlers.SpecialTooltipHandler.ITooltipCallback;
import com.enderio.core.common.util.FluidUtil;

import crazypants.enderio.base.config.config.PersonalConfig;
import crazypants.enderio.base.fluid.FluidFuelRegister;
import crazypants.enderio.base.fluid.IFluidCoolant;
import crazypants.enderio.base.fluid.IFluidFuel;
import crazypants.enderio.base.lang.Lang;
import crazypants.enderio.base.lang.LangPower;
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
        list.add(Lang.FUEL_HEADING.get());
        list.add(TextFormatting.ITALIC + " " + LangPower.RFt(fuel.getPowerPerCycle()));
        list.add(TextFormatting.ITALIC + " " + Lang.FUEL_BURNTIME.get(fuel.getTotalBurningTime()));
      } else {
        IFluidCoolant coolant = FluidFuelRegister.instance.getCoolant(fluid);
        if (coolant != null) {
          list.add(Lang.COOLANT_HEADING.get());
          list.add(TextFormatting.ITALIC + " " + Lang.COOLANT_DEGREES.get(LangPower.format((long) (coolant.getDegreesCoolingPerMBPerK() * 1000))));
        }
      }
    }
  }

  @Override
  public boolean shouldHandleItem(@Nonnull ItemStack item) {
    return PersonalConfig.tooltipsAddFuelToFluidContainers.get() && FluidUtil.getFluidTypeFromItem(item) != null;
  }

}
