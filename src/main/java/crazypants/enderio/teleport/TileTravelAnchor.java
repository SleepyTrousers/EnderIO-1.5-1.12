package crazypants.enderio.teleport;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.TileEntityEio;
import crazypants.util.BlockCoord;

public class TileTravelAnchor extends TileEntityEio implements ITravelAccessable {

  public enum AccessMode {
    PUBLIC,
    PRIVATE,
    PROTECTED
  }

  private AccessMode accessMode = AccessMode.PUBLIC;

  private ItemStack[] password = new ItemStack[5];

  private String placedBy;

  private List<String> authorisedUsers = new ArrayList<String>();

  @Override
  public boolean canBlockBeAccessed(EntityPlayer playerName) {
    if(accessMode == AccessMode.PUBLIC) {
      return true;
    }
    if(accessMode == AccessMode.PRIVATE) {
      return placedBy != null && placedBy.equals(playerName.getGameProfile().getId());
    }
    if(placedBy != null && placedBy.equals(playerName.getGameProfile().getId())) {
      return true;
    }
    return authorisedUsers.contains(playerName.getGameProfile().getId());
  }

  @Override
  public void clearAuthorisedUsers() {
    authorisedUsers.clear();
  }

  @Override
  public BlockCoord getLocation() {
    return new BlockCoord(this);
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
  public boolean getRequiresPassword(EntityPlayer username) {
    return getAccessMode() != AccessMode.PUBLIC && !canUiBeAccessed(username) && !authorisedUsers.contains(username.getGameProfile().getId());
  }

  @Override
  public boolean authoriseUser(EntityPlayer username, ItemStack[] password) {
    if(checkPassword(password)) {
      authorisedUsers.add(username.getGameProfile().getId());
      return true;
    }
    return false;
  }

  @Override
  public boolean canUiBeAccessed(EntityPlayer playerName) {
    return placedBy != null && placedBy.equals(playerName.getGameProfile().getId());
  }

  @Override
  public boolean canSeeBlock(EntityPlayer playerName) {
    if(accessMode != AccessMode.PRIVATE) {
      return true;
    }
    return placedBy != null && placedBy.equals(playerName.getGameProfile().getId());
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
  public String getPlacedBy() {
    return placedBy;
  }

  @Override
  public void setPlacedBy(EntityPlayer player) {
    if(player == null || player.getGameProfile() == null) {
      this.placedBy = null;
    } else {
      placedBy = player.getGameProfile().getId();
    }
  }

  @Override
  public boolean canUpdate() {
    return false;
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
  protected void readCustomNBT(NBTTagCompound root) {
    if(root.hasKey("accessMode")) {
      accessMode = AccessMode.values()[root.getShort("accessMode")];
    } else {
      //keep behaviour the same for blocks placed prior to this update
      accessMode = AccessMode.PUBLIC;
    }
    placedBy = root.getString("placedBy");
    for (int i = 0; i < password.length; i++) {
      if(root.hasKey("password" + i)) {
        NBTTagCompound stackRoot = (NBTTagCompound) root.getTag("password" + i);
        password[i] = ItemStack.loadItemStackFromNBT(stackRoot);
      } else {
        password[i] = null;
      }
    }
    authorisedUsers.clear();
    String userStr = root.getString("authorisedUsers");
    if(userStr != null && userStr.length() > 0) {
      String[] users = userStr.split(",");
      for (String user : users) {
        if(user != null) {
          user = user.trim();
          if(user.length() > 0) {
            authorisedUsers.add(user);
          }
        }
      }
    }
  }

  @Override
  public Packet getDescriptionPacket() {
    NBTTagCompound tag = new NBTTagCompound();
    writeCustomNBT(tag);
    return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);
  }

  @Override
  public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
    readCustomNBT(pkt.func_148857_g());
  }

  @Override
  protected void writeCustomNBT(NBTTagCompound root) {
    root.setShort("accessMode", (short) accessMode.ordinal());
    if(placedBy != null && placedBy.trim().length() > 0) {
      root.setString("placedBy", placedBy);
    }
    for (int i = 0; i < password.length; i++) {
      ItemStack stack = password[i];
      if(stack != null) {
        NBTTagCompound stackRoot = new NBTTagCompound();
        stack.writeToNBT(stackRoot);
        root.setTag("password" + i, stackRoot);
      }
    }
    StringBuffer userStr = new StringBuffer();
    for (String user : authorisedUsers) {
      if(user != null && user.trim().length() > 0) {
        userStr.append(user);
        userStr.append(",");
      }
    }
    if(authorisedUsers.size() > 0) {
      root.setString("authorisedUsers", userStr.toString());
    }
  }
}
