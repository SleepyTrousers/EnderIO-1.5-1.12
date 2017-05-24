package crazypants.enderio.gui;

import java.text.MessageFormat;
import java.util.List;

import com.enderio.core.client.handlers.SpecialTooltipHandler.ITooltipCallback;

import crazypants.enderio.EnderIO;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.base.te.AbstractMachineEntity;
import crazypants.enderio.machine.generator.stirling.StirlingGeneratorContainer;
import crazypants.enderio.machine.generator.stirling.TileEntityStirlingGenerator;
import crazypants.enderio.power.PowerDisplayUtil;
import crazypants.util.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

public class TooltipHandlerBurnTime implements ITooltipCallback {
  
  @Override
  public void addCommonEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
    int time = 0;
    TileEntityStirlingGenerator gen = getStirlingGen(itemstack);
    if (isStirlingGen(itemstack, gen)) {
      int rate = gen.getPowerUsePerTick();
      Object[] objects = { PowerDisplayUtil.formatPower((long)gen.getBurnTime(itemstack) * rate), PowerDisplayUtil.abrevation(), PowerDisplayUtil.formatPower(rate), PowerDisplayUtil.abrevation(), PowerDisplayUtil.perTickStr() };
      String msg = MessageFormat.format(EnderIO.lang.localize("power.generates"), objects);

      list.add(msg);
    } else if (Config.addFurnaceFuelTootip && (time = TileEntityFurnace.getItemBurnTime(itemstack)) > 0) {
      Object[] objects = { time };
      list.add(MessageFormat.format(EnderIO.lang.localize("tooltip.burntime"), objects));
    }
  }

  @Override
  public void addBasicEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {

  }

  @Override
  public void addDetailedEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
    
  }

  @Override
  public boolean shouldHandleItem(ItemStack item) {
    int time = TileEntityFurnace.getItemBurnTime(item);
    return time > 0 || isStirlingGen(item);
  }

  private TileEntityStirlingGenerator getStirlingGen(ItemStack stack) {
    EntityPlayer player = Minecraft.getMinecraft().player;
    if (player != null && player.openContainer instanceof StirlingGeneratorContainer) {
      AbstractMachineEntity te = ((StirlingGeneratorContainer) player.openContainer).getInv();
      if (te instanceof TileEntityStirlingGenerator) {
        return (TileEntityStirlingGenerator) te;
      }
    }
    return null;
  }
  
  private boolean isStirlingGen(ItemStack stack) {
    return isStirlingGen(stack, getStirlingGen(stack));
  }
  
  private boolean isStirlingGen(ItemStack stack, TileEntityStirlingGenerator gen) {
    return gen == null ? false : gen.getBurnTime(stack) > 0;
  }
}
