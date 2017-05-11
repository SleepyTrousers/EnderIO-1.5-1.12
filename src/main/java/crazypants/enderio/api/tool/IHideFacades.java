package crazypants.enderio.api.tool;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IHideFacades {

  boolean shouldHideFacades(@Nonnull ItemStack stack, @Nonnull EntityPlayer player);

}
