package crazypants.enderio.api.redstone;

import javax.annotation.Nonnull;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.events.EnderIOLifecycleEvent;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public final class CapabilityBundledRedstone {

  @CapabilityInject(IBundledRedstone.class)
  public static final Capability<IBundledRedstone> INSTANCE = null;

  @SubscribeEvent
  public static void register(EnderIOLifecycleEvent.PreInit event) {
    CapabilityManager.INSTANCE.register(IBundledRedstone.class, new IStorage<IBundledRedstone>() {
      @Override
      public NBTBase writeNBT(Capability<IBundledRedstone> capability, IBundledRedstone instance, EnumFacing side) {
        return null;
      }

      @Override
      public void readNBT(Capability<IBundledRedstone> capability, IBundledRedstone instance, EnumFacing side, NBTBase nbt) {
      }
    }, () -> new IBundledRedstone() {
    });
  }

  public static @Nonnull Capability<IBundledRedstone> getCapNN() {
    Capability<IBundledRedstone> cap = INSTANCE;
    if (cap == null) {
      throw new IllegalStateException("Bundled Redstone capability not loaded!");
    }
    return cap;
  }

  private CapabilityBundledRedstone() {
  }

}
