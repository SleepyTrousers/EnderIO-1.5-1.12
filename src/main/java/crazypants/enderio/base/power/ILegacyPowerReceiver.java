package crazypants.enderio.base.power;

import net.minecraft.util.EnumFacing;

public interface ILegacyPowerReceiver extends ILegacyPoweredTile {

  int getMaxEnergyRecieved(EnumFacing dir);

  int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate);

}
