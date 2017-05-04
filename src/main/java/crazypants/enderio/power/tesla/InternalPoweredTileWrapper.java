package crazypants.enderio.power.tesla;

import javax.annotation.Nullable;

import crazypants.enderio.power.IInternalPoweredTile;
import net.darkhax.tesla.api.ITeslaHolder;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class InternalPoweredTileWrapper implements ITeslaHolder {

  
  public static class PoweredTileCapabilityProvider implements ICapabilityProvider {

    private final IInternalPoweredTile tile;

    public PoweredTileCapabilityProvider(IInternalPoweredTile tile) {
      this.tile = tile;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
      return capability == TeslaCapabilities.CAPABILITY_HOLDER;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
      if (capability == TeslaCapabilities.CAPABILITY_HOLDER) {
        return (T) new InternalPoweredTileWrapper(tile, facing);
      }
      return null;
    }

  }
  
  private final IInternalPoweredTile tile;
  protected final EnumFacing from;
  
  public InternalPoweredTileWrapper(IInternalPoweredTile tile, EnumFacing from) {
    this.tile = tile;
    this.from = from;
  }

  @Override
  public long getStoredPower() {
    return tile.getEnergyStored(from);
  }

  @Override
  public long getCapacity() {
    return tile.getMaxEnergyStored(from);
  }

 
  
  
  
}
