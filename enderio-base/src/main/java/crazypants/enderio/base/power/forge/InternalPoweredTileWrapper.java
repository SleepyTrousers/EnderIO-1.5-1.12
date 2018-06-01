package crazypants.enderio.base.power.forge;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.power.ILegacyPoweredTile;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class InternalPoweredTileWrapper implements IEnergyStorage {

  public static class PoweredTileCapabilityProvider implements ICapabilityProvider {

    private final @Nonnull ILegacyPoweredTile tile;

    public PoweredTileCapabilityProvider(@Nonnull ILegacyPoweredTile tile) {
      this.tile = tile;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
      return capability == CapabilityEnergy.ENERGY && facing != null && tile.canConnectEnergy(facing);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
      if (capability == CapabilityEnergy.ENERGY && facing != null && tile.canConnectEnergy(facing)) {
        return (T) new InternalPoweredTileWrapper(tile, facing);
      }
      return null;
    }

  }

  private final @Nonnull ILegacyPoweredTile tile;
  protected final @Nullable EnumFacing from;

  public InternalPoweredTileWrapper(@Nonnull ILegacyPoweredTile tile, @Nullable EnumFacing from) {
    this.tile = tile;
    this.from = from;
  }

  @Override
  public int getEnergyStored() {
    return tile.getEnergyStored();
  }

  @Override
  public int getMaxEnergyStored() {
    return tile.getMaxEnergyStored();
  }

  @Override
  public int receiveEnergy(int maxReceive, boolean simulate) {
    return 0;
  }

  @Override
  public int extractEnergy(int maxExtract, boolean simulate) {
    return 0;
  }

  @Override
  public boolean canExtract() {
    return false;
  }

  @Override
  public boolean canReceive() {
    return false;
  }

}
