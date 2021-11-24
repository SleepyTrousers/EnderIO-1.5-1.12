package com.enderio.base.client.renderer;

import com.enderio.core.common.util.EnergyUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.CapabilityEnergy;

public class DarkSteelDurabilityRenderer {

    public static final int ENERGY_BAR_RGB = 0x00B168E4;

    public static void renderOverlay(ItemStack pStack, int pXPosition, int pYPosition) {
        if(EnergyUtil.getMaxEnergyStored(pStack) <= 0) {
            return;
        }
        RenderSystem.disableDepthTest();
        RenderSystem.disableTexture();
        RenderSystem.disableBlend();
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();

        int offset = 12;
        int barWidth = Math.round(13.0F - (float)getFilledRatio(pStack) * 13.0F);
        fillRect(bufferbuilder, pXPosition + 2, pYPosition + offset, 13, 1, 0, 0, 0);
        fillRect(bufferbuilder, pXPosition + 2, pYPosition + offset, barWidth, 1,
            FastColor.ARGB32.red(ENERGY_BAR_RGB), FastColor.ARGB32.green(ENERGY_BAR_RGB), FastColor.ARGB32.blue(ENERGY_BAR_RGB));

        RenderSystem.enableBlend();
        RenderSystem.enableTexture();
        RenderSystem.enableDepthTest();
    }

    private static double getFilledRatio(ItemStack stack) {
        return stack
            .getCapability(CapabilityEnergy.ENERGY)
            .map(energyStorage -> 1.0d - (double) energyStorage.getEnergyStored() / (double) energyStorage.getMaxEnergyStored())
            .orElse(0d);
    }

    private static void fillRect(BufferBuilder pRenderer, int pX, int pY, int pWidth, int pHeight, int pRed, int pGreen, int pBlue) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        pRenderer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        pRenderer.vertex(pX, pY, 0.0D).color(pRed, pGreen, pBlue, 255).endVertex();
        pRenderer.vertex(pX, pY + pHeight, 0.0D).color(pRed, pGreen, pBlue, 255).endVertex();
        pRenderer.vertex(pX + pWidth, pY + pHeight, 0.0D).color(pRed, pGreen, pBlue, 255).endVertex();
        pRenderer.vertex(pX + pWidth, pY, 0.0D).color(pRed, pGreen, pBlue, 255).endVertex();
        pRenderer.end();
        BufferUploader.end(pRenderer);
    }

}
