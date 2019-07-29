package crazypants.enderio.base.invpanel.capability;

import java.util.concurrent.Callable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.events.EnderIOLifecycleEvent;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public class CapabilityDatabaseHandler {

  @SuppressWarnings("null")
  @CapabilityInject(IDatabaseHandler.class)
  @Nonnull
  public static Capability<IDatabaseHandler> DATABASE_HANDLER_CAPABILITY = null;

  @SubscribeEvent
  public static void create(EnderIOLifecycleEvent.PreInit event) {
    CapabilityManager.INSTANCE.register(IDatabaseHandler.class, new Storage(), new Factory());

    NullHelper.notnullJ(DATABASE_HANDLER_CAPABILITY, "Database Handler Capability is not registered");
  }

  private static class Storage implements Capability.IStorage<IDatabaseHandler> {

    @Override
    @Nullable
    public NBTBase writeNBT(Capability<IDatabaseHandler> capability, IDatabaseHandler instance, EnumFacing side) {
      return null;
    }

    @Override
    public void readNBT(Capability<IDatabaseHandler> capability, IDatabaseHandler instance, EnumFacing side, NBTBase nbt) {
    }

  }

  private static class Factory implements Callable<IDatabaseHandler> {

    @Override
    public IDatabaseHandler call() throws Exception {
      return null;
    }

  }

}
