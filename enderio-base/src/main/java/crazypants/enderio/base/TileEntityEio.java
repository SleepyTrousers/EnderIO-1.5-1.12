package crazypants.enderio.base;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.NBTAction;
import com.enderio.core.common.TileEntityBase;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.vecmath.Vector4f;

import crazypants.enderio.base.config.config.DiagnosticsConfig;
import crazypants.enderio.base.lang.Lang;
import crazypants.enderio.base.paint.PaintUtil;
import crazypants.enderio.util.NbtValue;
import info.loenwind.autosave.Reader;
import info.loenwind.autosave.Writer;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Storable
public abstract class TileEntityEio extends TileEntityBase {

  private static final @Nonnull Vector4f COLOR = new Vector4f(1, 182f / 255f, 0, 0.4f);

  protected TileEntityEio() {
    super();
    if (DiagnosticsConfig.debugTraceTELivecycleExtremelyDetailed.get()) {
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
    if (DiagnosticsConfig.debugTraceTELivecycleExtremelyDetailed.get()) {
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
    if (DiagnosticsConfig.debugTraceTELivecycleExtremelyDetailed.get()) {
      StringBuilder sb = new StringBuilder("TE ").append(this).append(" unloaded");
      for (StackTraceElement elem : new Exception("Stackstrace").getStackTrace()) {
        sb.append(" at ").append(elem);
      }
      Log.warn(sb);
    }
  }

  @Override
  protected final void writeCustomNBT(@Nonnull NBTAction action, @Nonnull NBTTagCompound root) {
    Writer.write(action, root, this);
  }

  @Override
  protected final void readCustomNBT(@Nonnull NBTAction action, @Nonnull NBTTagCompound root) {
    Reader.read(action, root, this);
    if (action == NBTAction.CLIENT) {
      onAfterDataPacket();
    }
    onAfterNbtRead();
  }

  protected void onAfterDataPacket() {
    if (DiagnosticsConfig.debugUpdatePackets.get()) {
      EnderIO.proxy.markBlock(getWorld(), getPos(), COLOR);
    }
  }

  protected void onAfterNbtRead() {
  }

  @Override
  public void readCustomNBT(@Nonnull ItemStack stack) {
    if (NbtValue.DATAROOT.hasTag(stack)) {
      NBTTagCompound tagCompound = NbtValue.DATAROOT.getTag(stack);
      readCustomNBT(NBTAction.ITEM, tagCompound);
    }
    setPaintSource(PaintUtil.getSourceBlock(stack));
  }

  @Override
  public void writeCustomNBT(@Nonnull ItemStack stack) {
    final NBTTagCompound tag = new NBTTagCompound();
    writeCustomNBT(NBTAction.ITEM, tag);
    if (!tag.hasNoTags()) {
      NbtValue.DATAROOT.setTag(stack, tag);
      stack.setStackDisplayName(Lang.MACHINE_CONFIGURED.get(stack.getDisplayName()));
    }
    PaintUtil.setSourceBlock(stack, getPaintSource());
  }

  // ///////////////////////////////////////////////////////////////////////
  // PAINT START
  // ///////////////////////////////////////////////////////////////////////

  @Store({ NBTAction.CLIENT, NBTAction.SAVE })
  private IBlockState paintSource = null;

  public void setPaintSource(@Nullable IBlockState paintSource) {
    this.paintSource = paintSource;
    markDirty();
    updateBlock();
  }

  public IBlockState getPaintSource() {
    return paintSource;
  }

  // ///////////////////////////////////////////////////////////////////////
  // PAINT END
  // ///////////////////////////////////////////////////////////////////////

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
