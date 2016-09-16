package crazypants.enderio.power;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class PowerHandlerPoweredTile implements IEnergyStorage {

  
  public static class PoweredTileCapabilityProvider implements ICapabilityProvider {

    private final IInternalPoweredTile tile;

    public PoweredTileCapabilityProvider(IInternalPoweredTile tile) {
      this.tile = tile;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
      return capability == CapabilityEnergy.ENERGY;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
      if (capability == CapabilityEnergy.ENERGY) {
        return (T) new PowerHandlerPoweredTile(tile, facing);
      }
      return null;
    }

  }
  
  private final IInternalPoweredTile tile;
  protected final EnumFacing from;
  
  public PowerHandlerPoweredTile(IInternalPoweredTile tile, EnumFacing from) {
    this.tile = tile;
    this.from = from;
  }

  @Override
  public int getEnergyStored() {
    return tile.getEnergyStored(from);
  }

  @Override
  public int getMaxEnergyStored() {
    return tile.getEnergyStored(from);
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
