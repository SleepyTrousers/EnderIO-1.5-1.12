package crazypants.enderio.api.tool;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IHideFacades {
  
  boolean shouldHideFacades(ItemStack stack, EntityPlayer player);

}
