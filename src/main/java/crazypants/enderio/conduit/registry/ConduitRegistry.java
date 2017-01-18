package crazypants.enderio.conduit.registry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nonnull;

import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.geom.Offset;
import crazypants.enderio.conduit.geom.Offsets;
import crazypants.enderio.conduit.render.ConduitRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ConduitRegistry {

  public static class ConduitInfo {
    private final @Nonnull UUID networkUUID;
    private final @Nonnull Class<? extends IConduit> baseType;
    private final @Nonnull Offset none, x, y, z;
    private boolean canConnectToAnything;
    @SideOnly(Side.CLIENT)
    private @Nonnull Collection<ConduitRenderer> renderers;
    private @Nonnull Collection<Class<? extends IConduit>> members = new ArrayList<Class<? extends IConduit>>();

    public ConduitInfo(@Nonnull Class<? extends IConduit> baseType, @Nonnull Offset none, @Nonnull Offset x, @Nonnull Offset y, @Nonnull Offset z) {
      this.baseType = baseType;
      this.none = none;
      this.x = x;
      this.y = y;
      this.z = z;
      this.networkUUID = UUID.nameUUIDFromBytes(baseType.getName().getBytes());
    }

    public @Nonnull UUID getNetworkUUID() {
      return networkUUID;
    }

    public @Nonnull Class<? extends IConduit> getBaseType() {
      return baseType;
    }

    public void setCanConnectToAnything() {
      this.canConnectToAnything = true;
    }

    public boolean canConnectToAnything() {
      return canConnectToAnything;
    }

    @SideOnly(Side.CLIENT)
    public void addRenderer(@Nonnull ConduitRenderer renderer) {
      if (renderers == null) {
        renderers = new ArrayList<ConduitRenderer>();
      }
      renderers.add(renderer);
    }

    @SideOnly(Side.CLIENT)
    public @Nonnull Collection<ConduitRenderer> getRenderers() {
      if (renderers == null) {
        return Collections.<ConduitRenderer> emptyList();
      }
      return renderers;
    }

    public void addMember(@Nonnull Class<? extends IConduit> member) {
      members.add(member);
    }

    public @Nonnull Collection<Class<? extends IConduit>> getMembers() {
      return members;
    }

    protected @Nonnull Offset getNone() {
      return none;
    }

    protected @Nonnull Offset getX() {
      return x;
    }

    protected @Nonnull Offset getY() {
      return y;
    }

    protected @Nonnull Offset getZ() {
      return z;
    }

  }

  private static final List<ConduitInfo> conduitInfos = new ArrayList<ConduitInfo>();
  private static final Map<Class<? extends IConduit>, ConduitInfo> conduitCLassMap = new IdentityHashMap<Class<? extends IConduit>, ConduitInfo>();
  private static final Map<UUID, ConduitInfo> conduitUUIDMap = new HashMap<UUID, ConduitInfo>();

  public static void register(ConduitInfo info) {
    conduitInfos.add(info);
    Collections.sort(conduitInfos, UUID_COMPERATOR);
    conduitUUIDMap.put(info.getNetworkUUID(), info);
    conduitCLassMap.put(info.getBaseType(), info);
    for (Class<? extends IConduit> member : info.getMembers()) {
      conduitCLassMap.put(member, info);
    }

    Offset none = info.getNone(), x = info.getX(), y = info.getY(), z = info.getZ();
    while (!Offsets.registerOffsets(info.getBaseType(), none, x, y, z)) {
      z = z.next();
      if (z == null) {
        z = Offset.first();
      }
      if (z == info.getZ()) {
        y = y.next();
        if (y == null) {
          y = Offset.first();
        }
        if (y == info.getY()) {
          x = x.next();
          if (x == null) {
            x = Offset.first();
          }
          if (x == info.getX()) {
            none = none.next();
            if (none == null) {
              none = Offset.first();
            }
          }
        }
      }
      if (z == info.getZ() && y == info.getY() && x == info.getX() && none == info.getNone()) {
        throw new RuntimeException("Failed to find free offsets for " + info.getBaseType());
      }
    }
  }

  public static ConduitInfo get(IConduit conduit) {
    return conduitCLassMap.get(conduit.getClass());
  }

  public static ConduitInfo get(UUID uuid) {
    return conduitUUIDMap.get(uuid);
  }

  public static Collection<ConduitInfo> getAll() {
    return conduitInfos;
  }

  private static final Comparator<ConduitInfo> UUID_COMPERATOR = new Comparator<ConduitInfo>() {

    @Override
    public int compare(ConduitInfo o1, ConduitInfo o2) {
      return o1.getNetworkUUID().compareTo(o2.getNetworkUUID());
    }

  };

}
