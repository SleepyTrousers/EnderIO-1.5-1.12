package crazypants.enderio.machine.generator;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineBlock;

public class BlockCombustionGenerator extends AbstractMachineBlock<TileCombustionGenerator> {

  public static int renderId;
  
  protected IIcon frontOn;
  protected IIcon frontOff;

  public static BlockCombustionGenerator create() {
    BlockCombustionGenerator gen = new BlockCombustionGenerator();
    gen.init();
    return gen;
  }

  
  
  @Override
  public void registerBlockIcons(IIconRegister iIconRegister) {
    super.registerBlockIcons(iIconRegister);
    frontOn = iIconRegister.registerIcon("enderio:combustionGenFrontOn");
    frontOff = iIconRegister.registerIcon("enderio:combustionGenFront");    
  }



  protected BlockCombustionGenerator() {
    super(ModObject.blockCombustionGenerator, TileCombustionGenerator.class);
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    return new GuiCombustionGenerator(player.inventory, (TileCombustionGenerator) world.getTileEntity(x, y, z));
  }

  @Override
  protected int getGuiId() {
    return GuiHandler.GUI_ID_COMBUSTION_GEN;
  }

  @Override
  public int getRenderType() {    
    return renderId;
  }
  
  
  @Override
  public boolean renderAsNormalBlock() {
    return false;
  }
  

  @Override
  public boolean isOpaqueCube() {   
    return false;
  }
  
  public IIcon getBlankSideIcon() {
    return iconBuffer[0][3];
  }

  public IIcon getFrontOn() {
    return frontOn;
  }

  public IIcon getFrontOff() {
    return frontOff;
  }


  public String getTopIconKey(boolean active) {
    return super.getTopIconKey(active);
  }

  @Override
  public String getBackIconKey(boolean active) {
    return getMachineFrontIconKey(active);
  }

  @Override
  public String getMachineFrontIconKey(boolean active) {    
    return "enderio:blankMachinePanel";      
  
  }

  
}
