package crazypants.enderio.base.power.forge;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.config.config.DiagnosticsConfig;
import crazypants.enderio.base.power.ILegacyPowerReceiver;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;

public class InternalRecieverTileWrapper extends InternalPoweredTileWrapper {

  public static class RecieverTileCapabilityProvider extends PoweredTileCapabilityProvider {

    private final @Nonnull ILegacyPowerReceiver tile;

    public RecieverTileCapabilityProvider(@Nonnull ILegacyPowerReceiver tile) {
      super(tile);
      this.tile = tile;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
      if (capability == CapabilityEnergy.ENERGY && facing != null && tile.canConnectEnergy(facing)) {
        switch (DiagnosticsConfig.protectEnergyOverflow.get()) {
        case NONE:
          return (T) new InternalRecieverTileWrapper(tile, facing);
        case SOFT:
          return (T) new LimitingRecieverTileCapabilityProvider(tile, facing);
        case HARD:
          return (T) new ValidatingRecieverTileCapabilityProvider(tile, facing);
        }
      }
      return null;
    }

  }

  protected final @Nonnull ILegacyPowerReceiver tile;

  public InternalRecieverTileWrapper(@Nonnull ILegacyPowerReceiver tile, @Nonnull EnumFacing facing) {
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

    public ValidatingRecieverTileCapabilityProvider(@Nonnull ILegacyPowerReceiver tile, @Nonnull EnumFacing facing) {
      super(tile, facing);
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
      final int receivedEnergy = super.receiveEnergy(maxReceive, simulate);
      if (!simulate) {
        final long serverTickCount = EnderIO.proxy.getServerTickCount();
        if (serverTickCount != lastTick) {
          lastTick = serverTickCount;
          recv = receivedEnergy;
          cooldown = 0;
        } else {
          recv += receivedEnergy;
          final int maxEnergyRecieved = tile.getMaxEnergyRecieved(from);
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

    public LimitingRecieverTileCapabilityProvider(@Nonnull ILegacyPowerReceiver tile, @Nonnull EnumFacing facing) {
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
