package crazypants.enderio.item.darksteel.upgrade.energy;

import java.util.Random;

import javax.annotation.Nonnull;

import crazypants.enderio.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.power.PowerDisplayUtil;
import crazypants.enderio.teleport.ItemTravelStaff;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public abstract class EnergyUpgradeManager {

  protected static final @Nonnull String UPGRADE_NAME = "energyUpgrade";
  protected static final @Nonnull String KEY_CAPACITY = "capacity";
  protected static final @Nonnull String KEY_ENERGY = "energy";
  protected static final @Nonnull String KEY_MAX_IN = "maxInput";
  protected static final @Nonnull String KEY_MAX_OUT = "maxOuput";
  protected static final @Nonnull Random RANDOM = new Random();
  protected static final @Nonnull String KEY_LEVEL = "energyUpgradeLevel";

  public static EnergyUpgrade loadFromNBT(@Nonnull NBTTagCompound nbt) {
    if (!nbt.hasKey(AbstractUpgrade.KEY_UPGRADE_PREFIX + UPGRADE_NAME)) {
      return null;
    }
    return new EnergyUpgrade(nbt.getCompoundTag(AbstractUpgrade.KEY_UPGRADE_PREFIX + UPGRADE_NAME));
  }

  public static EnergyUpgrade loadFromItem(@Nonnull ItemStack stack) {
    final NBTTagCompound tagCompound = stack.getTagCompound();
    EnergyUpgrade upgrade = tagCompound == null ? null : loadFromNBT(tagCompound);
    if (upgrade == null && stack.getItem() instanceof ItemTravelStaff) {
      return EnergyUpgrade.EMPOWERED.copy();
    }
    return upgrade;
  
  }

  public static boolean itemHasAnyPowerUpgrade(@Nonnull ItemStack itemstack) {
    return loadFromItem(itemstack) != null;
  }

  public static AbstractUpgrade next(AbstractUpgrade upgrade) {
    if (upgrade == null) {
      return EnergyUpgrade.EMPOWERED;
    } else if (upgrade.getUnlocalizedName().equals(EnergyUpgrade.EMPOWERED.getUnlocalizedName())) {
      return EnergyUpgrade.EMPOWERED_TWO;
    } else if (upgrade.getUnlocalizedName().equals(EnergyUpgrade.EMPOWERED_TWO.getUnlocalizedName())) {
      return EnergyUpgrade.EMPOWERED_THREE;
    } else if (upgrade.getUnlocalizedName().equals(EnergyUpgrade.EMPOWERED_THREE.getUnlocalizedName())) {
      return EnergyUpgrade.EMPOWERED_FOUR;
    }
    return null;
  }

  public static int extractEnergy(@Nonnull ItemStack container, int maxExtract, boolean simulate) {
    EnergyUpgrade eu = loadFromItem(container);
    if (eu == null) {
      return 0;
    }
    int res = eu.extractEnergy(maxExtract, simulate);
    if (!simulate && res > 0) {
      eu.writeToItem(container);
    }
    return res;
  }

  public static int receiveEnergy(@Nonnull ItemStack container, int maxReceive, boolean simulate) {
    EnergyUpgrade eu = loadFromItem(container);
    if (eu == null) {
      return 0;
    }
    int res = eu.receiveEnergy(maxReceive, simulate);
    if (!simulate && res > 0) {
      eu.writeToItem(container);
    }
    return res;
  }

  public static void setPowerLevel(@Nonnull ItemStack item, int amount) {
    if (!itemHasAnyPowerUpgrade(item)) {
      return;
    }
    amount = Math.min(amount, getMaxEnergyStored(item));
    EnergyUpgrade eu = loadFromItem(item);
    eu.setEnergy(amount);
    eu.writeToItem(item);
  }

  public static void setPowerFull(@Nonnull ItemStack item) {
    if (!itemHasAnyPowerUpgrade(item)) {
      return;
    }
    EnergyUpgrade eu = loadFromItem(item);
    eu.setEnergy(eu.getCapacity());
    eu.writeToItem(item);
  }

  public static String getStoredEnergyString(@Nonnull ItemStack itemstack) {
    EnergyUpgrade up = loadFromItem(itemstack);
    if (up == null) {
      return null;
    }
    return PowerDisplayUtil.formatStoredPower(up.energy, up.capacity);
  }

  public static int getEnergyStored(@Nonnull ItemStack container) {
    EnergyUpgrade eu = loadFromItem(container);
    if (eu == null) {
      return 0;
    }
    return eu.getEnergy();
  }

  public static int getMaxEnergyStored(@Nonnull ItemStack container) {
    EnergyUpgrade eu = loadFromItem(container);
    if (eu == null) {
      return 0;
    }
    return eu.getCapacity();
  }

}