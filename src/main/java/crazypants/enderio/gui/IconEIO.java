package crazypants.enderio.gui;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import crazypants.render.RenderUtil;

public final class IconEIO {

  public static final IconEIO TICK = new IconEIO(0, 192);
  public static final IconEIO MINUS = new IconEIO(16, 192);
  public static final IconEIO PUBLIC = new IconEIO(32, 192);
  public static final IconEIO PRIVATE = new IconEIO(48, 192);
  public static final IconEIO CROSS = new IconEIO(64, 192);
  public static final IconEIO PLUS = new IconEIO(80, 192);
  public static final IconEIO ARROWS = new IconEIO(176, 192);

  public static final IconEIO BUTTON = new IconEIO(0, 208);
  public static final IconEIO BUTTON_HIGHLIGHT = new IconEIO(16, 208);
  public static final IconEIO BUTTON_DISABLED = new IconEIO(32, 208);
  public static final IconEIO BUTTON_DOWN = new IconEIO(48, 208);
  public static final IconEIO BUTTON_DOWN_HIGHLIGHT = new IconEIO(64, 208);
  public static final IconEIO CHECKED_BUTTON = new IconEIO(112, 208);

  public static final IconEIO WRENCH_OVERLAY_POWER = new IconEIO(0, 112);
  public static final IconEIO WRENCH_OVERLAY_POWER_OFF = new IconEIO(0 + 48, 112);

  public static final IconEIO WRENCH_OVERLAY_REDSTONE = new IconEIO(16, 112);
  public static final IconEIO WRENCH_OVERLAY_REDSTONE_OFF = new IconEIO(16 + 48, 112);

  public static final IconEIO WRENCH_OVERLAY_FLUID = new IconEIO(0, 128);
  public static final IconEIO WRENCH_OVERLAY_FLUID_OFF = new IconEIO(0 + 48, 128);

  public static final IconEIO WRENCH_OVERLAY_ITEM = new IconEIO(16, 128);
  public static final IconEIO WRENCH_OVERLAY_ITEM_OFF = new IconEIO(16 + 48, 128);

  public static final IconEIO WRENCH_OVERLAY_GAS = new IconEIO(32, 112);
  public static final IconEIO WRENCH_OVERLAY_GAS_OFF = new IconEIO(32 + 48, 112);

  public static final IconEIO WRENCH_OVERLAY_ME = new IconEIO(32, 128);
  public static final IconEIO WRENCH_OVERLAY_ME_OFF = new IconEIO(32 + 48, 128);

  public static final IconEIO PROBE_OVERLAY_PROBE = new IconEIO(112, 192, 32, 16);
  public static final IconEIO PROBE_OVERLAY_COPY = new IconEIO(144, 192, 32, 16);

  public static final IconEIO ACTIVE_TAB = new IconEIO(205, 0, 19, 24);
  public static final IconEIO INACTIVE_TAB = new IconEIO(237, 0, 19, 24);

  public static final IconEIO RIGHT_ARROW = new IconEIO(192, 48, 16, 32);
  public static final IconEIO LEFT_ARROW = new IconEIO(208, 48, 16, 32);

  public static final IconEIO REDSTONE_MODE_WITHOUT_SIGNAL = new IconEIO(64, 224);
  public static final IconEIO REDSTONE_MODE_WITH_SIGNAL = new IconEIO(80, 224);
  public static final IconEIO REDSTONE_MODE_ALWAYS = new IconEIO(96, 224);
  public static final IconEIO REDSTONE_MODE_NEVER = new IconEIO(112, 224);

  public static final IconEIO FILTER_META = new IconEIO(160, 224);
  public static final IconEIO FILTER_NBT = new IconEIO(176, 224);
  public static final IconEIO FILTER_ORE_DICT = new IconEIO(192, 224);
  public static final IconEIO FILTER_WHITELIST = new IconEIO(0, 224);
  public static final IconEIO FILTER_BLACKLIST = new IconEIO(0, 240);
  public static final IconEIO FILTER_STICKY = new IconEIO(16, 240);
  public static final IconEIO FILTER_STICKY_OFF = new IconEIO(32, 240);

  public static final IconEIO FILTER_META_OFF = new IconEIO(208, 224);
  public static final IconEIO FILTER_NBT_OFF = new IconEIO(224, 224);
  public static final IconEIO FILTER_ORE_DICT_OFF = new IconEIO(240, 224);

  @Deprecated
  public static final IconEIO INPUT_OLD = new IconEIO(128, 197 + 32, 30, 14);
  @Deprecated
  public static final IconEIO OUTPUT_OLD = new IconEIO(128, 180 + 32, 30, 14);

