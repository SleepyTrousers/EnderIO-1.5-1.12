package crazypants.enderio.machine;

import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.NBTAction;
import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.UserIdent;

import crazypants.enderio.EnderIO;
import crazypants.enderio.TileEntityEio;
import crazypants.enderio.api.redstone.IRedstoneConnectable;
import crazypants.enderio.capability.ItemTools.Limit;
import crazypants.enderio.config.Config;
import crazypants.enderio.paint.IPaintable;
import crazypants.enderio.paint.PainterUtil2;
import crazypants.enderio.paint.YetaUtil;
import crazypants.util.ResettingFlag;
import info.loenwind.autosave.Reader;
import info.loenwind.autosave.Writer;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import info.loenwind.autosave.handlers.enderio.HandleIOMode;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Storable
public abstract class AbstractMachineEntity extends TileEntityEio
    implements IMachine, IRedstoneModeControlable, IRedstoneConnectable, IIoConfigurable {

  private static final Limit PULL_PUSH_LIMIT = new Limit(1, 64);
  
  @Store({ NBTAction.CLIENT, NBTAction.SAVE })
  public @Nonnull EnumFacing facing = EnumFacing.SOUTH;

  // Client sync monitoring
  protected int ticksSinceSync = -1;
  @Store({ NBTAction.CLIENT, NBTAction.SAVE })
  protected ResettingFlag forceClientUpdate = new ResettingFlag();
  protected boolean lastActive;
  protected int ticksSinceActiveChanged = 0;

  @Store
  protected RedstoneControlMode redstoneControlMode;

  @Store({ NBTAction.CLIENT, NBTAction.SAVE })
  protected boolean redstoneCheckPassed;

  private boolean redstoneStateDirty = true;

  @Store(handler = HandleIOMode.class)
  protected Map<EnumFacing, IoMode> faceModes;

  protected boolean notifyNeighbours = false;

  @SideOnly(Side.CLIENT)
  private MachineSound sound;

  @Store(NBTAction.SAVE)
  private @Nullable UserIdent owner;

  private final ResourceLocation soundRes;

  public static ResourceLocation getSoundFor(String sound) {
    return sound == null ? null : new ResourceLocation(EnderIO.DOMAIN + ":" + sound);
  }

  public AbstractMachineEntity() {
    redstoneControlMode = RedstoneControlMode.IGNORE;
    soundRes = getSoundFor(getSoundName());
  }

  @Override
  public @Nonnull IoMode toggleIoModeForFace(@Nullable EnumFacing faceHit) {
    IoMode curMode = getIoMode(faceHit);
    IoMode mode = curMode.next();
    while (!supportsMode(faceHit, mode)) {
      mode = mode.next();
    }
    setIoMode(faceHit, mode);
    return mode != null ? mode : IoMode.NONE;
  }

  @Override
  public boolean supportsMode(@Nullable EnumFacing faceHit, @Nullable IoMode mode) {
    return true;
  }

  @Override
  public void setIoMode(@Nullable EnumFacing faceHit, @Nullable IoMode mode) {
    if (mode == IoMode.NONE && faceModes == null) {
      return;
    }
    if (faceModes == null) {
      faceModes = new EnumMap<EnumFacing, IoMode>(EnumFacing.class);
    }
    faceModes.put(faceHit, mode);
    forceClientUpdate.set();
    notifyNeighbours = true;

    updateBlock();
  }

  @Override
  public void clearAllIoModes() {
    if (faceModes != null) {
      faceModes = null;
      forceClientUpdate.set();
      notifyNeighbours = true;
      updateBlock();
    }
  }

  @Override
  public @Nonnull IoMode getIoMode(@Nullable EnumFacing face) {
    if (faceModes == null) {
      return IoMode.NONE;
    }
    IoMode res = faceModes.get(face);
    if (res == null) {
      return IoMode.NONE;
    }
    return res;
  }

  @Override
  public RedstoneControlMode getRedstoneControlMode() {
    return redstoneControlMode;
  }

  @Override
  public void setRedstoneControlMode(RedstoneControlMode redstoneControlMode) {
    this.redstoneControlMode = redstoneControlMode;
    redstoneStateDirty = true;
    updateBlock();
  }

  public @Nonnull EnumFacing getFacing() {
    return facing;
  }

  public void setFacing(EnumFacing facing) {
    this.facing = facing == null ? EnumFacing.SOUTH : facing;
    markDirty();
  }

  public abstract boolean isActive();

  public String getSoundName() {
    return null;
  }

  public boolean hasSound() {
    return getSoundName() != null;
  }

  public float getVolume() {
    return Config.machineSoundVolume;
  }

  public float getPitch() {
    return 1.0f;
  }

  protected boolean shouldPlaySound() {
    return isActive() && !isInvalid();
  }

  @SideOnly(Side.CLIENT)
  private void updateSound() {
    if (Config.machineSoundsEnabled && hasSound()) {
      if (shouldPlaySound()) {
        if (sound == null) {
          sound = new MachineSound(soundRes, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, getVolume(), getPitch());
          FMLClientHandler.instance().getClient().getSoundHandler().playSound(sound);
        }
      } else if (sound != null) {
        sound.endPlaying();
        sound = null;
      }
    }
  }

  // --- Process Loop
  // --------------------------------------------------------------------------

  @Override
  public void doUpdate() {
    getWorld().theProfiler.startSection(getMachineName());
    if (world.isRemote) {
      getWorld().theProfiler.startSection("clientTick");
      updateEntityClient();
      getWorld().theProfiler.endSection();
    } else { // else is server, do all logic only on the server
      getWorld().theProfiler.startSection("serverTick");

      boolean requiresClientSync = forceClientUpdate.peek();
      boolean prevRedCheck = redstoneCheckPassed;
      if (redstoneStateDirty) {
        redstoneCheckPassed = RedstoneControlMode.isConditionMet(redstoneControlMode, this);
        redstoneStateDirty = false;
      }

      if (shouldDoWorkThisTick(5)) {
        getWorld().theProfiler.startSection("sideIO");
        requiresClientSync |= doSideIo();
        getWorld().theProfiler.endSection();
      }

      requiresClientSync |= prevRedCheck != redstoneCheckPassed;

      getWorld().theProfiler.startSection("tasks");
      requiresClientSync |= processTasks(redstoneCheckPassed);
      getWorld().theProfiler.endSection();

      if (requiresClientSync) {
        getWorld().theProfiler.startSection("clientNotification");
        // this will cause 'getPacketDescription()' to be called and its result
        // will be sent to the PacketHandler on the other end of
        // client/server connection
        IBlockState bs = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, bs, bs, 3);

        // And this will make sure our current tile entity state is saved
        markDirty();
        getWorld().theProfiler.endSection();
      }

      if (notifyNeighbours) {
        getWorld().theProfiler.startSection("neighborNotification");
        world.notifyBlockOfStateChange(pos, getBlockType());
        notifyNeighbours = false;
        getWorld().theProfiler.endSection();
      }

      getWorld().theProfiler.endSection();
    }
    getWorld().theProfiler.endSection();
  }

  protected void updateEntityClient() {
    // check if the block on the client needs to update its texture
    if (isActive() != lastActive) {
      ticksSinceActiveChanged++;
      if (lastActive ? ticksSinceActiveChanged > 20 : ticksSinceActiveChanged > 4) {
        ticksSinceActiveChanged = 0;
        lastActive = isActive();
        forceClientUpdate.set();
      }
    } else {
      ticksSinceActiveChanged = 0;
    }

    if (hasSound()) {
      updateSound();
    }

    if (forceClientUpdate.read()) {
      IBlockState bs = world.getBlockState(pos);
      world.notifyBlockUpdate(pos, bs, bs, 3);
    } else {
      YetaUtil.refresh(this);
    }
  }

  protected boolean doSideIo() {
    if (faceModes == null) {
      return false;
    }
    boolean res = false;
    Set<Entry<EnumFacing, IoMode>> ents = faceModes.entrySet();
    for (Entry<EnumFacing, IoMode> ent : ents) {
      IoMode mode = ent.getValue();
      if (mode.pulls()) {
        res = res | doPull(ent.getKey());
      }
      if (mode.pushes()) {
        res = res | doPush(ent.getKey());
      }
    }
    return res;
  }

  protected abstract boolean doPull(EnumFacing dir);

  protected abstract boolean doPush(EnumFacing dir);

  protected Limit getPullLimit() {
    return PULL_PUSH_LIMIT.copy();
  }
  
  protected Limit getPushLimit() {
    return PULL_PUSH_LIMIT.copy();
  }

  protected abstract boolean processTasks(boolean redstoneCheck);

  // ---- Tile Entity
  // ------------------------------------------------------------------------------

  @Override
  public void invalidate() {
    super.invalidate();
    if (world.isRemote) {
      updateSound();
    }
  }

  @Override
  public void readCustomNBT(NBTTagCompound nbtRoot) {
    super.readCustomNBT(nbtRoot);
    readCommon(nbtRoot);
  }

  /**
   * Read state common to both block and item
   */
  public void readCommon(NBTTagCompound nbtRoot) {
  }

  public void readFromItemStack(ItemStack stack) {
    if (stack == null) {
      return;
    }
    NBTTagCompound root = stack.getTagCompound();
    if (root == null) {
      return;
    }
    Reader.read(NBTAction.ITEM, root, this);
    if (root.hasKey("eio.abstractMachine")) {
      try {
        doingOtherNbt = true;
        readCommon(root);
      } finally {
        doingOtherNbt = false;
      }
    }
    if (this instanceof IPaintable.IPaintableTileEntity) {
      paintSource = PainterUtil2.readNbt(root);
    }
    return;
  }

  @Override
  public void writeCustomNBT(NBTTagCompound nbtRoot) {
    super.writeCustomNBT(nbtRoot);
    writeCommon(nbtRoot);
  }

  /**
   * Write state common to both block and item
   */
  public void writeCommon(NBTTagCompound nbtRoot) {
  }

  public void writeToItemStack(ItemStack stack) {
    if (stack == null) {
      return;
    }
    NBTTagCompound root = stack.getTagCompound();
    if (root == null) {
      root = new NBTTagCompound();
      stack.setTagCompound(root);
    }

    root.setBoolean("eio.abstractMachine", true);
    try {
      doingOtherNbt = true;
      writeCommon(root);
    } finally {
      doingOtherNbt = false;
    }
    Writer.write(NBTAction.ITEM, root, this);

    if (this instanceof IPaintable.IPaintableTileEntity) {
      PainterUtil2.writeNbt(root, paintSource);
    }

    String name;
    if (stack.hasDisplayName()) {
      name = stack.getDisplayName();
    } else {
      name = EnderIO.lang.localizeExact(stack.getUnlocalizedName() + ".name");
    }
    name += " " + EnderIO.lang.localize("machine.tooltip.configured");
    stack.setStackDisplayName(name);
  }

  public boolean isSideDisabled(EnumFacing dir) {
    return getIoMode(dir) == IoMode.DISABLED;
  }

  public void onNeighborBlockChange(Block blockId) {
    redstoneStateDirty = true;
  }

  /* IRedstoneConnectable */

  @Override
  public boolean shouldRedstoneConduitConnect(World world, int x, int y, int z, EnumFacing from) {
    return true;
  }
  
  @Override
  public BlockCoord getLocation() {
    return new BlockCoord(pos);
  }

  @Override
  public boolean getRedstoneControlStatus() {
    return redstoneCheckPassed;
  }

  // ///////////////////////////////////////////////////////////////////////
  // PAINT START
  // ///////////////////////////////////////////////////////////////////////

  @Store
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

  void setOwner(EntityPlayer player) {
    this.owner = UserIdent.create(player.getGameProfile());
  }

  public UserIdent getOwner() {
    return owner != null ? owner : UserIdent.NOBODY;
  }

}
