package crazypants.enderio.integration.waila;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Provides NBT info from a TE to be sent to the client
 */
public interface IWailaNBTProvider {
  
  void getData(NBTTagCompound tag);
  
}
