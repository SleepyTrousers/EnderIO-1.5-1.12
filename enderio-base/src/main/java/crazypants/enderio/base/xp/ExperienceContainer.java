package crazypants.enderio.base.xp;

import java.security.InvalidParameterException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.common.util.ITankAccess;
import com.enderio.core.common.util.FluidUtil;

import crazypants.enderio.base.Log;
import crazypants.enderio.base.fluid.Fluids;
import crazypants.enderio.base.xp.XpUtil.TooManyXPLevelsException;
import crazypants.enderio.util.MathUtil;
import info.loenwind.autoconfig.factory.IValue;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;

public class ExperienceContainer extends FluidTank {
  // Note: We extend FluidTank instead of implementing IFluidTank because it has
  // some methods we need.

  public static final long MAX_XP_POINTS = XpUtil.liquidToExperience(Long.MAX_VALUE);

  // public/non-final for unit tests only
  public static @Nonnull IValue<Fluid> XP = Fluids.XP_JUICE::getFluid;

  private long experienceTotal;
  private boolean xpDirty;
  private final long maxXp;

  public ExperienceContainer() {
    this(Long.MAX_VALUE);
  }

  public ExperienceContainer(long maxStored) {
    super(null, 0);
    maxXp = Math.min(maxStored, MAX_XP_POINTS); // about 71,582,789 levels
  }

  /**
   * @return Number of XP points that can be stored stored.
   */
  public long getMaximumExperience() {
    return maxXp;
  }

  /**
   * @return Number of levels in the tank (see also {@link EntityPlayer#experienceLevel}).
   */
  public int getExperienceLevel() {
    return XpUtil.getLevelForExperience(experienceTotal);
  }

  /**
   * @return Percentage of next level reached in tank (see also {@link EntityPlayer#experience}).
   */
  public float getExperience() {
    return (experienceTotal - XpUtil.getExperienceForLevelL(getExperienceLevel())) / (float) getXpBarCapacity();
  }

  /**
   * @return Number of XP points stored. Amounts above {@link Integer#MAX_VALUE} will be limited to that.
   */
  public int getExperienceTotalIntLimited() {
    return MathUtil.limit(experienceTotal);
  }

  /**
   * @return Number of XP points stored.
   */
  public long getExperienceTotal() {
    return experienceTotal;
  }

  public boolean isDirty() {
    return xpDirty;
  }

  public void setDirty(boolean isDirty) {
    xpDirty = isDirty;
  }

  public void set(@Nonnull ExperienceContainer xpCon) {
    experienceTotal = xpCon.experienceTotal;
    onContentsChanged();
  }

  public int addExperience(int xpToAdd) {
    return MathUtil.limit(addExperience((long) xpToAdd));
  }

  public long addExperience(long xpToAdd) {
    long j = MathUtil.clamp(xpToAdd, 0, maxXp - experienceTotal);
    experienceTotal += j;
    xpDirty = true;
    onContentsChanged();
    return j;
  }

  public int removeExperience(int xpToRemove) {
    return MathUtil.limit(removeExperience((long) xpToRemove));
  }

  public long removeExperience(long xpToRemove) {
    long j = MathUtil.clamp(xpToRemove, 0, experienceTotal);
    experienceTotal -= j;
    xpDirty = true;
    onContentsChanged();
    return j;

  }

  private int getXpBarCapacity() {
    return XpUtil.getXpBarCapacity(getExperienceLevel());
  }

  public int getXpBarScaled(int scale) {
    int result = (int) (getExperience() * scale);
    return result;
  }

  public void givePlayerXp(@Nonnull EntityPlayer player, int levels) throws TooManyXPLevelsException {
    for (int i = 0; i < levels && experienceTotal > 0; i++) {
      givePlayerXpLevel(player);
    }
  }

  public void givePlayerXpLevel(@Nonnull EntityPlayer player) throws TooManyXPLevelsException {
    long currentXP = XpUtil.getPlayerXPL(player);
    long nextLevelXP = XpUtil.getExperienceForLevelL(player.experienceLevel + 1);
    long requiredXP = nextLevelXP - currentXP;

    XpUtil.addPlayerXP(player, removeExperience(requiredXP));
  }

  public void drainPlayerXpToReachContainerLevel(@Nonnull EntityPlayer player, int level) throws TooManyXPLevelsException {
    if (level >= 0 && level <= XpUtil.getMaxLevelsStorable()) {
      long targetXP = XpUtil.getExperienceForLevelL(level);
      long requiredXP = targetXP - experienceTotal;
      if (requiredXP <= 0) {
        return;
      }
      long drainXP = Math.min(requiredXP, XpUtil.getPlayerXPL(player));
      addExperience(drainXP);
      XpUtil.addPlayerXP(player, -drainXP);
    } else {
      Log.info("Invalid Call to drainPlayerXpToReachContainerLevel(), target level of ", level, " is out of range.");
    }
  }

  public void drainPlayerXpToReachPlayerLevel(@Nonnull EntityPlayer player, int level) throws TooManyXPLevelsException {
    if (level >= 0 && level <= XpUtil.getMaxLevelsStorable()) {
      long targetXP = XpUtil.getExperienceForLevelL(level);
      long drainXP = XpUtil.getPlayerXPL(player) - targetXP;
      if (drainXP <= 0) {
        return;
      }
      drainXP = addExperience(drainXP);
      if (drainXP > 0) {
        XpUtil.addPlayerXP(player, -drainXP);
      }
    } else {
      Log.info("Invalid Call to drainPlayerXpToReachPlayerLevel(), target level of ", level, " is out of range.");
    }
  }

