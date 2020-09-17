package crazypants.enderio.base.gui;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.render.IWidgetIcon;
import com.enderio.core.api.client.render.IWidgetMap;

import crazypants.enderio.base.EnderIO;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public enum IconEIO implements IWidgetIcon {

  TOP_NOICON(0, 0, 20, 1),
  TOP_MORE(0, 0, 100, 1),
  TOP_NOICON_WIDE(0, 1, 23, 1),
  TOP_NOMORE(0, 1, 100, 1),

  TICK(0, 192),
  MINUS(16, 192),
  LOCK_UNLOCKED(32, 192),
  LOCK_LOCKED(48, 192),
  CROSS(64, 192),
  PLUS(80, 192),
  QUESTION(16, 208),
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

  WRENCH_OVERLAY_RS(112, 112),
  WRENCH_OVERLAY_RS_OFF(112, 128),

  WRENCH_OVERLAY_DATA(128, 112),
  WRENCH_OVERLAY_DATA_OFF(128, 128),

  PROBE_OVERLAY_PROBE(112, 192, 16, 16),
  PROBE_OVERLAY_COPY(128, 192, 16, 16),
  PROBE_OVERLAY_PROBE_OFF(144, 192, 16, 16),
  PROBE_OVERLAY_COPY_OFF(160, 192, 16, 16),

  TAB_FRAME_RIGHT(208, 0, 48, 24),
  TAB_FRAME_LEFT(208, 24, 48, 24),
  TAB_BG(208, 48, 48, 24),

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

  FILTER_DAMAGE_00_25(0, 160),
  FILTER_DAMAGE_00_50(16, 160),
  FILTER_DAMAGE_00_75(32, 160),
  FILTER_DAMAGE_25_00(48, 160),
  FILTER_DAMAGE_50_00(64, 160),
  FILTER_DAMAGE_75_00(80, 160),
  FILTER_DAMAGE_01_00(96, 160),
  FILTER_DAMAGE_00_00(112, 160),
  FILTER_DAMAGE_YES(128, 160),
  FILTER_DAMAGE_NOT(144, 160),
  FILTER_DAMAGE_OFF(160, 160),

  TRASHCAN(176, 160),

  FILTER_SPECIES_BOTH(80, 208),
  FILTER_SPECIES_PRIMARY(96, 208),
  FILTER_SPECIES_SECONDARY(112, 208),

  INPUT(32, 176, 16, 8),
  OUTPUT(48, 176, 16, 8),
  INPUT_OUTPUT(32, 176, 32, 8),
  DISABLED(64, 176),
  LOOP(80, 176),
  LOOP_OFF(96, 176),

  VOID_LIQUID(112, 176),
  DUMP_LIQUID(128, 176),

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
  SORT_MOD_UP(240, 240, SORT_DIR_UP),

  SHOW_RANGE(144, 176),
  HIDE_RANGE(160, 176),

  MODE_BACKGROUND(0, 98, 16, 10),

  GENERIC_VERBOTEN(192, 160),
  CAPACITOR(208, 160),
  VISIBLE_NO(224, 160),
  VISIBLE_YES(240, 160),

  YETA_GEAR(48, 224),

  GEAR_LIGHT(16, 224),
  GEAR_DARK(32, 224),

  ARROW_LEFT(0, 208),

  ALLOY_MODE_FURNACE(0, 144),
  ALLOY_MODE_BOTH(16, 144),
  ALLOY_MODE_ALLOY(32, 144),

  RECIPE_BOOK(48, 144),
  INFO(64, 144),

  GLASS_LIGHT(80, 144),
  GLASS_DARK(96, 144),
  GLASS_PLAYER(112, 144),
  GLASS_MONSTER(128, 144),
  GLASS_ANIMAL(144, 144),
  GLASS_NOT(160, 144),

  ;

  // Texture size is actually 512 but everything is aligned to a 256 grid
  private static final int TEX_SIZE = 256;

  public final int x;
  public final int y;
  public final int width;
  public final int height;
  public final @Nullable IconEIO overlay;

  public static final @Nonnull ResourceLocation TEXTURE = EnderIO.proxy.getGuiTexture("widgetsv2");

  public static final @Nonnull IWidgetMap map = new IWidgetMap.WidgetMapImpl(TEX_SIZE, TEXTURE) {
    @Override
    @SideOnly(Side.CLIENT)
    public void render(@Nonnull IWidgetIcon widget, double x, double y, double width, double height, double zLevel, boolean doDraw, boolean flipY) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      super.render(widget, x, y, width, height, zLevel, doDraw, flipY);
    }
  };

  IconEIO(int x, int y) {
    this(x, y, 16, 16, null);
  }

  IconEIO(int x, int y, @Nonnull IconEIO overlay) {
    this(x, y, 16, 16, overlay);
  }

  IconEIO(int x, int y, int width, int height) {
    this(x, y, width, height, null);
  }

  private IconEIO(int x, int y, int width, int height, @Nullable IconEIO overlay) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.overlay = overlay;
  }

  @Override
  public @Nonnull IWidgetMap getMap() {
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
  public @Nullable IconEIO getOverlay() {
    return overlay;
  }

}
