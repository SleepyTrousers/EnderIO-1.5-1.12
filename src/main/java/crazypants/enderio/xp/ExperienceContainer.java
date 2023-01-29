package crazypants.enderio.xp;

import java.security.InvalidParameterException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;

import crazypants.enderio.EnderIO;
import io.netty.buffer.ByteBuf;

public class ExperienceContainer extends FluidTank {
    // Note: We extend FluidTank instead of implementing IFluidTank because it has
    // some methods we need.

    private int experienceLevel;
    private float experience;
    private int experienceTotal;
    private boolean xpDirty;
    private final int maxXp;

    public ExperienceContainer() {
        this(Integer.MAX_VALUE);
    }

    public ExperienceContainer(int maxStored) {
        super(null, 0);
        // enforce overflow-safe value
        maxXp = Math.min(maxStored, XpUtil.liquidToExperience(Integer.MAX_VALUE));
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

    public int addExperience(int xpToAdd) {
        int j = maxXp - experienceTotal;
        if (xpToAdd > j) {
            xpToAdd = j;
        }

        experience += (float) xpToAdd / (float) getXpBarCapacity();
        experienceTotal += xpToAdd;
        for (; experience >= 1.0F; experience /= getXpBarCapacity()) {
            experience = (experience - 1.0F) * getXpBarCapacity();
            experienceLevel++;
        }
        xpDirty = true;
        return xpToAdd;
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

    public void drainPlayerXpToReachContainerLevel(EntityPlayer player, int level) {
        int targetXP = XpUtil.getExperienceForLevel(level);
        int requiredXP = targetXP - experienceTotal;
        if (requiredXP <= 0) {
            return;
        }
        int drainXP = Math.min(requiredXP, XpUtil.getPlayerXP(player));
        addExperience(drainXP);
        XpUtil.addPlayerXP(player, -drainXP);
    }

    public void drainPlayerXpToReachPlayerLevel(EntityPlayer player, int level) {
        int targetXP = XpUtil.getExperienceForLevel(level);
        int drainXP = XpUtil.getPlayerXP(player) - targetXP;
        if (drainXP <= 0) {
            return;
        }
        drainXP = addExperience(drainXP);
        if (drainXP > 0) {
            XpUtil.addPlayerXP(player, -drainXP);
        }
    }

    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        if (resource == null || !canDrain(from, resource.getFluid())) {
            return null;
        }
        return drain(from, resource.amount, doDrain);
    }

    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        if (EnderIO.fluidXpJuice == null) {
            return null;
        }
        int available = getFluidAmount();
        int canDrain = Math.min(available, maxDrain);
        final int xpAskedToExtract = XpUtil.liquidToExperience(canDrain);
        // only return multiples of 1 XP (20mB) to avoid duping XP when being asked
        // for low values (like 10mB/t)
        final int fluidToExtract = XpUtil.experienceToLiquid(xpAskedToExtract);
        final int xpToExtract = XpUtil.liquidToExperience(fluidToExtract);
        if (doDrain) {
            int newXp = experienceTotal - xpToExtract;
            experience = 0;
            experienceLevel = 0;
            experienceTotal = 0;
            addExperience(newXp);
        }
        return new FluidStack(EnderIO.fluidXpJuice, fluidToExtract);
    }

    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return fluid != null && EnderIO.fluidXpJuice != null && fluid.getID() == EnderIO.fluidXpJuice.getID();
    }

    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
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
        int xp = XpUtil.liquidToExperience(resource.amount);
        int xpSpace = getMaximumExperiance() - getExperienceTotal();
        int canFillXP = Math.min(xp, xpSpace);
        if (canFillXP <= 0) {
            return 0;
        }
        if (doFill) {
            addExperience(canFillXP);
        }
        return XpUtil.experienceToLiquid(canFillXP);
    }

    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return fluid != null && EnderIO.fluidXpJuice != null && fluid.getID() == EnderIO.fluidXpJuice.getID();
    }

    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        if (EnderIO.fluidXpJuice == null) {
            return new FluidTankInfo[0];
        }
        return new FluidTankInfo[] {
                new FluidTankInfo(new FluidStack(EnderIO.fluidXpJuice, getFluidAmount()), getCapacity()) };
    }

    @Override
    public int getCapacity() {
        if (maxXp == Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return XpUtil.experienceToLiquid(maxXp);
    }

    @Override
    public int getFluidAmount() {
        return XpUtil.experienceToLiquid(experienceTotal);
    }

    @Override
    public FluidTank readFromNBT(NBTTagCompound nbtRoot) {
        experienceLevel = nbtRoot.getInteger("experienceLevel");
        experienceTotal = nbtRoot.getInteger("experienceTotal");
        experience = nbtRoot.getFloat("experience");
        return this;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbtRoot) {
        nbtRoot.setInteger("experienceLevel", experienceLevel);
        nbtRoot.setInteger("experienceTotal", experienceTotal);
        nbtRoot.setFloat("experience", experience);
        return nbtRoot;
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

    @Override
    public FluidStack getFluid() {
        return new FluidStack(EnderIO.fluidXpJuice, getFluidAmount());
    }

    @Override
    public FluidTankInfo getInfo() {
        return getTankInfo(ForgeDirection.UNKNOWN)[0];
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        return fill(ForgeDirection.UNKNOWN, resource, doFill);
    }

    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        return drain(ForgeDirection.UNKNOWN, maxDrain, doDrain);
    }

    @Override
    public void setFluid(FluidStack fluid) {
        experience = 0;
        experienceLevel = 0;
        experienceTotal = 0;
        if (fluid != null && fluid.getFluid() != null) {
            if (EnderIO.fluidXpJuice == fluid.getFluid()) {
                addExperience(XpUtil.liquidToExperience(fluid.amount));
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
}
