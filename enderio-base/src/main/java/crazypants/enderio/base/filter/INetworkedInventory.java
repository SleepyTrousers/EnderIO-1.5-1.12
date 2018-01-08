package crazypants.enderio.base.filter;

import javax.annotation.Nullable;

import net.minecraftforge.items.IItemHandler;

public interface INetworkedInventory {

  @Nullable
  IItemHandler getInventory();

}
