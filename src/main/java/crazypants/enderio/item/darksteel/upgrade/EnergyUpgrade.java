package crazypants.enderio.item.darksteel.upgrade;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.enderio.core.client.handlers.SpecialTooltipHandler;

import crazypants.enderio.EnderIO;
import crazypants.enderio.config.Config;
import crazypants.enderio.item.darksteel.IDarkSteelItem;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import crazypants.enderio.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EnergyUpgrade extends AbstractUpgrade {

  public static final AbstractUpgrade EMPOWERED = new EnergyUpgrade(0,
      "enderio.darksteel.upgrade.empowered_one", Config.darkSteelUpgradeVibrantCost,
      new ItemStack(EnderIO.itemMaterial, 1, Material.VIBRANT_CYSTAL.ordinal()),
      Config.darkSteelPowerStorageBase,
      Config.darkSteelPowerStorageBase / 100);

  public static final AbstractUpgrade EMPOWERED_TWO = new EnergyUpgrade(1,
      "enderio.darksteel.upgrade.empowered_two", Config.darkSteelUpgradePowerOneCost,
      new ItemStack(EnderIO.itemBasicCapacitor, 1, 0),
      Config.darkSteelPowerStorageLevelOne,
      Config.darkSteelPowerStorageLevelOne / 100);

  public static final AbstractUpgrade EMPOWERED_THREE = new EnergyUpgrade(2,
      "enderio.darksteel.upgrade.empowered_three", Config.darkSteelUpgradePowerTwoCost,
      new ItemStack(EnderIO.itemBasicCapacitor, 1, 1),
      Config.darkSteelPowerStorageLevelTwo,
      Config.darkSteelPowerStorageLevelTwo / 100);

  public static final AbstractUpgrade EMPOWERED_FOUR = new EnergyUpgrade(3,
      "enderio.darksteel.upgrade.empowered_four", Config.darkSteelUpgradePowerThreeCost,
      new ItemStack(EnderIO.itemBasicCapacitor, 1, 2),
      Config.darkSteelPowerStorageLevelThree,
      Config.darkSteelPowerStorageLevelThree / 100);

  private static final String UPGRADE_NAME = "energyUpgrade";
  private static final String KEY_CAPACITY = "capacity";
  private static final String KEY_ENERGY = "energy";
  
  private static final String KEY_MAX_IN = "maxInput";
  private static final String KEY_MAX_OUT = "maxOuput";


  private static final Random RANDOM = new Random();
  
  private static final String KEY_LEVEL = "energyUpgradeLevel";

  public static EnergyUpgrade loadFromItem(ItemStack stack) {
    if(stack == null) {
      return null;
    }
    if(stack.getTagCompound() == null) {
      return null;
    }
    if(!stack.getTagCompound().hasKey(KEY_UPGRADE_PREFIX + UPGRADE_NAME)) {
      return null;
    }
    return new EnergyUpgrade((NBTTagCompound) stack.getTagCompound().getTag(KEY_UPGRADE_PREFIX + UPGRADE_NAME));
  }

  public static boolean itemHasAnyPowerUpgrade(ItemStack itemstack) {
    return loadFromItem(itemstack) != null;
  }

  public static AbstractUpgrade next(AbstractUpgrade upgrade) {
    if(upgrade == null) {
      return EMPOWERED;
    } else if(upgrade.unlocName.equals(EMPOWERED.unlocName)) {
      return EMPOWERED_TWO;
    } else if(upgrade.unlocName.equals(EMPOWERED_TWO.unlocName)) {
      return EMPOWERED_THREE;
    } else if(upgrade.unlocName.equals(EMPOWERED_THREE.unlocName)) {
      return EMPOWERED_FOUR;
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
  
  public static void setPowerLevel(ItemStack item, int amount) {
    if(item == null || !itemHasAnyPowerUpgrade(item)) {
      return;
    }
    amount = Math.min(amount, getMaxEnergyStored(item));
    EnergyUpgrade eu = loadFromItem(item);
    eu.setEnergy(amount);
    eu.writeToItem(item);
  }
  
  public static void setPowerFull(ItemStack item) {
    if(item == null || !itemHasAnyPowerUpgrade(item)) {
      return;
    }
    EnergyUpgrade eu = loadFromItem(item);
    eu.setEnergy(eu.getCapacity());
    eu.writeToItem(item);
  }

  public static String getStoredEnergyString(ItemStack itemstack) {
    EnergyUpgrade up = loadFromItem(itemstack);
    if(up == null) {
      return null;
    }
    return PowerDisplayUtil.formatStoredPower(up.energy, up.capacity);
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

  protected final int capacity;
  protected final int level;
  protected int energy;

  protected final int maxInRF;
  protected final int maxOutRF;

  public EnergyUpgrade(int level, String name, int levels, ItemStack upgradeItem, int capacity, int maxReceiveIO) {
    super(UPGRADE_NAME, name, upgradeItem, levels);
    this.level = level;
    this.capacity = capacity;
    energy = 0;
    maxInRF = maxReceiveIO;
    maxOutRF = maxReceiveIO;
  }

  public EnergyUpgrade(NBTTagCompound tag) {
    super(UPGRADE_NAME, tag);
    level = tag.getInteger(KEY_LEVEL);
    capacity = tag.getInteger(KEY_CAPACITY);
    energy = tag.getInteger(KEY_ENERGY);
    maxInRF = tag.getInteger(KEY_MAX_IN);
    maxOutRF = tag.getInteger(KEY_MAX_OUT);
  }

  @Override
  public boolean hasUpgrade(ItemStack stack) {
    if(!super.hasUpgrade(stack)) {
      return false;
    }
    EnergyUpgrade up = loadFromItem(stack);
    if(up == null) {
      return false;
    }
    return up.unlocName.equals(unlocName);
  }

  @Override
  public boolean canAddToItem(ItemStack stack) {
    if(stack == null || stack.getItem() == null || !(stack.getItem() instanceof IDarkSteelItem)) {
      return false;
    }
    AbstractUpgrade up = next(loadFromItem(stack));
    if(up == null) {
      return false;
    }
    return up.unlocName.equals(unlocName);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addDetailedEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {

    List<String> upgradeStr = new ArrayList<String>();
    upgradeStr.add(TextFormatting.DARK_AQUA + EnderIO.lang.localizeExact(getUnlocalizedName() + ".name"));
    SpecialTooltipHandler.addDetailedTooltipFromResources(upgradeStr, getUnlocalizedName());

    String percDamage = (int)Math.round(getAbsorptionRatio() * 100) + "";
    String capString = PowerDisplayUtil.formatPower(capacity) + " " + PowerDisplayUtil.abrevation();
    for (int i = 0; i < upgradeStr.size(); i++) {
      String str = upgradeStr.get(i);
      str = str.replaceAll("\\$P", capString);
      str = str.replaceAll("\\$D", percDamage);
      upgradeStr.set(i, str);
    }
    list.addAll(upgradeStr);

  }

  @Override
  public void writeUpgradeToNBT(NBTTagCompound upgradeRoot) {
    upgradeRoot.setInteger(KEY_LEVEL, level);
    upgradeRoot.setInteger(KEY_CAPACITY, capacity);
    upgradeRoot.setInteger(KEY_ENERGY, energy);
    upgradeRoot.setInteger(KEY_MAX_IN, maxInRF);
    upgradeRoot.setInteger(KEY_MAX_OUT, maxOutRF);
  }

  public boolean isAbsorbDamageWithPower() {
    boolean res= RANDOM.nextDouble() < getAbsorptionRatio();
    return res;
  }

  private double getAbsorptionRatio() {
    int val = level;
    if(val >= Config.darkSteelPowerDamgeAbsorptionRatios.length) {
      System.out.println("EnergyUpgrade.getAbsorptionRatio: " + val);
      val = 0;
    }
    return Config.darkSteelPowerDamgeAbsorptionRatios[val];
  }

  public int getEnergy() {
    return energy;
  }

  public void setEnergy(int energy) {
    if(energy < 0) {
      energy = 0;
    }
    this.energy = energy;
  }

  public int getLevel() {
    return level;
  }

  public int receiveEnergy(int maxRF, boolean simulate) {

    int energyReceived = Math.min(capacity - energy, Math.min(maxInRF, maxRF));
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

}
