package crazypants.enderio.gui;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.enderio.core.client.handlers.SpecialTooltipHandler.ITooltipCallback;

import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.sagmill.IGrindingMultiplier;
import crazypants.enderio.machine.sagmill.SagMillRecipeManager;

public class TooltipHandlerGrinding implements ITooltipCallback {

  @Override
  @SideOnly(Side.CLIENT)
  public void addCommonEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {

  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addBasicEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addDetailedEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
    IGrindingMultiplier ball = SagMillRecipeManager.getInstance().getGrindballFromStack(itemstack);
    list.add(EnumChatFormatting.BLUE + EnderIO.lang.localize("darkGrindingBall.tooltip.detailed.line1"));
    list.add(EnumChatFormatting.GRAY + EnderIO.lang.localize("darkGrindingBall.tooltip.detailed.line2") + toPercent(ball.getGrindingMultiplier()));
    list.add(EnumChatFormatting.GRAY + EnderIO.lang.localize("darkGrindingBall.tooltip.detailed.line3") + toPercent(ball.getChanceMultiplier()));
    list.add(EnumChatFormatting.GRAY + EnderIO.lang.localize("darkGrindingBall.tooltip.detailed.line4") + toPercent(1 - ball.getPowerMultiplier()));
  }

  private String toPercent(float fl) {
    fl = fl * 100;
    int per = Math.round(fl);
    return " " + per + "%";
  }

  @Override
  public boolean shouldHandleItem(ItemStack item) {
    return SagMillRecipeManager.getInstance().getGrindballFromStack(item) != null;
  }
}