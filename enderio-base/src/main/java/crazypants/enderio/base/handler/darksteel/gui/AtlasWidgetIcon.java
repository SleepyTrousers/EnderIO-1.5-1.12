package crazypants.enderio.base.handler.darksteel.gui;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import com.enderio.core.api.client.render.IWidgetIcon;
import com.enderio.core.api.client.render.IWidgetMap;
import com.enderio.core.client.render.RenderUtil;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class AtlasWidgetIcon implements IWidgetIcon {

  private final @Nonnull IWidgetMap map;
  private final @Nonnull TextureAtlasSprite sprite;

  public AtlasWidgetIcon(@Nonnull TextureAtlasSprite sprite) {
    this.sprite = sprite;
    map = new IWidgetMap.WidgetMapImpl(0, TextureMap.LOCATION_BLOCKS_TEXTURE) {
      @Override
      public void render(@Nonnull IWidgetIcon widget, double x, double y, double width, double height, double zLevel, boolean doDraw, boolean flipY) {
        final BufferBuilder tes = Tessellator.getInstance().getBuffer();
        if (doDraw) {
          RenderUtil.bindTexture(getTexture());
          tes.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        }

        double minU = sprite.getMinU();
        double maxU = sprite.getMaxU();
        double minV = sprite.getMinV();
        double maxV = sprite.getMaxV();

        if (flipY) {
          tes.pos(x, y + height, zLevel).tex(minU, minV).endVertex();
          tes.pos(x + width, y + height, zLevel).tex(maxU, minV).endVertex();
          tes.pos(x + width, y + 0, zLevel).tex(maxU, maxV).endVertex();
          tes.pos(x, y + 0, zLevel).tex(minU, maxV).endVertex();
        } else {
          tes.pos(x, y + height, zLevel).tex(minU, maxV).endVertex();
          tes.pos(x + width, y + height, zLevel).tex(maxU, maxV).endVertex();
          tes.pos(x + width, y + 0, zLevel).tex(maxU, minV).endVertex();
          tes.pos(x, y + 0, zLevel).tex(minU, minV).endVertex();
        }
        final IWidgetIcon overlay = widget.getOverlay();
        if (overlay != null) {
          overlay.getMap().render(overlay, x, y, width, height, zLevel, false, flipY);
        }
        if (doDraw) {
          Tessellator.getInstance().draw();
        }
      }
    };

  }

  @Override
  public int getX() {
    return sprite.getOriginX();
  }

  @Override
  public int getY() {
    return sprite.getOriginY();
  }

  @Override
  public int getWidth() {
    return sprite.getIconWidth();
  }

  @Override
  public int getHeight() {
    return sprite.getIconHeight();
  }

  @Override
  @Nullable
  public IWidgetIcon getOverlay() {
    return null;
  }

  @Override
  @Nonnull
  public IWidgetMap getMap() {
    return map;
  }

}
