package crazypants.enderio.teleport;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
  
  private ItemStack itemLabel;

  private String placedBy;

  private List<String> authorisedUsers = new ArrayList<String>();

  @Override
  public boolean canBlockBeAccessed(EntityPlayer playerName) {
    if(accessMode == AccessMode.PUBLIC) {
      return true;
    }
    if(accessMode == AccessMode.PRIVATE) {
      return placedBy != null && placedBy.equals(playerName.getGameProfile().getName());
    }
    if(placedBy != null && placedBy.equals(playerName.getGameProfile().getName())) {
      return true;
    }
    return authorisedUsers.contains(playerName.getGameProfile().getName());
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
    return getAccessMode() != AccessMode.PUBLIC && !canUiBeAccessed(username) && !authorisedUsers.contains(username.getGameProfile().getName());
  }

  @Override
  public boolean authoriseUser(EntityPlayer username, ItemStack[] password) {
    if(checkPassword(password)) {
      authorisedUsers.add(username.getGameProfile().getName());
      return true;
    }
    return false;
  }

  @Override
  public boolean canUiBeAccessed(EntityPlayer playerName) {
    return placedBy != null && placedBy.equals(playerName.getGameProfile().getName());
  }

  @Override
  public boolean canSeeBlock(EntityPlayer playerName) {
    if(accessMode != AccessMode.PRIVATE) {
      return true;
    }
    return placedBy != null && placedBy.equals(playerName.getGameProfile().getName());
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

  public ItemStack getItemLabel() {
    return itemLabel;
  }

  public void setItemLabel(ItemStack lableIcon) {
    this.itemLabel = lableIcon;
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
      placedBy = player.getGameProfile().getName();
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
      //keep behavior the same for blocks placed prior to this update
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
    if(root.hasKey("itemLabel")) {
      NBTTagCompound stackRoot = (NBTTagCompound) root.getTag("itemLabel");
      itemLabel = ItemStack.loadItemStackFromNBT(stackRoot);
    } else {
      itemLabel = null;
    }
    
  }

  @Override
  protected void writeCustomNBT(NBTTagCompound root) {
    root.setShort("accessMode", (short) accessMode.ordinal());
    if(placedBy != null && !placedBy.trim().isEmpty()) {
      root.setString("placedBy", placedBy.toString());
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
      if(user != null) {
        userStr.append(user.toString());
        userStr.append(",");
      }
    }
    if(authorisedUsers.size() > 0) {
      root.setString("authorisedUsers", userStr.toString());
    }
    if(itemLabel != null) {
      NBTTagCompound labelRoot = new NBTTagCompound();
      itemLabel.writeToNBT(labelRoot);
      root.setTag("itemLabel", labelRoot);
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
}
