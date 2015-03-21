package crazypants.render;

import java.util.Collection;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.obj.GroupObject;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class TechneModelRenderer implements ISimpleBlockRenderingHandler {

  private Collection<GroupObject> model;
  private int renderId;

  protected VertexTransform vt;
  
  public TechneModelRenderer(String modelPath, int renderId) {
    this(modelPath, renderId, null);
  }

  public TechneModelRenderer(String modelPath, int renderId, VertexTransform vt) {
    this(TechneUtil.getModelAll(modelPath), renderId, vt);
  }
  
  public TechneModelRenderer(Collection<GroupObject> model, int renderId) {
    this(model, renderId, null);
  }

  public TechneModelRenderer(Collection<GroupObject> model, int renderId, VertexTransform vt) {
    this.model = model;
    this.renderId = renderId;
    this.vt = vt;
  }

  @Override
  public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
    TechneUtil.vt = this.vt;
    TechneUtil.renderInventoryBlock(model, block, metadata, renderer);
  }

  @Override
  public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
    TechneUtil.vt = this.vt;
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
}
