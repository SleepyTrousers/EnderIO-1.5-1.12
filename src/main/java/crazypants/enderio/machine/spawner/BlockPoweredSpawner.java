package crazypants.enderio.machine.spawner;

import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.machine.MachineRecipeRegistry;
import crazypants.enderio.machine.crusher.BlockCrusher;
import crazypants.enderio.machine.crusher.PacketGrindingBall;
import crazypants.enderio.network.PacketHandler;

public class BlockPoweredSpawner extends AbstractMachineBlock<TilePoweredSpawner> {

  public static BlockPoweredSpawner create() {   
    
    MachineRecipeRegistry.instance.registerRecipe(ModObject.blockPoweredSpawner.unlocalisedName, new DummyRecipe());
    
    BlockPoweredSpawner res = new BlockPoweredSpawner();
    res.init();
    return res;
  }
  
  protected BlockPoweredSpawner() {
    super(ModObject.blockPoweredSpawner, TilePoweredSpawner.class);    
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    return null;
  }

  @Override
  protected int getGuiId() {
    return GuiHandler.GUI_ID_POWERED_SPAWNER;
  }

  @Override
  protected String getMachineFrontIconKey(boolean active) {
    if(active) {
      return "enderio:poweredSpawnerFrontActive";
    }
    return "enderio:poweredSpawnerFront";
  }
  
  @Override
  public boolean isOpaqueCube() {
    return false;
  }

}
