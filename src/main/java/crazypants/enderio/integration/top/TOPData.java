package crazypants.enderio.integration.top;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;

import com.enderio.core.api.client.render.IWidgetIcon;
import com.enderio.core.api.common.util.IProgressTile;
import com.enderio.core.api.common.util.ITankAccess;
import com.enderio.core.api.common.util.ITankAccess.ITankData;
import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.common.util.UserIdent;

import crazypants.enderio.capability.EnderInventory;
import crazypants.enderio.capability.InventorySlot;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.machine.AbstractCapabilityMachineEntity;
import crazypants.enderio.machine.AbstractCapabilityPoweredMachineEntity;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.AbstractPoweredTaskEntity;
import crazypants.enderio.machine.ContinuousTask;
import crazypants.enderio.machine.IIoConfigurable;
import crazypants.enderio.machine.IRedstoneModeControlable;
import crazypants.enderio.machine.IoMode;
import crazypants.enderio.machine.RedstoneControlMode;
import crazypants.enderio.machine.RedstoneControlMode.IconHolder;
import crazypants.enderio.machine.invpanel.chest.TileInventoryChest;
import crazypants.enderio.machine.obelisk.spawn.AbstractMobObelisk;
import crazypants.enderio.machine.painter.blocks.TileEntityPaintedBlock;
import crazypants.enderio.machine.ranged.IRanged;
import crazypants.enderio.machine.spawner.TilePoweredSpawner;
import crazypants.enderio.paint.IPaintable.IPaintableTileEntity;
import crazypants.enderio.paint.PainterUtil2;
import crazypants.enderio.power.EnergyTank;
import crazypants.enderio.power.ILegacyPoweredTile;
import crazypants.enderio.power.IPowerStorage;
import crazypants.enderio.xp.ExperienceContainer;
import crazypants.enderio.xp.IHaveExperience;
import crazypants.util.CapturedMob;
import crazypants.util.Prep;
import mcjty.theoneprobe.api.IProbeHitData;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

class TOPData {
  static enum ProgressResult {
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

  public TOPData(TileEntity tileEntity, IProbeHitData hitData) {

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
        maxrf = te.getMaxEnergyStored(null);
        rf = te.getEnergyStored(null);
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
      redstoneTooltip = iconHolder.getTooltip();
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
      hasRange = bounds != null;
    }

    if (tileEntity instanceof AbstractMobObelisk) {
      AbstractMobObelisk te = (AbstractMobObelisk) tileEntity;
      mobs = te.getMobsInFilter();
      mobAction = te.getSpawnObeliskAction().getActionString();
      hasMobs = true;
    }

    if (tileEntity instanceof TilePoweredSpawner) {
      if (((TilePoweredSpawner) tileEntity).hasEntity()) {
        mobs = Collections.singletonList(((TilePoweredSpawner) tileEntity).getEntity());
        mobAction = AbstractMobObelisk.SpawnObeliskAction.SPAWN.getActionString();
        hasMobs = true;
      }
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
      paint1 = PainterUtil2.getPaintAsStack(((IPaintableTileEntity) tileEntity).getPaintSource());
      if (tileEntity instanceof TileEntityPaintedBlock.TileEntityTwicePaintedBlock) {
        paint2 = PainterUtil2.getPaintAsStack(((TileEntityPaintedBlock.TileEntityTwicePaintedBlock) tileEntity).getPaintSource2());
      }
      isPainted = Prep.isValid(paint1) || Prep.isValid(paint2);
    }

    if (tileEntity instanceof TileInventoryChest) {
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