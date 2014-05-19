package crazypants.enderio.teleport;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import crazypants.enderio.teleport.TileTravelAnchor.AccessMode;
import crazypants.util.BlockCoord;

public interface ITravelAccessable {

  boolean canBlockBeAccessed(EntityPlayer playerName);

  boolean canSeeBlock(EntityPlayer playerName);

  boolean canUiBeAccessed(EntityPlayer username);

  boolean getRequiresPassword(EntityPlayer username);

  boolean authoriseUser(EntityPlayer username, ItemStack[] password);

  AccessMode getAccessMode();

  void setAccessMode(AccessMode accessMode);

  ItemStack[] getPassword();

  void setPassword(ItemStack[] password);
  
  ItemStack getItemLabel();
  
  void setItemLabel(ItemStack lableIcon);

  String getPlacedBy();

//  void setPlacedBy(String placedBy);

  void setPlacedBy(EntityPlayer player);

  public void clearAuthorisedUsers();

  public BlockCoord getLocation();

}
