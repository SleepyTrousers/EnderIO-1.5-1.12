package crazypants.enderio.item;

import java.awt.Point;
import java.util.EnumMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import crazypants.enderio.api.tool.IConduitControl;
import crazypants.enderio.conduit.ConduitDisplayMode;
import crazypants.enderio.conduit.gas.GasUtil;
import crazypants.enderio.conduit.me.MEUtil;
import crazypants.enderio.gui.IconEIO;

public class YetaWrenchOverlayRenderer {

  private IconEIO[] onIcons;
  private IconEIO[] offIcons;
  private Point[] iconOffsets;
  private Map<ConduitDisplayMode, Integer> indices = new EnumMap<ConduitDisplayMode, Integer>(ConduitDisplayMode.class);

  public YetaWrenchOverlayRenderer() {
    MinecraftForge.EVENT_BUS.register(this);
  }

  @SubscribeEvent
  public void renderOverlay(RenderGameOverlayEvent event) {
    ItemStack equippedWrench = getEquippedWrench();
    if(equippedWrench != null && event.type == ElementType.ALL) {
      doRenderOverlay(event, equippedWrench);
    }
  }

  private ItemStack getEquippedWrench() {
    EntityPlayer player = Minecraft.getMinecraft().thePlayer;
    ItemStack equipped = player.getCurrentEquippedItem();
    if(equipped != null && equipped.getItem() instanceof IConduitControl) {
      return ((IConduitControl) equipped.getItem()).showOverlay(equipped, player) ? equipped : null;
    }
    return null;
  }

  private void doRenderOverlay(RenderGameOverlayEvent event, ItemStack equippedWrench) {
    initIcons();

    ConduitDisplayMode mode = ConduitDisplayMode.getDisplayMode(equippedWrench);
    ScaledResolution res = event.resolution;

    double offsetX = res.getScaledWidth() - (16 + ((onIcons.length / 2 + (onIcons.length % 2)) * 16));
    double offsetY = res.getScaledHeight() - 16;
    if(mode == ConduitDisplayMode.ALL) {
      GL11.glColor4f(1, 1, 1, 0.75f);
      renderAll(onIcons, offsetX, offsetY);
      return;
    }

    float c = 0.6f;
    GL11.glColor4f(c, c, c, 0.33f);
    renderAll(offIcons, offsetX, offsetY);
    if(indices.containsKey(mode)) {
      GL11.glColor4f(1, 1, 1, 0.75f);
      int index = indices.get(mode);
      Point p = iconOffsets[index];
      onIcons[index].renderIcon(offsetX + p.x, offsetY + p.y, 16, 16, 0, true);
    }
  }

  private void renderAll(IconEIO[] icons, double offsetX, double offsetY) {
    int i = 0;
    for (Point p : iconOffsets) {
      icons[i].renderIcon(offsetX + p.x, offsetY + p.y, 16, 16, 0, true);
      i++;
    }
  }

  private void initIcons() {
    int numIcons = getNumConduitsEnabled();
    if(onIcons != null && onIcons.length == numIcons) {
      return;
    }
    boolean meEnabled = MEUtil.isMEEnabled();
    boolean gasEnabled = GasUtil.isGasConduitEnabled();

    onIcons = new IconEIO[numIcons];
    offIcons = new IconEIO[numIcons];
    iconOffsets = new Point[numIcons];

    indices.put(ConduitDisplayMode.POWER, 0);
    onIcons[0] = IconEIO.WRENCH_OVERLAY_POWER;
    offIcons[0] = IconEIO.WRENCH_OVERLAY_POWER_OFF;

    indices.put(ConduitDisplayMode.REDSTONE, 1);
    onIcons[1] = IconEIO.WRENCH_OVERLAY_REDSTONE;
    offIcons[1] = IconEIO.WRENCH_OVERLAY_REDSTONE_OFF;

    indices.put(ConduitDisplayMode.FLUID, 2);
    onIcons[2] = IconEIO.WRENCH_OVERLAY_FLUID;
    offIcons[2] = IconEIO.WRENCH_OVERLAY_FLUID_OFF;

    indices.put(ConduitDisplayMode.ITEM, 3);
    onIcons[3] = IconEIO.WRENCH_OVERLAY_ITEM;
    offIcons[3] = IconEIO.WRENCH_OVERLAY_ITEM_OFF;

    if(meEnabled) {
      indices.put(ConduitDisplayMode.ME, 4);
      onIcons[4] = IconEIO.WRENCH_OVERLAY_ME;
      offIcons[4] = IconEIO.WRENCH_OVERLAY_ME_OFF;
    }
    if(gasEnabled) {
      int index = meEnabled ? 5 : 4;
      indices.put(ConduitDisplayMode.GAS, index);
      onIcons[index] = IconEIO.WRENCH_OVERLAY_GAS;
      offIcons[index] = IconEIO.WRENCH_OVERLAY_GAS_OFF;
    }

    int xOffset = numIcons == 5 ? 8 : 0;
    iconOffsets[0] = new Point(xOffset, -32);
    iconOffsets[1] = new Point(xOffset + 16, -32);
    xOffset = 0;
    iconOffsets[2] = new Point(xOffset, -16);
    iconOffsets[3] = new Point(xOffset + 16, -16);
    if(numIcons == 5) {
      iconOffsets[4] = new Point(xOffset + 32, -16);
    } else if(numIcons == 6) {
      iconOffsets[4] = new Point(xOffset + 32, -32);
      iconOffsets[5] = new Point(xOffset + 32, -16);
    }
  }

  private int getNumConduitsEnabled() {
    boolean meEnabled = MEUtil.isMEEnabled();
    boolean gasEnabled = GasUtil.isGasConduitEnabled();
    return 4 + (meEnabled ? 1 : 0) + (gasEnabled ? 1 : 0);
  }

}
