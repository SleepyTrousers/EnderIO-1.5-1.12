package crazypants.enderio.machine.painter.paint;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.Nullable;

import crazypants.enderio.EnderIO;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.paint.PainterUtil2;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;

public class PaintRegister implements IPaintRegister, ICapabilityProvider, INBTSerializable<NBTBase> {

  @CapabilityInject(IPaintRegister.class)
  public static Capability<IPaintRegister> CAP = null;

  // STATIC INIT

  public static void register() {
    CapabilityManager.INSTANCE.register(IPaintRegister.class, new IStorage<IPaintRegister>() {
      @Override
      public NBTBase writeNBT(Capability<IPaintRegister> capability, IPaintRegister instance, EnumFacing side) {
        return instance.serializeNBT();
      }

      @Override
      public void readNBT(Capability<IPaintRegister> capability, IPaintRegister instance, EnumFacing side, NBTBase nbt) {
        instance.deserializeNBT(nbt);
      }
    }, new Callable<IPaintRegister>() {
      @Override
      public IPaintRegister call() throws Exception {
        return new PaintRegister(false);
      }
    });
    MinecraftForge.EVENT_BUS.register(PaintRegister.class);
    PacketHandler.INSTANCE.registerMessage(PacketWorldPaintUpdate.Handler.class, PacketWorldPaintUpdate.class, PacketHandler.nextID(), Side.CLIENT);
  }

  // STATIC EVENT HANDLERS

  @SubscribeEvent
  public static void attach(AttachCapabilitiesEvent<World> event) {
    event.addCapability(new ResourceLocation(EnderIO.DOMAIN, "PaintRegistry"), new PaintRegister(!event.getObject().isRemote));
  }

  // constructor

  private final boolean isServer;

  private PaintRegister(boolean isServer) {
    this.isServer = isServer;
  }

  // ICapabilityProvider

  @Override
  public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
    return capability == CAP;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
    return capability == CAP ? (T) this : null;
  }

  // INBTSerializable<NBTBase>

  @Override
  public NBTBase serializeNBT() {
    NBTTagList tag = new NBTTagList();
    final ReentrantLock lock = data.getLock();
    lock.lock();
    try {
      data.cow();
      for (Entry<BlockPos, IBlockState> elem : data.entrySet()) {
        if (elem.getValue() != null) {
          NBTTagCompound subtag = new NBTTagCompound();
          subtag.setLong("pos", elem.getKey().toLong());
          PainterUtil2.writeNbt(subtag, elem.getValue());
          tag.appendTag(subtag);
        }
      }
      System.out.println("Wrote " + data.entrySet().size() + " paints");
    } finally {
      lock.unlock();
    }
    return tag;
  }

  @Override
  public void deserializeNBT(NBTBase nbt) {
    final ReentrantLock lock = data.getLock();
    lock.lock();
    try {
      data.clear();
      if (nbt instanceof NBTTagList) {
        NBTTagList tag = (NBTTagList) nbt;
        for (int i = 0; i < tag.tagCount(); i++) {
          NBTBase nbtBase = tag.get(i);
          if (nbtBase instanceof NBTTagCompound) {
            IBlockState blockState = PainterUtil2.readNbt((NBTTagCompound) nbtBase);
            if (blockState != null && ((NBTTagCompound) nbtBase).hasKey("pos")) {
              long pos = ((NBTTagCompound) nbtBase).getLong("pos");
              data.put(BlockPos.fromLong(pos), blockState);
            }
          }
        }
      }
      System.out.println("Read " + data.entrySet().size() + " paints");
    } finally {
      lock.unlock();
    }
  }

  // IPaintRegistry - Real Work

  private final CopyOnWriteHashMap<BlockPos, IBlockState> data = new CopyOnWriteHashMap<BlockPos, IBlockState>();

  @Override
  public IBlockState getPaintSource(BlockPos pos) {
    return data.get(pos);
  }

  private final Set<BlockPos> updates = new HashSet<BlockPos>();

  @Override
  public void setPaintSource(BlockPos pos, @Nullable IBlockState paintSource) {
    if (paintSource == null) {
      data.remove(pos);
    } else {
      data.put(pos.toImmutable(), paintSource);
    }
    if (isServer) {
      updates.add(pos);
    }
  }

  @Override
  public void resetClient() {
    if (!isServer) {
      data.clear();
    }
  }

  // static access helpers

  public static IBlockState getPaintSource(IBlockAccess world, BlockPos pos) {
    IPaintRegister capability = getCapability(world);
    if (capability != null) {
      return capability.getPaintSource(pos);
    }
    return null;
  }

  private static IPaintRegister getCapability(IBlockAccess worldIn) {
    World world = null;
    if (worldIn instanceof World) {
      world = (World) worldIn;
    } else if (worldIn instanceof ChunkCache) {
      world = ReflectionHelper.getPrivateValue(ChunkCache.class, (ChunkCache) worldIn, "worldObj", "world", "field_72815_e");
    }
    if (world != null) {
      return world.getCapability(CAP, null);
    }
    return null;
  }

  public static void setPaintSource(IBlockAccess world, BlockPos pos, @Nullable IBlockState paintSource) {
    IPaintRegister capability = getCapability(world);
    if (capability != null) {
      capability.setPaintSource(pos, paintSource);
    }
  }

  // tick

  @SubscribeEvent
  public static void onServerTick(TickEvent.WorldTickEvent event) {
    if (event.phase == Phase.END) {
      event.world.getCapability(CAP, null).tick(event.world);
    }
  }

  private final Set<EntityPlayerMP> subscribedPlayers = new HashSet<EntityPlayerMP>();

  @Override
  public void tick(World world) {
    int dimension = world.provider.getDimension();
    if (!subscribedPlayers.isEmpty()) {
      Iterator<EntityPlayerMP> iterator = subscribedPlayers.iterator();
      while (iterator.hasNext()) {
        if (!world.playerEntities.contains(iterator.next())) {
          iterator.remove();
        }
      }
    }
    if (!world.playerEntities.isEmpty()) {
      Set<EntityPlayerMP> newPlayers = new HashSet<EntityPlayerMP>();
      for (EntityPlayer player : world.playerEntities) {
        if (player instanceof EntityPlayerMP && !subscribedPlayers.contains(player)) {
          newPlayers.add((EntityPlayerMP) player);
        }
      }
      final ReentrantLock lock = data.getLock();
      lock.lock();
      try {
        if (!newPlayers.isEmpty() && !data.isEmpty()) {
          for (PacketWorldPaintUpdate packet : PacketWorldPaintUpdate.create(dimension, data, null)) {
            for (EntityPlayerMP player : newPlayers) {
              PacketHandler.sendTo(packet, player);
            }
          }
        }
        if (!subscribedPlayers.isEmpty() && !updates.isEmpty()) {
          for (PacketWorldPaintUpdate packet : PacketWorldPaintUpdate.create(dimension, data, updates)) {
            for (EntityPlayerMP player : subscribedPlayers) {
              PacketHandler.sendTo(packet, player);
            }
          }
        }
        updates.clear();
      } finally {
        lock.unlock();
      }
      if (!newPlayers.isEmpty()) {
        subscribedPlayers.addAll(newPlayers);
      }
    }
  }

}