package crazypants.enderio.machine.farm;

import static org.lwjgl.opengl.GL11.*;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.vecmath.Vector3f;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.config.Config;

@SideOnly(Side.CLIENT)
public class FarmingStationSpecialRenderer extends TileEntitySpecialRenderer {

    private void renderFarmStationAt(TileFarmStation tile, double x, double y, double z, float partialTickTime) {

        String toRender = tile.notification;
        if ("".equals(toRender) || Config.disableFarmNotification) {
            return;
        }

        glDisable(GL_LIGHTING);
        RenderUtil.drawBillboardedText(new Vector3f(x + 0.5, y + 1.5, z + 0.5), toRender, 0.25f);
        glEnable(GL_LIGHTING);
    }

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTickTime) {
        if (tile instanceof TileFarmStation) {
            renderFarmStationAt((TileFarmStation) tile, x, y, z, partialTickTime);
        }
    }
}
