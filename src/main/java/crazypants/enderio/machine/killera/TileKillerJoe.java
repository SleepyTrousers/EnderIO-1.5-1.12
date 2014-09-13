package crazypants.enderio.machine.killera;

import java.util.Collection;
import java.util.List;

import net.minecraft.command.IEntitySelector;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import com.google.common.collect.Multimap;
import com.mojang.authlib.GameProfile;

import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config;
import crazypants.enderio.fluid.LiquidXpUtil;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.machine.generator.zombie.NutrientTank;
import crazypants.enderio.network.PacketHandler;
import crazypants.render.BoundingBox;
import crazypants.util.BlockCoord;
import crazypants.util.FluidUtil;
import crazypants.util.ForgeDirectionOffsets;
import crazypants.vecmath.Vector3d;

public class TileKillerJoe extends AbstractMachineEntity implements IFluidHandler, IEntitySelector {

  private static int IO_MB_TICK = 250;

  protected AxisAlignedBB killBounds;

  protected AxisAlignedBB hooverBounds;

  protected FakePlayer attackera;

  final NutrientTank fuelTank = new NutrientTank(FluidContainerRegistry.BUCKET_VOLUME * 2);

  int lastFluidLevelUpdate;

  private boolean tanksDirty;

  private boolean isSwingInProgress;

  private int swingProgressInt;

  private float swingProgress;

  private float prevSwingProgress;

  int experienceLevel;

  float experience;

  private boolean xpDirty;

  private int experienceTotal;

  public TileKillerJoe() {
    super(new SlotDefinition(1, 0, 0));
  }

  @Override
  public String getMachineName() {
    return ModObject.blockKillerJoe.unlocalisedName;
  }

  @Override
  protected boolean isMachineItemValidForSlot(int i, ItemStack itemstack) {
    if(itemstack == null) {
      return false;
    }
    return itemstack.getItem() instanceof ItemSword;
  }

  @Override
  public boolean isActive() {
    return false;
  }

  @Override
  public float getProgress() {
    return 0;
  }

  @Override
  public void updateEntity() {
    updateArmSwingProgress();
    hooverXP();
    super.updateEntity();
  }

