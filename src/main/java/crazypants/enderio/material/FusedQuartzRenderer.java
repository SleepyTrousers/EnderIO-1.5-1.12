package crazypants.enderio.material;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import com.enderio.core.client.render.ConnectedTextureRenderer;
import com.enderio.core.client.render.ConnectedTextureRenderer.DefaultTextureCallback;
import com.enderio.core.client.render.CustomCubeRenderer;
import com.enderio.core.client.render.RenderUtil;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import crazypants.enderio.EnderIO;
import crazypants.enderio.conduit.render.ConduitBundleRenderer.FacadeAccessWrapper;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.painter.PainterUtil;
import crazypants.enderio.machine.painter.TileEntityPaintedBlock;

public class FusedQuartzRenderer implements ISimpleBlockRenderingHandler {

  static int renderPass;

  private ConnectedTextureRenderer connectedTextureRenderer = new ConnectedTextureRenderer() {
    @Override
    public boolean matchesMetadata(int meta1, int meta2) {
      return super.matchesMetadata(meta1, meta2) || BlockFusedQuartz.Type.byMeta(meta1).connectTo(meta2);
    }
  };

  public FusedQuartzRenderer() {
    connectedTextureRenderer.setMatchMeta(!Config.clearGlassConnectToFusedQuartz);
  }
  
  @Override
  public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
    renderer.setOverrideBlockTexture(EnderIO.blockFusedQuartz.getItemIcon(metadata));
    renderer.renderBlockAsItem(Blocks.glass, 0, 1);
    renderer.clearOverrideBlockTexture();
  }

  @Override
  public boolean shouldRender3DInInventory(int modelId) {
    return true;
  }

  @Override
  public int getRenderId() {
    return BlockFusedQuartz.renderId;
  }

  @Override
  public boolean renderWorldBlock(IBlockAccess blockAccess, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
    int meta = blockAccess.getBlockMetadata(x, y, z);
    //TODO:1.7 this makes it go splat
    //if((meta == 0 && renderPass != 0) || (meta == 1 && renderPass == 0)) {
    TileEntityPaintedBlock tecb = null;
    TileEntity te = blockAccess.getTileEntity(x, y, z);
    if(te instanceof TileEntityPaintedBlock) {
      tecb = (TileEntityPaintedBlock) te;
    }
    
    if(renderer.hasOverrideBlockTexture()) {
      renderer.renderStandardBlock(block, x, y, z);
    } else {
      IBlockAccess origBa = renderer.blockAccess;
      renderer.blockAccess = new FacadeAccessWrapper(origBa);
      try {
        renderFrame(renderer.blockAccess, x, y, z, tecb, false, meta);
      } finally {
        renderer.blockAccess = origBa;
      }
    }
    //    }
    return true;
  }

  public void renderFrameItem(ItemStack stack) {
    RenderUtil.bindBlockTexture();
    Tessellator.instance.startDrawingQuads();
    TileEntityPaintedBlock tecb = new TileEntityPaintedBlock();
    tecb.setSourceBlock(PainterUtil.getSourceBlock(stack));
    tecb.setSourceBlockMetadata(PainterUtil.getSourceBlockMetadata(stack));
    renderFrame(null, 0, 0, 0, tecb, true, stack.getItemDamage());
    Tessellator.instance.draw();
  }

  private void renderFrame(IBlockAccess blockAccess, int x, int y, int z, TileEntityPaintedBlock tecb, boolean forceAllEdges, int meta) {

    if(blockAccess == null) {
      //No lighting
      IIcon texture = EnderIO.blockFusedQuartz.getItemIcon(meta);
      for (ForgeDirection face : ForgeDirection.VALID_DIRECTIONS) {
        if(tecb != null && tecb.getSourceBlock() != null) {
          texture = tecb.getSourceBlock().getIcon(face.ordinal(), tecb.getSourceBlockMetadata());
        }
        RenderUtil.renderConnectedTextureFace(blockAccess, EnderIO.blockFusedQuartz, x, y, z, face, texture, forceAllEdges);
      }
      return;
    }

    CustomCubeRenderer.instance.setOverrideTexture(EnderIO.blockFusedQuartz.getIcon(0, meta));

    if(tecb != null && tecb.getSourceBlock() != null) {
      connectedTextureRenderer.setEdgeTexureCallback(new DefaultTextureCallback(tecb.getSourceBlock(), tecb.getSourceBlockMetadata()));
      CustomCubeRenderer.instance.renderBlock(blockAccess, EnderIO.blockFusedQuartz, x, y, z,
          connectedTextureRenderer);
    } else {
      connectedTextureRenderer.setEdgeTexture(EnderIO.blockFusedQuartz.getDefaultFrameIcon(meta));
      CustomCubeRenderer.instance.renderBlock(blockAccess, EnderIO.blockFusedQuartz, x, y, z, connectedTextureRenderer);
    }

    CustomCubeRenderer.instance.setOverrideTexture(null);
  }
}
