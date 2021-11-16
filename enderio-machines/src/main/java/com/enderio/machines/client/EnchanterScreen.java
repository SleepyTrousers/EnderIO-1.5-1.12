package com.enderio.machines.client;

import org.apache.commons.lang3.tuple.Pair;

import com.enderio.base.EnderIO;
import com.enderio.core.client.screen.EIOScreen;
import com.enderio.core.client.screen.EnumIconWidget;
import com.enderio.machines.common.menu.EnchanterMenu;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class EnchanterScreen extends EIOScreen<EnchanterMenu>{
    private static final ResourceLocation BG_TEXTURE = new ResourceLocation(EnderIO.DOMAIN, "textures/gui/enchanter.png");

    public EnchanterScreen(EnchanterMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }
    
    @Override
    protected void init() {
        super.init();
        addRenderableWidget(new EnumIconWidget<>(this, leftPos + imageWidth - 8 - 12, topPos + 6, () -> menu.getBlockEntity().getRedstoneControl(), control -> menu.getBlockEntity().setRedstoneControl(control)));
    }
    
    @Override
    protected ResourceLocation getBackgroundImage() {
        return BG_TEXTURE;
    }

    @Override
    protected Pair<Integer, Integer> getBackgroundImageSize() {
        return Pair.of(176, 166);
    }
    
    @Override
    public void render(PoseStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks) {
        super.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
        renderTooltip(pMatrixStack, pMouseX, pMouseY);
    }

    @Override
    protected void renderBg(PoseStack pPoseStack, float pPartialTicks, int pMouseX, int pMouseY) {
        renderGradleWeirdnessBackground(pPoseStack, pPartialTicks, pMouseX, pMouseY);
    }
}
