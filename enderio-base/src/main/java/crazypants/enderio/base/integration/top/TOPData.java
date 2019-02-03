package crazypants.enderio.base.integration.top;

import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import com.enderio.core.api.client.render.IWidgetIcon;
import com.enderio.core.api.common.util.IProgressTile;
import com.enderio.core.api.common.util.ITankAccess;
import com.enderio.core.api.common.util.ITankAccess.ITankData;
import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.common.inventory.EnderInventory;
import com.enderio.core.common.inventory.InventorySlot;
import com.enderio.core.common.util.NNList.Callback;
import com.enderio.core.common.util.UserIdent;

import crazypants.enderio.api.ILocalizable;
import crazypants.enderio.base.block.painted.TileEntityTwicePaintedBlock;
import crazypants.enderio.base.conduit.IConduitBundle;
import crazypants.enderio.base.machine.base.te.AbstractCapabilityMachineEntity;
import crazypants.enderio.base.machine.base.te.AbstractCapabilityPoweredMachineEntity;
import crazypants.enderio.base.machine.base.te.AbstractMachineEntity;
import crazypants.enderio.base.machine.baselegacy.AbstractPoweredTaskEntity;
import crazypants.enderio.base.machine.interfaces.IHasFillLevel;
import crazypants.enderio.base.machine.interfaces.IIoConfigurable;
import crazypants.enderio.base.machine.interfaces.INotifier;
import crazypants.enderio.base.machine.interfaces.IRedstoneModeControlable;
import crazypants.enderio.base.machine.modes.EntityAction;
import crazypants.enderio.base.machine.modes.IoMode;
import crazypants.enderio.base.machine.modes.RedstoneControlMode;
import crazypants.enderio.base.machine.modes.RedstoneControlMode.IconHolder;
import crazypants.enderio.base.machine.task.ContinuousTask;
import crazypants.enderio.base.paint.IPaintable.IPaintableTileEntity;
import crazypants.enderio.base.paint.PaintUtil;
import crazypants.enderio.base.power.EnergyTank;
import crazypants.enderio.base.power.IPowerStorage;
import crazypants.enderio.base.power.forge.tile.ILegacyPoweredTile;
import crazypants.enderio.base.render.ranged.IRanged;
import crazypants.enderio.base.transceiver.Channel;
import crazypants.enderio.base.transceiver.ChannelType;
import crazypants.enderio.base.transceiver.IChanneledMachine;
import crazypants.enderio.base.xp.ExperienceContainer;
import crazypants.enderio.base.xp.IHaveExperience;
import crazypants.enderio.util.CapturedMob;
import crazypants.enderio.util.Prep;
import mcjty.theoneprobe.api.IProbeHitData;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

class TOPData {
  enum ProgressResult {
    NONE,
    PROGRESS,
    PROGRESS_ACTIVE,
    PROGRESS_NO_POWER,
    PROGRESS_IDLE,
    NO_PROGRESS_ACTIVE,
    NO_PROGRESS_IDLE;
  }

  boolean hasStatus, hasProgress, hasRF, hasRedstone, hasIOMode, hasRange, hasMobs, hasXP, hasRFIO, hasItemFillLevel;
  boolean isActive, isPowered, redstoneControlStatus, isPainted;
  float progress;
  long rf, maxrf, fillMax, fillCur;
  int experienceLevel, xpBarScaled, maxRFIn, maxRFOut, avgRF;
  String redstoneTooltip, sideName, mobAction;
  IWidgetIcon redstoneIcon;
  IoMode ioMode;
  BoundingBox bounds;
  List<CapturedMob> mobs;
  TOPData.ProgressResult progressResult = TOPData.ProgressResult.NONE;
  List<ITankData> tankData = null;
  @Nonnull
  ItemStack paint1 = Prep.getEmpty();
  @Nonnull
  ItemStack paint2 = Prep.getEmpty();
  UserIdent owner = null;
  Set<? extends ILocalizable> notifications = null;
  Map<ChannelType, Set<Channel>> sendChannels, recvChannels;

