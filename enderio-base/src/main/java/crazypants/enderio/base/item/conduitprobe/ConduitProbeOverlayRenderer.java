package crazypants.enderio.base.item.conduitprobe;

import javax.annotation.Nonnull;

import org.lwjgl.opengl.GL11;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.util.Prep;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import static crazypants.enderio.base.init.ModObject.itemConduitProbe;

@EventBusSubscriber(modid = EnderIO.MODID, value = Side.CLIENT)
public class ConduitProbeOverlayRenderer {

  @SubscribeEvent
  public static void renderOverlay(@Nonnull RenderGameOverlayEvent.Post event) {
    if (event.getType() == ElementType.ALL) {
      ItemStack equippedProbe = getEquippedProbe();
      if (Prep.isValid(equippedProbe)) {
        doRenderOverlay(event, equippedProbe);
      }
    }
  }

  private static @Nonnull ItemStack getEquippedProbe() {
    ItemStack equipped = Minecraft.getMinecraft().player.getHeldItemMainhand();
    if (equipped.getItem() == itemConduitProbe.getItem()) {
      return equipped;
    }
    return Prep.getEmpty();
  }

  private static void doRenderOverlay(RenderGameOverlayEvent event, @Nonnull ItemStack equippedProbe) {
    IconEIO icon1, icon2;
    if (equippedProbe.getItemDamage() == 0) {
      icon1 = IconEIO.PROBE_OVERLAY_PROBE;
      icon2 = IconEIO.PROBE_OVERLAY_COPY_OFF;
    } else {
      icon1 = IconEIO.PROBE_OVERLAY_PROBE_OFF;
      icon2 = IconEIO.PROBE_OVERLAY_COPY;
    }
    ScaledResolution res = event.getResolution();

    double offsetX = res.getScaledWidth() - 48;
    double offsetY = res.getScaledHeight() - 16;
    GL11.glColor4f(1, 1, 1, 0.75f);
    icon1.getMap().render(icon1, offsetX - 32, offsetY - 32, 32, 32, 0, true);
    icon2.getMap().render(icon2, offsetX, offsetY - 32, 32, 32, 0, true);
  }

}