  @Override
  protected boolean processTasks(boolean redstoneCheckPassed) {

    if(!redstoneCheckPassed) {
      return false;
    }

    if(worldObj.getTotalWorldTime() % 10 != 0) {
      return false;
    }

    if(tanksDirty) {
      PacketHandler.sendToAllAround(new PacketNutrientLevel(this), this);
      tanksDirty = false;
    }
    if(xpDirty) {
      PacketHandler.sendToAllAround(new PacketExperianceTotal(this), this);
      xpDirty = false;
    }

    if(fuelTank.getFluidAmount() < fuelTank.getCapacity() * 0.7f) {
      return false;
    }
    
    if(getStackInSlot(0) == null) {
      return false;
    }

    List<EntityLivingBase> entsInBounds = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, getKillBounds());
    if(!entsInBounds.isEmpty()) {

      for (EntityLivingBase ent : entsInBounds) {
        if(!ent.isDead) {
          FakePlayer fakee = getAttackera();
          fakee.setCurrentItemOrArmor(0, getStackInSlot(0));
          fakee.onUpdate();
          fakee.attackTargetEntityWithCurrentItem(ent);
          useNutrient();
          swingWeapon();
          if(getStackInSlot(0).stackSize <= 0) {
            setInventorySlotContents(0, null);
          }
        }
        return false;
      }
    }
    return false;
  }

  //-------------------------------  XP

  public void addExperience(int xpToAdd) {
    int j = Integer.MAX_VALUE - this.experienceTotal;
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

  private int getXpBarCapacity(int level) {
    return level >= 30 ? 62 + (level - 30) * 7 : (level >= 15 ? 17 + (level - 15) * 3 : 17);
  }

  private int getXpBarCapacity() {
    return getXpBarCapacity(experienceLevel);
  }

  public int getXpBarScaled(int scale) {
    int result = (int) (experience * scale);
    return result;

  }
  
  public void givePlayerXp(EntityPlayer player, int levels) {
    for(int i=0;i<levels && experienceTotal > 0; i++) {
      givePlayerXp(player);
    }    
  }

  public void givePlayerXp(EntityPlayer player) {
    if(Config.killerJoeGivePlayerLevelXP) {
      int currentXP = getPlayerXP(player);
      int nextLevelXP = getExperienceForLevel(player.experienceLevel + 1) + 1;
      int requiredXP = nextLevelXP - currentXP;

      requiredXP = Math.min(experienceTotal, requiredXP);      
      player.addExperience(requiredXP);

      int newXp = experienceTotal - requiredXP;
      experience = 0;
      experienceLevel = 0;
      experienceTotal = 0;
      addExperience(newXp);
    } else {
      int takeXp = Math.min(getXpBarCapacity(), experienceTotal);
      player.addExperience(takeXp);

      int newXp = experienceTotal - takeXp;
      experience = 0;
      experienceLevel = 0;
      experienceTotal = 0;
      addExperience(newXp);
    }
  }

  private int getPlayerXP(EntityPlayer player) {
    return (int) (getExperienceForLevel(player.experienceLevel) + (player.experience * player.xpBarCap()));
  }

  private int getExperienceForLevel(int level) {
    if(level == 0) {
      return 0;
    }
    if(level > 0 && level < 16) {
      return level * 17;
    } else if(level > 15 && level < 31) {
      return (int) (1.5 * Math.pow(level, 2) - 29.5 * level + 360);
    } else {
      return (int) (3.5 * Math.pow(level, 2) - 151.5 * level + 2220);
    }
  }

  private void hooverXP() {

    double maxDist = Config.killerJoeAttackLength * 2;

    List<EntityXPOrb> xp = worldObj.selectEntitiesWithinAABB(EntityXPOrb.class, getHooverBounds(), this);

    for (EntityXPOrb entity : xp) {
      double xDist = (xCoord + 0.5D - entity.posX);
      double yDist = (yCoord + 0.5D - entity.posY);
      double zDist = (zCoord + 0.5D - entity.posZ);

      double totalDistance = Math.sqrt(xDist * xDist + yDist * yDist + zDist * zDist);

      if(totalDistance < 1.5) {
        hooverXP(entity);
      } else {
        double d = 1 - (Math.max(0.1, totalDistance) / maxDist);
        double speed = 0.0025 + (d * 0.02);

        entity.motionX += xDist / totalDistance * speed;
        entity.motionZ += zDist / totalDistance * speed;
        entity.motionY += yDist / totalDistance * speed;
        if(yDist > 0.5) {
          entity.motionY = 0.12;
        }

      }
    }
  }

  private void hooverXP(EntityXPOrb entity) {
    if(!worldObj.isRemote) {
      if(!entity.isDead) {
        addExperience(entity.getXpValue());
        entity.setDead();
      }
    }
  }

  @Override
  public boolean isEntityApplicable(Entity arg0) {
    return true;
  }

  //------------------------------- Weapon stuffs

  void swingWeapon() {
    if(getStackInSlot(0) == null) {
      return;
    }
    if(!isSwingInProgress || swingProgressInt >= getArmSwingAnimationEnd() / 2 || swingProgressInt < 0) {
      swingProgressInt = -1;
      isSwingInProgress = true;
      if(worldObj instanceof WorldServer) {
        PacketHandler.sendToAllAround(new PacketSwing(this), this);
      }
    }
  }

  float getSwingProgress(float p_70678_1_) {
    float f1 = swingProgress - prevSwingProgress;

    if(f1 < 0.0F) {
      ++f1;
    }

    return prevSwingProgress + f1 * p_70678_1_;
  }

  private void updateArmSwingProgress() {

    prevSwingProgress = swingProgress;

    int i = getArmSwingAnimationEnd();
    if(isSwingInProgress) {
      ++swingProgressInt;
      if(swingProgressInt >= i) {
        swingProgressInt = 0;
        isSwingInProgress = false;
      }
    } else {
      swingProgressInt = 0;
    }
    swingProgress = (float) swingProgressInt / (float) i;
  }

  private int getArmSwingAnimationEnd() {
    return 6;
  }

  FakePlayer getAttackera() {
    if(attackera == null) {  
      attackera = new Attackera();
    }
    return attackera;
  }

  private AxisAlignedBB getKillBounds() {
    if(killBounds == null) {
      BoundingBox bb = new BoundingBox(getLocation());
      Vector3d min = bb.getMin();
      Vector3d max = bb.getMax();
      max.y += Config.killerJoeAttackHeight;
      min.y -= Config.killerJoeAttackHeight;

      ForgeDirection facingDir = ForgeDirection.getOrientation(facing);
      if(ForgeDirectionOffsets.isPositiveOffset(facingDir)) {
        max.add(ForgeDirectionOffsets.offsetScaled(facingDir, Config.killerJoeAttackLength));
        min.add(ForgeDirectionOffsets.forDir(facingDir));
      } else {
        min.add(ForgeDirectionOffsets.offsetScaled(facingDir, Config.killerJoeAttackLength));
        max.add(ForgeDirectionOffsets.forDir(facingDir));

      }
      if(facingDir.offsetX == 0) {
        min.x -= Config.killerJoeAttackWidth;
        max.x += Config.killerJoeAttackWidth;
      } else {
        min.z -= Config.killerJoeAttackWidth;
        max.z += Config.killerJoeAttackWidth;
      }
      killBounds = AxisAlignedBB.getBoundingBox(min.x, min.y, min.z, max.x, max.y, max.z);
    }
    return killBounds;
  }

  private AxisAlignedBB getHooverBounds() {
    if(hooverBounds == null) {
      BoundingBox bb = new BoundingBox(getLocation());
      Vector3d min = bb.getMin();
      Vector3d max = bb.getMax();
      max.y += Config.killerJoeAttackHeight;
      min.y -= Config.killerJoeAttackHeight;

      ForgeDirection facingDir = ForgeDirection.getOrientation(facing);
      if(ForgeDirectionOffsets.isPositiveOffset(facingDir)) {
        max.add(ForgeDirectionOffsets.offsetScaled(facingDir, Config.killerJoeAttackLength * 2));
        min.add(ForgeDirectionOffsets.forDir(facingDir));
      } else {
        min.add(ForgeDirectionOffsets.offsetScaled(facingDir, Config.killerJoeAttackLength * 2));
        max.add(ForgeDirectionOffsets.forDir(facingDir));

      }
      if(facingDir.offsetX == 0) {
        min.x -= Config.killerJoeAttackWidth * 2;
        max.x += Config.killerJoeAttackWidth * 2;
      } else {
        min.z -= Config.killerJoeAttackWidth * 2;
        max.z += Config.killerJoeAttackWidth * 2;
      }
      hooverBounds = AxisAlignedBB.getBoundingBox(min.x, min.y, min.z, max.x, max.y, max.z);
    }
    return hooverBounds;
  }

  //------------------------------- Power

  @Override
  public boolean canConnectEnergy(ForgeDirection from) {
    return false;
  }

  //-------------------------------  Fluid Stuff

  private void useNutrient() {
    fuelTank.drain(Config.killerJoeNutrientUsePerAttackMb, true);
    tanksDirty = true;
  }

  @Override
  protected boolean doPull(ForgeDirection dir) {
    boolean res = super.doPull(dir);
    BlockCoord loc = getLocation().getLocation(dir);
    IFluidHandler target = FluidUtil.getFluidHandler(worldObj, loc);
    if(target != null) {
      FluidTankInfo[] infos = target.getTankInfo(dir.getOpposite());
      if(infos != null) {
        for (FluidTankInfo info : infos) {
          if(info.fluid != null && info.fluid.amount > 0) {
            if(canFill(dir, info.fluid.getFluid())) {
              FluidStack canPull = info.fluid.copy();
              canPull.amount = Math.min(IO_MB_TICK, canPull.amount);
              FluidStack drained = target.drain(dir.getOpposite(), canPull, false);
              if(drained != null && drained.amount > 0) {
                int filled = fill(dir, drained, false);
                if(filled > 0) {
                  drained = target.drain(dir.getOpposite(), filled, true);
                  fill(dir, drained, true);
                  return res;
                }
              }
            }
          }
        }
      }
    }
    return res;
  }

  @Override
  public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
    if(resource == null || resource.getFluid() == null || !canFill(from, resource.getFluid())) {
      return 0;
    }
    int res = fuelTank.fill(resource, doFill);
    if(res > 0 && doFill) {
      tanksDirty = true;
    }
    return res;
  }

  @Override
  public boolean canFill(ForgeDirection from, Fluid fluid) {
    return fluid != null && fluid.getID() == EnderIO.fluidNutrientDistillation.getID();
  }

  @Override
  public FluidTankInfo[] getTankInfo(ForgeDirection from) {
    return new FluidTankInfo[] { fuelTank.getInfo() };
  }

  @Override
  public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
    if(resource == null || !canDrain(from, resource.getFluid())) {
      return null;
    }    
    return drain(from, resource.amount, doDrain);
  }

  @Override
  public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
    int available = LiquidXpUtil.XPToLiquidRatio(experienceTotal);
    int canDrain = Math.min(available, maxDrain);
    if(doDrain) {      
      int newXp = experienceTotal - LiquidXpUtil.liquidToXPRatio(canDrain);
      experience = 0;
      experienceLevel = 0;
      experienceTotal = 0;
      addExperience(newXp);      
    }        
    return new FluidStack(EnderIO.fluidXpJuice, canDrain);
  }

  @Override
  public boolean canDrain(ForgeDirection from, Fluid fluid) {
    return fluid != null && fluid.getID() == EnderIO.fluidXpJuice.getID();
  }

  //-------------------------------  Save / Load

  @Override
  public void readCommon(NBTTagCompound nbtRoot) {
    super.readCommon(nbtRoot);
    if(nbtRoot.hasKey("fuelTank")) {
      NBTTagCompound tankRoot = (NBTTagCompound) nbtRoot.getTag("fuelTank");
      if(tankRoot != null) {
        fuelTank.readFromNBT(tankRoot);
      } else {
        fuelTank.setFluid(null);
      }
    } else {
      fuelTank.setFluid(null);
    }

    experienceLevel = nbtRoot.getInteger("experienceLevel");
    experienceTotal = nbtRoot.getInteger("experienceTotal");
    experience = nbtRoot.getFloat("experience");
  }

  @Override
  public void writeCommon(NBTTagCompound nbtRoot) {
    super.writeCommon(nbtRoot);
    if(fuelTank.getFluidAmount() > 0) {
      NBTTagCompound tankRoot = new NBTTagCompound();
      fuelTank.writeToNBT(tankRoot);
      nbtRoot.setTag("fuelTank", tankRoot);
    }

    nbtRoot.setInteger("experienceLevel", experienceLevel);
    nbtRoot.setInteger("experienceTotal", experienceTotal);
    nbtRoot.setFloat("experience", experience);

  }

  private class Attackera extends FakePlayer {

    ItemStack prevWeapon;

    public Attackera() {
      super(MinecraftServer.getServer().worldServerForDimension(getWorldObj().provider.dimensionId), new GameProfile(null,
          BlockKillerJoe.USERNAME + ":" + getLocation()));
      posX = xCoord + 0.5;
      posY = yCoord + 0.5;
      posZ = zCoord + 0.5;
    }

    @Override
    public void onUpdate() {
      ItemStack prev = prevWeapon;
      ItemStack cur = getCurrentEquippedItem();
      if(!ItemStack.areItemStacksEqual(cur, prev)) {
        if(prev != null) {
          getAttributeMap().removeAttributeModifiers(prev.getAttributeModifiers());
        }

        if(cur != null) {
          getAttributeMap().applyAttributeModifiers(cur.getAttributeModifiers());
        }
        prevWeapon = cur == null ? null : cur.copy();
      }
    }

  }

}
