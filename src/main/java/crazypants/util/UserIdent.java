package crazypants.util;

import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.UsernameCache;

import com.enderio.core.common.util.PlayerUtil;
import com.google.common.base.Charsets;
import com.mojang.authlib.GameProfile;

import crazypants.enderio.Log;

public class UserIdent {
  private static final String NONE_MARKER = "none";
  private final @Nullable UUID uuid;
  private final @Nonnull UUID uuid_offline;
  private final @Nonnull String playerName;

  public @Nonnull String getPlayerName() {
    if (uuid != null) {
      String lastKnownName = UsernameCache.getLastKnownUsername(uuid);
      if (lastKnownName != null) {
        return lastKnownName;
      }
    }
    return playerName;
  }

  public UUID getUUID() {
    return uuid != null ? uuid : uuid_offline;
  }

  @SuppressWarnings("null")
  // it's final(!), eclipse...
  public String getUUIDString() {
    return uuid != null ? uuid.toString() : NONE_MARKER;
  }

  /**
   * Create a UserIdent from a UUID object and a name. Use this when reading
   * stored data, it will check for username changes, implement them and write a
   * log message.
   */
  public static @Nonnull UserIdent create(@Nullable UUID uuid, @Nullable String playerName) {
    if (uuid != null) {
      if (nobody.equals(uuid)) {
        return nobody;
      }
      if (playerName != null) {
        String lastKnownName = UsernameCache.getLastKnownUsername(uuid);
        if (lastKnownName != null && !lastKnownName.equals(playerName)) {
          Log.warn("The user with the UUID " + uuid + " changed name from '" + playerName + "' to '" + lastKnownName + "'");
          return new UserIdent(uuid, lastKnownName);
        }
      }
      return new UserIdent(uuid, playerName);
    } else if (playerName != null) {
      return new UserIdent(null, playerName);
    } else {
      return nobody;
    }
  }

  /**
   * Create a UserIdent from a UUID string and a name. Use this when reading
   * stored data, it will check for username changes, implement them and write a
   * log message.
   */
  public static @Nonnull UserIdent create(@Nonnull String suuid, @Nullable String playerName) {
    if (NONE_MARKER.equals(suuid)) {
      return new UserIdent(null, playerName);
    }
    try {
      UUID uuid = UUID.fromString(suuid);
      if (nobody.equals(uuid)) {
        return nobody;
      }
      return create(uuid, playerName);
    } catch (IllegalArgumentException e) {
      return nobody;
    }
  }

  /**
   * Create a UserIdent from a legacy string. The string can either be a UUID or
   * a player name. Use this when reading legacy data or user configured values.
   */
  public static @Nonnull UserIdent create(@Nullable String legacyData) {
    UUID uuid = PlayerUtil.getPlayerUIDUnstable(legacyData);
    if (uuid != null) {
      return new UserIdent(uuid, legacyData);
    } else if (legacyData != null) {
      return new UserIdent(null, legacyData);
    } else {
      return nobody;
    }
  }

  /**
   * Create a UserIdent from a GameProfile. Use this when creating a UserIdent
   * for a currently active player.
   */
  public static @Nonnull UserIdent create(@Nullable GameProfile gameProfile) {
    if (gameProfile != null && (gameProfile.getId() != null || gameProfile.getName() != null)) {
      if (gameProfile.getId() != null && gameProfile.getName() != null
          && gameProfile.getId().equals(offlineUUID(gameProfile.getName()))) {
        return new UserIdent(null, gameProfile.getName());
      } else {
        return new UserIdent(gameProfile.getId(), gameProfile.getName());
      }
    } else {
      return nobody;
    }
  }

  private static @Nonnull UUID offlineUUID(String playerName) {
    UUID result = UUID.nameUUIDFromBytes(("OfflinePlayer:" + playerName).getBytes(Charsets.UTF_8));
    return result != null ? result : new UUID(-1, -1);
  }

  private UserIdent(UUID uuid, String playerName) {
    this.uuid = uuid;
    this.uuid_offline = offlineUUID(playerName);
    this.playerName = playerName != null ? playerName : "[" + uuid + "]";
  }

  @SuppressWarnings("null")
  // it's final(!), eclipse...
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + playerName.hashCode();
    result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
    return result;
  }

  /**
   * Please note that a UserIdent will successfully equal against GameProfiles
   * and UUIDs.
   */
  @SuppressWarnings("null")
  // it's final(!), eclipse...
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof UserIdent) {
      UserIdent other = (UserIdent) obj;
      if (this.uuid != null && other.uuid != null) {
        return this.uuid.equals(other.uuid);
      }
      return this.uuid_offline.equals(other.uuid_offline);
    } else if (obj instanceof GameProfile) {
      GameProfile other = (GameProfile) obj;
      UUID other_uuid = other.getId();
      if (this.uuid != null && other_uuid != null) {
        return this.uuid.equals(other_uuid);
      }
      UUID uuid_offline_other = offlineUUID(other.getName());
      return uuid_offline_other.equals(this.uuid) || this.uuid_offline.equals(uuid_offline_other);
    } else if (obj instanceof UUID) {
      return obj.equals(uuid) || obj.equals(uuid_offline);
    }
    return false;
  }

  @SuppressWarnings("null")
  // it's final(!), eclipse...
  public void saveToNbt(NBTTagCompound nbt, String prefix) {
    if (uuid != null) {
      nbt.setString(prefix + ".uuid", uuid.toString());
    }
    nbt.setString(prefix + ".login", playerName);
  }

  public static boolean existsInNbt(NBTTagCompound nbt, String prefix) {
    return nbt.hasKey(prefix + ".uuid") || nbt.hasKey(prefix + ".login");
  }

  public static @Nonnull UserIdent readfromNbt(NBTTagCompound nbt, String prefix) {
    String suuid = nbt.getString(prefix + ".uuid");
    String login = nbt.getString(prefix + ".login");
    if (Nobody.NOBODY_MARKER.equals(suuid)) {
      return nobody;
    }
    try {
      UUID uuid = UUID.fromString(suuid);
      return create(uuid, login);
    } catch (IllegalArgumentException e) {
      if (login != null && !login.isEmpty()) {
        return new UserIdent(null, login);
      } else {
        return nobody;
      }
    }
  }

  @Override
  public String toString() {
    return "User [uuid=" + (uuid != null ? uuid : "(unknown)") + ", name=" + playerName + "]";
  }

  public static final @Nonnull Nobody nobody = new Nobody();

  private static class Nobody extends UserIdent {
    private static final String NOBODY_MARKER = "nobody";

    private Nobody() {
      super(null, "[unknown player]");
    }

    @Override
    public boolean equals(Object obj) {
      return this == obj;
    }

    @Override
    public void saveToNbt(NBTTagCompound nbt, String prefix) {
      nbt.setString(prefix + ".uuid", NOBODY_MARKER);
    }

  }

}