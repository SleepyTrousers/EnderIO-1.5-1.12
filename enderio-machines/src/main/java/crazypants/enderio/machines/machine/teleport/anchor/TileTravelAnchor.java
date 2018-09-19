package crazypants.enderio.machines.machine.teleport.anchor;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import info.loenwind.autosave.util.NBTAction;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.UserIdent;

import crazypants.enderio.api.capacitor.ICapacitorKey;
import crazypants.enderio.api.teleport.ITravelAccessable;
import crazypants.enderio.api.teleport.TravelSource;
import crazypants.enderio.base.capacitor.CapacitorKey;
import crazypants.enderio.base.machine.base.te.AbstractCapabilityPoweredMachineEntity;
import crazypants.enderio.base.machine.modes.IoMode;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.machines.integration.ftblib.FtblIntegration;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Storable
public class TileTravelAnchor extends AbstractCapabilityPoweredMachineEntity implements ITravelAccessable, IPaintable.IPaintableTileEntity {

  protected TileTravelAnchor(@Nonnull ICapacitorKey maxEnergyRecieved, @Nonnull ICapacitorKey maxEnergyStored, @Nonnull ICapacitorKey maxEnergyUsed) {
    super(null, maxEnergyRecieved, maxEnergyStored, maxEnergyUsed);
  }

  public TileTravelAnchor() {
    this(CapacitorKey.NO_POWER, CapacitorKey.NO_POWER, CapacitorKey.NO_POWER);
  }

  @Store
  protected IBlockState sourceBlock;

  @Store
  @Nonnull
  private AccessMode accessMode = AccessMode.PUBLIC;

  // TODO: Don't send this to all clients, but only to the owner. So the GUI has to request it with a request packet.
  @Store
  @Nonnull
  private NNList<ItemStack> password = new NNList<>(5, ItemStack.EMPTY);

  @Store
  @Nonnull
  private ItemStack itemLabel = ItemStack.EMPTY;

  @Store
  private String label;

  @Store
  private List<UserIdent> authorisedUsers = new ArrayList<UserIdent>();

  @Store
  private boolean visible = true;

  // TODO: Check the users server-side instead of having this copy
  @Store({ NBTAction.SAVE, NBTAction.CLIENT })
  private @Nullable UserIdent travelOwner;

  @Override
  public void setOwner(@Nonnull EntityPlayer player) {
    super.setOwner(player);
    this.travelOwner = UserIdent.create(player.getGameProfile());
  }

  @Override
  public @Nonnull UserIdent getOwner() {
    return travelOwner != null ? travelOwner : super.getOwner();
  }

  private boolean isAuthorisedUser(UserIdent ident) {
    return authorisedUsers.contains(ident);
  }

  private boolean isOwnerUser(UserIdent ident) {
    return getOwner().equals(ident);
  }

  @Override
  public boolean canBlockBeAccessed(@Nonnull EntityPlayer playerName) {
    if (accessMode == AccessMode.PUBLIC) {
      return true;
    }
    // Covers protected and private access modes
    return isOwnerUser(UserIdent.create(playerName.getGameProfile())) || isAuthorisedUser(UserIdent.create(playerName.getGameProfile()))
    // TODO TeamMode button
        || FtblIntegration.isInSameTeam(UserIdent.create(playerName.getGameProfile()), getOwner());
  }

  @Override
  public void clearAuthorisedUsers() {
    authorisedUsers.clear();
  }

  private boolean checkPassword(ItemStack[] pwd) {
    if (pwd == null || pwd.length != password.size()) {
      return false;
    }
    for (int i = 0; i < pwd.length; i++) {
      ItemStack pw = password.get(i);
      ItemStack tst = pwd[i];
      if (pw.isEmpty() && !tst.isEmpty()) {
        return false;
      }
      if (!pw.isEmpty()) {
        if (tst.isEmpty() || !ItemStack.areItemStacksEqual(pw, tst)) {
          return false;
        }
      }
    }
    return true;
  }

  @Override
  public boolean getRequiresPassword(@Nonnull EntityPlayer playerName) {
    return getAccessMode() == AccessMode.PROTECTED && !canUiBeAccessed(playerName) && !isAuthorisedUser(UserIdent.create(playerName.getGameProfile()));
  }

  @Override
  public boolean authoriseUser(@Nonnull EntityPlayer username, @Nonnull ItemStack[] passwordIn) {
    if (checkPassword(passwordIn)) {
      authorisedUsers.add(UserIdent.create(username.getGameProfile()));
      return true;
    }
    return false;
  }

  @Override
  public boolean canUiBeAccessed(@Nonnull EntityPlayer playerName) {
    return isOwnerUser(UserIdent.create(playerName.getGameProfile()));
  }

  @Override
  public boolean canSeeBlock(@Nonnull EntityPlayer playerName) {
    if (accessMode != AccessMode.PRIVATE) {
      return true;
    }
    UserIdent ident = UserIdent.create(playerName.getGameProfile());
    return isOwnerUser(ident) || FtblIntegration.isInSameTeam(ident, getOwner());
  }

  @Override
  public @Nonnull AccessMode getAccessMode() {
    return accessMode;
  }

  @Override
  public void setAccessMode(@Nonnull AccessMode accessMode) {
    this.accessMode = accessMode;
  }

  @Override
  public @Nonnull NNList<ItemStack> getPassword() {
    return password;
  }

  @Override
  public void setPassword(@Nonnull NNList<ItemStack> password) {
    this.password = password;
  }

  @Override
  public @Nonnull ItemStack getItemLabel() {
    return itemLabel;
  }

  @Override
  public void setItemLabel(@Nonnull ItemStack lableIcon) {
    this.itemLabel = lableIcon;
  }

  @Override
  public String getLabel() {
    return label;
  }

  @Override
  public void setLabel(@Nullable String label) {
    this.label = label;
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
  public @Nonnull BlockPos getLocation() {
    return getPos();
  }

  @Override
  public boolean isActive() {
    return false;
  }

  @Override
  protected boolean processTasks(boolean redstoneCheck) {
    return false;
  }

  @Override
  public boolean supportsMode(@Nullable EnumFacing faceHit, @Nullable IoMode mode) {
    return mode == IoMode.NONE;
  }

  @Override
  public boolean isVisible() {
    return visible;
  }

  @Override
  public void setVisible(boolean visible) {
    this.visible = visible;
  }

}
