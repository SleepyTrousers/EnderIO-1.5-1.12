package crazypants.enderio.machine.soul;

import java.util.List;

import scala.xml.persistent.SetStorage;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import crazypants.enderio.EnderIO;
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
import crazypants.enderio.power.ICapacitor;
import crazypants.enderio.power.PowerHandlerUtil;
import crazypants.enderio.xp.ExperienceContainer;
import crazypants.enderio.xp.IHaveExperience;
import crazypants.enderio.xp.PacketExperianceContainer;
import crazypants.enderio.xp.XpUtil;

public class TileSoulBinder extends AbstractPoweredTaskEntity implements IHaveExperience, IFluidHandler {

  public static final int POWER_PER_TICK_ONE = Config.soulBinderLevelOnePowerPerTickRF;
  private static final BasicCapacitor CAP_ONE = new BasicCapacitor(POWER_PER_TICK_ONE * 2, 
      Capacitors.BASIC_CAPACITOR.capacitor.getMaxEnergyStored(), POWER_PER_TICK_ONE);

  public static final int POWER_PER_TICK_TWO = Config.soulBinderLevelTwoPowerPerTickRF;
  private static final BasicCapacitor CAP_TWO = new BasicCapacitor(POWER_PER_TICK_TWO * 2,
      Capacitors.ACTIVATED_CAPACITOR.capacitor.getMaxEnergyStored(), POWER_PER_TICK_TWO);

  public static final int  POWER_PER_TICK_THREE = Config.soulBinderLevelThreePowerPerTickRF;
  private static final BasicCapacitor CAP_THREE = new BasicCapacitor(POWER_PER_TICK_THREE * 2,
      Capacitors.ENDER_CAPACITOR.capacitor.getMaxEnergyStored(), POWER_PER_TICK_THREE);
  
  private ICapacitor capacitor;
  
  private ExperienceContainer xpCont = new ExperienceContainer(XpUtil.getExperienceForLevel(40));
  
  public TileSoulBinder() {
    super(new SlotDefinition(2, 2, 1));
    capacitor = CAP_ONE;
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

  public void setCapacitor(Capacitors capacitorType) {    
    switch (capacitorType) {
    case BASIC_CAPACITOR:
      capacitor = CAP_ONE;
      break;
    case ACTIVATED_CAPACITOR:
      capacitor = CAP_TWO;
      break;
    case ENDER_CAPACITOR:
      capacitor = CAP_THREE;
      break;
    default:
      capacitor = CAP_ONE;
      break;
    }
    super.setCapacitor(capacitorType);
  }
  
  @Override
  public ICapacitor getCapacitor() {
    return capacitor;
  }

  @Override
  public int getPowerUsePerTick() {
    if(capacitorType.ordinal() == 0) {
      return POWER_PER_TICK_ONE;
    } else if(capacitorType.ordinal() == 1) {
      return POWER_PER_TICK_TWO;
    }
    return POWER_PER_TICK_THREE;
  }

  @Override
  public int getProgressScaled(int scale) {
    int res = super.getProgressScaled(scale);
    if(currentTask != null) {
      res = Math.max(1, res);
    }
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
  
  
  
}