  TOPData(TileEntity tileEntity, IProbeHitData hitData) {

    if (tileEntity instanceof AbstractMachineEntity) {
      AbstractMachineEntity te = (AbstractMachineEntity) tileEntity;
      isActive = te.isActive();
      hasStatus = true;
      owner = te.getOwner();
    }

    if (tileEntity instanceof IPowerStorage) {
      IPowerStorage te = (IPowerStorage) tileEntity;
      maxrf = te.getMaxEnergyStoredL();
      rf = te.getEnergyStoredL();
      te.getMaxInput();
      hasRFIO = isPowered = hasRF = maxrf > 0;

      maxRFIn = te.getMaxInput();
      maxRFOut = te.getMaxOutput();
      avgRF = te.getAverageIOPerTick();
    } else if (tileEntity instanceof ILegacyPoweredTile) {
      ILegacyPoweredTile te = (ILegacyPoweredTile) tileEntity;
      if (te.displayPower()) {
        maxrf = te.getMaxEnergyStored();
        rf = te.getEnergyStored();
        isPowered = rf > 0;
        hasRF = maxrf > 0;
      }
    } else if (tileEntity instanceof AbstractCapabilityPoweredMachineEntity) {
      EnergyTank energy = ((AbstractCapabilityPoweredMachineEntity) tileEntity).getEnergy();
      maxrf = energy.getMaxEnergyStored();
      rf = energy.getEnergyStored();
      isPowered = rf > 0;
      hasRF = maxrf > 0;
    }

    if (tileEntity instanceof IProgressTile) {
      IProgressTile progressTile = (IProgressTile) tileEntity;
      progress = progressTile.getProgress();
      hasProgress = true;
      if (tileEntity instanceof AbstractPoweredTaskEntity) {
        AbstractPoweredTaskEntity te = (AbstractPoweredTaskEntity) tileEntity;
        if (te.getCurrentTask() instanceof ContinuousTask) {
          hasProgress = false;
        }
      } else if (tileEntity instanceof IConduitBundle) {
        hasProgress = false;
      }
    }

    if (tileEntity instanceof IRedstoneModeControlable) {
      IRedstoneModeControlable te = (IRedstoneModeControlable) tileEntity;
      RedstoneControlMode redstoneControlMode = te.getRedstoneControlMode();
      redstoneControlStatus = te.getRedstoneControlStatus();
      IconHolder iconHolder = RedstoneControlMode.IconHolder.getFromMode(redstoneControlMode);
      redstoneIcon = iconHolder.getIcon();
      redstoneTooltip = iconHolder.getUnlocalizedTooltip();
      hasRedstone = true;
    }

    if (tileEntity instanceof IIoConfigurable) {
      IIoConfigurable te = (IIoConfigurable) tileEntity;
      sideName = hitData.getSideHit().name().toLowerCase(Locale.US);
      ioMode = te.getIoMode(hitData.getSideHit());
      hasIOMode = true;
    }

    if (tileEntity instanceof IRanged) {
      IRanged te = (IRanged) tileEntity;
      bounds = te.getBounds();
      hasRange = true;
    }

    if (tileEntity instanceof EntityAction.Implementer) {
      EntityAction.Implementer te = (EntityAction.Implementer) tileEntity;
      mobs = te.getEntities();
      mobAction = te.getEntityAction().getActionString();
      hasMobs = !mobs.isEmpty();
    }

    if (tileEntity instanceof ITankAccess.IExtendedTankAccess) {
      tankData = ((ITankAccess.IExtendedTankAccess) tileEntity).getTankDisplayData();
    }

    if (tileEntity instanceof IHaveExperience) {
      ExperienceContainer experienceContainer = ((IHaveExperience) tileEntity).getContainer();
      hasXP = experienceContainer.getMaximumExperiance() > 0;
      experienceLevel = experienceContainer.getExperienceLevel();
      xpBarScaled = experienceContainer.getXpBarScaled(100);
    }

    if (tileEntity instanceof IPaintableTileEntity) {
      paint1 = PaintUtil.getPaintAsStack(((IPaintableTileEntity) tileEntity).getPaintSource());
      if (tileEntity instanceof TileEntityTwicePaintedBlock) {
        paint2 = PaintUtil.getPaintAsStack(((TileEntityTwicePaintedBlock) tileEntity).getPaintSource2());
      }
      isPainted = Prep.isValid(paint1) || Prep.isValid(paint2);
    }

    if (tileEntity instanceof IHasFillLevel) {
      fillMax = fillCur = 0L;
      for (InventorySlot slot : ((AbstractCapabilityMachineEntity) tileEntity).getInventory().getView(EnderInventory.Type.INOUT)) {
        if (Prep.isValid(slot.getStackInSlot(0))) {
          fillMax += Math.min(slot.getMaxStackSize(), slot.getStackInSlot(0).getMaxStackSize());
          fillCur += slot.getStackInSlot(0).getCount();
        } else {
          fillMax += slot.getMaxStackSize();
        }
      }
      hasItemFillLevel = true;
    }

    if (tileEntity instanceof INotifier) {
      notifications = ((INotifier) tileEntity).getNotification();
    }

    if (tileEntity instanceof IChanneledMachine) {
      sendChannels = new EnumMap<>(ChannelType.class);
      recvChannels = new EnumMap<>(ChannelType.class);
      ChannelType.VALUES.apply(new Callback<ChannelType>() {
        @Override
        public void apply(@Nonnull ChannelType type) {
          sendChannels.put(type, ((IChanneledMachine) tileEntity).getSendChannels(type));
          recvChannels.put(type, ((IChanneledMachine) tileEntity).getRecieveChannels(type));
        }
      });
    }

    calculateProgress();
  }

  private void calculateProgress() {
    if (hasProgress) {
      if (progress > 0) {
        if (hasRF && !isPowered) {
          progressResult = ProgressResult.PROGRESS_NO_POWER;
        } else {
          progressResult = ProgressResult.PROGRESS;
        }
      } else if (hasStatus && isActive) {
        progressResult = ProgressResult.PROGRESS_ACTIVE;
      } else {
        progressResult = ProgressResult.PROGRESS_IDLE;
      }
    } else if (hasStatus) {
      if (isActive) {
        progressResult = ProgressResult.NO_PROGRESS_ACTIVE;
      } else {
        progressResult = ProgressResult.NO_PROGRESS_IDLE;
      }
    }
  }
}
