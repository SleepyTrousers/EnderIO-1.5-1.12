package crazypants.enderio.base.transceiver;

import java.util.concurrent.Callable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ServerChannelRegister extends ChannelRegister implements ICapabilityProvider, IServerChannelRegister {

  public static ServerChannelRegister instance = new ServerChannelRegister();

  private final static @Nonnull ResourceLocation CAP_KEY = new ResourceLocation(EnderIO.DOMAIN, "channels");

  private long gen = 0;

  @SubscribeEvent
  public static void onWorldCaps(AttachCapabilitiesEvent<World> event) {
    event.addCapability(CAP_KEY, instance);
  }

  @Override
  public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
    return capability == SERVER_REGISTER;
  }

  @SuppressWarnings("unchecked")
  @Override
  @Nullable
  public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
    return (T) (capability == SERVER_REGISTER ? this : null);
  }

  @CapabilityInject(IServerChannelRegister.class)
  public static Capability<IServerChannelRegister> SERVER_REGISTER = null;

  public static void init(@Nonnull FMLPreInitializationEvent event) {
    CapabilityManager.INSTANCE.register(IServerChannelRegister.class, new IStorage<IServerChannelRegister>() {
      @Override
      public NBTBase writeNBT(Capability<IServerChannelRegister> capability, IServerChannelRegister instanceIn, EnumFacing side) {
        return instanceIn.serializeNBT();
      }

      @Override
      public void readNBT(Capability<IServerChannelRegister> capability, IServerChannelRegister instanceIn, EnumFacing side, NBTBase nbt) {
        instanceIn.deserializeNBT(nbt);
      }
    }, new Callable<IServerChannelRegister>() {
      @Override
      public IServerChannelRegister call() throws Exception {
        return ServerChannelRegister.instance;
      }
    });
    MinecraftForge.EVENT_BUS.register(ServerChannelRegister.class);
  }

  @Override
  public NBTBase serializeNBT() {
    NBTTagCompound nbt = new NBTTagCompound();
    NBTTagList list = new NBTTagList();

    long genOut = MinecraftServer.getCurrentTimeMillis();
    nbt.setLong("gen", genOut);

    for (Channel chan : channels.values()) {
      NBTTagCompound tag = new NBTTagCompound();
      chan.writeToNBT(tag);
      list.appendTag(tag);
    }
    nbt.setTag("list", list);

    Log.debug("Saving ServerChannelRegister with gen=" + genOut);
    return nbt;
  }

  @Override
  public void deserializeNBT(NBTBase nbtIn) {
    if (nbtIn instanceof NBTTagCompound) {
      NBTTagCompound nbt = (NBTTagCompound) nbtIn;
      long genIn = nbt.getLong("gen");
      if (genIn > gen) {
        Log.debug("Reading ServerChannelRegister with gen=" + genIn);
        reset();
        gen = genIn;
        NBTTagList list = nbt.getTagList("list", nbt.getId());
        for (int i = 0; i < list.tagCount(); i++) {
          NBTTagCompound tag = list.getCompoundTagAt(i);
          Channel channel = Channel.readFromNBT(tag);
          if (channel != null) {
            addChannel(channel);
          }
        }
      } else {
        Log.debug("Ignoring ServerChannelRegister with gen=" + genIn);
      }
    }

  }

  @Override
  public void reset() {
    super.reset();
    gen = 0;
  }

}
