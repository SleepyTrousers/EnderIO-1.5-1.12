package crazypants.enderio.conduit;

import javax.annotation.Nonnull;

import crazypants.enderio.api.tool.IHideFacades;
import crazypants.enderio.base.conduit.IConduit;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IConduitItem extends IHideFacades {

  @Nonnull
  Class<? extends IConduit> getBaseConduitType();

  IConduit createConduit(ItemStack item, EntityPlayer player);

}
