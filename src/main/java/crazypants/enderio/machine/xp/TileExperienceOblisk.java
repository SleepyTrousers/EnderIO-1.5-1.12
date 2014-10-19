package crazypants.enderio.machine.xp;

import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.xp.ExperienceContainer;
import crazypants.enderio.xp.IHaveExperience;
import crazypants.enderio.xp.PacketExperianceContainer;
import crazypants.util.BlockCoord;
import crazypants.util.FluidUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileExperienceOblisk extends AbstractMachineEntity implements IFluidHandler, IHaveExperience {

  private ExperienceContainer xpCont = new ExperienceContainer();

  public TileExperienceOblisk() {
    super(new SlotDefinition(0, 0, 0));
  }

  @Override
  public String getMachineName() {
    return ModObject.blockExperienceObelisk.unlocalisedName;
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
  public float getProgress() {
    return 0;
  }
  
  @Override
  public boolean canConnectEnergy(ForgeDirection from) {
    return false;
  }

  @Override
  protected boolean processTasks(boolean redstoneCheckPassed) {
    if(xpCont.isDirty()) {
      PacketHandler.sendToAllAround(new PacketExperianceContainer(this), this);
      xpCont.setDirty(false);
    }
    return false;
  }
  
  @Override
  protected boolean doPull(ForgeDirection dir) {
    boolean res = super.doPull(dir);
    FluidUtil.doPull(this, dir, Config.fluidConduitMaxIoRate);
    return res;
  }
  
  @Override
  protected boolean doPush(ForgeDirection dir) {
    boolean res = super.doPush(dir);
    FluidUtil.doPush(this, dir, Config.fluidConduitMaxIoRate);
    return res;
  }

  @Override
  public boolean canFill(ForgeDirection from, Fluid fluid) {
    return xpCont.canFill(from, fluid);
  }

  @Override
  public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
    return xpCont.fill(from, resource, doFill);
  }

  @Override
  public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
    return xpCont.drain(from, resource, doDrain);
  }

  @Override
  public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
    return xpCont.drain(from, maxDrain, doDrain);
  }

  @Override
  public boolean canDrain(ForgeDirection from, Fluid fluid) {
    return xpCont.canDrain(from, fluid);
  }

  @Override
  public FluidTankInfo[] getTankInfo(ForgeDirection from) {
    return xpCont.getTankInfo(from);
  }

  @Override
  public void readCommon(NBTTagCompound nbtRoot) {
    super.readCommon(nbtRoot);
    xpCont.readFromNBT(nbtRoot);
  }

  @Override
  public void writeCommon(NBTTagCompound nbtRoot) {
    super.writeCommon(nbtRoot);
    xpCont.writeToNBT(nbtRoot);
  }

  @Override
  public ExperienceContainer getContainer() {
    return xpCont;
  }

}
