package crazypants.enderio.teleport.anchor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.PlayerUtil;

import crazypants.enderio.TileEntityEio;
import crazypants.enderio.api.teleport.ITravelAccessable;
import crazypants.enderio.api.teleport.TravelSource;
import crazypants.enderio.paint.IPaintable;
import crazypants.util.UserIdent;

public class TileTravelAnchor extends TileEntityEio implements ITravelAccessable, IPaintable.IPaintableTileEntity {

  private static final String KEY_SOURCE_BLOCK_ID = "sourceBlock";
  private static final String KEY_SOURCE_BLOCK_META = "sourceBlockMeta";
  
  protected IBlockState sourceBlock;

  private AccessMode accessMode = AccessMode.PUBLIC;

  private ItemStack[] password = new ItemStack[5];
  
  private ItemStack itemLabel;
  
  private String label;

  private @Nonnull UserIdent owner = UserIdent.nobody;

  private List<UserIdent> authorisedUsers = new ArrayList<UserIdent>();

  @Override
  public boolean canBlockBeAccessed(EntityPlayer playerName) {
    if(accessMode == AccessMode.PUBLIC) {
      return true;
    }
    // Covers protected and private access modes
    return owner.equals(playerName.getGameProfile()) || authorisedUsers.contains(playerName.getGameProfile());

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
        && !authorisedUsers.contains(PlayerUtil.getPlayerUUID(playerName.getGameProfile().getName()));
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
    return owner.equals(playerName.getGameProfile());
  }

  @Override
  public boolean canSeeBlock(EntityPlayer playerName) {
    if(accessMode != AccessMode.PRIVATE) {
      return true;
    }
    return owner.equals(playerName.getGameProfile());
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
  public void setPaintSource(IBlockState sourceBlock) {
    this.sourceBlock = sourceBlock;
  }

  @Override
  protected void readCustomNBT(NBTTagCompound root) {
    if(root.hasKey("accessMode")) {
      accessMode = AccessMode.values()[root.getShort("accessMode")];
    } else {
      //keep behavior the same for blocks placed prior to this update
      accessMode = AccessMode.PUBLIC;
    }
    if (root.hasKey("placedBy")) {
      owner = UserIdent.create(root.getString("placedBy"));
    } else {
      owner = UserIdent.readfromNbt(root, "owner");
    }
    for (int i = 0; i < password.length; i++) {
      if(root.hasKey("password" + i)) {
        NBTTagCompound stackRoot = (NBTTagCompound) root.getTag("password" + i);
        password[i] = ItemStack.loadItemStackFromNBT(stackRoot);
      } else {
        password[i] = null;
      }
    }
    authorisedUsers.clear();
    if (root.hasKey("authorisedUsers")) {
    String userStr = root.getString("authorisedUsers");
    if(userStr != null && userStr.length() > 0) {
      String[] users = userStr.split(",");
      for (String user : users) {
        if(user != null) {
          user = user.trim();
          if(user.length() > 0) {
              authorisedUsers.add(UserIdent.create(user));
          }
        }
      }
    }
    } else {
      int userIdx = 0;
      while (UserIdent.existsInNbt(root, "authorisedUser" + userIdx)) {
        UserIdent.readfromNbt(root, "authorisedUser" + userIdx);
        userIdx++;
      }
    }
    if(root.hasKey("itemLabel")) {
      NBTTagCompound stackRoot = (NBTTagCompound) root.getTag("itemLabel");
      itemLabel = ItemStack.loadItemStackFromNBT(stackRoot);
    } else {
      itemLabel = null;
    }
    
    String sourceBlockStr = root.getString(KEY_SOURCE_BLOCK_ID);
    Block block = Block.getBlockFromName(sourceBlockStr);
    int sourceBlockMetadata = root.getInteger(KEY_SOURCE_BLOCK_META);
    if(block != null) {
      sourceBlock = block.getStateFromMeta(sourceBlockMetadata);
    } else {
      sourceBlock = null;
    }
    
    label = root.getString("label");
    if(label == null || label.trim().length() == 0) {
      label = null;
    }    
  }

  @Override
  protected void writeCustomNBT(NBTTagCompound root) {
    root.setShort("accessMode", (short) accessMode.ordinal());
    owner.saveToNbt(root, "owner");
    for (int i = 0; i < password.length; i++) {
      ItemStack stack = password[i];
      if(stack != null) {
        NBTTagCompound stackRoot = new NBTTagCompound();
        stack.writeToNBT(stackRoot);
        root.setTag("password" + i, stackRoot);
      }
    }
    int userIdx = 0;
    for (UserIdent user : authorisedUsers) {
      if(user != null) {
        user.saveToNbt(root, "authorisedUser" + userIdx);
        userIdx++;
      }
    }
    if(itemLabel != null) {
      NBTTagCompound labelRoot = new NBTTagCompound();
      itemLabel.writeToNBT(labelRoot);
      root.setTag("itemLabel", labelRoot);
    }
    
    if(sourceBlock != null) {
      root.setString(KEY_SOURCE_BLOCK_ID, Block.blockRegistry.getNameForObject(sourceBlock.getBlock()).toString());
      root.setInteger(KEY_SOURCE_BLOCK_META, sourceBlock.getBlock().getMetaFromState(sourceBlock));
    }
    
    
    if(label != null && label.trim().length() > 0) {
      root.setString("label", label);
    }
    
  }
    
  @Override
  public Packet<?> getDescriptionPacket() {
    NBTTagCompound tag = new NBTTagCompound();
    writeCustomNBT(tag);
    return new S35PacketUpdateTileEntity(getPos(), 1, tag);
  }

  @Override
  public BlockCoord getLocation() {
    return new BlockCoord(pos);
  }
  
}
