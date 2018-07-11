package crazypants.enderio.base.item.darksteel.upgrade.energy;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.client.handlers.SpecialTooltipHandler;

import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.block.skull.SkullType;
import crazypants.enderio.base.config.config.DarkSteelConfig;
import crazypants.enderio.base.config.factory.IValue;
import crazypants.enderio.base.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.base.item.travelstaff.ItemTravelStaff;
import crazypants.enderio.base.lang.LangPower;
import crazypants.enderio.base.material.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.base.init.ModObject.blockEndermanSkull;
import static crazypants.enderio.base.init.ModObject.itemBasicCapacitor;

public class EnergyUpgrade extends AbstractUpgrade {

  public class EnergyUpgradeHolder {
    private int energy;

    private EnergyUpgradeHolder(int energy) {
      setEnergy(energy);
    }

    private EnergyUpgradeHolder(@Nonnull ItemStack stack) {
      setEnergy(getOrCreateUpgradeNBT(stack).getInteger(EnergyUpgradeManager.KEY_ENERGY));
    }

    public int getEnergy() {
      return energy;
    }

    public void setEnergy(int energy) {
      this.energy = MathHelper.clamp(energy, 0, capacity.get());
    }

    public int receiveEnergy(int maxRF, boolean simulate) {
      int energyReceived = Math.max(0, Math.min(capacity.get() - energy, Math.min(maxInRF.get(), maxRF)));
      if (!simulate) {
        energy += energyReceived;
      }
      return energyReceived;
    }

    public int extractEnergy(int maxExtract, boolean simulate) {
      int energyExtracted = Math.max(0, Math.min(energy, Math.min(maxOutRF.get(), maxExtract)));
      if (!simulate) {
        energy -= energyExtracted;
      }
      return energyExtracted;
    }

    public @Nonnull EnergyUpgrade getUpgrade() {
      return EnergyUpgrade.this;
    }

    public void writeToItem(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item) {
      EnergyUpgrade.this.addToItem(stack, item);
      getOrCreateUpgradeNBT(stack).setInteger(EnergyUpgradeManager.KEY_ENERGY, energy);
    }

    public int getCapacity() {
      return capacity.get();
    }

  }

  public static final @Nonnull EnergyUpgrade EMPOWERED = new EnergyUpgrade(0, "enderio.darksteel.upgrade.empowered_one",
      DarkSteelConfig.energyUpgradeLevelCostEmpowered0, Material.VIBRANT_CYSTAL.getStack(), DarkSteelConfig.energyUpgradePowerStorageEmpowered0,
      DarkSteelConfig.energyUpgradePowerTransferEmpowered0, DarkSteelConfig.energyUpgradeAbsorptionRatioEmpowered0);

  public static final @Nonnull EnergyUpgrade EMPOWERED_TWO = new EnergyUpgrade(1, "enderio.darksteel.upgrade.empowered_two",
      DarkSteelConfig.energyUpgradeLevelCostEmpowered1, new ItemStack(itemBasicCapacitor.getItemNN(), 1, 0),
      DarkSteelConfig.energyUpgradePowerStorageEmpowered1, DarkSteelConfig.energyUpgradePowerTransferEmpowered1,
      DarkSteelConfig.energyUpgradeAbsorptionRatioEmpowered1);

  public static final @Nonnull EnergyUpgrade EMPOWERED_THREE = new EnergyUpgrade(2, "enderio.darksteel.upgrade.empowered_three",
      DarkSteelConfig.energyUpgradeLevelCostEmpowered2, new ItemStack(itemBasicCapacitor.getItemNN(), 1, 1),
      DarkSteelConfig.energyUpgradePowerStorageEmpowered2, DarkSteelConfig.energyUpgradePowerTransferEmpowered2,
      DarkSteelConfig.energyUpgradeAbsorptionRatioEmpowered2);

  public static final @Nonnull EnergyUpgrade EMPOWERED_FOUR = new EnergyUpgrade(3, "enderio.darksteel.upgrade.empowered_four",
      DarkSteelConfig.energyUpgradeLevelCostEmpowered3, new ItemStack(itemBasicCapacitor.getItemNN(), 1, 2),
      DarkSteelConfig.energyUpgradePowerStorageEmpowered3, DarkSteelConfig.energyUpgradePowerTransferEmpowered3,
      DarkSteelConfig.energyUpgradeAbsorptionRatioEmpowered3);

  public static final @Nonnull EnergyUpgrade EMPOWERED_FIVE = new EnergyUpgrade(4, "enderio.darksteel.upgrade.empowered_five",
      DarkSteelConfig.energyUpgradeLevelCostEmpowered4, new ItemStack(blockEndermanSkull.getBlockNN(), 1, SkullType.TORMENTED.ordinal()),
      DarkSteelConfig.energyUpgradePowerStorageEmpowered4, DarkSteelConfig.energyUpgradePowerTransferEmpowered4,
      DarkSteelConfig.energyUpgradeAbsorptionRatioEmpowered4);

  public static EnergyUpgrade loadAnyFromItem(@Nonnull ItemStack stack) {
    if (EMPOWERED_FIVE.hasUpgrade(stack)) {
      return EMPOWERED_FIVE;
    }
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

  protected final @Nonnull IValue<Integer> capacity;
  protected final int level;
  protected final @Nonnull IValue<Integer> maxInRF;
  protected final @Nonnull IValue<Integer> maxOutRF;
  protected final @Nonnull IValue<Double> absorptionRatio;

  public EnergyUpgrade(int level, @Nonnull String name, @Nonnull IValue<Integer> levels, @Nonnull ItemStack upgradeItem, @Nonnull IValue<Integer> capacity,
      @Nonnull IValue<Integer> maxReceiveIO, @Nonnull IValue<Double> absorptionRatio) {
    super(EnergyUpgradeManager.UPGRADE_NAME, level, name, upgradeItem, levels);
    this.level = level;
    this.capacity = capacity;
    maxInRF = maxReceiveIO;
    maxOutRF = maxReceiveIO;
    this.absorptionRatio = absorptionRatio;
  }

  @Override
  public boolean canAddToItem(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item) {
    AbstractUpgrade up = EnergyUpgradeManager.next(loadAnyFromItem(stack));
    if (up == null) {
      return false;
    }
    if (this == EMPOWERED_FIVE && item.getEquipmentData().getTier() < 2) {
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
      String capString = LangPower.RF(capacity.get());
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
    return absorptionRatio.get();
  }

  public int getLevel() {
    return level;
  }

  public @Nonnull EnergyUpgradeHolder getEnergyUpgradeHolder(@Nonnull ItemStack stack) {
    return new EnergyUpgradeHolder(stack);
  }

}
