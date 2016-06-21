package crazypants.enderio.api.teleport;

import java.util.UUID;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import com.enderio.core.common.util.BlockCoord;

import crazypants.util.UserIdent;

public interface ITravelAccessable {

  public enum AccessMode {
    PUBLIC,
    PRIVATE,
    PROTECTED
  }

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
  
  String getLabel();
  
  void setLabel(String label);

  @Deprecated
  UUID getPlacedBy();

  @Nonnull
  UserIdent getOwner();

  void setPlacedBy(EntityPlayer player);

  public void clearAuthorisedUsers();

  public BlockCoord getLocation();

}
