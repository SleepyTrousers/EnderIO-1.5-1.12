package crazypants.enderio.machine.vacuum;

import javax.annotation.Nullable;

import com.enderio.core.api.common.util.ITankAccess;
import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.common.fluid.FluidWrapper;
import com.google.common.base.Predicate;

import crazypants.enderio.TileEntityEio;
import crazypants.enderio.config.Config;
import crazypants.enderio.fluid.Fluids;
import crazypants.enderio.paint.IPaintable;
import crazypants.enderio.paint.YetaUtil;
import crazypants.enderio.xp.ExperienceContainer;
import crazypants.util.MagnetUtil;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import info.loenwind.autosave.annotations.Store.StoreFor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

@Storable
public class TileXPVacuum extends TileEntityEio implements Predicate<EntityXPOrb>, IPaintable.IPaintableTileEntity, ITankAccess {

  private static final int IO_MB_TICK = 1000;

  private double range = Config.xpVacuumRange;

  @Store
  private boolean formed = false;
  private boolean formedRender = false;

  @Store({ StoreFor.SAVE, StoreFor.ITEM })
  private final ExperienceContainer xpCon;

  public TileXPVacuum() {
    super();

    xpCon = new ExperienceContainer(Integer.MAX_VALUE);
    xpCon.setTileEntity(this);
    xpCon.setCanFill(!formed);
  }

  @Override
  public void doUpdate() {
    if (worldObj.isRemote) {
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
    return MagnetUtil.shouldAttract(getPos(), entity);
  }

  private static final double speed = 0.03;

  private void doHoover() {
    for (EntityXPOrb entity : worldObj.getEntitiesWithinAABB(EntityXPOrb.class, getBounds(), this)) {
      double x = (pos.getX() + 0.5D - entity.posX);
      double y = (pos.getY() + 0.5D - entity.posY);
      double z = (pos.getZ() + 0.5D - entity.posZ);

      double distance = Math.sqrt(x * x + y * y + z * z);
      if (distance < 1.25) {
        hooverEntity(entity);
      } else {
        double distScale = Math.min(1d, Math.max(0.25d, 1d - distance / range));
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

  private void hooverEntity(EntityXPOrb entity) {
    if (!worldObj.isRemote && !entity.isDead && xpCon.getFluidAmount() == 0) {
      int xpValue = entity.getXpValue();
      xpCon.addExperience(xpValue);
      entity.setDead();
    }
  }

  private int limitRange(int rangeIn) {
    return Math.max(0, Math.min(Config.vacuumChestRange, rangeIn));
  }

  public void setRange(int range) {
    this.range = limitRange(range);
    updateBlock();
  }

  @Override
  public boolean equals(@Nullable Object obj) {
    return super.equals(obj);
  }

  @Store({ StoreFor.CLIENT, StoreFor.SAVE })
  protected IBlockState sourceBlock;

  @Override
  public IBlockState getPaintSource() {
    return sourceBlock;
  }

  @Override
  public void setPaintSource(@Nullable IBlockState sourceBlock) {
    this.sourceBlock = sourceBlock;
    markDirty();
    updateBlock();
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  // RANGE

  public BoundingBox getBounds() {
    return new BoundingBox(getPos()).expand(getRange());
  }

  public double getRange() {
    return range;
  }

  // RANGE END

  private void doPush() {
    for (EnumFacing dir : EnumFacing.values()) {
      if (xpCon.getFluidAmount() > 0 && FluidWrapper.transfer(xpCon, worldObj, getPos().offset(dir), dir.getOpposite(), IO_MB_TICK) > 0) {
        setTanksDirty();
      }
    }
  }

  public boolean isFormed() {
    return formedRender;
  }

  // TANK

  @Override
  public FluidTank getInputTank(FluidStack forFluidType) {
    if (!formed && forFluidType != null && forFluidType.getFluid() == Fluids.fluidXpJuice) {
      return xpCon;
    }
    return null;
  }

  @Override
  public FluidTank[] getOutputTanks() {
    return new FluidTank[] { xpCon };
  }

  @Override
  public void setTanksDirty() {
    markDirty();
  }

  @Override
  public boolean hasCapability(Capability<?> capability, EnumFacing facingIn) {
    if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
      return true;
    }
    return super.hasCapability(capability, facingIn);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T getCapability(Capability<T> capability, EnumFacing facingIn) {
    if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
      return (T) xpCon;
    }
    return super.getCapability(capability, facingIn);
  }


}
