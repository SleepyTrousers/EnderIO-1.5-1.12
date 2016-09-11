package crazypants.enderio.gui;

import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.enderio.core.api.client.render.IWidgetIcon;
import com.enderio.core.api.client.render.IWidgetMap;

public enum IconEIO implements IWidgetIcon {

  TICK(0, 192),
  MINUS(16, 192),
  LOCK_UNLOCKED(32, 192),
  LOCK_LOCKED(48, 192),
  CROSS(64, 192),
  PLUS(80, 192),
  ARROWS(176, 192),
  ADD(112, 240),
  SUBTRACT(96, 240),

  WRENCH_OVERLAY_POWER(0, 112),
  WRENCH_OVERLAY_POWER_OFF(0 + 48, 112),

  WRENCH_OVERLAY_REDSTONE(16, 112),
  WRENCH_OVERLAY_REDSTONE_OFF(16 + 48, 112),

  WRENCH_OVERLAY_FLUID(0, 128),
  WRENCH_OVERLAY_FLUID_OFF(0 + 48, 128),

  WRENCH_OVERLAY_ITEM(16, 128),
  WRENCH_OVERLAY_ITEM_OFF(16 + 48, 128),

  WRENCH_OVERLAY_GAS(32, 112),
  WRENCH_OVERLAY_GAS_OFF(32 + 48, 112),

  WRENCH_OVERLAY_ME(32, 128),
  WRENCH_OVERLAY_ME_OFF(32 + 48, 128),

  WRENCH_OVERLAY_OC(96, 112),
  WRENCH_OVERLAY_OC_OFF(96, 128),

  PROBE_OVERLAY_PROBE(112, 192, 16, 16),
  PROBE_OVERLAY_COPY(128, 192, 16, 16),
  PROBE_OVERLAY_PROBE_OFF(144, 192, 16, 16),
  PROBE_OVERLAY_COPY_OFF(160, 192, 16, 16),

  ACTIVE_TAB(205, 0, 19, 24),
  INACTIVE_TAB(237, 0, 19, 24),

  REDSTONE_MODE_WITHOUT_SIGNAL(64, 224),
  REDSTONE_MODE_WITH_SIGNAL(80, 224),
  REDSTONE_MODE_ALWAYS(96, 224),
  REDSTONE_MODE_NEVER(112, 224),

  FILTER_META(160, 224),
  FILTER_NBT(176, 224),
  FILTER_ORE_DICT(192, 224),
  FILTER_WHITELIST(0, 224),
  FILTER_BLACKLIST(0, 240),
  FILTER_STICKY(16, 240),
  FILTER_STICKY_OFF(32, 240),

  FILTER_META_OFF(208, 224),
  FILTER_NBT_OFF(224, 224),
  FILTER_ORE_DICT_OFF(240, 224),

  FILTER_FUZZY_DISABLED(240, 176),
  FILTER_FUZZY_25(224, 176),
  FILTER_FUZZY_50(208, 176),
  FILTER_FUZZY_75(192, 176),
  FILTER_FUZZY_99(176, 176),

  @Deprecated
  INPUT_OLD(128, 197 + 32, 30, 14),
  @Deprecated
  OUTPUT_OLD(128, 180 + 32, 30, 14),

  INPUT(32, 176, 16, 8),
  OUTPUT(48, 176, 16, 8),
  INPUT_OUTPUT(32, 176, 32, 8),
  DISABLED(64, 176),
  LOOP(80, 176),
  LOOP_OFF(96, 176),

  ROUND_ROBIN(64, 240),
  ROUND_ROBIN_OFF(80, 240),

  IO_CONFIG_UP(16, 224),
  IO_CONFIG_DOWN(32, 224),
  IO_WHATSIT(128, 208),

  RECIPE(0, 224),

  SOUND(176, 208),

  XP(128, 224),
  XP_PLUS(144, 224),

  SINGLE_PLUS(240, 192),
  DOUBLE_PLUS(224, 192),
  TRIPLE_PLUS(208, 192),
  SINGLE_MINUS(240, 208),
  DOUBLE_MINUS(224, 208),
  TRIPLE_MINUS(208, 208),

  ENDER_RAIL(192, 208),

  FILTER(192, 192),

  ITEM_STACK(144, 208),
  ITEM_SINGLE(160, 208),

  SUN(160, 240),
  RAIN(176, 240),
  THUNDER(192, 240),

  SORT_DIR_DOWN(0, 176),
  SORT_DIR_UP(16, 176),

  SORT_NAME_DOWN(208, 240, SORT_DIR_DOWN),
  SORT_NAME_UP(208, 240, SORT_DIR_UP),
  SORT_SIZE_DOWN(224, 240, SORT_DIR_DOWN),
  SORT_SIZE_UP(224, 240, SORT_DIR_UP),
  SORT_MOD_DOWN(240, 240, SORT_DIR_DOWN),
  SORT_MOD_UP(240, 240, SORT_DIR_UP);

  // Texture size is actually 512 but everything is aligned to a 256 grid
  private static final int TEX_SIZE = 256;

  public final int x;
  public final int y;
  public final int width;
  public final int height;
  public final IconEIO overlay;

  public static final ResourceLocation TEXTURE = new ResourceLocation("enderio:textures/gui/widgetsv2.png");

  public static final IWidgetMap map = new IWidgetMap.WidgetMapImpl(TEX_SIZE, TEXTURE) {
    @Override
    public void render(IWidgetIcon widget, double x, double y, double width, double height, double zLevel, boolean doDraw,
        boolean flipY) {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      super.render(widget, x, y, width, height, zLevel, doDraw, flipY);
    }
  };

  IconEIO(int x, int y) {
    this(x, y, null);
  }

  IconEIO(int x, int y, IconEIO overlay) {
    this(x, y, 16, 16, overlay);
  }

  IconEIO(int x, int y, int width, int height) {
    this(x, y, width, height, null);
  }

  private IconEIO(int x, int y, int width, int height, IconEIO overlay) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.overlay = overlay;
  }

  @Override
  public IWidgetMap getMap() {
    return map;
  }

  @Override
  public int getX() {
    return x;
  }

  @Override
  public int getY() {
    return y;
  }

  @Override
  public int getWidth() {
    return width;
  }

  @Override
  public int getHeight() {
    return height;
  }

  @Override
  public IconEIO getOverlay() {
    return overlay;
  }
}
