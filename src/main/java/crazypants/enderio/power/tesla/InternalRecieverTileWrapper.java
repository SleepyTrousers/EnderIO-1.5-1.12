package crazypants.enderio.power.tesla;

import crazypants.enderio.power.IInternalPowerReceiver;
import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class InternalRecieverTileWrapper implements ITeslaConsumer {
  
  
  public static class RecieverTileCapabilityProvider implements ICapabilityProvider {

    private final IInternalPowerReceiver tile;

    public RecieverTileCapabilityProvider(IInternalPowerReceiver tile) {
      this.tile = tile;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
      if (capability == TeslaCapabilities.CAPABILITY_CONSUMER) {
        return (T) new InternalRecieverTileWrapper(tile, facing);
      }
      return null;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
      return capability == TeslaCapabilities.CAPABILITY_CONSUMER;
    }

  }
  
  private final IInternalPowerReceiver tile;
  private final EnumFacing facing;

  public InternalRecieverTileWrapper(IInternalPowerReceiver tile, EnumFacing facing) {
    this.tile = tile;
    this.facing = facing;
  }

  @Override
  public long givePower(long power, boolean simulated) {
    return tile.receiveEnergy(facing, (int)power, simulated);
  }

 

}
