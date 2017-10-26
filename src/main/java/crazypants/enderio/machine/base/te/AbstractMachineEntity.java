package crazypants.enderio.machine.base.te;

import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.NBTAction;
import com.enderio.core.common.util.UserIdent;

import crazypants.enderio.EnderIO;
import crazypants.enderio.TileEntityEio;
import crazypants.enderio.api.redstone.IRedstoneConnectable;
import crazypants.enderio.capability.ItemTools.Limit;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.interfaces.IIoConfigurable;
import crazypants.enderio.machine.interfaces.IMachine;
import crazypants.enderio.machine.interfaces.IRedstoneModeControlable;
import crazypants.enderio.machine.modes.IoMode;
import crazypants.enderio.machine.modes.RedstoneControlMode;
import crazypants.enderio.machine.sound.MachineSound;
import crazypants.enderio.paint.YetaUtil;
import crazypants.util.ResettingFlag;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import info.loenwind.autosave.handlers.enderio.HandleIOMode;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Storable
public abstract class AbstractMachineEntity extends TileEntityEio implements IMachine, IRedstoneModeControlable, IRedstoneConnectable, IIoConfigurable {

  private static final @Nonnull Limit PULL_PUSH_LIMIT = new Limit(1, 64);

  @Store({ NBTAction.SYNC, NBTAction.UPDATE, NBTAction.SAVE })
  public @Nonnull EnumFacing facing = EnumFacing.SOUTH;

  // Client sync monitoring
  protected int ticksSinceSync = -1;
  @Store({ NBTAction.UPDATE, NBTAction.SAVE })
  protected final @Nonnull ResettingFlag forceClientUpdate = new ResettingFlag();
  protected boolean lastActive;
  protected int ticksSinceActiveChanged = 0;

  @Store
  protected @Nonnull RedstoneControlMode redstoneControlMode = RedstoneControlMode.IGNORE;

  @Store({ NBTAction.UPDATE, NBTAction.SAVE })
  protected boolean redstoneCheckPassed;

  private boolean redstoneStateDirty = true;

  @Store(handler = HandleIOMode.class)
  protected Map<EnumFacing, IoMode> faceModes;

  protected boolean notifyNeighbours = false;

  @SideOnly(Side.CLIENT)
  private MachineSound sound;

  @Store(NBTAction.SAVE)
  private @Nullable UserIdent owner;

  private final @Nonnull ResourceLocation soundRes;

  public static @Nonnull ResourceLocation getSoundFor(@Nonnull String sound) {
    return new ResourceLocation(EnderIO.DOMAIN + ":" + sound);
  }

  public AbstractMachineEntity() {
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
    return mode;
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
  public @Nonnull RedstoneControlMode getRedstoneControlMode() {
    return redstoneControlMode;
  }

  @Override
  public void setRedstoneControlMode(@Nonnull RedstoneControlMode redstoneControlMode) {
    this.redstoneControlMode = redstoneControlMode;
    redstoneStateDirty = true;
    updateBlock();
  }

  public @Nonnull EnumFacing getFacing() {
    return facing;
  }

  public void setFacing(@Nonnull EnumFacing facing) {
    this.facing = facing;
    markDirty();
  }

  public abstract boolean isActive();

  public @Nonnull String getSoundName() {
    return "";
  }

  public boolean hasSound() {
    return !getSoundName().isEmpty();
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
          FMLClientHandler.instance().getClient().getSoundHandler()
              .playSound(sound = new MachineSound(soundRes, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, getVolume(), getPitch()));
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
    getWorld().profiler.startSection(getMachineName());
    if (world.isRemote) {
      getWorld().profiler.startSection("clientTick");
      updateEntityClient();
      getWorld().profiler.endSection();
    } else { // else is server, do all logic only on the server
      getWorld().profiler.startSection("serverTick");

      boolean requiresClientSync = forceClientUpdate.peek();
      boolean prevRedCheck = redstoneCheckPassed;
      if (redstoneStateDirty) {
        redstoneCheckPassed = RedstoneControlMode.isConditionMet(redstoneControlMode, this);
        redstoneStateDirty = false;
      }

      if (shouldDoWorkThisTick(5)) {
        getWorld().profiler.startSection("sideIO");
        requiresClientSync |= doSideIo();
        getWorld().profiler.endSection();
      }

      requiresClientSync |= prevRedCheck != redstoneCheckPassed;

      getWorld().profiler.startSection("tasks");
      requiresClientSync |= processTasks(redstoneCheckPassed);
      getWorld().profiler.endSection();

      if (requiresClientSync) {
        getWorld().profiler.startSection("clientNotification");
        // this will cause 'getPacketDescription()' to be called and its result
        // will be sent to the PacketHandler on the other end of
        // client/server connection
        IBlockState bs = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, bs, bs, 3);

        // And this will make sure our current tile entity state is saved
        markDirty();
        getWorld().profiler.endSection();
      }

      if (notifyNeighbours) {
        getWorld().profiler.startSection("neighborNotification");
        world.notifyNeighborsOfStateChange(pos, getBlockType(), false);
        notifyNeighbours = false;
        getWorld().profiler.endSection();
      }

      getWorld().profiler.endSection();
    }
    getWorld().profiler.endSection();
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

  protected @Nonnull Limit getPullLimit() {
    return PULL_PUSH_LIMIT.copy();
  }

  protected @Nonnull Limit getPushLimit() {
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

  public boolean isSideDisabled(EnumFacing dir) {
    return getIoMode(dir) == IoMode.DISABLED;
  }

  public void onNeighborBlockChange(@Nonnull IBlockState state, @Nonnull World worldIn, @Nonnull BlockPos posIn, @Nonnull Block blockIn,
      @Nonnull BlockPos fromPos) {
    redstoneStateDirty = true;
  }

  /* IRedstoneConnectable */

  @Override
  public boolean shouldRedstoneConduitConnect(@Nonnull World worldIn, @Nonnull BlockPos posIn, @Nonnull EnumFacing from) {
    return true;
  }

  @Override
  public @Nonnull BlockPos getLocation() {
    return getPos();
  }

  @Override
  public boolean getRedstoneControlStatus() {
    return redstoneCheckPassed;
  }

  public void setOwner(@Nonnull EntityPlayer player) {
    this.owner = UserIdent.create(player.getGameProfile());
  }

  public @Nonnull UserIdent getOwner() {
    return owner != null ? owner : UserIdent.NOBODY;
  }

  @Override
  public @Nonnull String getMachineName() {
    return getBlockType().getUnlocalizedName();
  }

}
