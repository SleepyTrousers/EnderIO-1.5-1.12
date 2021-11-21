package com.enderio.core.client.screen;

import com.enderio.core.common.util.Vector2i;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;

public interface IEnderScreen {

    default Screen getScreen() {
        return (Screen) this;
    }

    default void renderIcon(PoseStack pPoseStack, Vector2i pos, IIcon icon) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, icon.getTextureLocation());
        GuiComponent.blit(pPoseStack, pos.getX(), pos.getY(), icon.getTexturePosition().getX(), icon.getTexturePosition().getY(), icon.getIconSize().getX(),  icon.getIconSize().getY(), icon.getTextureSize().getX(), icon.getTextureSize().getY());
    }

    default void renderSimpleArea(PoseStack pPoseStack, Vector2i pos, Vector2i pos2) {
        GuiComponent.fill(pPoseStack, pos.getX(), pos.getY(), pos2.getX(), pos2.getY(), 0xFF8B8B8B);
        GuiComponent.fill(pPoseStack, pos.getX(), pos.getY(), pos2.getX() - 1, pos2.getY() - 1, 0xFF373737);
        GuiComponent.fill(pPoseStack, pos.getX() + 1, pos.getY() + 1, pos2.getX(), pos2.getY(), 0xFFFFFFFF);
        GuiComponent.fill(pPoseStack, pos.getX() + 1, pos.getY() + 1, pos2.getX() - 1, pos2.getY() - 1, 0xFF8B8B8B);
    }

    default void renderIconBackground(PoseStack pPoseStack, Vector2i pos, IIcon icon) {
        renderSimpleArea(pPoseStack, pos , pos.add(icon.getIconSize()).expand(2));
    }
     default void renderTooltipAfterEverything(PoseStack pPoseStack, Component pText, int pMouseX, int pMouseY) {
         addTooltip(new LateTooltipData(pPoseStack, pText, pMouseX, pMouseY));
    }

    /**
     * schedule a gui push to prevent multiple EnumIconWidgets interfering with one another:
     * If 2 EnumIconWidgets exist and the second one is expanded. On a click on the first one this will happen:
     * The
     * @param screen
     */
    void scheduleGuiPush(Screen screen);

    void addTooltip(LateTooltipData data);

    @RequiredArgsConstructor
    @Getter
    class LateTooltipData {
        private final PoseStack poseStack;
        private final Component text;
        private final int mouseX;
        private final int mouseY;
    }
}
