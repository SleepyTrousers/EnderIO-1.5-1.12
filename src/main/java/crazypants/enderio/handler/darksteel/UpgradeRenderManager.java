package crazypants.enderio.handler.darksteel;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import javax.annotation.Nonnull;

import crazypants.enderio.EnderIO;
import crazypants.enderio.Log;
import crazypants.enderio.item.darksteel.upgrade.elytra.CapeFilterLayer;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerCape;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.ReflectionHelper.UnableToAccessFieldException;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@EventBusSubscriber(modid = EnderIO.MODID, value = Side.CLIENT)
public class UpgradeRenderManager {

  // no WeakHashSet in Java...
  private static final Map<RenderPlayer, Object> injected = new WeakHashMap<RenderPlayer, Object>();

  private UpgradeRenderManager() {
  }

  @SideOnly(Side.CLIENT)
  @SubscribeEvent
  public static void onPlayerRenderPre(RenderPlayerEvent.Pre event) {
    final RenderPlayer renderer = event.getRenderer();
    if (renderer != null && !injected.containsKey(renderer)) {
      UpgradeRenderManager.replaceCapeLayer(renderer);
      renderer.addLayer(new UpgradeRenderDispatcher(renderer));
      injected.put(renderer, null);
    }
  }

  private static void replaceCapeLayer(@Nonnull RenderPlayer renderLivingBase) {
    try {
      List<LayerRenderer<AbstractClientPlayer>> value = ReflectionHelper.getPrivateValue(RenderLivingBase.class, renderLivingBase, "layerRenderers",
          "field_177097_h");
      if (value != null) {
        LayerRenderer<AbstractClientPlayer> capeLayer = null;
        for (LayerRenderer<AbstractClientPlayer> layerRenderer : value) {
          if (layerRenderer instanceof LayerCape && !(layerRenderer instanceof CapeFilterLayer)) {
            capeLayer = layerRenderer;
            break;
          }
        }
        if (capeLayer != null) {
          renderLivingBase.addLayer(new CapeFilterLayer(capeLayer));
          value.remove(capeLayer);
        }
      }
    } catch (UnableToAccessFieldException e) {
      Log.warn("Unable to access RenderLivingBase.layerRenderers, reason: " + e);
    }
  }

}
