package crazypants.enderio.power.tesla;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.power.ILegacyPowerReceiver;
import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class InternalRecieverTileWrapper implements ITeslaConsumer {

  public static class RecieverTileCapabilityProvider implements ICapabilityProvider {

    private final @Nonnull ILegacyPowerReceiver tile;

    public RecieverTileCapabilityProvider(@Nonnull ILegacyPowerReceiver tile) {
      this.tile = tile;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
      if (capability == TeslaCapabilities.CAPABILITY_CONSUMER) {
        return (T) new InternalRecieverTileWrapper(tile, facing);
      }
      return null;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
      return capability == TeslaCapabilities.CAPABILITY_CONSUMER;
    }

  }

  private final @Nonnull ILegacyPowerReceiver tile;
  private final EnumFacing facing;

  public InternalRecieverTileWrapper(@Nonnull ILegacyPowerReceiver tile, EnumFacing facing) {
    this.tile = tile;
    this.facing = facing;
  }

  @Override
  public long givePower(long power, boolean simulated) {
    return tile.receiveEnergy(facing, (int) power, simulated);
  }

}
