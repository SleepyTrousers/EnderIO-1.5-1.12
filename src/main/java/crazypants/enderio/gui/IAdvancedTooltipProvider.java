package crazypants.enderio.gui;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IAdvancedTooltipProvider {

  public void addCommonEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag);

  public void addBasicEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag);

  public void addDetailedEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag);


}
