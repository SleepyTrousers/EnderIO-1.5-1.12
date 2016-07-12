package crazypants.enderio.machine.obelisk.xp;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.common.util.ITankAccess;
import com.enderio.core.common.fluid.FluidWrapper;

import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config;
import crazypants.enderio.fluid.SmartTankFluidHandler;
import crazypants.enderio.fluid.SmartTankFluidMachineHandler;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.xp.ExperienceContainer;
import crazypants.enderio.xp.IHaveExperience;
import crazypants.enderio.xp.PacketExperianceContainer;
import crazypants.enderio.xp.XpUtil;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

@Storable
public class TileExperienceObelisk extends AbstractMachineEntity implements IHaveExperience, ITankAccess {

  @Store
  private ExperienceContainer xpCont = new ExperienceContainer(XpUtil.getExperienceForLevel(Config.xpObeliskMaxXpLevel));

  public TileExperienceObelisk() {
    super(new SlotDefinition(0, 0, 0));
    xpCont.setTileEntity(this);
  }

  @Override
  public @Nonnull String getMachineName() {
    return ModObject.blockExperienceObelisk.getUnlocalisedName();
  }

  @Override
  protected boolean isMachineItemValidForSlot(int i, ItemStack itemstack) {
    return false;
  }

  @Override
  public boolean isActive() {
    return true;
  }

  @Override
  protected boolean processTasks(boolean redstoneCheck) {
    if (xpCont.isDirty()) {
      PacketHandler.sendToAllAround(new PacketExperianceContainer(this), this);
      xpCont.setDirty(false);
    }
    return false;
  }

  @Override
  protected boolean doPull(@Nullable EnumFacing dir) {
    boolean res = super.doPull(dir);
    if (dir != null && xpCont.getFluidAmount() < xpCont.getCapacity()) {
      if (FluidWrapper.transfer(worldObj, getPos().offset(dir), dir.getOpposite(), xpCont, Config.fluidConduitMaxIoRate) > 0) {
        setTanksDirty();
      }
    }
    return res;
  }

  @Override
  protected boolean doPush(@Nullable EnumFacing dir) {
    boolean res = super.doPush(dir);
    if (dir != null && xpCont.getFluidAmount() > 0) {
      if (FluidWrapper.transfer(xpCont, worldObj, getPos().offset(dir), dir.getOpposite(), Config.fluidConduitMaxIoRate) > 0) {
        setTanksDirty();
      }
    }
    return res;
  }

  @Override
  public ExperienceContainer getContainer() {
    return xpCont;
  }

  @Override
  public FluidTank getInputTank(FluidStack forFluidType) {
    return xpCont;
  }

  @Override
  public FluidTank[] getOutputTanks() {
    return new FluidTank[] { xpCont };
  }

  @Override
  public void setTanksDirty() {
    xpCont.setDirty(true);
  }

  private SmartTankFluidHandler smartTankFluidHandler;

  @Override
  public boolean hasCapability(Capability<?> capability, EnumFacing facingIn) {
    return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facingIn);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T getCapability(Capability<T> capability, EnumFacing facingIn) {
    if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
      if (smartTankFluidHandler == null) {
        smartTankFluidHandler = new SmartTankFluidMachineHandler(this, xpCont);
      }
      return (T) smartTankFluidHandler.get(facingIn);
    }
    return super.getCapability(capability, facingIn);
  }

}
