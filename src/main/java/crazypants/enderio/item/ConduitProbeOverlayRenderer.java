package crazypants.enderio.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import crazypants.enderio.EnderIO;
import crazypants.enderio.conduit.ConduitDisplayMode;
import crazypants.enderio.conduit.gas.GasUtil;
import crazypants.enderio.gui.IconEIO;

public class ConduitProbeOverlayRenderer {

  //private ItemConduitProbe probe;

  public ConduitProbeOverlayRenderer() {
    MinecraftForge.EVENT_BUS.register(this);
  }

  @SubscribeEvent
  public void renderOverlay(RenderGameOverlayEvent event) {
    ItemStack equippedProbe = getEquippedProbe();
    if(equippedProbe != null && event.type == ElementType.ALL) {
      doRenderOverlay(event, equippedProbe);
    }
  }

  private ItemStack getEquippedProbe() {
    ItemStack equipped = Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem();
    if(equipped != null && equipped.getItem() == EnderIO.itemConduitProbe) {
      return equipped;
    }
    return null;
  }

  private void doRenderOverlay(RenderGameOverlayEvent event, ItemStack equippedProbe) {   
    IconEIO icon;
    if(equippedProbe.getItemDamage() == 0) {
      icon = IconEIO.PROBE_OVERLAY_PROBE;
    } else {
      icon = IconEIO.PROBE_OVERLAY_COPY;
    }
    ScaledResolution res = event.resolution;

    double offsetX = 16;
    double offsetY = res.getScaledHeight() - 16;
    GL11.glColor4f(1, 1, 1, 0.75f);
    icon.renderIcon(offsetX, offsetY - 32, 64, 32, 0, true);
  }

}
