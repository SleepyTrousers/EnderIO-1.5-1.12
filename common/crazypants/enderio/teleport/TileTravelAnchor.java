package crazypants.enderio.teleport;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.PacketHandler;
import crazypants.util.BlockCoord;
import crazypants.util.PacketUtil;

public class TileTravelAnchor extends TileEntity implements ITravelAccessable {

  private static final int REND_DIST_SQ = TravelSource.getMaxDistanceSq();

  enum AccessMode {
    PUBLIC,
    PRIVATE,
    PROTECTED
  }

  private AccessMode accessMode = AccessMode.PUBLIC;

  private ItemStack[] password = new ItemStack[5];

  private String placedBy;

  private List<String> authorisedUsers = new ArrayList<String>();

  public boolean canBlockBeAccessed(String playerName) {
    if(accessMode == AccessMode.PUBLIC) {
      return true;
    }
    if(accessMode == AccessMode.PRIVATE) {
      return placedBy != null && placedBy.equals(playerName);
    }
    if(placedBy != null && placedBy.equals(playerName)) {
      return true;
    }
    return authorisedUsers.contains(playerName);
  }

  @Override
  public void clearAuthorisedUsers() {
    authorisedUsers.clear();
  }

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
  public boolean getRequiresPassword(String username) {
    return getAccessMode() != AccessMode.PUBLIC && !canUiBeAccessed(username) && !authorisedUsers.contains(username);
  }

  @Override
  public boolean authoriseUser(String username, ItemStack[] password) {
    if(checkPassword(password)) {
      authorisedUsers.add(username);
      return true;
    }
    return false;
  }

  public boolean canUiBeAccessed(String playerName) {
    return placedBy != null && placedBy.equals(playerName);
  }

  public boolean canSeeBlock(String playerName) {
    if(accessMode != AccessMode.PRIVATE) {
      return true;
    }
    return placedBy != null && placedBy.equals(playerName);
  }

  public AccessMode getAccessMode() {
    return accessMode;
  }

  public void setAccessMode(AccessMode accessMode) {
    this.accessMode = accessMode;
  }

  public ItemStack[] getPassword() {
    return password;
  }

  public void setPassword(ItemStack[] password) {
    this.password = password;
  }

  public String getPlacedBy() {
    return placedBy;
  }

  public void setPlacedBy(String placedBy) {
    this.placedBy = placedBy;
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
  public void readFromNBT(NBTTagCompound root) {
    super.readFromNBT(root);
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
  public void writeToNBT(NBTTagCompound root) {
    super.writeToNBT(root);
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

  @Override
  public Packet getDescriptionPacket() {
    return PacketUtil.createTileEntityPacket(PacketHandler.CHANNEL, PacketHandler.ID_TILE_ENTITY, this);
  }

}
