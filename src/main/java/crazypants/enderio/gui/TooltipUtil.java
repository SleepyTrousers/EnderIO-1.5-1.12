package crazypants.enderio.gui;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import org.lwjgl.input.Keyboard;

import crazypants.util.Lang;

public class TooltipUtil {

  public static void addInformation(IAdvancedTooltipProvider tt, ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
    tt.addCommonEntries(itemstack, entityplayer, list, flag);
    if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
      tt.addAdvancedEntries(itemstack, entityplayer, list, flag);
    } else {
      tt.addBasicEntries(itemstack, entityplayer, list, flag);
      list.add(EnumChatFormatting.WHITE + "" + EnumChatFormatting.ITALIC + Lang.localize("item.tooltip.showDetails"));
    }
  }

}
