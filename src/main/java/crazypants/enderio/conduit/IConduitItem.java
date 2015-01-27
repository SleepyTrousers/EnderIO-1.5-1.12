package crazypants.enderio.conduit;

import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import crazypants.enderio.api.tool.IHideFacades;

public interface IConduitItem extends IHideFacades {

  Class<? extends IConduit> getBaseConduitType();

  IConduit createConduit(ItemStack item, EntityPlayer player);

}
