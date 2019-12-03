package crazypants.enderio.base.handler;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.handler.KeyTracker.Action;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.EntityViewRenderEvent.FOVModifier;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(modid = EnderIO.MODID, value = Side.CLIENT)
public class FovZoomHandler {

  static class FovAction implements Action {
    @Override
    public void execute() {
      fovLevelLast = fovLevelNext = 1;
    }
  }

  private static double fovLevelLast = 1;
  private static double fovLevelNext = 1;
  private static long lastWorldTime = 0;

  @SubscribeEvent
  public static void onFov(FOVModifier event) {
    long worldTime = EnderIO.proxy.getTickCount();
    while (worldTime > lastWorldTime) {
      if (worldTime - lastWorldTime > 10) {
        lastWorldTime = worldTime;
      } else {
        lastWorldTime++;
      }
      fovLevelLast = fovLevelNext;
      if (KeyTracker.fovPlusFast.getBinding().isKeyDown()) {
        fovLevelNext *= 1.05;
      } else if (KeyTracker.fovMinusFast.getBinding().isKeyDown()) {
        fovLevelNext /= 1.05;
      } else if (KeyTracker.fovPlus.getBinding().isKeyDown()) {
        fovLevelNext *= 1.01;
      } else if (KeyTracker.fovMinus.getBinding().isKeyDown()) {
        fovLevelNext /= 1.01;
      }
      fovLevelNext = MathHelper.clamp(fovLevelNext, .05, 1.3);
    }
    double val = fovLevelNext * event.getRenderPartialTicks() + fovLevelLast * (1 - event.getRenderPartialTicks());
    event.setFOV((float) (event.getFOV() * val));
  }

}
