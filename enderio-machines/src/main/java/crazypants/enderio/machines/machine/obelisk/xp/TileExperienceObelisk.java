package crazypants.enderio.machines.machine.obelisk.xp;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.common.util.ITankAccess;
import com.enderio.core.common.fluid.FluidWrapper;
import com.enderio.core.common.fluid.SmartTankFluidHandler;

import crazypants.enderio.base.fluid.SmartTankFluidMachineHandler;
import crazypants.enderio.base.machine.baselegacy.AbstractInventoryMachineEntity;
import crazypants.enderio.base.machine.baselegacy.SlotDefinition;
import crazypants.enderio.base.xp.ExperienceContainer;
import crazypants.enderio.base.xp.IHaveExperience;
import crazypants.enderio.base.xp.PacketExperienceContainer;
import crazypants.enderio.base.xp.XpUtil;
import crazypants.enderio.machines.config.config.ExperienceConfig;
import crazypants.enderio.machines.config.config.XPObeliskConfig;
import crazypants.enderio.machines.init.MachineObject;
import crazypants.enderio.machines.network.PacketHandler;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

@Storable
public class TileExperienceObelisk extends AbstractInventoryMachineEntity implements IHaveExperience, ITankAccess {

  @Store
  private final @Nonnull ExperienceContainer xpCont = new ExperienceContainer(XpUtil.getExperienceForLevel(XPObeliskConfig.maxLevels.get()));

  public TileExperienceObelisk() {
    super(new SlotDefinition(0, 0, 0));
    xpCont.setTileEntity(this);
  }

  @Override
  public @Nonnull String getMachineName() {
    return MachineObject.block_experience_obelisk.getUnlocalisedName();
  }

  @Override
  public boolean isMachineItemValidForSlot(int i, @Nonnull ItemStack itemstack) {
    return false;
  }

  @Override
  public boolean isActive() {
    return true;
  }

  @Override
  protected boolean processTasks(boolean redstoneCheck) {
    if (xpCont.isDirty()) {
      PacketHandler.sendToAllAround(new PacketExperienceContainer(this), this);
      xpCont.setDirty(false);
    }
    return false;
  }

  @Override
  protected boolean doPull(@Nullable EnumFacing dir) {
    if (super.doPull(dir)) {
      return true;
    }
    if (dir != null && xpCont.getFluidAmount() < xpCont.getCapacity()) {
      if (FluidWrapper.transfer(world, getPos().offset(dir), dir.getOpposite(), xpCont, ExperienceConfig.maxIO.get()) > 0) {
        setTanksDirty();
        return true;
      }
    }
    return false;
  }

  @Override
  protected boolean doPush(@Nullable EnumFacing dir) {
    if (super.doPush(dir)) {
      return true;
    }
    if (dir != null && xpCont.getFluidAmount() > 0) {
      if (FluidWrapper.transfer(xpCont, world, getPos().offset(dir), dir.getOpposite(), ExperienceConfig.maxIO.get()) > 0) {
        setTanksDirty();
        return true;
      }
    }
    return false;
  }

  @Override
  public @Nonnull ExperienceContainer getContainer() {
    return xpCont;
  }

  @Override
  public FluidTank getInputTank(FluidStack forFluidType) {
    return xpCont;
  }

  @Override
  public @Nonnull FluidTank[] getOutputTanks() {
    return new FluidTank[] { xpCont };
  }

  @Override
  public void setTanksDirty() {
    xpCont.setDirty(true);
  }

  private SmartTankFluidHandler smartTankFluidHandler;

  protected SmartTankFluidHandler getSmartTankFluidHandler() {
    if (smartTankFluidHandler == null) {
      smartTankFluidHandler = new SmartTankFluidMachineHandler(this, xpCont);
    }
    return smartTankFluidHandler;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facingIn) {
    if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
      return (T) getSmartTankFluidHandler().get(facingIn);
    }
    return super.getCapability(capability, facingIn);
  }

}
