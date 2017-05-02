package crazypants.enderio;

import java.util.ArrayList;
import java.util.List;

import com.enderio.core.common.TileEntityBase;
import com.enderio.core.common.vecmath.Vector4f;

import crazypants.enderio.config.Config;
import info.loenwind.autosave.Reader;
import info.loenwind.autosave.Writer;
import info.loenwind.autosave.annotations.Store.StoreFor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public abstract class TileEntityEio extends TileEntityBase {

  private static final Vector4f COLOR = new Vector4f(1, 182f / 255f, 0, 0.4f);
  protected boolean doingOtherNbt = false;

  protected TileEntityEio() {
    super();
    if (Config.debugTraceTELivecycleExtremelyDetailed) {
      StringBuilder sb = new StringBuilder("TE ").append(this).append(" created");
      for (StackTraceElement elem : new Exception("Stackstrace").getStackTrace()) {
        sb.append(" at ").append(elem);
      }
      Log.warn(sb);
    }
  }

  @Override
  public void invalidate() {
    super.invalidate();
    if (Config.debugTraceTELivecycleExtremelyDetailed) {
      StringBuilder sb = new StringBuilder("TE ").append(this).append(" invalidated");
      for (StackTraceElement elem : new Exception("Stackstrace").getStackTrace()) {
        sb.append(" at ").append(elem);
      }
      Log.warn(sb);
    }
  }

  @Override
  public void onChunkUnload() {
    super.onChunkUnload();
    if (Config.debugTraceTELivecycleExtremelyDetailed) {
      StringBuilder sb = new StringBuilder("TE ").append(this).append(" unloaded");
      for (StackTraceElement elem : new Exception("Stackstrace").getStackTrace()) {
        sb.append(" at ").append(elem);
      }
      Log.warn(sb);
    }
  }

  @Override
  public final SPacketUpdateTileEntity getUpdatePacket() {
    NBTTagCompound root = createClientUpdateNBT();
    
    return new SPacketUpdateTileEntity(getPos(), 1, root);
  }
  
  @Override
  public NBTTagCompound getUpdateTag() {    
    return createClientUpdateNBT();
  }

  protected NBTTagCompound createClientUpdateNBT() {
    NBTTagCompound root = new NBTTagCompound();
    try {
      doingOtherNbt = true;
      super.writeToNBT(root);
    } finally {
      doingOtherNbt = false;
    }
    Writer.write(StoreFor.CLIENT, root, this);
    return root;
  }

  @Override
  public final void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
    NBTTagCompound root = pkt.getNbtCompound();
    Reader.read(StoreFor.CLIENT, root, this);
    try {
      doingOtherNbt = true;
      super.readFromNBT(root);
    } finally {
      doingOtherNbt = false;
    }
    onAfterDataPacket();
    if (Config.debugUpdatePackets) {
      EnderIO.proxy.markBlock(getWorld(), getPos(), COLOR);
    }
  }

  protected void onAfterDataPacket() {
  }

  @Override
  protected void writeCustomNBT(NBTTagCompound root) {
    if (!doingOtherNbt) {
      Writer.write(StoreFor.SAVE, root, this);
    }
  }

  @Override
  protected void readCustomNBT(NBTTagCompound root) {
    if (!doingOtherNbt) {
      Reader.read(StoreFor.SAVE, root, this);
    }
  }

  public void readContentsFromNBT(NBTTagCompound nbtRoot) {
    Reader.read(StoreFor.ITEM, nbtRoot, this);
  }

  public void writeContentsToNBT(NBTTagCompound nbtRoot) {
    Writer.write(StoreFor.ITEM, nbtRoot, this);
  }

  private final static List<TileEntity> notTickingTileEntitiesS = new ArrayList<TileEntity>();
  private final static List<TileEntity> notTickingTileEntitiesC = new ArrayList<TileEntity>();
  /**
   * Called on each tick. Do not call super from any subclass, that will disable ticking this TE again.
   */
  @Override
  protected void doUpdate() {
    disableTicking();
  }

  protected void disableTicking() {
    if (world.isRemote) {
      notTickingTileEntitiesC.add(this);
    } else {
      notTickingTileEntitiesS.add(this);
    }
  }

  static {
    MinecraftForge.EVENT_BUS.register(TileEntityEio.class);
  }

  @SubscribeEvent
  public static void onServerTick(TickEvent.ServerTickEvent event) {
    for (TileEntity te : notTickingTileEntitiesS) {
      te.getWorld().tickableTileEntities.remove(te);
    }
    notTickingTileEntitiesS.clear();
  }

  @SubscribeEvent
  public static void onClientTick(TickEvent.ClientTickEvent event) {
    for (TileEntity te : notTickingTileEntitiesC) {
      te.getWorld().tickableTileEntities.remove(te);
    }
    notTickingTileEntitiesC.clear();
  }

}
