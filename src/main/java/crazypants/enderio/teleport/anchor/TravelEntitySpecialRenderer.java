package crazypants.enderio.teleport.anchor;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.CubeRenderer;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.Util;
import com.enderio.core.common.vecmath.Vector3d;
import com.enderio.core.common.vecmath.Vector3f;
import com.enderio.core.common.vecmath.Vector4f;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.api.teleport.ITravelAccessable;
import crazypants.enderio.api.teleport.TravelSource;
import crazypants.enderio.teleport.TravelController;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;

@SideOnly(Side.CLIENT)
public class TravelEntitySpecialRenderer extends TileEntitySpecialRenderer {

    private final Vector4f selectedColor;
    private final Vector4f highlightColor;

    public TravelEntitySpecialRenderer() {
        this(new Vector4f(1, 0.25f, 0, 0.5f), new Vector4f(1, 1, 1, 0.25f));
    }

    public TravelEntitySpecialRenderer(Vector4f selectedColor, Vector4f highlightColor) {
        this.selectedColor = selectedColor;
        this.highlightColor = highlightColor;
    }

    @Override
    public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f) {

        if (!TravelController.instance.showTargets()) {
            return;
        }

        ITravelAccessable ta = (ITravelAccessable) tileentity;

        if (!ta.isVisible()) {
            return;
        }

        BlockCoord onBlock = TravelController.instance.onBlockCoord;
        if (onBlock != null && onBlock.equals(ta.getLocation())) {
            return;
        }
        if (!ta.canSeeBlock(Minecraft.getMinecraft().thePlayer)) {
            return;
        }

        Vector3d eye = Util.getEyePositionEio(Minecraft.getMinecraft().thePlayer);
        Vector3d loc = new Vector3d(tileentity.xCoord + 0.5, tileentity.yCoord + 0.5, tileentity.zCoord + 0.5);
        double maxDistance = TravelSource.BLOCK.getMaxDistanceTravelledSq();
        TravelSource source = TravelController.instance.getTravelItemTravelSource(Minecraft.getMinecraft().thePlayer);
        if (source != null) {
            maxDistance = source.getMaxDistanceTravelledSq();
        }
        if (eye.distanceSquared(loc) > maxDistance) {
            return;
        }

        double sf = TravelController.instance.getScaleForCandidate(loc);

        BlockCoord bc = new BlockCoord(tileentity);
        TravelController.instance.addCandidate(bc);

        Minecraft.getMinecraft().entityRenderer.disableLightmap(0);

        RenderUtil.bindBlockTexture();
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_LIGHTING_BIT);

        GL11.glEnable(GL12.GL_RESCALE_NORMAL);

        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LIGHTING);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glColor3f(1, 1, 1);

        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);

        Tessellator.instance.startDrawingQuads();
        renderBlock(tileentity.getWorldObj(), sf);
        Tessellator.instance.draw();

        Tessellator.instance.startDrawingQuads();
        Tessellator.instance.setBrightness(15 << 20 | 15 << 4);
        if (TravelController.instance.isBlockSelected(bc)) {
            Tessellator.instance.setColorRGBA_F(selectedColor.x, selectedColor.y, selectedColor.z, selectedColor.w);
            CubeRenderer.render(BoundingBox.UNIT_CUBE.scale(sf + 0.05, sf + 0.05, sf + 0.05), getSelectedIcon());
        } else {
            Tessellator.instance.setColorRGBA_F(highlightColor.x, highlightColor.y, highlightColor.z, highlightColor.w);
            CubeRenderer.render(BoundingBox.UNIT_CUBE.scale(sf + 0.05, sf + 0.05, sf + 0.05), getHighlightIcon());
        }
        Tessellator.instance.draw();
        GL11.glPopMatrix();

        renderLabel(tileentity, x, y, z, ta, sf);

        GL11.glPopAttrib();

        Minecraft.getMinecraft().entityRenderer.enableLightmap(0);
    }

    private EntityItem ei;

    private void renderLabel(TileEntity tileentity, double x, double y, double z, ITravelAccessable ta, double sf) {
        float globalScale = (float) sf;
        ItemStack itemLabel = ta.getItemLabel();
        if (itemLabel != null && itemLabel.getItem() != null) {

            boolean isBlock = itemLabel.getItem() instanceof ItemBlock;

            float alpha = 0.5f;
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_CONSTANT_COLOR);
            float col = 0.5f;
            GL14.glBlendColor(col, col, col, col);
            GL11.glColor4f(1, 1, 1, 1);
            {
                GL11.glPushMatrix();
                GL11.glTranslatef((float) x + 0.5f, (float) y + 0.5f, (float) z + 0.5f);
                if (!isBlock && Minecraft.getMinecraft().gameSettings.fancyGraphics) {
                    RenderUtil.rotateToPlayer();
                }

                {
                    GL11.glPushMatrix();
                    GL11.glScalef(globalScale, globalScale, globalScale);

                    {
                        GL11.glPushMatrix();
                        if (isBlock) {
                            GL11.glTranslatef(0f, -0.25f, 0);
                        } else {
                            GL11.glTranslatef(0f, -0.5f, 0);
                        }

                        GL11.glScalef(2, 2, 2);

                        if (ei == null) {
                            ei = new EntityItem(tileentity.getWorldObj(), x, y, z, itemLabel);
                        } else {
                            ei.setEntityItemStack(itemLabel);
                        }
                        RenderUtil.render3DItem(ei, false);
                        GL11.glPopMatrix();
                    }
                    GL11.glPopMatrix();
                }
                GL11.glPopMatrix();
            }
        }

        String toRender = ta.getLabel();
        if (toRender != null && toRender.trim().length() > 0) {
            GL11.glColor4f(1, 1, 1, 1);
            Vector4f bgCol = RenderUtil.DEFAULT_TEXT_BG_COL;
            if (TravelController.instance.isBlockSelected(new BlockCoord(tileentity))) {
                bgCol = new Vector4f(selectedColor.x, selectedColor.y, selectedColor.z, selectedColor.w);
            }

            {
                GL11.glPushMatrix();
                GL11.glTranslatef((float) x + 0.5f, (float) y + 0.5f, (float) z + 0.5f);
                {
                    GL11.glPushMatrix();
                    GL11.glScalef(globalScale, globalScale, globalScale);
                    Vector3f pos = new Vector3f(0, 1.2f, 0);
                    float size = 0.5f;
                    RenderUtil.drawBillboardedText(pos, toRender, size, bgCol);
                    GL11.glPopMatrix();
                }
                GL11.glPopMatrix();
            }
        }
    }

    protected void renderBlock(IBlockAccess world, double sf) {
        Tessellator.instance.setColorRGBA_F(1, 1, 1, 0.75f);
        CubeRenderer.render(BoundingBox.UNIT_CUBE.scale(sf, sf, sf), EnderIO.blockTravelPlatform.getIcon(0, 0));
    }

    public Vector4f getSelectedColor() {
        return selectedColor;
    }

    public IIcon getSelectedIcon() {
        return EnderIO.blockTravelPlatform.selectedOverlayIcon;
    }

    public Vector4f getHighlightColor() {
        return highlightColor;
    }

    public IIcon getHighlightIcon() {
        return EnderIO.blockTravelPlatform.highlightOverlayIcon;
    }
}
