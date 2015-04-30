package crazypants.enderio.machine.soul;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import com.enderio.core.common.util.FluidUtil;
import com.enderio.core.common.util.ITankAccess;

import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.AbstractPoweredTaskEntity;
import crazypants.enderio.machine.IMachineRecipe;
import crazypants.enderio.machine.MachineRecipeInput;
import crazypants.enderio.machine.MachineRecipeRegistry;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.power.BasicCapacitor;
import crazypants.enderio.power.Capacitors;
import crazypants.enderio.xp.ExperienceContainer;
import crazypants.enderio.xp.IHaveExperience;
import crazypants.enderio.xp.PacketExperianceContainer;
import crazypants.enderio.xp.XpUtil;

public class TileSoulBinder extends AbstractPoweredTaskEntity implements IHaveExperience, IFluidHandler, ITankAccess {

  public static final int POWER_PER_TICK_ONE = Config.soulBinderLevelOnePowerPerTickRF;
  private static final BasicCapacitor CAP_ONE = new BasicCapacitor(POWER_PER_TICK_ONE * 2, 
      Capacitors.BASIC_CAPACITOR.capacitor.getMaxEnergyStored(), POWER_PER_TICK_ONE);

  public static final int POWER_PER_TICK_TWO = Config.soulBinderLevelTwoPowerPerTickRF;
  private static final BasicCapacitor CAP_TWO = new BasicCapacitor(POWER_PER_TICK_TWO * 2,
      Capacitors.ACTIVATED_CAPACITOR.capacitor.getMaxEnergyStored(), POWER_PER_TICK_TWO);

  public static final int  POWER_PER_TICK_THREE = Config.soulBinderLevelThreePowerPerTickRF;
  private static final BasicCapacitor CAP_THREE = new BasicCapacitor(POWER_PER_TICK_THREE * 2,
      Capacitors.ENDER_CAPACITOR.capacitor.getMaxEnergyStored(), POWER_PER_TICK_THREE);
  
  private final ExperienceContainer xpCont = new ExperienceContainer(XpUtil.getExperienceForLevel(Config.soulBinderMaxXpLevel));
  
  public TileSoulBinder() {
    super(new SlotDefinition(2, 2, 1));
  }

  @Override
  public ExperienceContainer getContainer() {  
    return xpCont;
  }

  @Override
  public String getMachineName() {    
    return ModObject.blockSoulBinder.unlocalisedName;
  }
    
  @Override
  public int getInventoryStackLimit() {
    return 1;
  }
  
  @Override
  protected boolean processTasks(boolean redstoneChecksPassed) {
    if(xpCont.isDirty()) {
      PacketHandler.sendToAllAround(new PacketExperianceContainer(this), this);
      xpCont.setDirty(false);
    }
    return super.processTasks(redstoneChecksPassed);
  }

  @Override
  protected IMachineRecipe canStartNextTask(float chance) {
    IMachineRecipe recipe = super.canStartNextTask(chance);
    if(recipe == null) {
      return null;
    }
    int xpRequired = ((ISoulBinderRecipe)recipe).getExperienceRequired();
    if(xpCont.getExperienceTotal() >= xpRequired) {
      return recipe;
    }
    return null;
  }
  
  public int getCurrentlyRequiredLevel() {
    if(currentTask != null) {
      return -1;
    }
    IMachineRecipe nextRecipe = MachineRecipeRegistry.instance.getRecipeForInputs(getMachineName(), getRecipeInputs());
    if(! (nextRecipe instanceof ISoulBinderRecipe)) {
      return -1;
    }
    return ((ISoulBinderRecipe)nextRecipe).getExperienceRequired();    
  }
  

  @Override
  protected boolean startNextTask(IMachineRecipe nextRecipe, float chance) {
    int xpRequired = ((ISoulBinderRecipe)nextRecipe).getExperienceRequired();
    if(xpCont.getExperienceLevel() < xpRequired) {
      return false;
    }        
    if(super.startNextTask(nextRecipe, chance)) {           
      xpCont.drain(ForgeDirection.UNKNOWN, XpUtil.getLiquidForLevel(xpRequired), true);
      return true;
    }
    return false;
  }

  @Override
  protected boolean isMachineItemValidForSlot(int slot, ItemStack item) {
    if(!slotDefinition.isInputSlot(slot)) {
      return false;
    }
    MachineRecipeInput newInput = new MachineRecipeInput(slot, item);
    int otherSlot = slot == 0 ? 1 : 0;    
    if(inventory[otherSlot] == null) {
      List<IMachineRecipe> recipes = MachineRecipeRegistry.instance.getRecipesForInput(getMachineName(), newInput);
      if(recipes.isEmpty()) {
        return false;
      }    
      for(IMachineRecipe rec : recipes) {
        if(rec != null && rec.isValidInput(newInput)) {
          return true;
        }
      }  
    } else {
      MachineRecipeInput[] inputs = new MachineRecipeInput[] {
          newInput,
          new MachineRecipeInput(otherSlot, inventory[otherSlot])
      };
      return MachineRecipeRegistry.instance.getRecipeForInputs(getMachineName(), inputs) != null;
    }
    return false;
  }

  @Override
  public void onCapacitorTypeChange() {
    switch (getCapacitorType()) {
    case BASIC_CAPACITOR:
      setCapacitor(CAP_ONE);
      break;
    case ACTIVATED_CAPACITOR:
      setCapacitor(CAP_TWO);
      break;
    case ENDER_CAPACITOR:
      setCapacitor(CAP_THREE);
      break;
    }
  }

  @Override
  protected boolean doPull(ForgeDirection dir) {
    boolean res = super.doPull(dir);
    FluidUtil.doPull(this, dir, Config.fluidConduitExtractRate);
    return res;
  }
  
  @Override
  protected boolean doPush(ForgeDirection dir) {
    boolean res = super.doPush(dir);
    FluidUtil.doPush(this, dir, Config.fluidConduitExtractRate);
    return res;
  }

  @Override
  public boolean canFill(ForgeDirection from, Fluid fluid) {    
    return xpCont.canFill(from, fluid);
  }
  
  @Override
  public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {   
    return xpCont.fill(from, resource,doFill);
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
  
}
