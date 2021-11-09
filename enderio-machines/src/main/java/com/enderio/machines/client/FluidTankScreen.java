package com.enderio.machines.client;

import com.enderio.base.EnderIO;
import com.enderio.core.client.screen.EIOScreen;
import com.enderio.machines.common.menu.FluidTankMenu;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.apache.commons.lang3.tuple.Pair;

public class FluidTankScreen extends EIOScreen<FluidTankMenu> {
    private static final ResourceLocation BG_TEXTURE = new ResourceLocation(EnderIO.DOMAIN, "textures/gui/tank.png");
    public FluidTankScreen(FluidTankMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
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
    protected void renderBg(PoseStack pPoseStack, float pPartialTicks, int pMouseX, int pMouseY) {
        renderGradleWeirdnessBackground(pPoseStack, pPartialTicks, pMouseX, pMouseY);
    }
}
