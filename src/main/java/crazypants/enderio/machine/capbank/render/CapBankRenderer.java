package crazypants.enderio.machine.capbank.render;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
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
import crazypants.enderio.machine.capbank.render.FillGauge.GaugeInfo;
import crazypants.enderio.machine.capbank.render.FillGauge.GaugeKey;
import crazypants.enderio.power.PowerHandlerUtil;
import crazypants.render.ConnectedTextureRenderer;
import crazypants.render.CubeRenderer;
import crazypants.render.CustomCubeRenderer;
import crazypants.render.RenderUtil;

public class CapBankRenderer extends TileEntitySpecialRenderer implements ISimpleBlockRenderingHandler, IItemRenderer {

  private ConnectedTextureRenderer connectedTexRenderer;

  private Map<InfoDisplayType, IInfoRenderer> infoRenderers;
  private FillGauge fillGaugeRenderer;

  public CapBankRenderer() {
    connectedTexRenderer = new ConnectedTextureRenderer();
    connectedTexRenderer.setMatchMeta(true);
    fillGaugeRenderer = new FillGauge();
    infoRenderers = new HashMap<InfoDisplayType, IInfoRenderer>();
    infoRenderers.put(InfoDisplayType.LEVEL_BAR, fillGaugeRenderer);
    infoRenderers.put(InfoDisplayType.IO, new IoDisplay());
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


    CapBankClientNetwork nw = new CapBankClientNetwork(-1);
    nw.setMaxEnergyStoredL(CapBankType.getTypeFromMeta(item.getItemDamage()).getMaxEnergyStored());
    nw.setEnergyStored(PowerHandlerUtil.getStoredEnergyForItem(item));

    GaugeInfo gi = new GaugeInfo(1, 0);
    GaugeKey key = new GaugeKey(ForgeDirection.SOUTH, FillGauge.Type.SINGLE);
    fillGaugeRenderer.doRender(nw, RenderUtil.BRIGHTNESS_MAX, gi, key);
    //    key = new GaugeKey(ForgeDirection.EAST, FillGauge.Type.SINGLE);
    //    fillGaugeRenderer.doRender(nw, RenderUtil.BRIGHTNESS_MAX, gi, key);

  }

  private void renderBorder(IBlockAccess blockAccess, int x, int y, int z, int meta) {
    IIcon texture = EnderIO.blockCapBank.getBorderIcon(0, meta);
    for (ForgeDirection face : ForgeDirection.VALID_DIRECTIONS) {
      RenderUtil.renderConnectedTextureFace(blockAccess, EnderIO.blockCapBank,  x, y, z, face, texture,
          blockAccess == null, false, false);
    }
  }

  //---- Info Display

  @Override
  public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTick) {

    TileCapBank cb = (TileCapBank) te;


    GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
    GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
    GL11.glPolygonOffset(-1.0f, -1.0f);

    GL11.glPushMatrix();
    GL11.glTranslatef((float) x, (float) y, (float) z);

    for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
      InfoDisplayType type = cb.getDisplayType(dir);
      if(type != InfoDisplayType.NONE) {
        IInfoRenderer rend = infoRenderers.get(type);
        if(rend != null) {
          rend.render(cb, dir, x, y, z, partialTick);
        }
      }
    }

    GL11.glPopMatrix();
    GL11.glPopAttrib();

  }

}
