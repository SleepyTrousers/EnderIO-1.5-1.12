package crazypants.enderio.base.machine.base.te;

import java.util.EnumMap;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.NNIterator;
import com.enderio.core.common.util.UserIdent;

import crazypants.enderio.base.TileEntityEio;
import crazypants.enderio.base.capability.ItemTools.Limit;
import crazypants.enderio.base.config.config.PersonalConfig;
import crazypants.enderio.base.diagnostics.Prof;
import crazypants.enderio.base.machine.interfaces.IIoConfigurable;
import crazypants.enderio.base.machine.interfaces.IMachine;
import crazypants.enderio.base.machine.interfaces.IRedstoneModeControlable;
import crazypants.enderio.base.machine.modes.IoMode;
import crazypants.enderio.base.machine.modes.RedstoneControlMode;
import crazypants.enderio.base.machine.sound.MachineSound;
import crazypants.enderio.base.paint.YetaUtil;
import crazypants.enderio.base.recipe.RecipeLevel;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import info.loenwind.autosave.util.NBTAction;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Storable
public abstract class AbstractMachineEntity extends TileEntityEio implements IMachine, IRedstoneModeControlable, IIoConfigurable {

  private static final @Nonnull Limit PULL_PUSH_LIMIT = new Limit(1, 64);

  @Store({ NBTAction.CLIENT, NBTAction.SAVE })
  protected @Nonnull EnumFacing facing = EnumFacing.SOUTH;

  // Client sync monitoring
  protected int ticksSinceSync = -1;
  protected boolean updateClients = false;
  protected boolean lastActive;
  protected int ticksSinceActiveChanged = 0;

  @Store
  protected @Nonnull RedstoneControlMode redstoneControlMode = RedstoneControlMode.IGNORE;

  @Store({ NBTAction.CLIENT, NBTAction.SAVE })
  protected boolean redstoneCheckPassed;

  private boolean redstoneStateDirty = true;

  @Store
  protected EnumMap<EnumFacing, IoMode> faceModes;

  protected boolean notifyNeighbours = false;

  @SideOnly(Side.CLIENT)
  private MachineSound sound;

  @Store(NBTAction.SAVE)
  private @Nullable UserIdent owner;

