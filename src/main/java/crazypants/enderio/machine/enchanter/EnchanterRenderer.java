package crazypants.enderio.machine.enchanter;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import crazypants.enderio.EnderIO;
import crazypants.render.BoundingBox;
import crazypants.render.CubeRenderer;
import crazypants.render.IconUtil;
import crazypants.render.VertexRotation;
import crazypants.render.VertexTransform;
import crazypants.vecmath.Vector3d;
import crazypants.vecmath.Vector3f;
import crazypants.vecmath.Vertex;

public class EnchanterRenderer implements ISimpleBlockRenderingHandler, IItemRenderer {

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
    Tessellator tes = Tessellator.instance;
    tes.startDrawingQuads();
    renderWorldBlock(null, 0, 0, 0, null, 0, null);
    tes.draw();

  }

  @Override
  public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
  }

  @Override
  public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {

    Tessellator tes = Tessellator.instance;
    tes.addTranslation(x, y, z);
    tes.setColorOpaque_F(1, 1, 1);

    IIcon[] icons = new IIcon[6];
    for(int i=0;i<icons.length;i++) {
      if(i == ForgeDirection.UP.ordinal() || i == ForgeDirection.DOWN.ordinal()) {
        icons[i] = Blocks.obsidian.getBlockTextureFromSide(0);
      } else {
        icons[i] = EnderIO.blockConduitBundle.getConnectorIcon();
      }        
    }
    
      
    IIcon tex = EnderIO.blockConduitBundle.getConnectorIcon();
        
    float max = 0.85f;
    float min = 0.15f;
    float minY = 0;
    float maxY = 0.1f;
    BoundingBox bb = new BoundingBox(min, minY, min, max, maxY, max);    
    CubeRenderer.render(bb,tex,true);

    min = 0.40f;
    max = 0.6f;
    minY = 0.1f;
    maxY = 0.75f;
    
    bb = new BoundingBox(min, minY, min, max, maxY, max);
    CubeRenderer.render(bb, icons, true);

    min = 0.00f;
    max = 1.0f;
    minY = 0.75f;
    maxY = 0.85f;

    bb = new BoundingBox(min, minY, min, max, maxY, max);
    CubeRenderer.render(bb, icons, true);

    
    minY = 0.85f;
    maxY = 0.90f;
    min = 0.3f;
    max = 0.7f;

    int facing = ForgeDirection.NORTH.ordinal();
    if(world != null) {
      TileEntity te = world.getTileEntity(x, y, z);
      if(te instanceof TileEnchanter) {
        facing = ((TileEnchanter)te).getFacing();
      }
    }
    boolean flip = facing == ForgeDirection.EAST.ordinal() || facing == ForgeDirection.WEST.ordinal(); 
    
//    float xOff = flip ? 0 : 0.1f;
//    float zOff = flip ? 0.1f : 0f;
    float xOff = 0.1f;
    float zOff = 0f;
    
    tex = EnderIO.blockEnchanter.getBlockTextureFromSide(0);
    bb = new BoundingBox(min - xOff, minY, min - zOff, max + xOff, maxY, max + zOff);
    if(flip) {    
      CubeRenderer.render(bb, tex, new VertexRotation(Math.PI / 2, new Vector3d(0, 1, 0), new Vector3d(0.5, 0.5, 0.5)), true);
    } else {
      CubeRenderer.render(bb, tex, true);
    }
    tes.addTranslation(-x, -y, -z);

    return true;
  }

  @Override
  public boolean shouldRender3DInInventory(int modelId) {
    return true;
  }

  @Override
  public int getRenderId() {
    return BlockEnchanter.renderId;
  }

}
