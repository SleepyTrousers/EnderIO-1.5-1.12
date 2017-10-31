package crazypants.enderio.transceiver;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NullHelper;
import com.enderio.core.common.util.UserIdent;
import com.mojang.authlib.GameProfile;

import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.nbt.NBTTagCompound;

@Storable
public class Channel {

  public static Channel readFromNBT(NBTTagCompound root) {
    if (!root.hasKey("name")) {
      return null;
    }
    String name = root.getString("name");
    UserIdent user = UserIdent.readfromNbt(root, "user");
    ChannelType type = NullHelper.notnullJ(ChannelType.values()[root.getShort("type")], "Enum.values()");
    return new Channel(name, user, type);
  }

  @Store
  private final @Nonnull String name;
  @Store
  private final @Nonnull UserIdent user;
  @Store
  private final @Nonnull ChannelType type;

  public Channel(@Nonnull String name, @Nonnull GameProfile profile, @Nonnull ChannelType type) {
    this.name = name.trim();
    this.user = UserIdent.create(profile);
    this.type = type;
  }

  public Channel(@Nonnull String name, @Nonnull UserIdent user, @Nonnull ChannelType type) {
    this.name = name.trim();
    this.user = user;
    this.type = type;
  }

  public Channel(@Nonnull String name, @Nonnull ChannelType type) {
    this.name = name.trim();
    this.user = UserIdent.NOBODY;
    this.type = type;
  }

  public boolean isPublic() {
    return user == UserIdent.NOBODY;
  }

  public void writeToNBT(NBTTagCompound root) {
    if (name.isEmpty()) {
      return;
    }
    root.setString("name", name);
    user.saveToNbt(root, "user");
    root.setShort("type", (short) type.ordinal());
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + name.hashCode();
    result = prime * result + type.hashCode();
    result = prime * result + user.hashCode();
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Channel other = (Channel) obj;
    if (!getName().equals(other.getName()))
      return false;
    if (type != other.type)
      return false;
    if (!user.equals(other.user))
      return false;
    return true;
  }

  public @Nonnull String getName() {
    return name;
  }

  public @Nonnull ChannelType getType() {
    return type;
  }

  public @Nonnull UserIdent getUser() {
    return user;
  }

  @Override
  public String toString() {
    return "Channel [name=" + name + ", user=" + user + ", type=" + type + "]";
  }

}
