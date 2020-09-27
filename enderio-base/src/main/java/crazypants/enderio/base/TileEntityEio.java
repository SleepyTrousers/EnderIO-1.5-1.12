package crazypants.enderio.base;

import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.TileEntityBase;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;
import com.enderio.core.common.vecmath.Vector4f;

import crazypants.enderio.base.autosave.BaseHandlers;
import crazypants.enderio.base.config.config.DiagnosticsConfig;
import crazypants.enderio.base.machine.base.te.ICap;
import crazypants.enderio.base.paint.PaintUtil;
import crazypants.enderio.util.HandlePaintSource;
import crazypants.enderio.util.NbtValue;
import info.loenwind.autosave.Reader;
import info.loenwind.autosave.Writer;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import info.loenwind.autosave.util.NBTAction;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
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
      for (StackTraceElement elem : new Exception("Stacktrace").getStackTrace()) {
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
      for (StackTraceElement elem : new Exception("Stacktrace").getStackTrace()) {
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
      for (StackTraceElement elem : new Exception("Stacktrace").getStackTrace()) {
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
    Writer.write(BaseHandlers.REGISTRY, action, root, this);
  }

  protected final void readCustomNBT(@Nonnull NBTAction action, @Nonnull NBTTagCompound root) {
    Reader.read(BaseHandlers.REGISTRY, action, root, this);
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
    }
    PaintUtil.setSourceBlock(stack, getPaintSource());
  }

  /**
   * The block is processed by {@link Block#getPickBlock(IBlockState, RayTraceResult, World, BlockPos, EntityPlayer)} but not in "copy" mode. Add stuff that
   * belongs to the block's identity but not its state.
   */
  protected @Nonnull ItemStack processPickBlock(@Nonnull RayTraceResult target, @Nonnull EntityPlayer player, @Nonnull ItemStack stack) {
    PaintUtil.setSourceBlock(stack, getPaintSource());
    return stack;
  }

  // ///////////////////////////////////////////////////////////////////////
  // PAINT START
  // ///////////////////////////////////////////////////////////////////////

  @Store(value = { NBTAction.CLIENT, NBTAction.SAVE }, handler = HandlePaintSource.class)
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

  /*************************************
   * CAPABILITIES
   *************************************/

  private final @Nonnull ICap.List iCaps = new ICap.List((capability, facingIn) -> super.getCapability(capability, facingIn));

  public final void addICap(@Nonnull ICap iCap) {
    iCaps.add(iCap);
  }

  public final void addICap(Capability<?> capability, @Nonnull Function<EnumFacing, Object> func) {
    iCaps.add(capability, func);
  }

  @Override
  public final boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing1) {
    return getCapability(capability, facing1) != null;
  }

  @SuppressWarnings("unchecked")
  @Override
  public final <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facingIn) {
    return iCaps.first(capability, facingIn);
  }

  public final void TickCentral_TrueITickableUpdate() {
    // System.out.println("Attach to net.minecraftforge.server.timings.TimeTracker.TILE_ENTITY_UPDATE instead of asm'ing mod code. That's rude and
    // unnecessary.");
    disableTicking();
  }

}
