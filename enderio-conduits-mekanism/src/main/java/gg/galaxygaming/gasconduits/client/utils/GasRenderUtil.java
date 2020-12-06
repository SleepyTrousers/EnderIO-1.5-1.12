package gg.galaxygaming.gasconduits.client.utils;

import com.enderio.core.client.render.RenderUtil;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasStack;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

public class GasRenderUtil {

    @Nonnull
    public static TextureAtlasSprite getStillTexture(@Nonnull GasStack gasStack) {
        Gas gas = gasStack.getGas();
        return gas == null ? RenderUtil.getMissingSprite() : getStillTexture(gas);
    }

    @Nonnull
    public static TextureAtlasSprite getStillTexture(@Nonnull Gas gas) {
        TextureAtlasSprite textureEntry = gas.getSprite();
        return textureEntry != null ? textureEntry : RenderUtil.getMissingSprite();
    }

    public static void renderGuiTank(@Nullable GasStack gas, int capacity, int amount, double x, double y, double width, double height) {
        if (gas == null || gas.getGas() == null || amount <= 0) {
            return;
        }

        TextureAtlasSprite icon = getStillTexture(gas);

        int renderAmount = (int) Math.max(Math.min(height, amount * height / capacity), 1);
        int posY = (int) (y + height - renderAmount);

        RenderUtil.bindBlockTexture();
        int color = gas.getGas().getTint();
        GlStateManager.color((color >> 16 & 0xFF) / 255f, (color >> 8 & 0xFF) / 255f, (color & 0xFF) / 255f);

        GlStateManager.enableBlend();
        for (int i = 0; i < width; i += 16) {
            for (int j = 0; j < renderAmount; j += 16) {
                int drawWidth = (int) Math.min(width - i, 16);
                int drawHeight = Math.min(renderAmount - j, 16);

                int drawX = (int) (x + i);
                int drawY = posY + j;

                double minU = icon.getMinU();
                double maxU = icon.getMaxU();
                double minV = icon.getMinV();
                double maxV = icon.getMaxV();

                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder tes = tessellator.getBuffer();
                tes.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
                double v = minV + (maxV - minV) * drawHeight / 16F;
                double u = minU + (maxU - minU) * drawWidth / 16F;
                tes.pos(drawX, drawY + drawHeight, 0).tex(minU, v).endVertex();
                tes.pos(drawX + drawWidth, drawY + drawHeight, 0).tex(u, v).endVertex();
                tes.pos(drawX + drawWidth, drawY, 0).tex(u, minV).endVertex();
                tes.pos(drawX, drawY, 0).tex(minU, minV).endVertex();
                tessellator.draw();
            }
        }
        GlStateManager.disableBlend();
        GlStateManager.color(1, 1, 1);
    }
}