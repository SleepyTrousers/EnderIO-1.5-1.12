package crazypants.enderio.conduit.item;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.conduit.ConnectionMode;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.conduit.geom.ConnectionModeGeometry;
import crazypants.enderio.conduit.geom.Offset;
import crazypants.enderio.conduit.render.ConduitBundleRenderer;
import crazypants.enderio.conduit.render.DefaultConduitRenderer;

public class ItemConduitRenderer extends DefaultConduitRenderer {

    @Override
    public boolean isRendererForConduit(IConduit conduit) {
        if (conduit instanceof IItemConduit) {
            return true;
        }
        return false;
    }

    @Override
    public void renderEntity(ConduitBundleRenderer conduitBundleRenderer, IConduitBundle te, IConduit conduit, double x,
            double y, double z, float partialTick, float worldLight, RenderBlocks rb) {
        super.renderEntity(conduitBundleRenderer, te, conduit, x, y, z, partialTick, worldLight, rb);

        IItemConduit pc = (IItemConduit) conduit;
        for (ForgeDirection dir : conduit.getExternalConnections()) {
            DyeColor inChannel = null;
            DyeColor outChannel = null;
            IIcon inTex = null;
            IIcon outTex = null;
            boolean render = true;
            if (conduit.getConnectionMode(dir) == ConnectionMode.INPUT) {
                inTex = pc.getTextureForInputMode();
                inChannel = pc.getInputColor(dir);
            } else if (conduit.getConnectionMode(dir) == ConnectionMode.OUTPUT) {
                outTex = pc.getTextureForOutputMode();
                outChannel = pc.getOutputColor(dir);
            } else if (conduit.getConnectionMode(dir) == ConnectionMode.IN_OUT) {
                inTex = pc.getTextureForInOutMode(true);
                outTex = pc.getTextureForInOutMode(false);
                inChannel = pc.getInputColor(dir);
                outChannel = pc.getOutputColor(dir);
            } else {
                render = false;
            }

            if (render && !rb.hasOverrideBlockTexture()) {
                Offset offset = te.getOffset(IItemConduit.class, dir);
                ConnectionModeGeometry.renderModeConnector(dir, offset, pc.getTextureForInOutBackground(), true);

                if (inChannel != null) {
                    Tessellator.instance.setColorOpaque_I(inChannel.getColor());
                    ConnectionModeGeometry.renderModeConnector(dir, offset, inTex, false);
                }
                if (outChannel != null) {
                    Tessellator.instance.setColorOpaque_I(outChannel.getColor());
                    ConnectionModeGeometry.renderModeConnector(dir, offset, outTex, false);
                }

                Tessellator.instance.setColorOpaque_F(1f, 1f, 1f);
            }
        }
    }
}
