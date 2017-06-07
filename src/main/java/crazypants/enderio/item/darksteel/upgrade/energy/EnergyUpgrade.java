package crazypants.enderio.item.darksteel.upgrade.energy;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.client.handlers.SpecialTooltipHandler;

import crazypants.enderio.EnderIO;
import crazypants.enderio.config.Config;
import crazypants.enderio.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.handler.darksteel.IDarkSteelItem;
import crazypants.enderio.item.travelstaff.ItemTravelStaff;
import crazypants.enderio.material.material.Material;
import crazypants.enderio.power.PowerDisplayUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.init.ModObject.itemBasicCapacitor;
import static crazypants.enderio.init.ModObject.itemMaterial;

public class EnergyUpgrade extends AbstractUpgrade {

  public static final @Nonnull EnergyUpgrade EMPOWERED = new EnergyUpgrade(0, "enderio.darksteel.upgrade.empowered_one", Config.darkSteelUpgradeVibrantCost,
      new ItemStack(itemMaterial.getItemNN(), 1, Material.VIBRANT_CYSTAL.ordinal()), Config.darkSteelPowerStorageBase, Config.darkSteelPowerStorageBase / 100);

  public static final @Nonnull EnergyUpgrade EMPOWERED_TWO = new EnergyUpgrade(1, "enderio.darksteel.upgrade.empowered_two",
      Config.darkSteelUpgradePowerOneCost, new ItemStack(itemBasicCapacitor.getItemNN(), 1, 0), Config.darkSteelPowerStorageLevelOne,
      Config.darkSteelPowerStorageLevelOne / 100);

  public static final @Nonnull EnergyUpgrade EMPOWERED_THREE = new EnergyUpgrade(2, "enderio.darksteel.upgrade.empowered_three",
      Config.darkSteelUpgradePowerTwoCost, new ItemStack(itemBasicCapacitor.getItemNN(), 1, 1), Config.darkSteelPowerStorageLevelTwo,
      Config.darkSteelPowerStorageLevelTwo / 100);

  public static final @Nonnull EnergyUpgrade EMPOWERED_FOUR = new EnergyUpgrade(3, "enderio.darksteel.upgrade.empowered_four",
      Config.darkSteelUpgradePowerThreeCost, new ItemStack(itemBasicCapacitor.getItemNN(), 1, 2), Config.darkSteelPowerStorageLevelThree,
      Config.darkSteelPowerStorageLevelThree / 100);

  protected final int capacity;
  protected final int level;
  protected int energy;
  protected final int maxInRF;
  protected final int maxOutRF;

  public EnergyUpgrade(int level, @Nonnull String name, int levels, @Nonnull ItemStack upgradeItem, int capacity, int maxReceiveIO) {
    super(EnergyUpgradeManager.UPGRADE_NAME, name, upgradeItem, levels);
    this.level = level;
    this.capacity = capacity;
    energy = 0;
    maxInRF = maxReceiveIO;
    maxOutRF = maxReceiveIO;
  }

  public EnergyUpgrade(@Nonnull NBTTagCompound tag) {
    super(EnergyUpgradeManager.UPGRADE_NAME, tag);
    level = tag.getInteger(EnergyUpgradeManager.KEY_LEVEL);
    capacity = tag.getInteger(EnergyUpgradeManager.KEY_CAPACITY);
    energy = tag.getInteger(EnergyUpgradeManager.KEY_ENERGY);
    maxInRF = tag.getInteger(EnergyUpgradeManager.KEY_MAX_IN);
    maxOutRF = tag.getInteger(EnergyUpgradeManager.KEY_MAX_OUT);
  }

  EnergyUpgrade copy() {
    return new EnergyUpgrade(level, unlocName, levelCost, upgradeItem, maxInRF, maxOutRF);
  }

  @Override
  public boolean hasUpgrade(@Nonnull ItemStack stack) {
    if (!super.hasUpgrade(stack) && !(stack.getItem() instanceof ItemTravelStaff)) {
      return false;
    }
    EnergyUpgrade up = EnergyUpgradeManager.loadFromItem(stack);
    if (up == null) {
      return false;
    }
    return up.unlocName.equals(unlocName);
  }

  @Override
  public boolean canAddToItem(@Nonnull ItemStack stack) {
    if (!(stack.getItem() instanceof IDarkSteelItem)) {
      return false;
    }
    AbstractUpgrade up = EnergyUpgradeManager.next(EnergyUpgradeManager.loadFromItem(stack));
    if (up == null) {
      return false;
    }
    return up.getUnlocalizedName().equals(unlocName);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addDetailedEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {

    List<String> upgradeStr = new ArrayList<String>();
    upgradeStr.add(TextFormatting.DARK_AQUA + EnderIO.lang.localizeExact(getUnlocalizedName() + ".name"));
    if (itemstack.isItemStackDamageable()) {
      SpecialTooltipHandler.addDetailedTooltipFromResources(upgradeStr, getUnlocalizedName());

      String percDamage = (int) Math.round(getAbsorptionRatio() * 100) + "";
      String capString = PowerDisplayUtil.formatPower(capacity) + " " + PowerDisplayUtil.abrevation();
      for (int i = 0; i < upgradeStr.size(); i++) {
        String str = upgradeStr.get(i);
        str = str.replaceAll("\\$P", capString);
        str = str.replaceAll("\\$D", percDamage);
        upgradeStr.set(i, str);
      }
    }
    list.addAll(upgradeStr);
  }

  @Override
  public void writeUpgradeToNBT(@Nonnull NBTTagCompound upgradeRoot) {
    upgradeRoot.setInteger(EnergyUpgradeManager.KEY_LEVEL, level);
    upgradeRoot.setInteger(EnergyUpgradeManager.KEY_CAPACITY, capacity);
    upgradeRoot.setInteger(EnergyUpgradeManager.KEY_ENERGY, energy);
    upgradeRoot.setInteger(EnergyUpgradeManager.KEY_MAX_IN, maxInRF);
    upgradeRoot.setInteger(EnergyUpgradeManager.KEY_MAX_OUT, maxOutRF);
  }

  public boolean isAbsorbDamageWithPower() {
    boolean res = EnergyUpgradeManager.RANDOM.nextDouble() < getAbsorptionRatio();
    return res;
  }

  private double getAbsorptionRatio() {
    int val = level;
    if (val >= Config.darkSteelPowerDamgeAbsorptionRatios.length) {
      val = 0;
    }
    return Config.darkSteelPowerDamgeAbsorptionRatios[val];
  }

  public int getEnergy() {
    return energy;
  }

  public void setEnergy(int energy) {
    if (energy < 0) {
      energy = 0;
    }
    this.energy = energy;
  }

  public int getLevel() {
    return level;
  }

  public int receiveEnergy(int maxRF, boolean simulate) {
    int energyReceived = Math.min(capacity - energy, Math.min(maxInRF, maxRF));
    if (!simulate) {
      energy += energyReceived;
    }
    return energyReceived;
  }

  public int extractEnergy(int maxExtract, boolean simulate) {
    int energyExtracted = Math.min(energy, Math.min(maxOutRF, maxExtract));
    if (!simulate) {
      energy -= energyExtracted;
    }
    return energyExtracted;
  }

  public int getCapacity() {
    return capacity;
  }

}
