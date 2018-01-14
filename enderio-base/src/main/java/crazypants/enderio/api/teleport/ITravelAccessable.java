package crazypants.enderio.api.teleport;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.UserIdent;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public interface ITravelAccessable {

  public enum AccessMode {
    PUBLIC,
    PRIVATE,
    PROTECTED
  }

  boolean canBlockBeAccessed(@Nonnull EntityPlayer playerName);

  boolean canSeeBlock(@Nonnull EntityPlayer playerName);

  boolean canUiBeAccessed(@Nonnull EntityPlayer username);

  boolean getRequiresPassword(@Nonnull EntityPlayer username);

  boolean authoriseUser(@Nonnull EntityPlayer username, @Nonnull ItemStack[] password);

  @Nonnull
  AccessMode getAccessMode();

  void setAccessMode(@Nonnull AccessMode accessMode);

  @Nonnull
  NNList<ItemStack> getPassword();

  void setPassword(@Nonnull NNList<ItemStack> password);

  @Nonnull
  ItemStack getItemLabel();

  void setItemLabel(@Nonnull ItemStack lableIcon);

  @Nullable
  String getLabel();

  void setLabel(@Nullable String label);

  @Nonnull
  UserIdent getOwner();

  void clearAuthorisedUsers();

  @Nonnull
  BlockPos getLocation();

  /**
   * Is this block a travel target for the staff or a travel anchor?
   */
  default boolean isTravelTarget() {
    return true;
  }

}
