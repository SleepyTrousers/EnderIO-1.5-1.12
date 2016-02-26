package crazypants.enderio.machine.obelisk;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Timer;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.RenderUtil;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import static org.lwjgl.opengl.GL11.*;

@SideOnly(Side.CLIENT)
public class ObeliskSpecialRenderer<T extends TileEntity> extends TileEntitySpecialRenderer implements IItemRenderer {

  private ItemStack floatingStack;

  private Random rand = new Random();
  private ObeliskRenderer blockRen;

  public ObeliskSpecialRenderer(ItemStack itemStack, ObeliskRenderer renderer) {
    this.floatingStack = itemStack;
    this.blockRen = renderer;
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

    blockRen.renderInventoryBlock(Block.getBlockFromItem(item.getItem()), item.getItemDamage(), 0, (RenderBlocks) data[0]);
    Timer t = RenderUtil.getTimer();
    renderItemStack(null, Minecraft.getMinecraft().theWorld, 0, 0, 0, t == null ? 0 : t.renderPartialTicks);
    GL11.glPopMatrix();
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

  protected void renderItemStack(T te, World world, double x, double y, double z, float tick) {
    if(ei == null) {
      ei = new EntityItem(world, 0, 0, 0, getFloatingItem(te));
    }

    ei.setEntityItemStack(getFloatingItem(te));
    ei.hoverStart = (float) ((EnderIO.proxy.getTickCount() * 0.05f + (tick * 0.05f)) % (Math.PI * 2));
    ei.age = 0;

    glPushMatrix();
    glPushAttrib(GL_ALL_ATTRIB_BITS);
    GL11.glEnable(GL11.GL_BLEND);
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    GL11.glEnable(GL11.GL_ALPHA_TEST);
    RenderHelper.enableStandardItemLighting();
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
    glPopAttrib();
    glPopMatrix();
  }

  /**
   * @param te CAN BE NULL
   */
  protected ItemStack getFloatingItem(T te) {
    return floatingStack;
  }


}
