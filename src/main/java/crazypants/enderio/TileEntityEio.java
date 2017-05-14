package crazypants.enderio;

import javax.annotation.Nonnull;

import com.enderio.core.common.NBTAction;
import com.enderio.core.common.TileEntityBase;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.vecmath.Vector4f;

import crazypants.enderio.config.Config;
import info.loenwind.autosave.Reader;
import info.loenwind.autosave.Writer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public abstract class TileEntityEio extends TileEntityBase {

  private static final @Nonnull Vector4f COLOR = new Vector4f(1, 182f / 255f, 0, 0.4f);

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
  protected void writeCustomNBT(@Nonnull NBTAction action, @Nonnull NBTTagCompound root) {
    Writer.write(action, root, this);
  }

  @Override
  protected void readCustomNBT(@Nonnull NBTAction action, @Nonnull NBTTagCompound root) {
    Reader.read(action, root, this);
    if (action == NBTAction.SYNC || action == NBTAction.UPDATE) {
      onAfterDataPacket();
      if (Config.debugUpdatePackets) {
        EnderIO.proxy.markBlock(getWorld(), getPos(), COLOR);
      }
    }
  }

  protected void onAfterDataPacket() {
  }

  public void readContentsFromNBT(@Nonnull NBTTagCompound nbtRoot) {
    Reader.read(NBTAction.ITEM, nbtRoot, this);
  }

  public void writeContentsToNBT(@Nonnull NBTTagCompound nbtRoot) {
    Writer.write(NBTAction.ITEM, nbtRoot, this);
  }

  private final static NNList<TileEntity> notTickingTileEntitiesS = new NNList<TileEntity>();
  private final static NNList<TileEntity> notTickingTileEntitiesC = new NNList<TileEntity>();
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
