package crazypants.enderio.item.skull;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.generator.zombie.TileZombieGenerator;
import crazypants.render.BoundingBox;
import crazypants.render.CubeRenderer;
import crazypants.render.RenderUtil;
import crazypants.render.VertexRotation;
import crazypants.vecmath.Vector3d;
import net.minecraft.block.Block;
import net.minecraft.client.model.ModelEnderman;
import net.minecraft.client.model.ModelSkeletonHead;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderEnderman;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class EndermanSkullRenderer implements ISimpleBlockRenderingHandler {

  public EndermanSkullRenderer() {   
  }

  @Override
  public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
    renderWorldBlock(null, 0, 0, 0, block, modelId, renderer);
  }

  @Override
  public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
    Tessellator tes = Tessellator.instance;
    tes.addTranslation(x, y, z);
    tes.setColorOpaque_F(1, 1, 1);
    
    int brightness;
    if(world == null) {
      brightness = 15 << 20 | 15 << 4;
    } else {
      brightness = world.getLightBrightnessForSkyBlocks(x, y, z, 0);
    }
    tes.setBrightness(brightness);
    

    IIcon[] icons = new IIcon[6];
    for(int i=0;i<icons.length;i++) {      
      icons[i] = block.getIcon(i, 0);              
    }
    
    float yaw = 0;
    if(world != null) {
      TileEntity te = world.getTileEntity(x, y, z);
      if(te instanceof TileEndermanSkull) {
        yaw = ((TileEndermanSkull)te).yaw;
      }
    }
    
    VertexRotation rot = new VertexRotation(Math.toRadians(yaw), new Vector3d(0,1,0), new Vector3d(0.5,0,0.5));
    
    float size = 0.25f;
    float height = 0.5f;
    BoundingBox bb = new BoundingBox(size, 0, size, 1 - size, height, 1 - size); 
    CubeRenderer.render(bb, icons, rot, true);
    
    tes.addTranslation(-x, -y, -z);

    return true;
  }

  @Override
  public boolean shouldRender3DInInventory(int modelId) {
    return false;
  }

  @Override
  public int getRenderId() {
    return BlockEndermanSkull.renderId;
  }
  
  
}
