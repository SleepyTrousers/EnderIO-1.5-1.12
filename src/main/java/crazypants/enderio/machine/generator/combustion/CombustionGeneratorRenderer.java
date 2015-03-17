package crazypants.enderio.machine.generator.combustion;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.machine.OverlayRenderer;
import crazypants.enderio.machine.TechneModelRenderer;
import crazypants.render.CustomCubeRenderer;
import crazypants.render.VertexRotationFacing;

@SideOnly(Side.CLIENT)
public class CombustionGeneratorRenderer extends TechneModelRenderer {

  private CustomCubeRenderer ccr = new CustomCubeRenderer();

  private OverlayRenderer overlayRenderer = new OverlayRenderer();

  public CombustionGeneratorRenderer() {
    super("models/combustionGen", BlockCombustionGenerator.renderId, new VertexRotationFacing(ForgeDirection.NORTH));
  }

  @Override
  public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
    TileCombustionGenerator gen = null;
    ForgeDirection facing = ForgeDirection.SOUTH;
    if(world != null) {
      TileEntity te = world.getTileEntity(x, y, z);
      if(te instanceof TileCombustionGenerator) {
        gen = (TileCombustionGenerator) te;
        facing = gen.getFacingDir();
      } else {
        gen = null;
      }
    }

    vt = new VertexRotationFacing(ForgeDirection.NORTH);
    ((VertexRotationFacing) vt).setRotation(facing);

    if(gen != null) {
      overlayRenderer.setTile(gen);
      ccr.renderBlock(world, block, x, y, z, overlayRenderer);
    }

    super.renderWorldBlock(world, x, y, z, block, modelId, renderer);

    return true;
  }

  @Override
  public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
    ((VertexRotationFacing) vt).setRotation(ForgeDirection.SOUTH);
    super.renderInventoryBlock(block, metadata, modelId, renderer);
  }
}
