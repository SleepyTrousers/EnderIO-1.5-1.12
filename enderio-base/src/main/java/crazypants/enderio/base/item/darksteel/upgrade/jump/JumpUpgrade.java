package crazypants.enderio.base.item.darksteel.upgrade.jump;

import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;

import com.enderio.core.client.ClientUtil;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.api.upgrades.IDarkSteelUpgrade;
import crazypants.enderio.api.upgrades.IRule;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.config.config.DarkSteelConfig;
import crazypants.enderio.base.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.base.handler.darksteel.Rules;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgradeManager;
import crazypants.enderio.base.sound.SoundHelper;
import crazypants.enderio.base.sound.SoundRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@EventBusSubscriber(modid = EnderIO.MODID)
public class JumpUpgrade extends AbstractUpgrade {

  private static final @Nonnull String UPGRADE_NAME = "jumpBoost";

  private static final String[] numbers = { "one", "two", "three" };

  // TODO 1.13: Fix level range for consistency
  public static final @Nonnull JumpUpgrade JUMP_ONE = new JumpUpgrade(1);
  public static final @Nonnull JumpUpgrade JUMP_TWO = new JumpUpgrade(2);
  public static final @Nonnull JumpUpgrade JUMP_THREE = new JumpUpgrade(3);

  @SubscribeEvent
  public static void registerDarkSteelUpgrades(@Nonnull RegistryEvent.Register<IDarkSteelUpgrade> event) {
    event.getRegistry().register(JUMP_ONE);
    event.getRegistry().register(JUMP_TWO);
    event.getRegistry().register(JUMP_THREE);
  }

  private final short level;

  public static JumpUpgrade loadAnyFromItem(@Nonnull ItemStack stack) {
    if (JUMP_THREE.hasUpgrade(stack)) {
      return JUMP_THREE;
    }
    if (JUMP_TWO.hasUpgrade(stack)) {
      return JUMP_TWO;
    }
    if (JUMP_ONE.hasUpgrade(stack)) {
      return JUMP_ONE;
    }
    return null;
  }

  public static boolean isEquipped(@Nonnull EntityPlayer player) {
    return loadAnyFromItem(player.getItemStackFromSlot(EntityEquipmentSlot.FEET)) != null;
  }

  public JumpUpgrade(int level) {
    super(UPGRADE_NAME, level, "enderio.darksteel.upgrade.jump_" + numbers[level - 1], DarkSteelConfig.jumpUpgradeCost.get(level - 1));
    this.level = (short) level;
  }

  @Override
  protected int getMinVariant() {
    return 1;
  }

  @Override
  @Nonnull
  public List<IRule> getRules() {
    return new NNList<>(Rules.forSlot(EntityEquipmentSlot.FEET), EnergyUpgrade.HAS_ANY, Rules.withLevels(level, null, JUMP_ONE, JUMP_TWO),
        Rules.itemTypeTooltip(EntityEquipmentSlot.FEET));
  }

  @Override
  public boolean canOtherBeRemoved(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item, @Nonnull IDarkSteelUpgrade other) {
    return !EnergyUpgradeManager.isLowestPowerUpgrade(other);
  }

  public short getLevel() {
    return level;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void doMultiplayerSFX(@Nonnull EntityPlayer player) {
    SoundHelper.playSound(player.world, player, SoundRegistry.JUMP, 1.0f, player.world.rand.nextFloat() * 0.5f + 0.75f);

    Random rand = player.world.rand;
    for (int i = rand.nextInt(10) + 5; i >= 0; i--) {
      final double posX = player.posX + (rand.nextDouble() * 0.5 - 0.25);
      final double posY = player.posY - player.getYOffset();
      final double posZ = player.posZ + (rand.nextDouble() * 0.5 - 0.25);
      // Note: for EntityOtherPlayerMP the motion fields are not set and may contain garbage
      final double velX = ((player instanceof EntityOtherPlayerMP) ? 0 : player.motionX) + (rand.nextDouble() * 0.5 - 0.25);
      final double velY = ((player instanceof EntityOtherPlayerMP) ? 0 : (player.motionY / 2)) + (rand.nextDouble() * -0.5);
      final double velZ = ((player instanceof EntityOtherPlayerMP) ? 0 : player.motionZ) + (rand.nextDouble() * 0.5 - 0.25);
      Particle fx = Minecraft.getMinecraft().effectRenderer.spawnEffectParticle(EnumParticleTypes.REDSTONE.getParticleID(), posX, posY, posZ, 1, 1, 1);
      ClientUtil.setParticleVelocity(fx, velX, velY, velZ);
      Minecraft.getMinecraft().effectRenderer.addEffect(NullHelper.notnullM(fx, "spawnEffectParticle() failed unexptedly"));
    }
  }

}
