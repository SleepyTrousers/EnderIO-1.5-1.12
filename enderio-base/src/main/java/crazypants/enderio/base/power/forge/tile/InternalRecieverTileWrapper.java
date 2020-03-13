package crazypants.enderio.base.power.forge.tile;

import java.util.EnumMap;
import java.util.WeakHashMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.config.config.DiagnosticsConfig;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.energy.IEnergyStorage;

public class InternalRecieverTileWrapper extends InternalPoweredTileWrapper {

  private static final @Nonnull ThreadLocal<WeakHashMap<ILegacyPoweredTile.Receiver, EnumMap<EnumFacing, IEnergyStorage>>> CACHE = NullHelper
      .notnullJ(ThreadLocal.withInitial(() -> new WeakHashMap<>()), "ThreadLocal.withInitial()");

  public static @Nullable IEnergyStorage get(@Nonnull ILegacyPoweredTile.Receiver tile, @Nullable EnumFacing facing) {
    if (facing != null && tile.canConnectEnergy(facing)) {
      EnumMap<EnumFacing, IEnergyStorage> sideMap = CACHE.get().computeIfAbsent(tile, theTile -> new EnumMap<>(EnumFacing.class));
      IEnergyStorage energyStorage = sideMap.get(facing);
      if (energyStorage == null) { // no computeIfAbsent() here as it would need to create a new Function every time to capture the tile
        sideMap.put(facing, energyStorage = getInternal(tile, facing));
      }
      return energyStorage;
    }
    return null;
  }

  private static @Nonnull IEnergyStorage getInternal(@Nonnull ILegacyPoweredTile.Receiver tile, @Nonnull EnumFacing facing) {
    switch (DiagnosticsConfig.protectEnergyOverflow.get()) {
    case NONE:
      return new InternalRecieverTileWrapper(tile, facing);
    case SOFT:
      return new LimitingRecieverTileCapabilityProvider(tile, facing);
    case HARD:
    default:
      return new ValidatingRecieverTileCapabilityProvider(tile, facing);
    }
  }

  protected final @Nonnull ILegacyPoweredTile.Receiver tile;

  public InternalRecieverTileWrapper(@Nonnull ILegacyPoweredTile.Receiver tile, @Nonnull EnumFacing facing) {
    super(tile, facing);
    this.tile = tile;
  }

  @Override
  public int receiveEnergy(int maxReceive, boolean simulate) {
    return tile.receiveEnergy(from, maxReceive, simulate);
  }

  @Override
  public boolean canReceive() {
    return true;
  }

  public static class ValidatingRecieverTileCapabilityProvider extends InternalRecieverTileWrapper {

    private long lastTick = -1L;
    private int recv = 0;
    private int cooldown = 0;

    public ValidatingRecieverTileCapabilityProvider(@Nonnull ILegacyPoweredTile.Receiver tile, @Nonnull EnumFacing facing) {
      super(tile, facing);
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
      final long serverTickCount = EnderIO.proxy.getServerTickCount();
      if (serverTickCount != lastTick) {
        lastTick = serverTickCount;
        recv = 0;
      }
      final int maxEnergyRecieved = tile.getMaxEnergyRecieved(from);
      final int receivedEnergy = super.receiveEnergy(Math.max(0, Math.min(maxReceive, maxEnergyRecieved - recv)), simulate);
      if (!simulate) {
        if (recv == 0) { // first call each tick is allowed to push anything
          recv += receivedEnergy;
        } else {
          recv += maxReceive; // we didn't take that much, but they did try to push that much, so count what they wanted to push to us
          if (recv > maxEnergyRecieved) {
            if (cooldown <= 0) {
              final BlockPos pos = tile.getLocation().offset(from);
              ((TileEntity) tile).getWorld().createExplosion(null, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, 1f, (recv / 2 > maxEnergyRecieved));
              cooldown = 30;
            } else {
              cooldown--;
            }
          }
        }
      }
      return receivedEnergy;
    }

  }

  public static class LimitingRecieverTileCapabilityProvider extends InternalRecieverTileWrapper {

    private long lastTick = -1L;
    private int recv = 0;

    public LimitingRecieverTileCapabilityProvider(@Nonnull ILegacyPoweredTile.Receiver tile, @Nonnull EnumFacing facing) {
      super(tile, facing);
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
      final long serverTickCount = EnderIO.proxy.getServerTickCount();
      if (serverTickCount != lastTick) {
        lastTick = serverTickCount;
        recv = 0;
      }
      int max = Math.min(maxReceive, tile.getMaxEnergyRecieved(from) - recv);
      final int receivedEnergy = max > 0 ? super.receiveEnergy(max, simulate) : 0;
      if (!simulate) {
        recv += receivedEnergy;
      }
      return receivedEnergy;
    }

  }

}
