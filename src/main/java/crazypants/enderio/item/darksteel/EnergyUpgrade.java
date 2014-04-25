package crazypants.enderio.item.darksteel;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.Config;
import crazypants.enderio.EnderIO;
import crazypants.enderio.material.Material;

public class EnergyUpgrade extends AbstractUpgrade {

  public static final EnergyUpgrade VIBRANT = new EnergyUpgrade(
      "darksteel.upgrade.vibrant",Config.darkSteelUpgradeVibrantCost,
      new ItemStack(EnderIO.itemMaterial, 1, Material.VIBRANT_CYSTAL.ordinal()),
      Config.darkSteelPowerStorageBase,
      Config.darkSteelPowerStorageBase / 100) {

    @Override
    public boolean canAddToItem(ItemStack stack) {
      return !hasUpgrade(stack);
    }
  };

  public static final EnergyUpgrade ENERGY_ONE = new EnergyUpgrade(
      "darksteel.upgrade.energy_one",Config.darkSteelUpgradePowerOneCost,
      new ItemStack(EnderIO.itemBasicCapacitor, 1, 0),
      Config.darkSteelPowerStorageLevelOne,
      Config.darkSteelPowerStorageLevelOne / 100);

  public static final EnergyUpgrade ENERGY_TWO = new EnergyUpgrade(
      "darksteel.upgrade.energy_two",Config.darkSteelUpgradePowerTwoCost,
      new ItemStack(EnderIO.itemBasicCapacitor, 1, 1),
      Config.darkSteelPowerStorageLevelTwo,
      Config.darkSteelPowerStorageLevelTwo / 100);

  public static final EnergyUpgrade ENERGY_THREE = new EnergyUpgrade(
      "darksteel.upgrade.energy_three",Config.darkSteelUpgradePowerThreeCost,
      new ItemStack(EnderIO.itemBasicCapacitor, 1, 2),
      Config.darkSteelPowerStorageLevelThree,
      Config.darkSteelPowerStorageLevelThree / 100);

  private static final String UPGRADE_KEY = "energyUpgrade";
  private static final String KEY_CAPACITY = "capacity";
  private static final String KEY_ENERGY = "energy";
  private static final String KEY_ABS_WITH_POWER = "absDamWithPower";
  private static final String KEY_MAX_IN = "maxInput";
  private static final String KEY_MAX_OUT = "maxOuput";

  public static EnergyUpgrade loadFromItem(ItemStack stack) {
    if(stack == null) {
      return null;
    }
    if(stack.stackTagCompound == null) {
      return null;
    }
    if(!stack.stackTagCompound.hasKey(KEY_UPGRADE_PREFIX + UPGRADE_KEY)) {
      return null;
    }
    return new EnergyUpgrade((NBTTagCompound) stack.stackTagCompound.getTag(KEY_UPGRADE_PREFIX + UPGRADE_KEY));
  }

  public static boolean itemHasAnyPowerUpgrade(ItemStack itemstack) {
    return loadFromItem(itemstack) != null;
  }

  protected int capacity;
  protected int energy;
  protected boolean absorbDamageWithPower;

  protected ItemStack upgradeItem;
  protected int maxInRF;
  protected int maxOutRF;

  public EnergyUpgrade(String name, int levels, ItemStack upgradeItem, int capcity, int maxReceiveIO) {
    super(UPGRADE_KEY, name, levels);
    this.upgradeItem = upgradeItem;
    this.capacity = capcity;
    energy = 0;
    maxInRF = maxReceiveIO;
    maxOutRF = maxReceiveIO;
  }

  public EnergyUpgrade(NBTTagCompound tag) {
    super(UPGRADE_KEY, tag);
    capacity = tag.getInteger(KEY_CAPACITY);
    energy = tag.getInteger(KEY_ENERGY);
    absorbDamageWithPower = tag.getBoolean(KEY_ABS_WITH_POWER);
    maxInRF = tag.getInteger(KEY_MAX_IN);
    maxOutRF = tag.getInteger(KEY_MAX_OUT);
  }

  @Override
  public boolean isUpgradeItem(ItemStack stack) {
    if(stack == null || stack.getItem() == null) {
      return false;
    }
    return stack.isItemEqual(upgradeItem);
  }

  @Override
  public boolean canAddToItem(ItemStack stack) {
    if(stack == null || stack.getItem() == null) {
      return false;
    }
    if(stack.getItem() instanceof IDarkSteelItem) {
      if(!itemHasAnyPowerUpgrade(stack)) {
        return false;
      }
      EnergyUpgrade curUp = loadFromItem(stack);
      if(curUp == null) {
        return true;
      }
      return curUp.capacity < capacity;
    }
    return false;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addCommonEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
    if(!unlocName.equals(VIBRANT.unlocName)) {
      VIBRANT.addCommonEntries(itemstack, entityplayer, list, flag);
    }
    super.addCommonEntries(itemstack, entityplayer, list, flag);
  }

  @Override
  public void writeUpgradeToNBT(NBTTagCompound upgradeRoot) {
    upgradeRoot.setInteger(KEY_CAPACITY, capacity);
    upgradeRoot.setInteger(KEY_ENERGY, energy);
    upgradeRoot.setBoolean(KEY_ABS_WITH_POWER, absorbDamageWithPower);
    upgradeRoot.setInteger(KEY_MAX_IN, maxInRF);
    upgradeRoot.setInteger(KEY_MAX_OUT, maxOutRF);
  }

  public boolean isAbsorbDamageWithPower() {
    return absorbDamageWithPower;
  }

  public void setAbsorbDamageWithPower(boolean val) {
    absorbDamageWithPower = val;
  }

  public int getEnergy() {
    return energy;
  }

  public void setEnergy(int energy) {
    this.energy = energy;
  }

  public int receiveEnergy(int maxRF, boolean simulate) {

    int energyReceived = Math.min(capacity - energy, Math.min(this.maxInRF, maxRF));
    if(!simulate) {
      energy += energyReceived;
    }
    return energyReceived;
  }

  public int extractEnergy(int maxExtract, boolean simulate) {
    int energyExtracted = Math.min(energy, Math.min(maxOutRF, maxExtract));
    if(!simulate) {
      energy -= energyExtracted;
    }
    return energyExtracted;
  }

  public int getCapacity() {
    return capacity;
  }

  @Override
  public boolean hasUpgrade(ItemStack stack) {
    if(!super.hasUpgrade(stack)) {
      return false;
    }
    EnergyUpgrade up = loadFromItem(stack);
    return up.unlocName.equals(unlocName);
  }

}
