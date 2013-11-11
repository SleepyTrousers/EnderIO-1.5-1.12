package crazypants.enderio.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;

import org.lwjgl.opengl.GL11;

import crazypants.enderio.conduit.ConduitDisplayMode;
import crazypants.enderio.gui.IconEIO;

public class YetaWrenchOverlayRenderer {

  private ItemYetaWrench wrench;

  public YetaWrenchOverlayRenderer(ItemYetaWrench wrench) {
    this.wrench = wrench;
    MinecraftForge.EVENT_BUS.register(this);
  }

  @ForgeSubscribe(priority = EventPriority.NORMAL)
  public void renderOverlay(RenderGameOverlayEvent event) {
    ItemStack equippedWrench = getEquippedWrench();
    if(equippedWrench != null && event.type == ElementType.ALL) {
      doRenderOverlay(event, equippedWrench);
    }
  }

  private ItemStack getEquippedWrench() {
    ItemStack equipped = Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem();
    if(equipped != null && equipped.itemID == wrench.itemID) {
      return equipped;
    }
    return null;
  }

  private void doRenderOverlay(RenderGameOverlayEvent event, ItemStack equippedWrench) {
    ConduitDisplayMode mode = ConduitDisplayMode.getDisplayMode(equippedWrench);
    ScaledResolution res = event.resolution;

    double offsetX = 16;
    double offsetY = res.getScaledHeight() - 16;

    if(mode == ConduitDisplayMode.ALL) {
      GL11.glColor4f(1, 1, 1, 0.75f);
      IconEIO.WRENCH_OVERLAY_ALL_ON.renderIcon(offsetX, offsetY - 32, 32, 32, 0, true);
      return;
    }

    float c = 0.6f;
    GL11.glColor4f(c, c, c, 0.33f);
    IconEIO.WRENCH_OVERLAY_ALL_OFF.renderIcon(offsetX, offsetY - 32, 32, 32, 0, true);
    GL11.glColor4f(1, 1, 1, 0.75f);
    if(mode == ConduitDisplayMode.POWER) {
      IconEIO.WRENCH_OVERLAY_POWER.renderIcon(offsetX, offsetY - 32, 16, 16, 0, true);
    } else if(mode == ConduitDisplayMode.FLUID) {
      IconEIO.WRENCH_OVERLAY_FLUID.renderIcon(offsetX + 16, offsetY - 16, 16, 16, 0, true);
    } else if(mode == ConduitDisplayMode.ITEM) {
      IconEIO.WRENCH_OVERLAY_ITEM.renderIcon(offsetX, offsetY - 16, 16, 16, 0, true);
    } else if(mode == ConduitDisplayMode.REDSTONE) {
      IconEIO.WRENCH_OVERLAY_REDSTONE.renderIcon(offsetX + 16, offsetY - 32, 16, 16, 0, true);
    }

  }

}