  @Store({ NBTAction.SAVE, NBTAction.ITEM })
  private final @Nonnull NNList<ItemStack> outputQueue = new NNList<>();

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
    if (faceModes.get(faceHit) != mode) {
      faceModes.put(faceHit, mode);
      notifyNeighbours = true;

      markDirty();
      updateBlock();
    }
  }

  @Override
  public void clearAllIoModes() {
    if (faceModes != null) {
      faceModes = null;
      notifyNeighbours = true;
      markDirty();
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
  public void rotate(@Nonnull Rotation rotation) {
    if (rotation == Rotation.NONE) {
      return;
    }
    setFacing(rotation.rotate(getFacing()));
    if (faceModes != null) {
      EnumMap<EnumFacing, IoMode> rotatedFaceModes = new EnumMap<>(EnumFacing.class);
      for (final EnumFacing side : faceModes.keySet()) {
        rotatedFaceModes.put(rotation.rotate(side), faceModes.get(side));
      }
      faceModes = rotatedFaceModes;
    }
    notifyNeighbours = true;
    updateBlock();
  }

  @Override
  public void mirror(@Nonnull Mirror mirror) {
    if (mirror == Mirror.NONE) {
      return;
    }
    setFacing(mirror.mirror(getFacing()));
    if (faceModes != null) {
      EnumMap<EnumFacing, IoMode> mirroredFaceModes = new EnumMap<EnumFacing, IoMode>(EnumFacing.class);
      for (final EnumFacing side : faceModes.keySet()) {
        mirroredFaceModes.put(mirror.mirror(side), faceModes.get(side));
      }
      faceModes = mirroredFaceModes;
    }
    notifyNeighbours = true;
    updateBlock();
  }

  @Override
  public @Nonnull RedstoneControlMode getRedstoneControlMode() {
    return redstoneControlMode;
  }

  @Override
  public void setRedstoneControlMode(@Nonnull RedstoneControlMode redstoneControlMode) {
    this.redstoneControlMode = redstoneControlMode;
    redstoneStateDirty = true;
    markDirty();
  }

  public @Nonnull EnumFacing getFacing() {
    return facing;
  }

  public void setFacing(@Nonnull EnumFacing facing) {
    this.facing = facing;
    markDirty();
  }

  public abstract boolean isActive();

  public @Nullable ResourceLocation getSound() {
    return null;
  }

  public boolean hasSound() {
    return getSound() != null;
  }

  public float getVolume() {
    return PersonalConfig.machineSoundsVolume.get();
  }

  public float getPitch() {
    return 1.0f;
  }

  protected boolean shouldPlaySound() {
    return isActive() && !isInvalid();
  }

  @SideOnly(Side.CLIENT)
  private void updateSound() {
    if (PersonalConfig.machineSoundsEnabled.get() && hasSound()) {
      final ResourceLocation soundRL = getSound();
      if (shouldPlaySound() && soundRL != null) {
        if (sound == null) {
          FMLClientHandler.instance().getClient().getSoundHandler()
              .playSound(sound = new MachineSound(soundRL, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, getVolume(), getPitch()));
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
    if (world.isRemote) {
      Prof.start(getWorld(), "clientTick");
      updateEntityClient();
      Prof.stop(getWorld());
    } else { // else is server, do all logic only on the server
      Prof.start(getWorld(), "redstoneCheck");
      if (redstoneStateDirty) {
        boolean prevRedCheck = redstoneCheckPassed;
        redstoneCheckPassed = RedstoneControlMode.isConditionMet(redstoneControlMode, this);
        redstoneStateDirty = false;
        updateClients |= prevRedCheck != redstoneCheckPassed;
      }

      if (shouldProcessOutputQueue()) {
        Prof.next(getWorld(), "outputQueue");
        processOutputQueue();
      }

      if (shouldDoWorkThisTick(5)) {
        Prof.next(getWorld(), "sideIO");
        doSideIo();
      }

      Prof.next(getWorld(), "tasks");
      updateClients |= processTasks(redstoneCheckPassed);

      if (updateClients) {
        Prof.next(getWorld(), "clientNotification");
        // this will cause 'getPacketDescription()' to be called and its result
        // will be sent to the PacketHandler on the other end of
        // client/server connection
        forceUpdatePlayers();
        // And this will make sure our current tile entity state is saved
        markDirty();
        updateClients = false;
      }

      if (notifyNeighbours) {
        Prof.next(getWorld(), "neighborNotification");
        world.notifyNeighborsOfStateChange(pos, getBlockType(), false);
        notifyNeighbours = false;
      }
      Prof.stop(getWorld());
    }
  }

  protected void updateEntityClient() {
    // check if the block on the client needs to update its texture
    if (isActive() != lastActive) {
      ticksSinceActiveChanged++;
      if (lastActive ? ticksSinceActiveChanged > 20 : ticksSinceActiveChanged > 4) {
        ticksSinceActiveChanged = 0;
        lastActive = isActive();
        updateBlock();
      }
    } else {
      ticksSinceActiveChanged = 0;
    }

    if (hasSound()) {
      updateSound();
    }

    YetaUtil.refresh(this);
  }

  protected final void doSideIo() {
    if (faceModes == null) {
      return;
    }
    Set<Entry<EnumFacing, IoMode>> ents = faceModes.entrySet();
    for (Entry<EnumFacing, IoMode> ent : ents) {
      IoMode mode = ent.getValue();
      if (mode.pulls()) {
        Prof.start(getWorld(), "pull");
        boolean done = doPull(ent.getKey());
        Prof.stop(getWorld());
        if (done) {
          return;
        }
      }
      if (mode.pushes()) {
        Prof.start(getWorld(), "push");
        boolean done = doPush(ent.getKey());
        Prof.stop(getWorld());
        if (done) {
          return;
        }
      }
    }
  }

  protected abstract boolean doPull(EnumFacing dir);

  protected abstract boolean doPush(EnumFacing dir);

  protected @Nonnull Limit getPullLimit() {
    return PULL_PUSH_LIMIT.copy();
  }

  protected @Nonnull Limit getPushLimit() {
    return PULL_PUSH_LIMIT.copy();
  }

  protected boolean shouldProcessOutputQueue() {
    return !outputQueue.isEmpty() && shouldDoWorkThisTick(7);
  }

  protected void processOutputQueue() {
    for (NNIterator<ItemStack> iter = outputQueue.iterator(); iter.hasNext();) {
      if (mergeOutput(iter.next())) {
        iter.remove();
      }
    }
  }

  protected abstract boolean mergeOutput(@Nonnull ItemStack next);

  protected void addToOutputQueue(@Nonnull ItemStack stack) {
    outputQueue.add(stack);
  }

  protected boolean isOutputQueued() {
    return !outputQueue.isEmpty();
  }

  protected @Nonnull NNList<ItemStack> getOutputQueue() {
    return outputQueue;
  }

  protected abstract boolean processTasks(boolean redstoneCheck);

  protected @Nonnull RecipeLevel getMachineLevel() {
    return RecipeLevel.IGNORE;
  }

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

  @Override
  public @Nonnull BlockPos getLocation() {
    return getPos();
  }

  @Override
  public boolean getRedstoneControlStatus() {
    return redstoneCheckPassed;
  }

  public void setOwner(@Nonnull EntityPlayer player) {
    if (!world.isRemote) {
      this.owner = UserIdent.create(player.getGameProfile());
    }
  }

  public @Nonnull UserIdent getOwner() {
    return owner != null ? owner : UserIdent.NOBODY;
  }

  @Override
  public @Nonnull String getMachineName() {
    return getBlockType().getUnlocalizedName();
  }

}
