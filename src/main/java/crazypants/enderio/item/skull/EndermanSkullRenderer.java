package crazypants.enderio.item.skull;

import javax.annotation.Nonnull;

import com.enderio.core.client.render.ManagedTESR;
import com.enderio.core.client.render.RenderUtil;

import crazypants.enderio.EnderIO;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;

import static crazypants.enderio.ModObject.blockEndermanSkull;

public class EndermanSkullRenderer extends ManagedTESR<TileEndermanSkull> {

  public EndermanSkullRenderer() {
    super(blockEndermanSkull.getBlock());
  }

  @Override
  protected void renderTileEntity(@Nonnull TileEndermanSkull te, @Nonnull IBlockState blockState, float partialTicks, int destroyStage) {

    // RenderUtil.setupLightmapCoords(te.getPos(), te.getWorld());

    GlStateManager.translate(0.5f, 0, 0.5f);
    GlStateManager.rotate(getYaw(te), 0, 1, 0);
    GlStateManager.translate(-0.5f, 0, -0.5f);

    RenderUtil.renderBlockModel(te.getWorld(), te.getPos(), true);
  }

  float getYaw(TileEndermanSkull te) {
    if (te.lastTick != EnderIO.proxy.getTickCount()) {
      te.lastTick = EnderIO.proxy.getTickCount();
      if (te.lookingAt > 0) {
        te.lookingAt--;
      }

      Angle yaw = new Angle(360, te.yaw);

      EntityPlayerSP player = Minecraft.getMinecraft().player;
      double d = te.getPos().distanceSqToCenter(player.posX, player.posY, player.posZ);
      if (d < 10 * 10) {
        double speed = d < 3 * 3 ? 2.5 : d < 6 * 6 ? 1.5 : .5;

        double d0 = player.posX - (te.getPos().getX() + 0.5F);
        double d1 = player.posZ - (te.getPos().getZ() + 0.5F);
        Angle target = new Angle(360, MathHelper.atan2(d1, d0) * 180.0 / Math.PI + 90);

        Angle diff = new Angle(180, yaw.get() - target.get());

        if (diff.get() > 1 || diff.get() < -1) {
          if (diff.get() > 0) {
            yaw.add(-Math.min(diff.get(), speed));
          } else {
            yaw.add(Math.min(-diff.get(), speed));
          }
        }
      } else {
        yaw.add(1);
      }

      te.yaw = (float) yaw.get();
    }

    return -te.yaw + (te.lookingAt > 0 ? (((te.lastTick & 1) == 0) ? 1 : -1) : 0);
  }

  private static class Angle {
    private final double offset;
    private double a;

    Angle(double offset, double a) {
      this.offset = offset;
      set(a);
    }

    void set(double a) {
      while (a >= offset) {
        a -= 360;
      }
      while (a < (offset - 360)) {
        a += 360;
      }
      this.a = a;
    }

    void add(double b) {
      set(a + b);
    }

    double get() {
      return a;
    }
  }

}
