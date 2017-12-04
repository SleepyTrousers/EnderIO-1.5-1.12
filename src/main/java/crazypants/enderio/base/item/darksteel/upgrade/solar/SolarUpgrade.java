package crazypants.enderio.base.item.darksteel.upgrade.solar;

import javax.annotation.Nonnull;

import crazypants.enderio.base.config.ValueFactory.IValue;
import crazypants.enderio.base.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.base.handler.darksteel.IRenderUpgrade;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgradeManager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SolarUpgrade extends AbstractUpgrade {

  private static final @Nonnull String KEY_LEVEL = "level";

  private static final @Nonnull String UPGRADE_NAME = "solar";

  public static SolarUpgrade loadFromItem(@Nonnull ItemStack stack) {
    final NBTTagCompound tagCompound = stack.getTagCompound();
    if (tagCompound == null) {
      return null;
    }
    if (!tagCompound.hasKey(KEY_UPGRADE_PREFIX + UPGRADE_NAME)) {
      return null;
    }
    return new SolarUpgrade((NBTTagCompound) tagCompound.getTag(KEY_UPGRADE_PREFIX + UPGRADE_NAME));
  }

  private final int level;
  private final IValue<Integer> realLevelCost;

  public SolarUpgrade(@Nonnull NBTTagCompound tag) {
    super(UPGRADE_NAME, tag);
    level = tag.getByte(KEY_LEVEL);
    this.realLevelCost = null;
  }

  public SolarUpgrade(@Nonnull String unlocName, @Nonnull ItemStack item, int level, IValue<Integer> levelCost) {
    super(UPGRADE_NAME, unlocName, item, levelCost.get());
    this.level = level;
    this.realLevelCost = levelCost;
  }

  @Override
  public int getLevelCost() {
    if (realLevelCost != null) {
      return realLevelCost.get();
    }
    return super.getLevelCost();
  }

  @Override
  public boolean canAddToItem(@Nonnull ItemStack stack) {
    if (stack.getItem() != ModObject.itemDarkSteelHelmet.getItemNN() || !EnergyUpgradeManager.itemHasAnyPowerUpgrade(stack)) {
      return false;
    }
    SolarUpgrade up = loadFromItem(stack);
    if (up == null) {
      return getLevel() == 1;
    }
    return up.getLevel() == getLevel() - 1;
  }

  @Override
  public boolean hasUpgrade(@Nonnull ItemStack stack) {
    if (!super.hasUpgrade(stack)) {
      return false;
    }
    SolarUpgrade up = loadFromItem(stack);
    if (up == null) {
      return false;
    }
    return up.unlocName.equals(unlocName);
  }

  @Override
  public void writeUpgradeToNBT(@Nonnull NBTTagCompound upgradeRoot) {
    upgradeRoot.setByte(KEY_LEVEL, (byte) getLevel());
  }

  public int getRFPerSec() {
    return SolarUpgradeManager.getRFforLevel(level);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IRenderUpgrade getRender() {
    return SolarUpgradeLayer.instance;
  }

  public int getLevel() {
    return level;
  }

  public static int calculateLightRatio(World world) {
    // TODO 1.11 copy from TE
    return 0;
  }

}
