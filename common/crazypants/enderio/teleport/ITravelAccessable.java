package crazypants.enderio.teleport;

import net.minecraft.item.ItemStack;
import crazypants.enderio.teleport.TileTravelAnchor.AccessMode;

public interface ITravelAccessable {

  public boolean canBlockBeAccessed(String playerName, ItemStack[] pwd);

  public boolean canSeeBlock(String playerName);

  public AccessMode getAccessMode();

  public void setAccessMode(AccessMode accessMode);

  public ItemStack[] getPassword();

  public void setPassword(ItemStack[] password);

  public String getPlacedBy();

  public void setPlacedBy(String placedBy);

  public boolean canUiBeAccessed(String username);

}
