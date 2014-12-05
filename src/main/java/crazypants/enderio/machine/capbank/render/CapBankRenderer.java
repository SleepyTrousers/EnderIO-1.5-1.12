package crazypants.enderio.machine.capbank.render;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.capbank.BlockCapBank;
import crazypants.enderio.machine.capbank.CapBankType;
import crazypants.enderio.machine.capbank.InfoDisplayType;
import crazypants.enderio.machine.capbank.TileCapBank;
import crazypants.enderio.machine.capbank.network.CapBankClientNetwork;
import crazypants.render.BoundingBox;
import crazypants.render.ConnectedTextureRenderer;
import crazypants.render.CubeRenderer;
import crazypants.render.CustomCubeRenderer;
import crazypants.render.RenderUtil;
import crazypants.util.ForgeDirectionOffsets;
import crazypants.vecmath.Vector3d;

public class CapBankRenderer extends TileEntitySpecialRenderer implements ISimpleBlockRenderingHandler, IItemRenderer {

  private ConnectedTextureRenderer connectedTexRenderer;
  private FillGauge fillGauge;

  public CapBankRenderer() {
    connectedTexRenderer = new ConnectedTextureRenderer();
    connectedTexRenderer.setMatchMeta(true);
    fillGauge = new FillGauge();
  }

  //------- Block

  @Override
  public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
  }

  @Override
  public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {

    int meta = world.getBlockMetadata(x, y, z);
    meta = MathHelper.clamp_int(meta, 0, CapBankType.types().size() - 1);
    CapBankType type = CapBankType.getTypeFromMeta(meta);
    if(!type.isMultiblock()) {
      connectedTexRenderer.setForceAllEdges(true);
    } else {
      connectedTexRenderer.setForceAllEdges(false);
    }
    connectedTexRenderer.setEdgeTexture(EnderIO.blockCapBank.getBorderIcon(0, meta));
    CustomCubeRenderer.instance.renderBlock(world, block, x, y, z, connectedTexRenderer);
    return true;
  }

  @Override
  public boolean shouldRender3DInInventory(int modelId) {
    return true;
  }

  @Override
  public int getRenderId() {
    return BlockCapBank.renderId;
  }

  //------- Item 
  @Override
  public boolean handleRenderType(ItemStack item, ItemRenderType type) {
    return true;
  }

  @Override
  public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
    return true;
  }

  @Override
  public void renderItem(ItemRenderType type, ItemStack item, Object... data) {

    RenderUtil.bindBlockTexture();
    Tessellator tes = Tessellator.instance;

    tes.startDrawingQuads();
    CubeRenderer.render(EnderIO.blockCapBank, item.getItemDamage());
    tes.draw();

    GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
    GL11.glPolygonOffset(-1.0f, -1.0f);

    tes.startDrawingQuads();
    tes.setColorRGBA_F(1, 1, 1, 1);
    renderBorder(null, 0, 0, 0, item.getItemDamage());
    tes.draw();

    GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);

  }

  private void renderBorder(IBlockAccess blockAccess, int x, int y, int z, int meta) {
    IIcon texture = EnderIO.blockCapBank.getBorderIcon(0, meta);
    for (ForgeDirection face : ForgeDirection.VALID_DIRECTIONS) {
      RenderUtil.renderConnectedTextureFace(blockAccess, x, y, z, face, texture,
          blockAccess == null, false, false);
    }
  }

  //---- Info Display

  @Override
  public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTick) {

    TileCapBank cb = (TileCapBank) te;

    float f = cb.getWorldObj().getBlockLightValue(te.xCoord, te.yCoord, te.zCoord);
    int l = cb.getWorldObj().getLightBrightnessForSkyBlocks(te.xCoord, te.yCoord, te.zCoord, 0);
    int l1 = l % 65536;
    int l2 = l / 65536;
    Tessellator.instance.setColorOpaque_F(f, f, f);
    OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, l1, l2);

    GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
    GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
    GL11.glPolygonOffset(-1.0f, -1.0f);
    GL11.glDisable(GL11.GL_LIGHTING);

    //    GL11.glEnable(GL11.GL_BLEND);
    //    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

    GL11.glPushMatrix();
    GL11.glTranslatef((float) x, (float) y, (float) z);

    for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
      InfoDisplayType type = cb.getDisplayType(dir);
      if(type != InfoDisplayType.NONE) {
        renderInfoDisplay(cb, dir, type, x, y, z, partialTick);
      }
    }

    GL11.glPopMatrix();
    GL11.glPopAttrib();
    //GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);

  }

  private void renderInfoDisplay(TileCapBank cb, ForgeDirection dir, InfoDisplayType type, double x, double y, double z, float partialTick) {
    CapBankClientNetwork nw = (CapBankClientNetwork) cb.getNetwork();
    if(type == InfoDisplayType.LEVEL_BAR && nw != null) {
      nw.requestPowerUpdate(cb, 20);
    }

    RenderUtil.bindBlockTexture();

    int brightness = cb.getWorldObj().getLightBrightnessForSkyBlocks(cb.xCoord + dir.offsetX, cb.yCoord + dir.offsetY, cb.zCoord + dir.offsetZ, 0);

    Tessellator tes = Tessellator.instance;
    tes.startDrawingQuads();
    tes.setBrightness(brightness);
    tes.setColorOpaque_F(1, 1, 1);

    if(type == InfoDisplayType.LEVEL_BAR) {
      fillGauge.render(cb, nw, dir);
    }

    tes.draw();


  }

  protected BoundingBox getBoundForFace(ForgeDirection dir) {

    double widthScale = 0.25;
    double heightScale = 0.8;
    ;
    boolean isUp = dir.offsetY != 0;

    double xScale = dir.offsetX == 0 ? widthScale : 1;
    double yScale = isUp ? widthScale : heightScale;
    double zScale = isUp ? heightScale : (dir.offsetZ == 0 ? widthScale : 1);

    BoundingBox bb = BoundingBox.UNIT_CUBE;
    Vector3d off = ForgeDirectionOffsets.forDirCopy(dir);
    off.scale(-1);
    bb = bb.translate(off);
    bb = bb.scale(xScale, yScale, zScale);
    off.scale(-1);
    bb = bb.translate(off);
    return bb;
  }

  //  private int getEnergyStoredScaled(CapBankClientNetwork network, int scale) {
  //    return (int) VecmathUtil.clamp(Math.round(scale * network.getEnergyStoredRatio()), 0, scale);
  //  }


}
