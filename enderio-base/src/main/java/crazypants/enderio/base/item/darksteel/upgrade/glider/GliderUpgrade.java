package crazypants.enderio.base.item.darksteel.upgrade.glider;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.Util;
import com.enderio.core.common.vecmath.VecmathUtil;
import com.enderio.core.common.vecmath.Vector3d;
import com.enderio.core.common.vecmath.Vector4d;

import crazypants.enderio.api.upgrades.IHasPlayerRenderer;
import crazypants.enderio.api.upgrades.IRenderUpgrade;
import crazypants.enderio.base.config.Config;
import crazypants.enderio.base.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.base.handler.darksteel.DarkSteelController;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.item.darksteel.upgrade.elytra.ElytraUpgrade;
import crazypants.enderio.base.material.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GliderUpgrade extends AbstractUpgrade implements IHasPlayerRenderer {

  private static final @Nonnull String UPGRADE_NAME = "glide";

  public static final @Nonnull GliderUpgrade INSTANCE = new GliderUpgrade();

  public GliderUpgrade() {
    super(UPGRADE_NAME, "enderio.darksteel.upgrade.glider", Material.GLIDER_WINGS.getStack(), Config.darkSteelGliderCost);
  }

  @Override
  public boolean canAddToItem(@Nonnull ItemStack stack) {
    return stack.getItem() == ModObject.itemDarkSteelChestplate.getItemNN() && !ElytraUpgrade.INSTANCE.hasUpgrade(stack)
        && !GliderUpgrade.INSTANCE.hasUpgrade(stack);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull IRenderUpgrade getRender() {
    return GliderUpgradeLayer.instance;
  }

  @Override
  public void onPlayerTick(@Nonnull ItemStack stack, @Nonnull EntityPlayer player) {
    if (!DarkSteelController.isGlideActive(player)) {
      return;
    }

    if (!player.onGround && player.motionY < 0 && !player.isSneaking() && !player.isInWater()) {
      double horizontalSpeed = Config.darkSteelGliderHorizontalSpeed;
      double verticalSpeed = Config.darkSteelGliderVerticalSpeed;
      if (player.isSprinting()) {
        verticalSpeed = Config.darkSteelGliderVerticalSpeedSprinting;
      }

      Vector3d look = Util.getLookVecEio(player);
      Vector3d side = new Vector3d();
      side.cross(new Vector3d(0, 1, 0), look);
      Vector3d playerPos = new Vector3d(player.prevPosX, player.prevPosY, player.prevPosZ);
      Vector3d b = new Vector3d(playerPos);
      b.y += 1;
      Vector3d c = new Vector3d(playerPos);
      c.add(side);
      Vector4d plane = new Vector4d();
      VecmathUtil.computePlaneEquation(playerPos, b, c, plane);
      double dist = Math.abs(VecmathUtil.distanceFromPointToPlane(plane, new Vector3d(player.posX, player.posY, player.posZ)));
      double minDist = 0.15;
      if (dist < minDist) {
        double dropRate = (minDist * 10) - (dist * 10);
        verticalSpeed = verticalSpeed + (verticalSpeed * dropRate * 8);
        horizontalSpeed -= (0.02 * dropRate);
      }

      double x = Math.cos(Math.toRadians(player.rotationYawHead + 90)) * horizontalSpeed;
      double z = Math.sin(Math.toRadians(player.rotationYawHead + 90)) * horizontalSpeed;

      player.motionX += x;
      player.motionZ += z;

      player.motionY = verticalSpeed;
      player.fallDistance = 0f;
    }
  }

}
