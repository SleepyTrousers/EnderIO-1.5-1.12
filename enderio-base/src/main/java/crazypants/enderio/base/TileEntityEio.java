package crazypants.enderio.base;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import info.loenwind.autosave.util.NBTAction;

import com.enderio.core.api.common.util.IProgressTile;
import com.enderio.core.common.TileEntityBase;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;
import com.enderio.core.common.vecmath.Vector4f;

import crazypants.enderio.autosave.handlers.EIOHandlers;
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
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Storable
@EventBusSubscriber(modid = EnderIO.MODID)
public abstract class TileEntityEio extends TileEntityBase {

  private static final @Nonnull Vector4f COLOR_UPD = new Vector4f(1, 182f / 255f, 0, 0.2f);
  private static final @Nonnull Vector4f COLOR_REN = new Vector4f(0x61 / 255f, 0x2d / 255f, 0xb5 / 255f, 0.4f);
  private static final @Nonnull Vector4f COLOR_REN_SRV = new Vector4f(0, 0x6d / 255f, 0x8f / 255f, 0.8f);

  @Store(NBTAction.CLIENT)
  private boolean forceClientRerender = false;

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
  
  @Deprecated
  private @Nonnull NBTAction convertAction(com.enderio.core.common.NBTAction action) {
    return NullHelper.notnullJ(NBTAction.values()[action.ordinal()], "Enum.values()");
  }

  @Override
  @Deprecated
  protected void readCustomNBT(@Nonnull com.enderio.core.common.NBTAction action, @Nonnull NBTTagCompound root) {
    readCustomNBT(convertAction(action), root);
  }
  
  @Override
  @Deprecated
  protected void writeCustomNBT(@Nonnull com.enderio.core.common.NBTAction action, @Nonnull NBTTagCompound root) {
    writeCustomNBT(convertAction(action), root);
  }

  protected final void writeCustomNBT(@Nonnull NBTAction action, @Nonnull NBTTagCompound root) {
    onBeforeNbtWrite();
    Writer.write(EIOHandlers.REGISTRY, action, root, this);
  }

  protected final void readCustomNBT(@Nonnull NBTAction action, @Nonnull NBTTagCompound root) {
    Reader.read(EIOHandlers.REGISTRY, action, root, this);
    if (action == NBTAction.CLIENT) {
      onAfterDataPacket();
    }
    onAfterNbtRead();
  }

  protected void onAfterDataPacket() {
    if (forceClientRerender) {
      super.updateBlock();
      forceClientRerender = false;
      if (DiagnosticsConfig.debugChunkRerenders.get()) {
        EnderIO.proxy.markBlock(getWorld(), getPos(), COLOR_REN_SRV);
      }
    } else if (DiagnosticsConfig.debugUpdatePackets.get()) {
      EnderIO.proxy.markBlock(getWorld(), getPos(), COLOR_UPD);
    }
  }

  @Override
  protected void updateBlock() {
    super.updateBlock();
    if (!world.isRemote) {
      forceClientRerender = true;
      forceUpdatePlayers();
      forceClientRerender = false;
    } else if (DiagnosticsConfig.debugChunkRerenders.get()) {
      EnderIO.proxy.markBlock(getWorld(), getPos(), COLOR_REN);
    }
  }

  protected void onBeforeNbtWrite() {
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
    if (this.paintSource != paintSource) {
      this.paintSource = paintSource;
      markDirty();
      updateBlock();
    }
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
