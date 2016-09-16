package crazypants.enderio.power;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;

public class PowerHandlerRecieverTile extends PowerHandlerPoweredTile {
  
  
  public static class RecieverTileCapabilityProvider extends PoweredTileCapabilityProvider {

    private final IInternalPowerReceiver tile;

    public RecieverTileCapabilityProvider(IInternalPowerReceiver tile) {
      super(tile);
      this.tile = tile;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
      if (capability == CapabilityEnergy.ENERGY) {
        return (T) new PowerHandlerRecieverTile(tile, facing);
      }
      return null;
    }

  }
  
  private final IInternalPowerReceiver tile;

  public PowerHandlerRecieverTile(IInternalPowerReceiver tile, EnumFacing facing) {
    super(tile, facing);
    this.tile = tile;
  }

  @Override
  public int receiveEnergy(int maxReceive, boolean simulate) {
    return tile.receiveEnergy(from, maxReceive, simulate);
  }

  @Override
  public int extractEnergy(int maxExtract, boolean simulate) {
    return 0;
  }

  @Override
  public boolean canReceive() {
    return true;
  }

}
