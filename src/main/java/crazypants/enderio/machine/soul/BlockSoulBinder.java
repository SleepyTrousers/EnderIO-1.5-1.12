package crazypants.enderio.machine.soul;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.MachineRecipeRegistry;
import crazypants.enderio.machine.farm.BlockFarmStation;
import crazypants.enderio.machine.farm.PacketFarmAction;
import crazypants.enderio.machine.farm.PacketUpdateNotification;
import crazypants.enderio.machine.farm.TileFarmStation;
import crazypants.enderio.machine.painter.GuiPainter;
import crazypants.enderio.machine.painter.PainterContainer;
import crazypants.enderio.machine.painter.TileEntityPainter;
import crazypants.enderio.network.PacketHandler;

public class BlockSoulBinder extends AbstractMachineBlock<TileSoulBinder> {
  
  public static int renderId;
  
  public static BlockSoulBinder create() {    
    BlockSoulBinder result = new BlockSoulBinder();
    result.init();
    return result;
  }

  IIcon zombieSkullIcon;
  IIcon skeletonSkullIcon;
  IIcon creeperSkullIcon;
  IIcon endermanSkullIcon;
  IIcon endermanSkullIconOn;
  
  protected BlockSoulBinder() {
    super(ModObject.blockSoulBinder, TileSoulBinder.class);
  }
  
  @Override
  protected void init() {    
    super.init();
    MachineRecipeRegistry.instance.registerRecipe(ModObject.blockSoulBinder.unlocalisedName, SoulBinderSpawnerRecipe.instance);
  }
  
  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileSoulBinder) {
      return new ContainerSoulBinder(player.inventory, (AbstractMachineEntity) te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileSoulBinder) {
      return new GuiSoulBinder(player.inventory, (AbstractMachineEntity) te);
    }
    return null;
  }

  @Override
  protected int getGuiId() {
    return GuiHandler.GUI_ID_SOUL_BINDER;
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
    return 7;
  }
  
  @Override
  @SideOnly(Side.CLIENT)
  public void registerBlockIcons(IIconRegister iIconRegister) {    
    super.registerBlockIcons(iIconRegister);
    zombieSkullIcon = iIconRegister.registerIcon("enderio:skullZombie");
    creeperSkullIcon = iIconRegister.registerIcon("enderio:skullCreeper");
    skeletonSkullIcon = iIconRegister.registerIcon("enderio:skullSkeleton");
    endermanSkullIcon = iIconRegister.registerIcon("enderio:endermanSkullFront");
    endermanSkullIconOn= iIconRegister.registerIcon("enderio:endermanSkullFrontEyes");
  }

  @Override
  protected String getMachineFrontIconKey(boolean active) {
    if(active) {
      return "enderio:blockSoulBinderOn";
    }
    return "enderio:blockSoulBinder";
  }
  
  

  @Override
  protected String getSideIconKey(boolean active) {
    return "enderio:blockSoulBinderSide";
  }

  @Override
  public int getRenderType() {    
    return renderId;
  }
  
  @SideOnly(Side.CLIENT)
  @Override
  public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
    // If active, randomly throw some smoke around
    if(isActive(world, x, y, z)) {
      float startX = x + 1.0F;
      float startY = y + 1.0F;
      float startZ = z + 1.0F;
      for (int i = 0; i < 2; i++) {
        float xOffset = -0.2F - rand.nextFloat() * 0.6F;
        float yOffset = -0.1F + rand.nextFloat() * 0.2F;
        float zOffset = -0.2F - rand.nextFloat() * 0.6F;        
        
        EntityFX fx = Minecraft.getMinecraft().renderGlobal.doSpawnParticle("spell", startX + xOffset, startY + yOffset, startZ + zOffset, 0.0D, 0.0D, 0.0D);
        //EntityFX fx = Minecraft.getMinecraft().renderGlobal.doSpawnParticle("instantSpell", startX + xOffset, startY + yOffset, startZ + zOffset, 0.0D, 0.0D, 0.0D);
        if(fx != null) {
          fx.setRBGColorF(0.2f, 0.2f, 0.8f);          
          //fx.setRBGColorF(0.1f, 0.4f, 0.1f);
          fx.motionY *= 0.5f;
        }

      }
    }
  }  

}