  public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
    if (resource == null || !canDrain(from, resource.getFluid())) {
      return null;
    }
    return drain(from, resource.amount, doDrain);
  }

  public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
    long available = getFluidAmountL();
    long toDrain = Math.min(available, maxDrain);
    final long xpAskedToExtract = XpUtil.liquidToExperience(toDrain);
    // only return multiples of 1 XP (20mB) to avoid duping XP when being asked
    // for low values (like 10mB/t)
    final int fluidToExtract = MathUtil.limit(XpUtil.experienceToLiquid(xpAskedToExtract)); // limit to fluidstack int
    final long xpToExtract = XpUtil.liquidToExperience(fluidToExtract);
    if (doDrain) {
      long newXp = experienceTotal - xpToExtract;
      experienceTotal = 0;
      addExperience(newXp);
    }
    return new FluidStack(XP.get(), fluidToExtract);
  }

  public boolean canFill(EnumFacing from, Fluid fluidIn) {
    return canFill() && fluidIn != null && FluidUtil.areFluidsTheSame(fluidIn, XP.get());
  }

  public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
    if (resource == null) {
      return 0;
    }
    if (resource.amount <= 0) {
      return 0;
    }
    if (!canFill(from, resource.getFluid())) {
      return 0;
    }
    // need to do these calcs in XP instead of fluid space to avoid type overflows
    long xp = XpUtil.liquidToExperience((long) resource.amount);
    long xpSpace = getMaximumExperience() - getExperienceTotal();
    long canFillXP = Math.min(xp, xpSpace);
    if (canFillXP <= 0) {
      return 0;
    }
    if (doFill) {
      addExperience(canFillXP);
    }
    return MathUtil.limit(XpUtil.experienceToLiquid(canFillXP)); // safe, cannot be bigger than int input
  }

  public boolean canDrain(EnumFacing from, Fluid fluidIn) {
    return fluidIn != null && FluidUtil.areFluidsTheSame(fluidIn, XP.get());
  }

  public @Nonnull FluidTankInfo[] getTankInfo(EnumFacing from) {
    return new FluidTankInfo[] { new FluidTankInfo(new FluidStack(XP.get(), getFluidAmount()), getCapacity()) };
  }

  @Override
  @Deprecated
  public int getCapacity() {
    return MathUtil.limit(getCapacityL());
  }

  public long getCapacityL() {
    if (maxXp == Long.MAX_VALUE) {
      return Long.MAX_VALUE;
    }
    return XpUtil.experienceToLiquid(maxXp);
  }

  @Override
  @Deprecated
  public int getFluidAmount() {
    return MathUtil.limit(getFluidAmountL());
  }

  public long getFluidAmountL() {
    return XpUtil.experienceToLiquid(experienceTotal);
  }

  @Override
  public @Nonnull FluidTank readFromNBT(NBTTagCompound nbtRoot) {
    if (nbtRoot.hasKey("xp", Constants.NBT.TAG_LONG)) {
      experienceTotal = nbtRoot.getLong("experienceTotal");
    } else if (nbtRoot.hasKey("experienceTotal", Constants.NBT.TAG_INT)) {
      // TODO 1.16: Remove compat
      experienceTotal = nbtRoot.getInteger("experienceTotal");
    } else {
      experienceTotal = 0L;
    }
    return this;
  }

  @Override
  public NBTTagCompound writeToNBT(NBTTagCompound nbtRoot) {
    nbtRoot.setLong("xp", experienceTotal);
    // TODO Q2 2021 for 1.12, 1.16: Remove compat
    nbtRoot.setInteger("experienceTotal", getExperienceTotalIntLimited());
    return nbtRoot;
  }

  public void toBytes(ByteBuf buf) {
    buf.writeLong(experienceTotal);
  }

  public void fromBytes(ByteBuf buf) {
    experienceTotal = buf.readLong();
  }

  @Override
  public @Nonnull FluidStack getFluid() {
    return new FluidStack(XP.get(), getFluidAmount());
  }

  @Override
  public FluidTankInfo getInfo() {
    return getTankInfo(null)[0];
  }

  @Override
  public int fill(FluidStack resource, boolean doFill) {
    return fill(null, resource, doFill);
  }

  @Override
  public FluidStack drain(int maxDrain, boolean doDrain) {
    return drain(null, maxDrain, doDrain);
  }

  @Override
  public FluidStack drain(FluidStack resource, boolean doDrain) {
    return drain(null, resource, doDrain);
  }

  @Override
  public void setFluid(@Nullable FluidStack fluid) {
    experienceTotal = 0;
    if (fluid != null && fluid.getFluid() != null) {
      if (XP.get() == fluid.getFluid()) {
        addExperience(XpUtil.liquidToExperience((long) fluid.amount));
      } else {
        throw new InvalidParameterException(fluid.getFluid() + " is no XP juice");
      }
    }
    xpDirty = true;
  }

  @Override
  public void setCapacity(int capacity) {
    throw new InvalidParameterException();
  }

  @Override
  protected void onContentsChanged() {
    super.onContentsChanged();
    if (tile instanceof ITankAccess) {
      ((ITankAccess) tile).setTanksDirty();
    } else if (tile != null) {
      tile.markDirty();
    }
  }

  @Override
  public boolean canFillFluidType(FluidStack resource) {
    return canFill() && resource != null && resource.getFluid() != null && FluidUtil.areFluidsTheSame(resource.getFluid(), XP.get());
  }

  @Override
  public boolean canDrainFluidType(@Nullable FluidStack resource) {
    return canFill() && resource != null && resource.getFluid() != null && FluidUtil.areFluidsTheSame(resource.getFluid(), XP.get());
  }

}
