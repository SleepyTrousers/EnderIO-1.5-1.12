package com.enderio.core.mixin.client;

import com.enderio.core.client.render.IItemOverlayRender;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
    @Inject(at = @At("RETURN"), method = "renderGuiItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V")
    public void renderGuiItemDecorations(Font pFr, ItemStack pStack, int pXPosition, int pYPosition, @Nullable String pText, CallbackInfo callbackInfo) {
        if (!pStack.isEmpty()) {
            if (pStack.getItem() instanceof IItemOverlayRender item) {
                item.renderOverlay(pStack, pXPosition, pYPosition);
            }
        }
    }
}
