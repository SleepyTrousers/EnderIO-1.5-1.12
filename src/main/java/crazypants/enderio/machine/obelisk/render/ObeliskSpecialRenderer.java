package crazypants.enderio.machine.obelisk.render;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.RenderUtil;

import static org.lwjgl.opengl.GL11.GL_ALL_ATTRIB_BITS;
import static org.lwjgl.opengl.GL11.glDepthMask;
import static org.lwjgl.opengl.GL11.glPopAttrib;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushAttrib;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTranslated;

import crazypants.enderio.EnderIO;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ObeliskSpecialRenderer<T extends TileEntity> extends TileEntitySpecialRenderer<T> {

  private ItemStack floatingStack;

  private Random rand = new Random();
  
  private RenderEntityItem rei;
  
  private final Block block;

  public ObeliskSpecialRenderer(Block block, ItemStack itemStack) {
    this.floatingStack = itemStack;
    this.block = block;
  }

  private EntityItem ei = null;

  @SuppressWarnings("unchecked")
  @Override
  public void renderTileEntityAt(TileEntity te, double x, double y, double z, float tick, int b) {

    World world = null;
    if (te != null) {
      world = te.getWorld();
      RenderUtil.setupLightmapCoords(te.getPos(), te.getWorld());
    } else {
      world = Minecraft.getMinecraft().theWorld;
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
    }

    renderItemStack((T) te, world, x, y, z, tick);
  }

  private void renderItemStack(T te, World world, double x, double y, double z, float tick) {
    if (ei == null) {
      ei = new EntityItem(world, 0, 0, 0, getFloatingItem(te));
    }
    ei.setEntityItemStack(getFloatingItem(te));
    ei.hoverStart = (float) ((EnderIO.proxy.getTickCount() * 0.05f + (tick * 0.05f)) % (Math.PI * 2));

    RenderUtil.bindBlockTexture();

    glPushMatrix();
    glPushAttrib(GL_ALL_ATTRIB_BITS);
    GL11.glEnable(GL11.GL_BLEND);
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    GL11.glEnable(GL11.GL_ALPHA_TEST);
    RenderHelper.enableStandardItemLighting();
    glTranslated(x + 0.5, y + 0.7, z + 0.5);
    glScalef(1.1f, 1.1f, 1.1f);
    glDepthMask(true);

    BlockPos p;
    if (te != null) {
      p = te.getPos();
    } else {
      p = new BlockPos(0, 0, 0);
    }

    rand.setSeed(p.getX() + p.getY() + p.getZ());
    rand.nextBoolean();
    if (Minecraft.getMinecraft().gameSettings.fancyGraphics) {
      GlStateManager.rotate(rand.nextFloat() * 360f, 0, 1, 0);
    }
    ei.hoverStart += rand.nextFloat();

    GlStateManager.translate(0, -0.15f, 0);
    if(rei == null) {
      rei = new InnerRenderEntityItem(Minecraft.getMinecraft().getRenderManager(), Minecraft.getMinecraft().getRenderItem());
    }
    rei.doRender(ei, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);

    glPopAttrib();
    glPopMatrix();

    if (te == null) {
      // Item
      GlStateManager.pushMatrix();
      GlStateManager.translate(0.5f, 0.5f, 0.5f);
      GlStateManager.scale(2, 2, 2);
      GlStateManager.color(1, 1, 1);
      GlStateManager.disableLighting();
      RenderUtil.renderBlockModelAsItem(world, new ItemStack(block), block.getDefaultState());
      RenderHelper.enableStandardItemLighting();
      GlStateManager.popMatrix();
    }
  }

  /**
   * @param te
   *          CAN BE NULL
   */
  protected ItemStack getFloatingItem(T te) {
    return floatingStack;
  }

  private static class InnerRenderEntityItem extends RenderEntityItem {

    public InnerRenderEntityItem(RenderManager renderManagerIn, RenderItem renderItem) {
      super(renderManagerIn, renderItem);      
    }
    
    @Override
    public boolean shouldBob() {
      return false;
    }
  }

}
