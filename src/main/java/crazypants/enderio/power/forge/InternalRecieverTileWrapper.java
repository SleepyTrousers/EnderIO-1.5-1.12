package crazypants.enderio.power.forge;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.power.ILegacyPowerReceiver;
import net.minecraft.util.EnumFacing;
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
      if (capability == CapabilityEnergy.ENERGY) {
        return (T) new InternalRecieverTileWrapper(tile, facing);
      }
      return null;
    }

  }
  
  private final @Nonnull ILegacyPowerReceiver tile;

  public InternalRecieverTileWrapper(@Nonnull ILegacyPowerReceiver tile, @Nullable EnumFacing facing) {
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

}
