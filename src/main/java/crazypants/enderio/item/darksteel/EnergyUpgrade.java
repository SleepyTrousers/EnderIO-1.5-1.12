package crazypants.enderio.item.darksteel;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.Config;
import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import crazypants.enderio.material.Material;

public class EnergyUpgrade extends AbstractUpgrade {

  public static final EnergyUpgrade VIBRANT = new EnergyUpgrade(
      "enderio.darksteel.upgrade.vibrant", Config.darkSteelUpgradeVibrantCost,
      new ItemStack(EnderIO.itemMaterial, 1, Material.VIBRANT_CYSTAL.ordinal()),
      Config.darkSteelPowerStorageBase,
      Config.darkSteelPowerStorageBase / 100);

  public static final EnergyUpgrade ENERGY_ONE = new EnergyUpgrade(
      "enderio.darksteel.upgrade.energy_one", Config.darkSteelUpgradePowerOneCost,
      new ItemStack(EnderIO.itemBasicCapacitor, 1, 0),
      Config.darkSteelPowerStorageLevelOne,
      Config.darkSteelPowerStorageLevelOne / 100);

  public static final EnergyUpgrade ENERGY_TWO = new EnergyUpgrade(
      "enderio.darksteel.upgrade.energy_two", Config.darkSteelUpgradePowerTwoCost,
      new ItemStack(EnderIO.itemBasicCapacitor, 1, 1),
      Config.darkSteelPowerStorageLevelTwo,
      Config.darkSteelPowerStorageLevelTwo / 100);

  public static final EnergyUpgrade ENERGY_THREE = new EnergyUpgrade(
      "enderio.darksteel.upgrade.energy_three", Config.darkSteelUpgradePowerThreeCost,
      new ItemStack(EnderIO.itemBasicCapacitor, 1, 2),
      Config.darkSteelPowerStorageLevelThree,
      Config.darkSteelPowerStorageLevelThree / 100);

  private static final String UPGRADE_KEY = "energyUpgrade";
  private static final String KEY_CAPACITY = "capacity";
  private static final String KEY_ENERGY = "energy";
  private static final String KEY_ABS_WITH_POWER = "absDamWithPower";
  private static final String KEY_MAX_IN = "maxInput";
  private static final String KEY_MAX_OUT = "maxOuput";
  private static final String KEY_UPGRADE_ITEM = "upgradeItem";

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

  public static EnergyUpgrade next(EnergyUpgrade upgrade) {
    if(upgrade == null) {
      return VIBRANT;
    } else if(upgrade.unlocName.equals(VIBRANT.unlocName)) {
      return ENERGY_ONE;
    } else if(upgrade.unlocName.equals(ENERGY_ONE.unlocName)) {
      return ENERGY_TWO;
    } else if(upgrade.unlocName.equals(ENERGY_TWO.unlocName)) {
      return ENERGY_THREE;
    }
    return null;
  }

  public static int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {
    EnergyUpgrade eu = EnergyUpgrade.loadFromItem(container);
    if(eu == null) {
      return 0;
    }
    int res = eu.extractEnergy(maxExtract, simulate);
    if(!simulate && res > 0) {
      eu.writeToItem(container);
    }
    return res;
  }

  public static int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {
    EnergyUpgrade eu = EnergyUpgrade.loadFromItem(container);
    if(eu == null) {
      return 0;
    }
    int res = eu.receiveEnergy(maxReceive, simulate);
    if(!simulate && res > 0) {
      eu.writeToItem(container);
    }
    return res;
  }

  public static String getStoredEnergyString(ItemStack itemstack) {
    EnergyUpgrade up = loadFromItem(itemstack);
    if(up == null) {
      return null;
    }
    return PowerDisplayUtil.formatStoredPower(up.energy / 10, up.capacity/10);
  }

  public static int getEnergyStored(ItemStack container) {
    EnergyUpgrade eu = EnergyUpgrade.loadFromItem(container);
    if(eu == null) {
      return 0;
    }
    return eu.getEnergy();
  }

  public static int getMaxEnergyStored(ItemStack container) {
    EnergyUpgrade eu = EnergyUpgrade.loadFromItem(container);
    if(eu == null) {
      return 0;
    }
    return eu.getCapacity();
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
    if(tag.hasKey(KEY_UPGRADE_ITEM)) {
      upgradeItem = ItemStack.loadItemStackFromNBT((NBTTagCompound) tag.getTag(KEY_UPGRADE_ITEM));
    }
  }

  @Override
  public boolean isUpgradeItem(ItemStack stack) {
    if(stack == null || stack.getItem() == null) {
      return false;
    }
    return stack.isItemEqual(upgradeItem) && stack.stackSize == upgradeItem.stackSize;
  }

  @Override
  public ItemStack getUpgradeItem() {
    return upgradeItem;
  }

  @Override
  public boolean canAddToItem(ItemStack stack) {
    if(stack == null || stack.getItem() == null || !(stack.getItem() instanceof IDarkSteelItem)) {
      return false;
    }
    EnergyUpgrade up = next(loadFromItem(stack));
    if(up == null) {
      return false;
    }
    return up.unlocName.equals(unlocName);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addDetailedEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
    int startIndex = list.size();
    super.addDetailedEntries(itemstack, entityplayer, list, flag);
    int endIndex = list.size();
    int cap = capacity;
//    if(!unlocName.equals(VIBRANT.unlocName)) {
//      cap = cap - VIBRANT.capacity;
//    }
//    cap /= 10;

    String capString = PowerDisplayUtil.formatPower(cap) + " " + PowerDisplayUtil.abrevation();
    for(int i=startIndex;i<endIndex;i++) {
      String str = (String)list.get(i);
      str = str.replaceAll("\\$P", capString);
      list.set(i, str);
    }
  }

//  @Override
//  @SideOnly(Side.CLIENT)
//  public void addBasicEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
//    if(!unlocName.equals(VIBRANT.unlocName)) {
//      VIBRANT.addBasicEntries(itemstack, entityplayer, list, flag);
//    }
//    super.addBasicEntries(itemstack, entityplayer, list, flag);
//  }

  @Override
  public void writeUpgradeToNBT(NBTTagCompound upgradeRoot) {
    upgradeRoot.setInteger(KEY_CAPACITY, capacity);
    upgradeRoot.setInteger(KEY_ENERGY, energy);
    upgradeRoot.setBoolean(KEY_ABS_WITH_POWER, absorbDamageWithPower);
    upgradeRoot.setInteger(KEY_MAX_IN, maxInRF);
    upgradeRoot.setInteger(KEY_MAX_OUT, maxOutRF);
    NBTTagCompound itemRoot = new NBTTagCompound();
    if(upgradeItem != null) {
      upgradeItem.writeToNBT(itemRoot);
      upgradeRoot.setTag(KEY_UPGRADE_ITEM, itemRoot);
    }
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
