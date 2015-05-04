package crazypants.enderio.teleport.telepad;

import java.util.Collection;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.obj.GroupObject;

import com.google.common.collect.Lists;

import crazypants.render.CubeRenderer;
import crazypants.render.TechneModelRenderer;
import crazypants.render.TechneUtil;

public class TelePadRenderer extends TechneModelRenderer {

  public TelePadRenderer() {
    super(TechneUtil.getModel("models/telePad"), BlockTelePad.renderId);
  }

  protected Collection<GroupObject> getModel() {
    Collection<GroupObject> model = Lists.newArrayList();
    for (String s : this.model.keySet()) {
      if(!s.equals("glass") && !s.contains("blade")) {
        model.add(this.model.get(s));
      }
    }
    return model;
  }

  @Override
  protected Collection<GroupObject> getModel(Block block, int metadata) {
    return getModel();
  }

  @Override
  protected Collection<GroupObject> getModel(IBlockAccess world, int x, int y, int z) {
    return getModel();
  }

  Map<String, GroupObject> getFullModel() {
    return model;
  }

  @Override
  public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
    TileTelePad te = (TileTelePad) world.getTileEntity(x, y, z);
    IIcon cached = renderer.overrideBlockTexture;
    boolean ret = true;
    if(te.inNetwork()) {
      renderer.overrideBlockTexture = null;
      if(te.isMaster()) {
        super.renderWorldBlock(world, x, y, z, block, modelId, renderer);
      } else {
        ret = false;
      }
    } else {
      renderer.renderStandardBlock(block, x, y, z);
    }
    renderer.overrideBlockTexture = cached;
    return ret;
  }

  @Override
  public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
    Tessellator.instance.startDrawingQuads();
    CubeRenderer.render(block, metadata);
    Tessellator.instance.draw();
  }
}
