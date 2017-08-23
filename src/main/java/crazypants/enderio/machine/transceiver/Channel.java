package crazypants.enderio.machine.transceiver;

import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;

import com.enderio.core.common.util.PlayerUtil;
import com.mojang.authlib.GameProfile;

import crazypants.util.UserIdent;


public class Channel {
 
  public static Channel readFromNBT(NBTTagCompound root) {
    if(!root.hasKey("name")) {
      return null;
    }
    String name = root.getString("name");
    UserIdent user;
    if(root.hasKey("user")) {
      String legacyUser = root.getString("user");
      user = UserIdent.create(legacyUser);
    } else {
      user = UserIdent.readfromNbt(root, "user");
    }
    ChannelType type = ChannelType.values()[root.getShort("type")];
    return new Channel(name, user, type);
  }
  
  private final String name;
  private final UserIdent user;
  final ChannelType type;

  public Channel(String name, GameProfile profile, ChannelType type) {
    this.name = trim(name);
    this.user = UserIdent.create(profile);
    this.type = type;
  }

  public Channel(String name, UserIdent user, ChannelType type) {
    this.name = trim(name);
    this.user = user;
    this.type = type;
  }

  public Channel(String name, ChannelType type) {
    this.name = trim(name);
    this.user = UserIdent.nobody;
    this.type = type;
  }

  public boolean isPublic() {
    return user == UserIdent.nobody;
  }

  public void writeToNBT(NBTTagCompound root) {
    if(name == null || name.isEmpty()) {
      return;
    } 
    root.setString("name", name);
    user.saveToNbt(root, "user");
    root.setShort("type", (short)type.ordinal());
  }
  
  private String trim(String str) {
    if(str == null) {
      return null;
    }
    str = str.trim();
    if(str.isEmpty()) {
      return null;
    }
    return str;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    result = prime * result + ((user == null) ? 0 : user.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if(this == obj)
      return true;
    if(obj == null)
      return false;
    if(getClass() != obj.getClass())
      return false;
    Channel other = (Channel) obj;
    if(getName() == null) {
      if(other.getName() != null)
        return false;
    } else if(!getName().equals(other.getName()))
      return false;
    if(type != other.type)
      return false;
    if(user == null) {
      if(other.user != null)
        return false;
    } else if(!user.equals(other.user))
      return false;
    return true;
  }

  public String getName() {
    return name;
  }

  public ChannelType getType() {
    return type;
  }

  public UserIdent getUser() {
    return user;
  }

  @Override
  public String toString() {
    return "Channel [name=" + name + ", user=" + user + ", type=" + type + "]";
  }

}
