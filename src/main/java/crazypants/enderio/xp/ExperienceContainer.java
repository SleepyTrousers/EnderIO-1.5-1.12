package crazypants.enderio.xp;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import crazypants.enderio.EnderIO;
import crazypants.enderio.config.Config;

public class ExperienceContainer {

  private int experienceLevel;
  private float experience;
  private int experienceTotal;
  private boolean xpDirty;
  private final int maxXp;
  
  public ExperienceContainer() {
    this(Integer.MAX_VALUE);
  }
  
  public ExperienceContainer(int maxStored) {
    maxXp = maxStored;
  }
  
  public int getMaximumExperiance() {    
    return maxXp;
  }

  public int getExperienceLevel() {
    return experienceLevel;
  }

  public float getExperience() {
    return experience;
  }

  public int getExperienceTotal() {
    return experienceTotal;
  }

  public boolean isDirty() {
    return xpDirty;
  }
  
  public void setDirty(boolean isDirty) {
    xpDirty = isDirty;
  }
  
  public void set(ExperienceContainer xpCon) {
    experienceTotal = xpCon.experienceTotal;
    experienceLevel = xpCon.experienceLevel;
    experience = xpCon.experience;    
  }

  public void addExperience(int xpToAdd) {
    int j = maxXp - experienceTotal;
    if(xpToAdd > j) {
      xpToAdd = j;
    }

    experience += (float) xpToAdd / (float) getXpBarCapacity();
    experienceTotal += xpToAdd;
    for (; experience >= 1.0F; experience /= (float) getXpBarCapacity()) {
      experience = (experience - 1.0F) * (float) getXpBarCapacity();
      experienceLevel++;
    }
    xpDirty = true;
  }

  private int getXpBarCapacity() {
    return XpUtil.getXpBarCapacity(experienceLevel);
  }

  public int getXpBarScaled(int scale) {
    int result = (int) (experience * scale);
    return result;

  }

  public void givePlayerXp(EntityPlayer player, int levels) {
    for (int i = 0; i < levels && experienceTotal > 0; i++) {
      givePlayerXpLevel(player);
    }
  }

  public void givePlayerXpLevel(EntityPlayer player) {
    int currentXP = XpUtil.getPlayerXP(player);
    int nextLevelXP = XpUtil.getExperienceForLevel(player.experienceLevel + 1) + 1;
    int requiredXP = nextLevelXP - currentXP;

    requiredXP = Math.min(experienceTotal, requiredXP);
    player.addExperience(requiredXP);

    int newXp = experienceTotal - requiredXP;
    experience = 0;
    experienceLevel = 0;
    experienceTotal = 0;
    addExperience(newXp);
  }
  
    
  public void drainPlayerXpToReachLevel(EntityPlayer player, int level) {    
    int targetXP = XpUtil.getExperienceForLevel(level);
    int requiredXP = targetXP - experienceTotal;
    if(requiredXP <= 0) {
      return;
    }
    int drainXP = Math.min(requiredXP, XpUtil.getPlayerXP(player));
    addExperience(drainXP);
    XpUtil.addPlayerXP(player, -drainXP);    
  }
  
  public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
    if(resource == null || !canDrain(from, resource.getFluid())) {
      return null;
    }    
    return drain(from, resource.amount, doDrain);
  }

  
  public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
    int available = getFluidAmount();
    int canDrain = Math.min(available, maxDrain);
    if(doDrain) {      
      int newXp = experienceTotal - XpUtil.liquidToExperiance(canDrain);
      experience = 0;
      experienceLevel = 0;
      experienceTotal = 0;
      addExperience(newXp);      
    }        
    return new FluidStack(EnderIO.fluidXpJuice, canDrain);
  }

  public boolean canFill(ForgeDirection from, Fluid fluid) {
    return fluid != null && fluid.getID() == EnderIO.fluidXpJuice.getID();
  }
  
  public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
    if(resource == null) {
      return 0;
    }
    if(!canFill(from, resource.getFluid())) {
      return 0;
    }
    int canFill = Math.min(resource.amount, getMaxFluidAmount() - getFluidAmount());
    if(doFill) {
      addExperience(XpUtil.liquidToExperiance(canFill));
    }
    return canFill;
  }
  
  public boolean canDrain(ForgeDirection from, Fluid fluid) {
    return fluid != null && fluid.getID() == EnderIO.fluidXpJuice.getID();
  }
  
  public FluidTankInfo[] getTankInfo(ForgeDirection from) {    
    return new FluidTankInfo[] {
      new FluidTankInfo(new FluidStack(EnderIO.fluidXpJuice, getFluidAmount()), getMaxFluidAmount())  
    };
  }

  private int getMaxFluidAmount() {
    return XpUtil.experianceToLiquid(maxXp);
  }

  private int getFluidAmount() {
   return XpUtil.experianceToLiquid(experienceTotal);
  }
  
  public void readFromNBT(NBTTagCompound nbtRoot) {
    experienceLevel = nbtRoot.getInteger("experienceLevel");
    experienceTotal = nbtRoot.getInteger("experienceTotal");
    experience = nbtRoot.getFloat("experience");
  }
  
  
  public void writeToNBT(NBTTagCompound nbtRoot) {   
    nbtRoot.setInteger("experienceLevel", experienceLevel);
    nbtRoot.setInteger("experienceTotal", experienceTotal);
    nbtRoot.setFloat("experience", experience);
  }
   
  public void toBytes(ByteBuf buf) {
    buf.writeInt(experienceTotal);
    buf.writeInt(experienceLevel);
    buf.writeFloat(experience);    
  }
  
  public void fromBytes(ByteBuf buf) {
    experienceTotal = buf.readInt();
    experienceLevel = buf.readInt();
    experience = buf.readFloat();
  }

}
