package crazypants.enderio.conduits.refinedstorage.conduit.gui;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.render.IWidgetIcon;
import com.enderio.core.api.client.render.IWidgetMap;

import crazypants.enderio.base.EnderIO;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public enum IconRS implements IWidgetIcon {

  WRENCH_OVERLAY_RS(0, 0),
  WRENCH_OVERLAY_RS_OFF(32, 0),

  ;

  // Texture size is actually 512 but everything is aligned to a 256 grid
  private static final int TEX_SIZE = 32;

  public final int x;
  public final int y;
  public final int width;
  public final int height;
  public final @Nullable IconRS overlay;

  public static final @Nonnull ResourceLocation TEXTURE = EnderIO.proxy.getGuiTexture("rs_widgets");

  public static final @Nonnull IWidgetMap map = new IWidgetMap.WidgetMapImpl(TEX_SIZE, TEXTURE) {
    @Override
    @SideOnly(Side.CLIENT)
    public void render(@Nonnull IWidgetIcon widget, double x, double y, double width, double height, double zLevel, boolean doDraw, boolean flipY) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      super.render(widget, x, y, width, height, zLevel, doDraw, flipY);
    }
  };

  IconRS(int x, int y) {
    this(x, y, 16, 16, null);
  }

  IconRS(int x, int y, @Nonnull IconRS overlay) {
    this(x, y, 16, 16, overlay);
  }

  IconRS(int x, int y, int width, int height) {
    this(x, y, width, height, null);
  }

  private IconRS(int x, int y, int width, int height, @Nullable IconRS overlay) {
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
  public @Nullable IconRS getOverlay() {
    return overlay;
  }

}
