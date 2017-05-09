package crazypants.enderio.api.tool;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public interface IHideFacades {
  
  boolean shouldHideFacades(@Nonnull ItemStack stack, EntityPlayer player);

}
