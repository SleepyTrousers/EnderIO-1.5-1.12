package crazypants.enderio.conduits.conduit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.conduit.ConnectionMode;
import crazypants.enderio.base.conduit.IClientConduit;
import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.conduit.IConduitBundle;
import crazypants.enderio.base.conduit.RaytraceResult;
import crazypants.enderio.base.conduit.geom.CollidableCache;
import crazypants.enderio.base.conduit.geom.CollidableCache.CacheKey;
import crazypants.enderio.base.conduit.geom.CollidableComponent;
import crazypants.enderio.base.conduit.geom.ConduitGeometryUtil;
import crazypants.enderio.conduits.render.BlockStateWrapperConduitBundle;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class AbstractClientConduit implements IClientConduit.WithDefaultRendering {

  public static final float TRANSMISSION_SCALE = 0.3f;

  protected final @Nonnull Set<EnumFacing> conduitConnections = EnumSet.noneOf(EnumFacing.class);

  protected final @Nonnull Set<EnumFacing> externalConnections = EnumSet.noneOf(EnumFacing.class);

  protected final @Nonnull List<CollidableComponent> collidables = new ArrayList<CollidableComponent>();

  protected final @Nonnull EnumMap<EnumFacing, ConnectionMode> conectionModes = new EnumMap<EnumFacing, ConnectionMode>(EnumFacing.class);

  protected @Nullable IConduitBundle bundle;

  protected AbstractClientConduit() {
  }

  @Override
  @Nonnull
  public ConnectionMode getConnectionMode(@Nonnull EnumFacing dir) {
    ConnectionMode res = conectionModes.get(dir);
    if (res == null) {
      return getDefaultConnectionMode();
    }
    return res;
  }

  @Nonnull
  protected ConnectionMode getDefaultConnectionMode() {
    return ConnectionMode.IN_OUT;
  }

  @Override
  public boolean haveCollidablesChangedSinceLastCall() {
    return false;
  }

  @Override
  public void setBundle(@Nullable IConduitBundle tileConduitBundle) {
    bundle = tileConduitBundle;
  }

  @Override
  @Nonnull
  public IConduitBundle getBundle() {
    return NullHelper.notnull(bundle, "Logic error in conduit---no bundle set");
  }

  // Connections
  @Override
  @Nonnull
  public Set<EnumFacing> getConduitConnections() {
    return conduitConnections;
  }

  @Override
  public boolean containsConduitConnection(@Nonnull EnumFacing dir) {
    return conduitConnections.contains(dir);
  }

  @Override
  public boolean canConnectToExternal(@Nonnull EnumFacing direction, boolean ignoreConnectionMode) {
    return false;
  }

  @Override
  @Nonnull
  public Set<EnumFacing> getExternalConnections() {
    return externalConnections;
  }

  @Override
  public boolean hasExternalConnections() {
    return !externalConnections.isEmpty();
  }

  @Override
  public boolean hasConduitConnections() {
    return !conduitConnections.isEmpty();
  }

  @Override
  public boolean containsExternalConnection(@Nonnull EnumFacing dir) {
    return externalConnections.contains(dir);
  }

  @Override
  public boolean isActive() {
    return false;
  }

  @Override
  public void readFromNBT(@Nonnull NBTTagCompound conduitBody) {
    conduitConnections.clear();
    int[] dirs = conduitBody.getIntArray("connections");
    for (int i = 0; i < dirs.length; i++) {
      conduitConnections.add(EnumFacing.values()[dirs[i]]);
    }

    externalConnections.clear();
    dirs = conduitBody.getIntArray("externalConnections");
    for (int i = 0; i < dirs.length; i++) {
      externalConnections.add(EnumFacing.values()[dirs[i]]);
    }

    conectionModes.clear();
    byte[] modes = conduitBody.getByteArray("conModes");
    if (modes.length == 6) {
      int i = 0;
      for (EnumFacing dir : EnumFacing.VALUES) {
        conectionModes.put(dir, ConnectionMode.values()[modes[i]]);
        i++;
      }
    }

    CollidableCache cc = CollidableCache.instance;
    for (EnumFacing dir : EnumFacing.VALUES) {
      if (dir != null && isConnectedTo(dir) && getConnectionMode(dir) != ConnectionMode.DISABLED) {
        collidables.addAll(cc.getCollidables(cc.createKey(getCollidableType(), getBundle().getOffset(getBaseConduitType(), dir), dir), this));
      }
    }
  }

  @Override
  public int getLightValue() {
    return 0;
  }

  @Override
  public boolean onBlockActivated(@Nonnull EntityPlayer player, @Nonnull EnumHand hand, @Nonnull RaytraceResult res, @Nonnull List<RaytraceResult> all) {
    return false;
  }

  @Override
  public float getSelfIlluminationForState(@Nonnull CollidableComponent component) {
    return isActive() ? 1 : 0;
  }

  @Override
  public float getTransmitionGeometryScale() {
    return TRANSMISSION_SCALE;
  }

  @Override
  @Nonnull
  public Collection<CollidableComponent> createCollidables(@Nonnull CacheKey key) {
    return NullHelper.notnullJ(Collections.singletonList(
        new CollidableComponent(getCollidableType(), ConduitGeometryUtil.instance.getBoundingBox(getBaseConduitType(), key.dir, key.offset), key.dir, null)),
        "Collections#singletonList");
  }

  @Override
  @Nonnull
  public Class<? extends IConduit> getCollidableType() {
    return getBaseConduitType();
  }

  @Override
  @Nonnull
  public List<CollidableComponent> getCollidableComponents() {
    return collidables;
  }

  @Override
  public boolean shouldMirrorTexture() {
    return true;
  }

  @SideOnly(Side.CLIENT)
  public void hashCodeForModelCaching(BlockStateWrapperConduitBundle.ConduitCacheKey hashCodes) {
    hashCodes.add(this.getClass());
    hashCodes.add(conduitConnections, externalConnections, conectionModes);
  }

}
