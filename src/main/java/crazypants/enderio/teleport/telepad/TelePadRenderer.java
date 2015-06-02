package crazypants.enderio.teleport.telepad;

import java.util.Collection;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.obj.GroupObject;
import net.minecraftforge.common.util.ForgeDirection;

import com.google.common.collect.Lists;

import crazypants.render.CubeRenderer;
import crazypants.render.TechneModelRenderer;
import crazypants.render.TechneUtil;
import crazypants.util.BlockCoord;

public class TelePadRenderer extends TechneModelRenderer {

  public static final Map<String, GroupObject> all = TechneUtil.getModel("models/telePad");

  public TelePadRenderer() {
    super(getModel(), BlockTelePad.renderId);
  }

  private static Collection<GroupObject> getModel() {
    Collection<GroupObject> model = Lists.newArrayList();
    for (String s : all.keySet()) {
      if(!s.equals("glass") && !s.contains("blade")) {
        model.add(all.get(s));
      }
    }
    return model;
  }

  @Override
  public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
    TileTelePad te = (TileTelePad) world.getTileEntity(x, y, z);
    boolean ret = true;
    if (te.inNetwork()) {
      if (renderer.hasOverrideBlockTexture()) {
        IIcon icon = renderer.overrideBlockTexture;
        renderer.renderFaceYNeg(block, x, y, z, icon);
        renderer.renderFaceYPos(block, x, y, z, icon);
        BlockCoord bc = te.getLocation();
        if (!isTelepad(world, bc, ForgeDirection.EAST)) {
          renderer.renderFaceXPos(block, x, y, z, icon);
        }
        if (!isTelepad(world, bc, ForgeDirection.WEST)) {
          renderer.renderFaceXNeg(block, x, y, z, icon);
        }
        if (!isTelepad(world, bc, ForgeDirection.SOUTH)) {
          renderer.renderFaceZPos(block, x, y, z, icon);
        }
        if (!isTelepad(world, bc, ForgeDirection.NORTH)) {
          renderer.renderFaceZNeg(block, x, y, z, icon);
        }
      } else {
        if (te.isMaster()) {
          super.renderWorldBlock(world, x, y, z, block, modelId, renderer);
        } else {
          ret = false;
        }
      }
    } else {
      renderer.renderStandardBlock(block, x, y, z);
    }
    return ret;
  }
  
  private boolean isTelepad(IBlockAccess world, BlockCoord pos, ForgeDirection dir) {
    return pos.getLocation(dir).getTileEntity(world) instanceof TileTelePad;
  }

  @Override
  public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
    Tessellator.instance.startDrawingQuads();
    CubeRenderer.render(block, metadata);
    Tessellator.instance.draw();
  }
}
