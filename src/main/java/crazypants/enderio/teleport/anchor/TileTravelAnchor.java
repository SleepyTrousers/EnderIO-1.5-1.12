package crazypants.enderio.teleport.anchor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.BlockCoord;

import crazypants.enderio.TileEntityEio;
import crazypants.enderio.api.teleport.ITravelAccessable;
import crazypants.enderio.api.teleport.TravelSource;
import crazypants.enderio.paint.IPaintable;
import crazypants.util.UserIdent;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import info.loenwind.autosave.handlers.endercore.HandleUserIdent;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Storable
public class TileTravelAnchor extends TileEntityEio implements ITravelAccessable, IPaintable.IPaintableTileEntity {

  @Store
  protected IBlockState sourceBlock;

  @Store
  private AccessMode accessMode = AccessMode.PUBLIC;

  // TODO: Don't send this to all clients, but only to the owner. So the GUI has to request it with a request packet.
  @Store
  private ItemStack[] password = new ItemStack[5];
  
  @Store
  private ItemStack itemLabel;
  
  @Store
  private String label;

  @Store
  private @Nonnull UserIdent owner = UserIdent.nobody;

  @Store(handler = HandleUserIdent.HandleUserIdentArrayList.class)
  private List<UserIdent> authorisedUsers = new ArrayList<UserIdent>();

  private boolean isAuthorisedUser(UserIdent ident) {
    return authorisedUsers.contains(ident);
  }

  private boolean isOwnerUser(UserIdent ident) {
    return owner.equals(ident);
  }

  @Override
  public boolean canBlockBeAccessed(EntityPlayer playerName) {
    if(accessMode == AccessMode.PUBLIC) {
      return true;
    }
    // Covers protected and private access modes
    return isOwnerUser(UserIdent.create(playerName.getGameProfile())) || isAuthorisedUser(UserIdent.create(playerName.getGameProfile()));

  }

  @Override
  public void clearAuthorisedUsers() {
    authorisedUsers.clear();
  }

  private boolean checkPassword(ItemStack[] pwd) {
    if(pwd == null || pwd.length != password.length) {
      return false;
    }
    for (int i = 0; i < pwd.length; i++) {
      ItemStack pw = password[i];
      ItemStack tst = pwd[i];
      if(pw == null && tst != null) {
        return false;
      }
      if(pw != null) {
        if(tst == null || !ItemStack.areItemStacksEqual(pw, tst)) {
          return false;
        }
      }
    }
    return true;
  }

  @Override
  public boolean getRequiresPassword(EntityPlayer playerName) {
    return getAccessMode() == AccessMode.PROTECTED && !canUiBeAccessed(playerName)
        && !isAuthorisedUser(UserIdent.create(playerName.getGameProfile()));
  }

  @Override
  public boolean authoriseUser(EntityPlayer username, ItemStack[] passwordIn) {
    if (checkPassword(passwordIn)) {
      authorisedUsers.add(UserIdent.create(username.getGameProfile()));
      return true;
    }
    return false;
  }

  @Override
  public boolean canUiBeAccessed(EntityPlayer playerName) {
    return isOwnerUser(UserIdent.create(playerName.getGameProfile()));
  }

  @Override
  public boolean canSeeBlock(EntityPlayer playerName) {
    if(accessMode != AccessMode.PRIVATE) {
      return true;
    }
    return isOwnerUser(UserIdent.create(playerName.getGameProfile()));
  }

  @Override
  public AccessMode getAccessMode() {
    return accessMode;
  }

  @Override
  public void setAccessMode(AccessMode accessMode) {
    this.accessMode = accessMode;
  }

  @Override
  public ItemStack[] getPassword() {
    return password;
  }

  @Override
  public void setPassword(ItemStack[] password) {
    this.password = password;
  }

  @Override
  public ItemStack getItemLabel() {
    return itemLabel;
  }

  @Override
  public void setItemLabel(ItemStack lableIcon) {
    this.itemLabel = lableIcon;
  }

  @Override
  public String getLabel() {  
    return label;
  }

  @Override
  public void setLabel(String label) {
    this.label = label;    
  }

  @Override
  @Deprecated
  public UUID getPlacedBy() {
    return owner.getUUID();
  }

  @Override
  public @Nonnull UserIdent getOwner() {
    return owner;
  }

  @Override
  public void setPlacedBy(EntityPlayer player) {
    if (player != null) {
      this.owner = UserIdent.create(player.getGameProfile());
    } else {
      this.owner = UserIdent.nobody;
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public double getMaxRenderDistanceSquared() {
    return TravelSource.getMaxDistanceSq();
  }

  @Override
  public boolean shouldRenderInPass(int pass) {
    return pass == 1;
  }
  
  @Override
  public IBlockState getPaintSource() {
    return sourceBlock;
  }

  @Override
  public void setPaintSource(@Nullable IBlockState sourceBlock) {
    this.sourceBlock = sourceBlock;
  }

  @Override
  public BlockCoord getLocation() {
    return new BlockCoord(pos);
  }
  
}
