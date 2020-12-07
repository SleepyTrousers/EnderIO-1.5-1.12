package crazypants.enderio.machines.darksteel.upgrade.solar;

import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.api.upgrades.IDarkSteelUpgrade;
import crazypants.enderio.api.upgrades.IHasPlayerRenderer;
import crazypants.enderio.api.upgrades.IRenderUpgrade;
import crazypants.enderio.api.upgrades.IRule;
import crazypants.enderio.base.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.base.handler.darksteel.Rules;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgradeManager;
import crazypants.enderio.base.power.PowerHandlerUtil;
import crazypants.enderio.machines.EnderIOMachines;
import crazypants.enderio.machines.config.config.SolarConfig;
import crazypants.enderio.machines.machine.solar.ISolarType;
import crazypants.enderio.machines.machine.solar.SolarType;
import crazypants.enderio.machines.machine.solar.TileSolarPanel;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@EventBusSubscriber(modid = EnderIOMachines.MODID)
public class SolarUpgrade extends AbstractUpgrade implements IHasPlayerRenderer {

  private static final @Nonnull String UPGRADE_NAME = "solar";
  static final @Nonnull String NAME = "enderio.darksteel.upgrade.solar_";

  public static final @Nonnull NNList<SolarUpgrade> INSTANCES = new NNList<>( //
      new SolarUpgrade(SolarType.SIMPLE), new SolarUpgrade(SolarType.NORMAL), new SolarUpgrade(SolarType.ADVANCED), new SolarUpgrade(SolarType.VIBRANT));

  @SuppressWarnings("unused")
  @SubscribeEvent
  public static void registerDarkSteelUpgrades(@Nonnull RegistryEvent.Register<IDarkSteelUpgrade> event) {
    if (Loader.isModLoaded("enderioendergy") && false /* TODO: Make textures for the upgrades */) {
      INSTANCES.add(new SolarUpgrade(SolarType.COMPRESSED));
      INSTANCES.add(new SolarUpgrade(SolarType.CONCENTRATED));
      INSTANCES.add(new SolarUpgrade(SolarType.ULTIMATE));
    }
    INSTANCES.apply(event.getRegistry()::register);
  }

  private final @Nonnull SolarType type;

  public SolarUpgrade(@Nonnull SolarType type) {
    super(EnderIOMachines.MODID, UPGRADE_NAME, ISolarType.getMetaFromType(type), NAME + ISolarType.getMetaFromType(type), type::getUpgradeLevelCost);
    this.type = type;
  }

  @Override
  @Nonnull
  public List<IRule> getRules() {
    return new NNList<>(Rules.forSlot(EntityEquipmentSlot.HEAD), EnergyUpgrade.HAS_ANY, Rules.withLevels(getLevel(), INSTANCES),
        Rules.itemTypeTooltip(EntityEquipmentSlot.HEAD));
  }

  @Override
  public boolean canOtherBeRemoved(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item, @Nonnull IDarkSteelUpgrade other) {
    return !EnergyUpgradeManager.isLowestPowerUpgrade(other);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull IRenderUpgrade getRender(@Nonnull AbstractClientPlayer player) {
    return SolarUpgradeLayer.withUpgrade(this);
  }

  public int getLevel() {
    return ISolarType.getMetaFromType(type);
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

    int RFperSecond = Math.round(type.getRfperSecond() * TileSolarPanel.calculateLightRatio(player.world));

    int leftover = RFperSecond % 20;
    boolean addExtraRF = player.world.getTotalWorldTime() % 20 < leftover;

    int toAdd = (RFperSecond / 20) + (addExtraRF ? 1 : 0);

    if (toAdd != 0) {
      int nextIndex = player.getEntityData().getInteger("dsarmor:solar") % 4;

      for (int i = 0; i < 4 && toAdd > 0; i++) {
        ItemStack stack = player.inventory.armorInventory.get(nextIndex);
        IEnergyStorage cap = PowerHandlerUtil.getCapability(stack, null);
        if (cap != null && (SolarConfig.helmetChargeOthers.get() || EnergyUpgradeManager.loadFromItem(stack) != null)) {
          toAdd -= cap.receiveEnergy(toAdd, false);
        }
        nextIndex = (nextIndex + 1) % 4;
      }

      player.getEntityData().setInteger("dsarmor:solar", nextIndex);
    }
  }

  public @Nonnull ItemStack getRenderItem() {
    return type.getItemStack();
  }

}
