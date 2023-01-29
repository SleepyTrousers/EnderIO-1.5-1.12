package crazypants.enderio.teleport.telepad;

import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.obj.GroupObject;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.CubeRenderer;
import com.enderio.core.client.render.IconUtil;
import com.enderio.core.client.render.TechneUtil;
import com.google.common.collect.Lists;

import crazypants.enderio.EnderIO;
import crazypants.enderio.teleport.TravelController;
import crazypants.enderio.teleport.anchor.TravelEntitySpecialRenderer;

public class TelePadSpecialRenderer extends TravelEntitySpecialRenderer {

    private final Map<String, GroupObject> fullModel;

    private final GroupObject blade1;
    private final GroupObject blade2;
    private final GroupObject blade3;
    // Reverse order for top to bottom
    private final List<GroupObject> blades;

    private final GroupObject glass;

    private static Random rand = new Random();

    public TelePadSpecialRenderer(TelePadRenderer telePadRenderer) {
        super();
        fullModel = telePadRenderer.getFullModel();
        blade1 = fullModel.get("blade1");
        blade2 = fullModel.get("blade2");
        blade3 = fullModel.get("blade3");
        blades = Lists.newArrayList(blade3, blade2, blade1);

        glass = fullModel.get("glass");
    }

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float p_147500_8_) {
        TileTelePad tp = (TileTelePad) te;
        if (tp.isTravelSource()) {
            TravelController.instance.addCandidate(tp.getLocation());
            {
                GL11.glPushMatrix();
                GL11.glTranslated(x + 0.5, y + 0.01, z + 0.5);
                Tessellator tes = Tessellator.instance;
                tes.setBrightness(0xF000F0);
                GL11.glDepthMask(true);
                rand.setSeed(te.xCoord + te.yCoord + te.zCoord);
                for (int i = 0; i < 3; i++) {
                    GL11.glPushMatrix();
                    GL11.glRotatef(
                            tp.bladeRots[i] + rand.nextInt(360) + (tp.spinSpeed * p_147500_8_ * (i + 20)),
                            0,
                            1,
                            0);
                    render(blades.get(i), te, tes);
                    GL11.glPopMatrix();
                }
                GL11.glEnable(GL11.GL_BLEND);
                render(glass, te, tes);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glPopMatrix();
            }
        }

        super.renderTileEntityAt(te, x, y, z, p_147500_8_);
    }

    @Override
    protected void renderBlock(IBlockAccess world, double sf) {
        Tessellator.instance.setColorRGBA_F(1, 1, 1, 0.75f);
        CubeRenderer.render(BoundingBox.UNIT_CUBE.scale(sf, sf, sf), EnderIO.blockTelePad.getHighlightIcon());
    }

    @Override
    public IIcon getSelectedIcon() {
        return IconUtil.whiteTexture;
    }

    @Override
    public IIcon getHighlightIcon() {
        return IconUtil.whiteTexture;
    }

    private void render(GroupObject model, TileEntity te, Tessellator tes) {
        final IIcon icon = te.getBlockType().getIcon(te.getWorldObj(), te.xCoord, te.yCoord, te.zCoord, 0);
        if (icon != null && model != null) {
            tes.startDrawingQuads();
            TechneUtil.renderWithIcon(model, icon, null, Tessellator.instance, te.getWorldObj(), 0, 0, 0, null, false);
            tes.draw();
        }
    }
}
