package crazypants.render;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.obj.GroupObject;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import crazypants.enderio.machine.AbstractMachineEntity;

public class TechneModelRenderer implements ISimpleBlockRenderingHandler {

  private List<GroupObject> model;
  private int renderId;

  protected VertexTransform vt = new VertexRotationFacing(ForgeDirection.NORTH);
  
  private boolean vtDefault = true;

  public TechneModelRenderer(String modelPath, int renderId) {
    this(modelPath, renderId, null);
  }

  /** {@link #shouldRotate()} has no effect as {@link #vt} overrides it */
  public TechneModelRenderer(String modelPath, int renderId, VertexTransform vt) {
    model = TechneUtil.getModel(modelPath);
    this.renderId = renderId;
    if(vt != null) {
      this.vt = vt;
      vtDefault = false;
    }
  }

  @Override
  public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
    if(useVT()) {
      if(vtDefault) {
        ((VertexRotationFacing) vt).setRotation(ForgeDirection.SOUTH);
      }
      TechneUtil.vt = this.vt;
    }
    TechneUtil.renderInventoryBlock(model, block, metadata, renderer);
  }

  @Override
  public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
    if(useVT()) {
      TileEntity te = world.getTileEntity(x, y, z);
      if(vtDefault && te instanceof AbstractMachineEntity) {
        ((VertexRotationFacing) vt).setRotation(((AbstractMachineEntity) te).getFacingDir());
      }
      TechneUtil.vt = this.vt;
    }
    return TechneUtil.renderWorldBlock(model, world, x, y, z, block, renderer);
  }

  @Override
  public boolean shouldRender3DInInventory(int modelId) {
    return true;
  }

  @Override
  public int getRenderId() {
    return renderId;
  }

  private boolean useVT() {
    return shouldRotate() || !vtDefault;
  }

  protected boolean shouldRotate() {
    return true;
  }
}
