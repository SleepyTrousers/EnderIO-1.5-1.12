package crazypants.enderio.machine.enchanter;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.RenderUtil;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EnchanterModelRenderer extends TileEntitySpecialRenderer<TileEnchanter> {

  private static final String TEXTURE = "enderio:models/BookStand.png";

  private EnchanterModel model = new EnchanterModel();

  @Override
  public void renderTileEntityAt(TileEnchanter te, double x, double y, double z, float tick, int b) {

    World world = te.getWorld();

    float f = world.getLightBrightness(te.getPos());    
    int l = world.getLightFor(EnumSkyBlock.SKY, te.getPos());
    int l1 = l % 65536;
    int l2 = l / 65536;
    GlStateManager.color(f, f, f);    
    OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, l1, l2);
    GL11.glPushMatrix();
    GL11.glTranslatef((float) x, (float) y, (float) z);
    renderModel(te.getFacing());
    GL11.glPopMatrix();
  }

  private void renderModel(EnumFacing facing) {

    GL11.glPushMatrix();

    GL11.glTranslatef(0.5F, 1.5f, 0.5F);
    GL11.glRotatef(180F, 1F, 0F, 0F);
//    GL11.glScalef(1.2f, 0.9f, 1.2f);

    GL11.glRotatef(facing.getHorizontalIndex() * -90F, 0F, 1F, 0F);

    RenderUtil.bindTexture(TEXTURE);
    model.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);

    GL11.glTranslatef(-0.5F, -1.5f, -0.5F);
    GL11.glPopMatrix();

  }

}
