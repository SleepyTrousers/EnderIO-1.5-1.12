package crazypants.enderio.power.tesla;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.power.ILegacyPoweredTile;
import net.darkhax.tesla.api.ITeslaHolder;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class InternalPoweredTileWrapper implements ITeslaHolder {

  public static class PoweredTileCapabilityProvider implements ICapabilityProvider {

    private final @Nonnull ILegacyPoweredTile tile;

    public PoweredTileCapabilityProvider(@Nonnull ILegacyPoweredTile tile) {
      this.tile = tile;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
      return capability == TeslaCapabilities.CAPABILITY_HOLDER;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
      if (capability == TeslaCapabilities.CAPABILITY_HOLDER) {
        return (T) new InternalPoweredTileWrapper(tile, facing);
      }
      return null;
    }

  }

  private final @Nonnull ILegacyPoweredTile tile;
  protected final EnumFacing from;

  public InternalPoweredTileWrapper(@Nonnull ILegacyPoweredTile tile, EnumFacing from) {
    this.tile = tile;
    this.from = from;
  }

  @Override
  public long getStoredPower() {
    return tile.getEnergyStored();
  }

  @Override
  public long getCapacity() {
    return tile.getMaxEnergyStored();
  }

}
