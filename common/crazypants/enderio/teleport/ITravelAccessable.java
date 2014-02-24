package crazypants.enderio.teleport;

import net.minecraft.item.ItemStack;
import crazypants.enderio.teleport.TileTravelAnchor.AccessMode;
import crazypants.util.BlockCoord;

public interface ITravelAccessable {

  public boolean canBlockBeAccessed(String playerName);

  public boolean canSeeBlock(String playerName);

  public boolean canUiBeAccessed(String username);

  public boolean getRequiresPassword(String username);

  public boolean authoriseUser(String username, ItemStack[] password);

  public AccessMode getAccessMode();

  public void setAccessMode(AccessMode accessMode);

  public ItemStack[] getPassword();

  public void setPassword(ItemStack[] password);

  public String getPlacedBy();

  public void setPlacedBy(String placedBy);

  public void clearAuthorisedUsers();

  public BlockCoord getLocation();

}
