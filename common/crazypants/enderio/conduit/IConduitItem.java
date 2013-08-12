package crazypants.enderio.conduit;

import net.minecraft.item.ItemStack;

public interface IConduitItem {

  Class<? extends IConduit> getBaseConduitType();

  IConduit createConduit(ItemStack item);

}
