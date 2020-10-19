package crazypants.enderio.machines.machine.vacuum.xp;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.common.util.ITankAccess;
import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.common.fluid.FluidWrapper;
import com.enderio.core.common.util.MagnetUtil;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.Callback;
import com.enderio.core.common.vecmath.Vector4f;
import com.google.common.base.Predicate;

import crazypants.enderio.base.TileEntityEio;
import crazypants.enderio.base.fluid.Fluids;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.paint.YetaUtil;
import crazypants.enderio.base.render.ranged.IRanged;
import crazypants.enderio.base.render.ranged.RangeParticle;
import crazypants.enderio.base.xp.ExperienceContainer;
import crazypants.enderio.machines.config.config.VacuumConfig;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import info.loenwind.autosave.util.NBTAction;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Storable
public class TileXPVacuum extends TileEntityEio implements Predicate<EntityXPOrb>, IPaintable.IPaintableTileEntity, IRanged, ITankAccess {

  private static final int IO_MB_TICK = 10000; // no need to slow down the vacuum any more than necessary, let the limit be the piping

  @Store
  private int range = VacuumConfig.vacuumXPRange.get();
  private boolean showingRange;

  @Store
  private boolean formed = false;
  private boolean formedRender = false;

  @Store({ NBTAction.SAVE, NBTAction.ITEM })
  private final @Nonnull ExperienceContainer xpCon;

  public TileXPVacuum() {
    xpCon = new ExperienceContainer(Integer.MAX_VALUE);
    xpCon.setTileEntity(this);
    xpCon.setCanFill(!formed);
    addICap(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing -> getContainer());
  }

  @Override
  public void doUpdate() {
    if (world.isRemote) {
      YetaUtil.refresh(this);
      if (formed != formedRender) {
        formedRender = formed;
        updateBlock();
      }
      if (formed) {
        doHoover();
      }
    } else {
      if (formed) {
        doHoover();
        if (xpCon.getFluidAmount() > 0) {
          doPush();
        }
      } else if (xpCon.getFluidAmount() > 0) {
        formed = true;
        markDirty();
        updateBlock();
      }
      xpCon.setCanFill(!formed);
    }
  }

  @Override
  public boolean apply(@Nullable EntityXPOrb entity) {
    return MagnetUtil.shouldAttract(getPos(), entity, true);
  }

  private static final double speed = VacuumConfig.vacuumXPVelocity.get();

  private void doHoover() {
    boolean pickUpThisTick = xpCon.getFluidAmount() == 0;
    for (EntityXPOrb entity : world.getEntitiesWithinAABB(EntityXPOrb.class, getBounds())) {
      // note the Predicate parameter
      double x = (pos.getX() + 0.5D - entity.posX);
      double y = (pos.getY() + 0.5D - entity.posY);
      double z = (pos.getZ() + 0.5D - entity.posZ);

      double distance = Math.sqrt(x * x + y * y + z * z);
      if (distance < 1.25) {
        if (pickUpThisTick && !world.isRemote && !entity.isDead) {
          int xpValue = entity.getXpValue();
          int remaining = xpValue - xpCon.addExperience(xpValue);
          if (remaining <= 0) {
            entity.setDead();
          } else {
            entity.xpValue = remaining;
          }
        }
      } else {
        double distScale = Math.min(1d, Math.max(0.25d, 1d - distance / VacuumConfig.vacuumXPRange.get()));
        distScale *= distScale;

        entity.motionX += x / distance * distScale * speed;
        if (entity.posY < pos.getY()) {
          entity.motionY += y / distance * distScale * speed + .03;
        } else {
          entity.motionY += y / distance * distScale * speed;
        }
        entity.motionZ += z / distance * distScale * speed;
      }
    }
  }

  @Override
  public boolean equals(@Nullable Object obj) {
    return super.equals(obj);
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  private final @Nonnull Callback<EnumFacing> push_callback = new Callback<EnumFacing>() {
    @Override
    public void apply(@Nonnull EnumFacing dir) {
      if (xpCon.getFluidAmount() > 0 && FluidWrapper.transfer(xpCon, world, getPos().offset(dir), dir.getOpposite(), IO_MB_TICK) > 0) {
        setTanksDirty();
      }
    }
  };

  private void doPush() {
    if (xpCon.getFluidAmount() > 0) {
      NNList.FACING.apply(push_callback);
    }
  }

  public boolean isFormed() {
    return formedRender;
  }

  // TANK

  @Override
  public FluidTank getInputTank(FluidStack forFluidType) {
    if (!formed && forFluidType != null && forFluidType.getFluid() == Fluids.XP_JUICE.getFluid()) {
      return xpCon;
    }
    return null;
  }

  @Override
  public @Nonnull FluidTank[] getOutputTanks() {
    return new FluidTank[] { xpCon };
  }

  @Override
  public void setTanksDirty() {
    markDirty();
  }

  @Nonnull
  public ExperienceContainer getContainer() {
    return xpCon;
  }

  // RANGE

  private final static Vector4f color = new Vector4f(.46f, 1.0f, .29f, .4f);

  @Override
  public boolean isShowingRange() {
    return showingRange;
  }

  @SideOnly(Side.CLIENT)
  public void setShowRange(boolean showRange) {
    if (showingRange == showRange) {
      return;
    }
    showingRange = showRange;
    if (showingRange) {
      Minecraft.getMinecraft().effectRenderer.addEffect(new RangeParticle<TileXPVacuum>(this, color));
    }
  }

  @Override
  @Nonnull
  public BoundingBox getBounds() {
    return new BoundingBox(getPos()).expand(getRange() + (range == 0 ? 1 / 32f : 0));
  }

  private int limitRange(int rangeIn) {
    return Math.max(0, Math.min(VacuumConfig.vacuumXPRange.get(), rangeIn));
  }

  public void setRange(int range) {
    this.range = limitRange(range);
    markDirty();
  }

  public float getRange() {
    return range;
  }

}