  public static final IconEIO INPUT = new IconEIO(224, 64, 16, 8);
  public static final IconEIO OUTPUT = new IconEIO(240, 64, 16, 8);
  public static final IconEIO INPUT_OUTPUT = new IconEIO(224, 48, 32, 16);
  public static final IconEIO DISABLED = new IconEIO(48, 240);

  public static final IconEIO LOOP = new IconEIO(224, 96);
  public static final IconEIO LOOP_OFF = new IconEIO(240, 96);

  public static final IconEIO ROUND_ROBIN = new IconEIO(64, 240);
  public static final IconEIO ROUND_ROBIN_OFF = new IconEIO(80, 240);

  public static final IconEIO IO_CONFIG_UP = new IconEIO(16, 224);
  public static final IconEIO IO_CONFIG_DOWN = new IconEIO(32, 224);
  public static final IconEIO IO_WHATSIT = new IconEIO(128, 208);

  public static final IconEIO RECIPE = new IconEIO(0, 224);
  public static final IconEIO RECIPE_BUTTON = new IconEIO(80, 208);

  public static final IconEIO ADD_BUT = new IconEIO(208, 32, 8, 8);
  public static final IconEIO MINUS_BUT = new IconEIO(208, 40, 8, 8);

  public static final IconEIO SOUND = new IconEIO(176, 208);

  public static final IconEIO XP = new IconEIO(128, 224);
  public static final IconEIO XP_PLUS = new IconEIO(144, 224);

  public static final IconEIO SINGLE_PLUS = new IconEIO(240, 192);
  public static final IconEIO DOUBLE_PLUS = new IconEIO(224, 192);
  public static final IconEIO TRIPLE_PLUS = new IconEIO(208, 192);
  public static final IconEIO SINGLE_MINUS = new IconEIO(240, 208);
  public static final IconEIO DOUBLE_MINUS = new IconEIO(224, 208);
  public static final IconEIO TRIPLE_MINUS = new IconEIO(208, 208);

  public static final IconEIO ENDER_RAIL = new IconEIO(192, 208);

  public static final IconEIO FILTER = new IconEIO(192, 192);

  public static final IconEIO ITEM_STACK = new IconEIO(144, 208);
  public static final IconEIO ITEM_SINGLE = new IconEIO(160, 208);
  
  public static final IconEIO FARM_UNLOCK = new IconEIO(128, 240);
  public static final IconEIO FARM_LOCK   = new IconEIO(144, 240);
  
  // Texture size is actually 512 but everything is aligned to a 256 grid
  private static final int TEX_SIZE = 256;
  private static final double PIX_SIZE = 1d / TEX_SIZE;

  public final double minU;
  public final double maxU;
  public final double minV;
  public final double maxV;
  public final double width;
  public final double height;

  public static final ResourceLocation TEXTURE = new ResourceLocation("enderio:textures/gui/widgets.png");

  public IconEIO(int x, int y) {
    this(x, y, 16, 16);
  }

  public IconEIO(int x, int y, int width, int height) {
    this(width, height, (float) (PIX_SIZE * x), (float) (PIX_SIZE * (x + width)), (float) (PIX_SIZE * y), (float) (PIX_SIZE * (y + height)));
  }

  public IconEIO(double width, double height, double minU, double maxU, double minV, double maxV) {
    this.width = width;
    this.height = height;
    this.minU = minU;
    this.maxU = maxU;
    this.minV = minV;
    this.maxV = maxV;
  }

  public void renderIcon(double x, double y) {
    renderIcon(x, y, width, height, 0, false);
  }

  public void renderIcon(double x, double y, boolean doDraw) {
    renderIcon(x, y, width, height, 0, doDraw);
  }

  public void renderIcon(double x, double y, double width, double height, double zLevel, boolean doDraw) {
    renderIcon(x, y, width, height, zLevel, doDraw, false);
  }

  public void renderIcon(double x, double y, double width, double height, double zLevel, boolean doDraw, boolean flipY) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

    Tessellator tessellator = Tessellator.instance;
    if(doDraw) {
      RenderUtil.bindTexture(TEXTURE);
      tessellator.startDrawingQuads();
    }
    if(flipY) {
      tessellator.addVertexWithUV(x, y + height, zLevel, minU, minV);
      tessellator.addVertexWithUV(x + width, y + height, zLevel, maxU, minV);
      tessellator.addVertexWithUV(x + width, y + 0, zLevel, maxU, maxV);
      tessellator.addVertexWithUV(x, y + 0, zLevel, minU, maxV);
    } else {
      tessellator.addVertexWithUV(x, y + height, zLevel, minU, maxV);
      tessellator.addVertexWithUV(x + width, y + height, zLevel, maxU, maxV);
      tessellator.addVertexWithUV(x + width, y + 0, zLevel, maxU, minV);
      tessellator.addVertexWithUV(x, y + 0, zLevel, minU, minV);
    }
    if(doDraw) {
      tessellator.draw();
    }
  }

}
