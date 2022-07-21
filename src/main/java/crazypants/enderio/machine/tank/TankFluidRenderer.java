package crazypants.enderio.machine.tank;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.CubeRenderer;
import com.enderio.core.client.render.RenderUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.tool.SmartTank;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class TankFluidRenderer extends TileEntitySpecialRenderer {

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTick) {

        TileTank tank = (TileTank) te;
        if (tank.tank.getFluidAmount() <= 0) {
            return;
        }
        renderTankFluid(tank.tank, (float) x, (float) y, (float) z);
    }

    public static void renderTankFluid(SmartTank tank, float x, float y, float z) {
        if (tank == null || tank.getFluid() == null) {
            return;
        }
        IIcon icon = tank.getFluid().getFluid().getStillIcon();
        if (icon != null) {
            float fullness = tank.getFilledRatio();
            y += 0.01f; // prevent bottom side z-fighting
            float scale = 0.98f;
            float yScale = 0.98f * fullness;
            BoundingBox bb = BoundingBox.UNIT_CUBE.scale(scale, yScale, scale);
            bb = bb.translate(0, -(1 - yScale) / 2, 0);

            GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            RenderUtil.bindBlockTexture();

            Tessellator.instance.startDrawingQuads();
            Tessellator.instance.addTranslation(x, y, z);
            CubeRenderer.render(bb, icon);
            Tessellator.instance.addTranslation(-x, -y, -z);
            Tessellator.instance.draw();

            GL11.glPopAttrib();
        }
    }
}
