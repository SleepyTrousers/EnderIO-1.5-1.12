package crazypants.enderio.gui;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

import com.enderio.core.client.handlers.SpecialTooltipHandler.ITooltipCallback;
import com.enderio.core.common.util.Lang;

import crazypants.enderio.config.Config;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.generator.stirling.StirlingGeneratorContainer;
import crazypants.enderio.machine.generator.stirling.TileEntityStirlingGenerator;
import crazypants.enderio.machine.power.PowerDisplayUtil;

public class TooltipHandlerBurnTime implements ITooltipCallback {

  private TileEntityStirlingGenerator gen = null;
  
  @Override
  public void addCommonEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
    int time = 0;
    if (gen != null) {
      int rate = gen.getPowerUsePerTick();

      String msg = String.format("%s %s %s %s %s %s%s",
          Lang.localize("power.generates"),
          PowerDisplayUtil.formatPower((long)gen.getBurnTime(itemstack) * rate),
          PowerDisplayUtil.abrevation(),
          Lang.localize("power.generation_rate"),
          PowerDisplayUtil.formatPower(rate),
          PowerDisplayUtil.abrevation(),
          PowerDisplayUtil.perTickStr());

      list.add(msg);
    } else if (Config.addFurnaceFuelTootip && (time = TileEntityFurnace.getItemBurnTime(itemstack)) > 0) {
      list.add(Lang.localize("tooltip.burntime") + " " + time);
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

  private boolean isStirlingGen(ItemStack stack) {
    EntityPlayer player = Minecraft.getMinecraft().thePlayer;
    if (player != null && player.openContainer instanceof StirlingGeneratorContainer) {
      AbstractMachineEntity te = ((StirlingGeneratorContainer) player.openContainer).getTileEntity();
      if (te instanceof TileEntityStirlingGenerator) {
        gen = (TileEntityStirlingGenerator) te;
        int burnTime = gen.getBurnTime(stack);
        return burnTime > 0;
      }
    }
    return false;
  }
}
