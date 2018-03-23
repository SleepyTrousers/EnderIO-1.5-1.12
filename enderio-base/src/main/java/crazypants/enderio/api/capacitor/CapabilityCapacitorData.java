package crazypants.enderio.api.capacitor;

import javax.annotation.Nonnull;

import crazypants.enderio.base.capacitor.DefaultCapacitorData;
import crazypants.enderio.base.capacitor.ICapacitorData;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public final class CapabilityCapacitorData {
  
  @CapabilityInject(ICapacitorData.class)
  public static final Capability<ICapacitorData> INSTANCE = null;
  
  public static void register() {
    // TODO default IStorage ?
    CapabilityManager.INSTANCE.register(ICapacitorData.class, new IStorage<ICapacitorData>() {
      @Override
      public NBTBase writeNBT(Capability<ICapacitorData> capability, ICapacitorData instance, EnumFacing side) {
        return null;
      }

      @Override
      public void readNBT(Capability<ICapacitorData> capability, ICapacitorData instance, EnumFacing side, NBTBase nbt) {}
    }, () -> DefaultCapacitorData.NONE);
  }
  
  public static @Nonnull Capability<ICapacitorData> getCapNN() {
    Capability<ICapacitorData> cap = INSTANCE;
    if (cap == null) {
      throw new IllegalStateException("Capacitor data capability not loaded!");
    }
    return cap;
  }

  private CapabilityCapacitorData() {}
  
  

}
