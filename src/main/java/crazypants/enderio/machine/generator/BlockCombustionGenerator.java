package crazypants.enderio.machine.generator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineBlock;

public class BlockCombustionGenerator extends AbstractMachineBlock<TileCombustionGenerator> {

  public static BlockCombustionGenerator create() {
    BlockCombustionGenerator gen = new BlockCombustionGenerator();
    gen.init();
    return gen;
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
  protected String getMachineFrontIconKey(boolean active) {
    if(active) {
      return "enderio:combustionGenFrontOn";
    }
    return "enderio:combustionGenFront";
  }

  
}
