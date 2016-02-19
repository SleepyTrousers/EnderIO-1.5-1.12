package crazypants.enderio.conduit.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.util.BlockCoord;

import crazypants.enderio.EnderIO;
import crazypants.enderio.conduit.ConduitUtil;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.conduit.TileConduitBundle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ConduitBundleRenderer extends TileEntitySpecialRenderer<TileConduitBundle>  {

  public ConduitBundleRenderer(float conduitScale) {
  }

  @Override
  public void renderTileEntityAt(TileConduitBundle te, double x, double y, double z, float partialTick, int b) {
    IConduitBundle bundle = te;
    EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
    if(bundle.hasFacade() && bundle.getFacadeId().isOpaqueCube() && !ConduitUtil.isFacadeHidden(bundle, player)) {
      return;
    }

    float brightness = -1;
    for (IConduit con : bundle.getConduits()) {
      if(ConduitUtil.renderConduit(player, con)) {
        ConduitRenderer renderer = EnderIO.proxy.getRendererForConduit(con);
        if(renderer.isDynamic()) {
          if(brightness == -1) {
            BlockCoord loc = bundle.getLocation();
            brightness = bundle.getEntity().getWorld().getLightFor(EnumSkyBlock.SKY, loc.getBlockPos());

            RenderUtil.bindBlockTexture();

            GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
            GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glShadeModel(GL11.GL_SMOOTH);

            GL11.glPushMatrix();
            GL11.glTranslated(x, y, z);

            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer tes = tessellator.getWorldRenderer();
            tes.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
          }
          renderer.renderDynamicEntity(this, bundle, con, x, y, z, partialTick, brightness);

        }
      }
    }

    if(brightness != -1) {
      Tessellator.getInstance().draw();

      GL11.glShadeModel(GL11.GL_FLAT);
      GL11.glPopMatrix();
      GL11.glPopAttrib();
      GL11.glPopAttrib();
    }

  }

//  public static class FacadeAccessWrapper extends IBlockAccessWrapper {
//
//    public FacadeAccessWrapper(IBlockAccess ba) {
//      super(ba);
//    }
//
//    @Override
//    public Block getBlock(int x, int y, int z) {
//      Block res = super.getBlock(x, y, z);
//      if(res == EnderIO.blockConduitBundle) {
//        TileEntity te = getTileEntity(x, y, z);
//        if(te instanceof TileConduitBundle) {
//          TileConduitBundle tcb = (TileConduitBundle) te;
//          Block fac = tcb.getFacadeId();
//          if(fac != null) {
//            res = fac;
//          }
//        }
//      }
//      return res;
//    }
//
//    @Override
//    @SideOnly(Side.CLIENT)
//    public int getLightBrightnessForSkyBlocks(int var1, int var2, int var3, int var4) {
//      return wrapped.getLightBrightnessForSkyBlocks(var1, var2, var3, var4);
//    }
//
//    @Override
//    public int getBlockMetadata(int x, int y, int z) {
//      Block block = super.getBlock(x, y, z);
//      if(block == EnderIO.blockConduitBundle) {
//        TileEntity te = getTileEntity(x, y, z);
//        if(te instanceof TileConduitBundle) {
//          TileConduitBundle tcb = (TileConduitBundle) te;
//          Block fac = tcb.getFacadeId();
//          if(fac != null) {
//            return tcb.getFacadeMetadata();
//          }
//        }
//      }
//      return super.getBlockMetadata(x, y, z);
//    }
//
//  }

}
