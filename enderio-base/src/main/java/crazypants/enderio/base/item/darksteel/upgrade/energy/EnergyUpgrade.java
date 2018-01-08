package crazypants.enderio.base.item.darksteel.upgrade.energy;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.client.handlers.SpecialTooltipHandler;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.config.Config;
import crazypants.enderio.base.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.base.handler.darksteel.IDarkSteelItem;
import crazypants.enderio.base.item.travelstaff.ItemTravelStaff;
import crazypants.enderio.base.lang.LangPower;
import crazypants.enderio.base.material.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.base.init.ModObject.itemBasicCapacitor;
import static crazypants.enderio.base.init.ModObject.itemMaterial;

public class EnergyUpgrade extends AbstractUpgrade {

  public class EnergyUpgradeHolder {
    private int energy;

    private EnergyUpgradeHolder(int energy) {
      this.energy = energy;
    }

    private EnergyUpgradeHolder(@Nonnull ItemStack stack) {
      this.energy = getUpgradeNBT(stack).getInteger(EnergyUpgradeManager.KEY_ENERGY);
    }

    public int getEnergy() {
      return energy;
    }

    public void setEnergy(int energy) {
      this.energy = energy;
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

    public @Nonnull EnergyUpgrade getUpgrade() {
      return EnergyUpgrade.this;
    }

    public void writeToItem(@Nonnull ItemStack stack) {
      EnergyUpgrade.this.writeToItem(stack);
      getUpgradeNBT(stack).setInteger(EnergyUpgradeManager.KEY_ENERGY, energy);
    }

    public int getCapacity() {
      return capacity;
    }

  }

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

  public static EnergyUpgrade loadAnyFromItem(@Nonnull ItemStack stack) {
    if (EMPOWERED_FOUR.hasUpgrade(stack)) {
      return EMPOWERED_FOUR;
    }
    if (EMPOWERED_THREE.hasUpgrade(stack)) {
      return EMPOWERED_THREE;
    }
    if (EMPOWERED_TWO.hasUpgrade(stack)) {
      return EMPOWERED_TWO;
    }
    if (EMPOWERED.hasUpgrade(stack) || stack.getItem() instanceof ItemTravelStaff) {
      return EMPOWERED;
    }
    return null;
  }

  protected final int capacity;
  protected final int level;
  protected final int maxInRF;
  protected final int maxOutRF;

  public EnergyUpgrade(int level, @Nonnull String name, int levels, @Nonnull ItemStack upgradeItem, int capacity, int maxReceiveIO) {
    super(EnergyUpgradeManager.UPGRADE_NAME, level, name, upgradeItem, levels);
    this.level = level;
    this.capacity = capacity;
    maxInRF = maxReceiveIO;
    maxOutRF = maxReceiveIO;
  }

  @Override
  public boolean canAddToItem(@Nonnull ItemStack stack) {
    if (!(stack.getItem() instanceof IDarkSteelItem)) {
      return false;
    }
    AbstractUpgrade up = EnergyUpgradeManager.next(loadAnyFromItem(stack));
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
      String capString = LangPower.RF(capacity);
      for (int i = 0; i < upgradeStr.size(); i++) {
        String str = upgradeStr.get(i);
        str = str.replaceAll("\\$P", capString);
        str = str.replaceAll("\\$D", percDamage);
        upgradeStr.set(i, str);
      }
    }
    list.addAll(upgradeStr);
  }

  public boolean isAbsorbDamageWithPower() {
    return EnergyUpgradeManager.RANDOM.nextDouble() < getAbsorptionRatio();
  }

  private double getAbsorptionRatio() {
    int val = level;
    if (val >= Config.darkSteelPowerDamgeAbsorptionRatios.length) {
      val = 0;
    }
    return Config.darkSteelPowerDamgeAbsorptionRatios[val];
  }

  public int getLevel() {
    return level;
  }

  public @Nonnull EnergyUpgradeHolder getEnergyUpgradeHolder(@Nonnull ItemStack stack) {
    return new EnergyUpgradeHolder(stack);
  }

}
