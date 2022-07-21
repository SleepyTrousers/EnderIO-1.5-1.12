package crazypants.enderio.conduit;

import crazypants.enderio.api.tool.IHideFacades;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IConduitItem extends IHideFacades {

    Class<? extends IConduit> getBaseConduitType();

    IConduit createConduit(ItemStack item, EntityPlayer player);
}
