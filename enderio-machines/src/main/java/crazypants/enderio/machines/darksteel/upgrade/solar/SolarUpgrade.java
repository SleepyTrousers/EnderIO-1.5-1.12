package crazypants.enderio.machines.darksteel.upgrade.solar;

import javax.annotation.Nonnull;

import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.api.upgrades.IHasPlayerRenderer;
import crazypants.enderio.api.upgrades.IRenderUpgrade;
import crazypants.enderio.base.config.Config;
import crazypants.enderio.base.config.ValueFactory.IValue;
import crazypants.enderio.base.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgradeManager;
import crazypants.enderio.base.power.PowerHandlerUtil;
import crazypants.enderio.machines.EnderIOMachines;
import crazypants.enderio.machines.config.config.SolarConfig;
import crazypants.enderio.machines.init.MachineObject;
import crazypants.enderio.machines.machine.solar.TileSolarPanel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SolarUpgrade extends AbstractUpgrade implements IHasPlayerRenderer {

  private static final @Nonnull String UPGRADE_NAME = "solar";
  static final @Nonnull String NAME = "enderio.darksteel.upgrade.solar_";

  public static final SolarUpgrade SOLAR1 = new SolarUpgrade(new ItemStack(MachineObject.block_solar_panel.getItemNN(), 1, 0), 1,
      SolarConfig.darkSteelSolarOneCost, SolarConfig.darkSteelSolarOneGen);

  public static final SolarUpgrade SOLAR2 = new SolarUpgrade(new ItemStack(MachineObject.block_solar_panel.getItemNN(), 1, 1), 2,
      SolarConfig.darkSteelSolarTwoCost, SolarConfig.darkSteelSolarTwoGen);

  public static final SolarUpgrade SOLAR3 = new SolarUpgrade(new ItemStack(MachineObject.block_solar_panel.getItemNN(), 1, 2), 3,
      SolarConfig.darkSteelSolarThreeCost, SolarConfig.darkSteelSolarThreeGen);

  public static SolarUpgrade loadAnyFromItem(@Nonnull ItemStack stack) {
    if (SOLAR3.hasUpgrade(stack)) {
      return SOLAR3;
    }
    if (SOLAR2.hasUpgrade(stack)) {
      return SOLAR2;
    }
    if (SOLAR1.hasUpgrade(stack)) {
      return SOLAR1;
    }
    return null;
  }

  private final int level;
  private final @Nonnull IValue<Integer> rf;

  public SolarUpgrade(@Nonnull ItemStack item, int level, @Nonnull IValue<Integer> levelCost, @Nonnull IValue<Integer> rf) {
    super(EnderIOMachines.MODID, UPGRADE_NAME, level, NAME + level, item, levelCost);
    this.level = level;
    this.rf = rf;
  }

  @Override
  public boolean canAddToItem(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item) {
    if (!item.isForSlot(EntityEquipmentSlot.HEAD) || !EnergyUpgradeManager.itemHasAnyPowerUpgrade(stack)) {
      return false;
    }
    SolarUpgrade up = loadAnyFromItem(stack);
    if (up == null) {
      return getLevel() == 1;
    }
    return up.getLevel() == getLevel() - 1;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull IRenderUpgrade getRender() {
    return SolarUpgradeLayer.instance;
  }

  public int getLevel() {
    return level;
  }

  @Override
  public void onPlayerTick(@Nonnull ItemStack helm, @Nonnull IDarkSteelItem item, @Nonnull EntityPlayer player) {
    // no processing on client
    if (player.world.isRemote) {
      return;
    }

    if (!player.world
        .canBlockSeeSky(new BlockPos(MathHelper.floor(player.posX), MathHelper.floor(player.posY + player.eyeHeight + .25), MathHelper.floor(player.posZ)))) {
      return;
    }

    int RFperSecond = Math.round(rf.get() * TileSolarPanel.calculateLightRatio(player.world));

    int leftover = RFperSecond % 20;
    boolean addExtraRF = player.world.getTotalWorldTime() % 20 < leftover;

    int toAdd = (RFperSecond / 20) + (addExtraRF ? 1 : 0);

    if (toAdd != 0) {
      int nextIndex = player.getEntityData().getInteger("dsarmor:solar") % 4;

      for (int i = 0; i < 4 && toAdd > 0; i++) {
        ItemStack stack = player.inventory.armorInventory.get(nextIndex);
        IEnergyStorage cap = PowerHandlerUtil.getCapability(stack, null);
        if (cap != null && (EnergyUpgradeManager.loadFromItem(stack) != null || Config.darkSteelSolarChargeOthers)) { // TODO: move config
          toAdd -= cap.receiveEnergy(toAdd, false);
        }
        nextIndex = (nextIndex + 1) % 4;
      }

      player.getEntityData().setInteger("dsarmor:solar", nextIndex);
    }
  }

}
