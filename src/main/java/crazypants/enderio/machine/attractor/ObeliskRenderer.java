package crazypants.enderio.machine.attractor;

import static org.lwjgl.opengl.GL11.glDepthMask;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTranslated;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.Timer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.render.BoundingBox;
import crazypants.render.CubeRenderer;
import crazypants.render.RenderUtil;
import crazypants.render.VertexTransform;
import crazypants.vecmath.Vector3d;
import crazypants.vecmath.Vector3f;
import crazypants.vecmath.Vertex;

@SideOnly(Side.CLIENT)
public class ObeliskRenderer<T extends TileEntity> extends TileEntitySpecialRenderer implements ISimpleBlockRenderingHandler, IItemRenderer {

  private VertXForm xform = new VertXForm();
  private VertXForm2 xform2 = new VertXForm2();
  private ItemStack floatingStack;

  private Random rand = new Random();

  public ObeliskRenderer(ItemStack itemStack) {
    this.floatingStack = itemStack;
  }

  @Override
  public boolean handleRenderType(ItemStack item, ItemRenderType type) {
    return true;
  }

  @Override
  public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
    return helper == ItemRendererHelper.ENTITY_BOBBING || helper == ItemRendererHelper.ENTITY_ROTATION;
  }

  @Override
  public void renderItem(ItemRenderType type, ItemStack item, Object... data) {

    GL11.glPushMatrix();
    switch(type) {
    case ENTITY:
      GL11.glTranslatef(0, 0.25f, 0);
      break;
    case EQUIPPED:
      GL11.glRotatef(22, 0, 0, 1);
      GL11.glRotatef(-10, 1, 0, 0);
      GL11.glTranslatef(0.3f, -0.4f, -0.4f);
      break;
    case EQUIPPED_FIRST_PERSON:
      GL11.glTranslatef(0.75f, 0, 0);
      break;
    case INVENTORY:
      GL11.glTranslatef(8, 9, 0);
      GL11.glScalef(12F, 12F, 12F);
      GL11.glScalef(1.0F, 1.0F, -1F);
      GL11.glRotatef(210F, 1, 0, 0);
      GL11.glRotatef(-45F, 0, 1, 0);
      break;
    default:
      break;
    }

    renderInventoryBlock(EnderIO.blockAttractor, item.getItemDamage(), 0, (RenderBlocks) data[0]);
    Timer t = RenderUtil.getTimer();
    renderItemStack(null, Minecraft.getMinecraft().theWorld, 0, 0, 0, t.renderPartialTicks);
    GL11.glPopMatrix();
    GL11.glEnable(GL11.GL_LIGHTING);
  }

  private EntityItem ei = null;

  @SuppressWarnings("unchecked")
  @Override
  public void renderTileEntityAt(TileEntity te, double x, double y, double z, float tick) {

    World world = te.getWorldObj();
    float f = world.getBlockLightValue(te.xCoord, te.yCoord, te.zCoord);
    int l = world.getLightBrightnessForSkyBlocks(te.xCoord, te.yCoord, te.zCoord, 0);
    int l1 = l % 65536;
    int l2 = l / 65536;
    Tessellator.instance.setColorOpaque_F(f, f, f);
    OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, l1, l2);

    renderItemStack((T) te, world, x, y, z, tick);
  }

  private void renderItemStack(T te, World world, double x, double y, double z, float tick) {
    if(ei == null) {
      ei = new EntityItem(world, 0, 0, 0, getFloatingItem(te));
    }

    ei.setEntityItemStack(getFloatingItem(te));
    ei.hoverStart = world.getTotalWorldTime() * 0.05f + (tick * 0.05f);
    ei.age = 0;

    glPushMatrix();
    glTranslated(x + 0.5, y + 0.7, z + 0.5);
    glScalef(1.1f, 1.1f, 1.1f);
    glDepthMask(true);

    if(te != null) {
      rand.setSeed(te.xCoord + te.yCoord + te.zCoord);
      rand.nextBoolean();
      if(Minecraft.getMinecraft().gameSettings.fancyGraphics) {
        glRotatef(rand.nextFloat() * 360f, 0, 1, 0);
      }
      ei.hoverStart += rand.nextFloat();
    }

    RenderManager.instance.renderEntityWithPosYaw(ei, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
    glPopMatrix();
  }

  /**
   * @param te CAN BE NULL
   */
  protected ItemStack getFloatingItem(T te) {
    return floatingStack;
  }

  @Override
  public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {

    GL11.glDisable(GL11.GL_LIGHTING);
    Tessellator.instance.startDrawingQuads();
    renderWorldBlock(null, 0, 0, 0, block, 0, renderer);
    Tessellator.instance.draw();
    GL11.glEnable(GL11.GL_LIGHTING);

  }

  @Override
  public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {

    BoundingBox bb = BoundingBox.UNIT_CUBE;

    Tessellator.instance.addTranslation(x, y, z);

    IIcon icon = EnderIO.blockAttractor.getOnIcon();
    if(world != null) {
      icon = block.getIcon(world, x, y, z, 0);
      Tessellator.instance.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));
    }

    if(renderer.hasOverrideBlockTexture()) {
      icon = renderer.overrideBlockTexture;
    }

    float height = 0.475f;
    float width = 0.5f;
    bb = BoundingBox.UNIT_CUBE.scale(width, height, 1).translate(0, -0.5f + height / 2, 0);
    xform2.isX = false;
    CubeRenderer.render(bb, icon, xform2, true);

    bb = BoundingBox.UNIT_CUBE.scale(1, height, width).translate(0, -0.5f + height / 2, 0);
    xform2.isX = true;
    CubeRenderer.render(bb, icon, xform2, true);

    Tessellator.instance.addTranslation(-x, -y, -z);

    return true;
  }

  @Override
  public boolean shouldRender3DInInventory(int modelId) {
    return true;
  }

  @Override
  public int getRenderId() {
    return BlockAttractor.renderId;
  }

  private static class VertXForm implements VertexTransform {

    public VertXForm() {
    }

    @Override
    public void apply(Vertex vertex) {
      apply(vertex.xyz);
    }

    @Override
    public void apply(Vector3d vec) {
      double pinch = 0.8;
      if(vec.y > 0.5) {
        pinch = 0.4;
      }
      vec.x -= 0.5;
      vec.x *= pinch;
      vec.x += 0.5;
      vec.z -= 0.5;
      vec.z *= pinch;
      vec.z += 0.5;

      double scale = 0.5;
      vec.y -= 0.5;
      vec.y *= scale;
      vec.y += (0.5 * scale);
    }

    @Override
    public void applyToNormal(Vector3f vec) {
    }

  }

  private static class VertXForm2 implements VertexTransform {

    boolean isX = true;

    public VertXForm2() {
    }

    @Override
    public void apply(Vertex vertex) {
      apply(vertex.xyz);
    }

    @Override
    public void apply(Vector3d vec) {
      double pinch = 0.9;
      if(vec.y > 0.2) {
        pinch = 0.5;
      }
      if(isX) {
        vec.x -= 0.5;
        vec.x *= pinch;
        vec.x += 0.5;
      } else {
        vec.z -= 0.5;
        vec.z *= pinch;
        vec.z += 0.5;
      }
    }

    @Override
    public void applyToNormal(Vector3f vec) {
    }

  }

}
