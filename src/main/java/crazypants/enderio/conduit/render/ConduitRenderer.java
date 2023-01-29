package crazypants.enderio.conduit.render;

import net.minecraft.client.renderer.RenderBlocks;

import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IConduitBundle;

public interface ConduitRenderer {

    boolean isRendererForConduit(IConduit conduit);

    void renderEntity(ConduitBundleRenderer conduitBundleRenderer, IConduitBundle te, IConduit con, double x, double y,
            double z, float partialTick, float worldLight, RenderBlocks rb);

    boolean isDynamic();

    void renderDynamicEntity(ConduitBundleRenderer conduitBundleRenderer, IConduitBundle te, IConduit con, double x,
            double y, double z, float partialTick, float worldLight);
}
