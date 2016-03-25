package crazypants.enderio.item;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import cofh.api.energy.IEnergyContainerItem;
import crazypants.enderio.item.darksteel.upgrade.EnergyUpgrade;

public class PowerBarOverlayRenderHelper {

  /*
   * These flags are not suited for the config file, but we might decide later to use different values.
   * 
   * MIMIC_VANILLA_RENDERBUG: Vanilla renders the bars 1 pixel too wide. If set to true, we'll do also.
   * 
   * HIDE_VANILLA_RENDERBUG: Overpaint vanilla's mistake instead. Don't use together with MIMIC_VANILLA_RENDERBUG.
   * 
   * SHOW_ON_FULL: Show power bar when energy storage is full. (Vanilla damage bar is hidden when full)
   * 
   * SHOW_ON_FULL_UPGRADEABLE: Same but for items that can be upgraded to have an energy storage. This will give a visual difference between unupgraded and full
   * items.
   * 
   * SHOW_ON_EMPTY: Show power bar when energy storage is empty. Should not be false if SHOW_ON_FULL is false, too.
   * 
   * SHOW_ON_EMPTY_UPGRADEABLE: Same for upgradable items.
   */
  private static final boolean MIMIC_VANILLA_RENDERBUG = false;
  private static final boolean HIDE_VANILLA_RENDERBUG = true;
  private static final boolean SHOW_ON_FULL = false;
  private static final boolean SHOW_ON_FULL_UPGRADEABLE = true;
  private static final boolean SHOW_ON_EMPTY = true;
  private static final boolean SHOW_ON_EMPTY_UPGRADEABLE = true;

  private static final double BAR_W = MIMIC_VANILLA_RENDERBUG ? 13d : 12d;

  /**
   * Instance for items that always have their power buffer
   */
  public static final PowerBarOverlayRenderHelper instance = new PowerBarOverlayRenderHelper(false);
  /**
   * Instance for items that can be upgraded to get a power buffer
   */
  public static final PowerBarOverlayRenderHelper instance_upgradeable = new PowerBarOverlayRenderHelper(true);

  private final boolean isUpgradeableItem;

  protected PowerBarOverlayRenderHelper(boolean isUpgradeableItem) {
    this.isUpgradeableItem = isUpgradeableItem;
  }

  public void render(ItemStack stack, int xPosition, int yPosition) {
    if (hasEnergyStore(stack) && stack.getItem() instanceof IEnergyContainerItem) {
      IEnergyContainerItem energyItem = (IEnergyContainerItem) stack.getItem();
      int maxEnergy = energyItem.getMaxEnergyStored(stack);
      if (maxEnergy > 0) {
        int energy = energyItem.getEnergyStored(stack);
        if (shouldShowBar(maxEnergy, energy)) {
          double level = (double) energy / (double) maxEnergy;
          boolean up = stack.getItem().showDurabilityBar(stack);
          boolean top = stack.stackSize != 1;
          render(level, xPosition, yPosition, top ? 12 : up ? 2 : 0);
          return;
        }
      }
    }
    if (HIDE_VANILLA_RENDERBUG && stack.getItem().showDurabilityBar(stack)) {
      overpaintVanillaRenderBug(xPosition, yPosition);
    }
  }

  private boolean shouldShowBar(int maxEnergy, int energy) {
    if (energy < 0 || energy > maxEnergy) {
      return false;
    }
    if (energy == 0) {
      return isUpgradeableItem ? SHOW_ON_EMPTY_UPGRADEABLE : SHOW_ON_EMPTY;
    }
    if (energy == maxEnergy) {
      return isUpgradeableItem ? SHOW_ON_FULL_UPGRADEABLE : SHOW_ON_FULL;
    }
    return true;
  }

  private boolean hasEnergyStore(ItemStack stack) {
    return stack != null && (!isUpgradeableItem || EnergyUpgrade.loadFromItem(stack) != null);
  }

