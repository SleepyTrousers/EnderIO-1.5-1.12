package crazypants.enderio.machine.spawnguard;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.machine.attractor.BlockAttractor;
import crazypants.enderio.machine.attractor.ContainerAttractor;
import crazypants.enderio.machine.attractor.GuiAttractor;
import crazypants.enderio.machine.attractor.TileAttractor;

public class BlockSpawnGuard extends AbstractMachineBlock<TileSpawnGuard> {

  public static int renderId;
  
  public static BlockSpawnGuard create() {
    BlockSpawnGuard res = new BlockSpawnGuard();
    res.init();
    
    //Just making sure its loaded
    SpawnGuardController.instance.toString();
    
    return res;
  }
  
  protected BlockSpawnGuard() {
    super(ModObject.blockSpawnGuard, TileSpawnGuard.class);
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileSpawnGuard) {
      return new ContainerSpawnGuard(player.inventory, (TileSpawnGuard)te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileSpawnGuard) {
      return new GuiSpawnGurad(player.inventory, (TileSpawnGuard)te);
    }
    return null;
  }

  @Override
  protected int getGuiId() {    
    return GuiHandler.GUI_ID_SPAWN_GUARD;
  }

  @Override
  protected String getMachineFrontIconKey(boolean active) {
    if(active) {
      return "enderio:blockAttractorSideOn";
    }
    return "enderio:blockAttractorSide";
  }
  
  @Override
  protected String getSideIconKey(boolean active) {
    if(active) {
      return "enderio:blockAttractorSideOn";
    }
    return "enderio:blockAttractorSide";
  }

  @Override
  protected String getBackIconKey(boolean active) {
    if(active) {
      return "enderio:blockAttractorSideOn";
    }
    return "enderio:blockAttractorSide";
  }

  @Override
  protected String getTopIconKey(boolean active) {
    return "enderio:blockSoulMachineTop";
  }

  @Override
  public boolean renderAsNormalBlock() {
    return false;
  }

  @Override
  public boolean isOpaqueCube() {
    return false;
  }
  
  @Override
  public int getLightOpacity() {
    return 0;
  }
  
  @Override
  public int getRenderType() {    
    return renderId;
  }
  
  @SideOnly(Side.CLIENT)
  @Override
  public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
    if(isActive(world, x, y, z) && world.getTotalWorldTime() % 5 == 0) {
      float startX = x + 1.0F;
      float startY = y + 0.85F;
      float startZ = z + 1.0F;
      for (int i = 0; i < 1; i++) {
        float xOffset = -0.2F - rand.nextFloat() * 0.6F;
        float yOffset = -0.1F + rand.nextFloat() * 0.2F;
        float zOffset = -0.2F - rand.nextFloat() * 0.6F;        
        
        EntityFX fx = Minecraft.getMinecraft().renderGlobal.doSpawnParticle("spell", startX + xOffset, startY + yOffset, startZ + zOffset, 0.0D, 0.0D, 0.0D);
        if(fx != null) {
          fx.setRBGColorF(0.2f, 0.2f, 0.8f);          
          fx.motionY *= 0.5f;
        }

      }
    }
  }  

}
