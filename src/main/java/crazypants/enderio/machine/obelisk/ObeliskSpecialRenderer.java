package crazypants.enderio.machine.obelisk;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.GL_ALL_ATTRIB_BITS;
import static org.lwjgl.opengl.GL11.glDepthMask;
import static org.lwjgl.opengl.GL11.glPopAttrib;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushAttrib;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTranslated;

import crazypants.enderio.EnderIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ObeliskSpecialRenderer<T extends TileEntity> extends TileEntitySpecialRenderer<T> {

  private ItemStack floatingStack;

  private Random rand = new Random();

  public ObeliskSpecialRenderer(ItemStack itemStack) {
    this.floatingStack = itemStack;
  }

  
  private EntityItem ei = null;

  @SuppressWarnings("unchecked")
  @Override
  public void renderTileEntityAt(TileEntity te, double x, double y, double z, float tick, int b) {

    World world = te.getWorld();
    float f = world.getLightBrightness(te.getPos());    
    int l = world.getLightFor(EnumSkyBlock.SKY, te.getPos());
    int l1 = l % 65536;
    int l2 = l / 65536;
    GlStateManager.color(f, f, f);    
    OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, l1, l2);

    renderItemStack((T) te, world, x, y, z, tick);
  }

  private void renderItemStack(T te, World world, double x, double y, double z, float tick) {
    if(ei == null) {
      ei = new EntityItem(world, 0, 0, 0, getFloatingItem(te));
    }

    ei.setEntityItemStack(getFloatingItem(te));
    ei.hoverStart = (float) ((EnderIO.proxy.getTickCount() * 0.05f + (tick * 0.05f)) % (Math.PI * 2));
    //TODO: 1.8
//    ei.age = 0;

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
      BlockPos p = te.getPos();
      rand.setSeed(p.getX() + p.getY() + p.getZ());
      rand.nextBoolean();
      if(Minecraft.getMinecraft().gameSettings.fancyGraphics) {
        glRotatef(rand.nextFloat() * 360f, 0, 1, 0);
      }
      ei.hoverStart += rand.nextFloat();
    }
    
    Minecraft.getMinecraft().getRenderManager().renderEntityWithPosYaw(ei, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
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
