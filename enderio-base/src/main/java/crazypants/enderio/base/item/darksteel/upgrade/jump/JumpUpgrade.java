package crazypants.enderio.base.item.darksteel.upgrade.jump;

import java.util.Random;

import javax.annotation.Nonnull;

import com.enderio.core.client.ClientUtil;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.api.upgrades.IDarkSteelUpgrade;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.config.config.DarkSteelConfig;
import crazypants.enderio.base.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgradeManager;
import crazypants.enderio.base.sound.SoundHelper;
import crazypants.enderio.base.sound.SoundRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
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
    super(UPGRADE_NAME, level, "enderio.darksteel.upgrade.jump_" + numbers[level - 1], new ItemStack(Blocks.PISTON),
        DarkSteelConfig.jumpUpgradeCost.get(level - 1));
    this.level = (short) level;
  }

  @Override
  public boolean canAddToItem(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item) {
    if (!item.isForSlot(EntityEquipmentSlot.FEET) || !EnergyUpgradeManager.itemHasAnyPowerUpgrade(stack)) {
      return false;
    }
    JumpUpgrade up = loadAnyFromItem(stack);
    if (up == null) {
      return getLevel() == 1;
    }
    return up.getLevel() == getLevel() - 1;
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
      Particle fx = Minecraft.getMinecraft().effectRenderer.spawnEffectParticle(EnumParticleTypes.REDSTONE.getParticleID(),
          player.posX + (rand.nextDouble() * 0.5 - 0.25), player.posY - player.getYOffset(), player.posZ + (rand.nextDouble() * 0.5 - 0.25), 1, 1, 1);
      ClientUtil.setParticleVelocity(fx, player.motionX + (rand.nextDouble() * 0.5 - 0.25), (player.motionY / 2) + (rand.nextDouble() * -0.05),
          player.motionZ + (rand.nextDouble() * 0.5 - 0.25));
      Minecraft.getMinecraft().effectRenderer.addEffect(NullHelper.notnullM(fx, "spawnEffectParticle() failed unexptedly"));
    }
  }

}