  public void render(double level, int xPosition, int yPosition, int offset) {
    double width = level * BAR_W;
    GlStateManager.disableLighting();
    GlStateManager.disableDepth();
    GlStateManager.disableTexture2D();
    GlStateManager.disableAlpha();
    GlStateManager.disableBlend();
    GlStateManager.shadeModel(GL11.GL_SMOOTH);
    Tessellator tessellator = Tessellator.getInstance();
    WorldRenderer worldrenderer = tessellator.getWorldRenderer();
    worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
    this.drawPlain(worldrenderer, xPosition + 2, yPosition + 13 - offset, 13, 2, 0, 0, 0, 255);
    this.drawGrad(worldrenderer, xPosition + 2, yPosition + 13 - offset, (BAR_W + width) / 2, 1, 0x02, 0x03, 0x60, 255, 0x2D, 0xCE, 0xFA, 255);
    this.drawRight(worldrenderer, xPosition + 2 + (int) BAR_W, yPosition + 13 - offset, BAR_W - width, 1, 0x00, 0x00, 0x30, 255);
    if (HIDE_VANILLA_RENDERBUG && offset == 2) {
      overpaintVanillaRenderBug(worldrenderer, xPosition, yPosition);
    }
    tessellator.draw();
    GlStateManager.shadeModel(GL11.GL_FLAT);
    GlStateManager.enableAlpha();
    GlStateManager.enableTexture2D();
    GlStateManager.enableLighting();
    GlStateManager.enableDepth();
  }

  private void drawGrad(WorldRenderer renderer, int x, int y, double width, int height, int redL, int greenL, int blueL, int alphaL, int redR, int greenR,
      int blueR, int alphaR) {
    renderer.pos(x + 0, y + 0, 0.0D).color(redL, greenL, blueL, alphaL).endVertex();
    renderer.pos(x + 0, y + height, 0.0D).color(redL, greenL, blueL, alphaL).endVertex();
    renderer.pos(x + width, y + height, 0.0D).color(redR, greenR, blueR, alphaR).endVertex();
    renderer.pos(x + width, y + 0, 0.0D).color(redR, greenR, blueR, alphaR).endVertex();
  }

  private void drawPlain(WorldRenderer renderer, int x, int y, double width, int height, int red, int green, int blue, int alpha) {
    renderer.pos(x + 0, y + 0, 0.0D).color(red, green, blue, alpha).endVertex();
    renderer.pos(x + 0, y + height, 0.0D).color(red, green, blue, alpha).endVertex();
    renderer.pos(x + width, y + height, 0.0D).color(red, green, blue, alpha).endVertex();
    renderer.pos(x + width, y + 0, 0.0D).color(red, green, blue, alpha).endVertex();
  }

  private void drawRight(WorldRenderer renderer, int x, int y, double width, int height, int red, int green, int blue, int alpha) {
    renderer.pos(x - width, y + 0, 0.0D).color(red, green, blue, alpha).endVertex();
    renderer.pos(x - width, y + height, 0.0D).color(red, green, blue, alpha).endVertex();
    renderer.pos(x, y + height, 0.0D).color(red, green, blue, alpha).endVertex();
    renderer.pos(x, y + 0, 0.0D).color(red, green, blue, alpha).endVertex();
  }

  private void overpaintVanillaRenderBug(int xPosition, int yPosition) {
    GlStateManager.disableLighting();
    GlStateManager.disableDepth();
    GlStateManager.disableTexture2D();
    GlStateManager.disableAlpha();
    GlStateManager.disableBlend();
    Tessellator tessellator = Tessellator.getInstance();
    WorldRenderer worldrenderer = tessellator.getWorldRenderer();
    worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
    overpaintVanillaRenderBug(worldrenderer, xPosition, yPosition);
    tessellator.draw();
    GlStateManager.enableAlpha();
    GlStateManager.enableTexture2D();
    GlStateManager.enableLighting();
    GlStateManager.enableDepth();
  }

  private void overpaintVanillaRenderBug(WorldRenderer worldrenderer, int xPosition, int yPosition) {
    this.drawPlain(worldrenderer, xPosition + 2 + 12, yPosition + 13, 1, 1, 0, 0, 0, 255);
  }

}
