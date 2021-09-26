package com.enderio.base.client.renderers;

import com.enderio.base.EnderIO;
import com.enderio.core.common.util.PoseStackHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class GearBEWLR extends BlockEntityWithoutLevelRenderer {

    public static final GearBEWLR INSTANCE = new GearBEWLR(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    private float tpr = 120; //TODO config ticks per rotation

    public GearBEWLR(BlockEntityRenderDispatcher pBlockEntityRenderDispatcher, EntityModelSet pEntityModelSet) {
        super(pBlockEntityRenderDispatcher, pEntityModelSet);
    }

    @Override
    public void renderByItem(ItemStack pStack, TransformType pTransformType, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pOverlay) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null) {
            return;
        }
        BakedModel model = mc.getModelManager().getModel(new ResourceLocation(EnderIO.MODID, "item/" + pStack.getItem().getRegistryName().getPath().toString() + "_helper"));
        pPoseStack.pushPose();
        if (tpr != 0) {
            PoseStackHelper.rotateAroundPivot(pPoseStack, new Vector3f(0.5F, 0.5F, 0F), Vector3f.ZP, (360.0F / tpr) * (mc.player.clientLevel.getGameTime() % tpr), true); // rotates the item 360/tpr degrees each tick
        }
        mc.getItemRenderer().renderModelLists(model, pStack, pPackedLight, pOverlay, pPoseStack, pBuffer.getBuffer(RenderType.cutout()));
        pPoseStack.popPose();
    }
}
