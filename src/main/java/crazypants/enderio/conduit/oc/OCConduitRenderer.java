package crazypants.enderio.conduit.oc;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;

import com.enderio.core.client.render.CubeRenderer;

import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.geom.CollidableComponent;
import crazypants.enderio.conduit.render.DefaultConduitRenderer;

public class OCConduitRenderer extends DefaultConduitRenderer {

    @Override
    public boolean isRendererForConduit(IConduit conduit) {
        return conduit instanceof IOCConduit;
    }

    @Override
    protected void renderConduit(IIcon tex, IConduit conduit, CollidableComponent component, float selfIllum) {
        if (IOCConduit.COLOR_CONTROLLER_ID.equals(component.data)) {
            if (conduit.containsExternalConnection(component.dir)) {
                int c = ((IOCConduit) conduit).getSignalColor(component.dir).getColor();
                Tessellator tessellator = Tessellator.instance;
                tessellator.setColorOpaque_I(c);
                CubeRenderer.render(component.bound, tex);
                tessellator.setColorOpaque(255, 255, 255);
            }
        } else {
            super.renderConduit(tex, conduit, component, selfIllum);
        }
    }
}
